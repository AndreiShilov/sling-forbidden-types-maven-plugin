package io.github.andreishilov.maven.plugins;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import io.github.andreishilov.maven.plugins.filters.ExtensionFileFilter;

@Mojo(name = "forbidden-types", defaultPhase = LifecyclePhase.VERIFY)
public class SlingForbiddenTypesEnforcer extends AbstractMojo {

    //todo --> I guess it is also can bbe configurable to check for example in sightly
    private static final String XML_EXTENSION = "xml";
    //todo --> can be also configurable
    private static final String[] DESIRED_SLING_ATTRIBUTES = new String[] {"sling:resourceType", "sling:resourceSuperType"};

    @Parameter(readonly = true, defaultValue = "${project}")
    private MavenProject project;

    @Parameter
    private List<String> forbiddenTypes;

    @Parameter
    private boolean failAfterFullAnalysis = false;

    public void execute() throws MojoExecutionException {
        final Log log = getLog();

        if(forbiddenTypes == null){
            log.debug("Nothing to analize");
            return;
        }

        log.debug("Deprecated types = [" + String.join(", ", forbiddenTypes) + "]");
        log.debug("FailAfterFullAnalysis = [" + failAfterFullAnalysis + "]");

        final File basedir = project.getBasedir();
//        todo --> path to the root also can be configurable
        final String path = basedir.getAbsolutePath() + "/src/main/content/jcr_root";
        final File sourceFolder = Paths.get(path).toFile();

        final Collection<File> files = FileUtils.listFiles(sourceFolder, new ExtensionFileFilter(XML_EXTENSION), DirectoryFileFilter.DIRECTORY);

        boolean marker = false;
        for (File file : files) {
            marker = checkFileContent(file) || marker;
            log.debug(MessageFormat.format("Marker = [{0}], File Path = [{1}]", marker, file.getAbsolutePath()));
        }

        if (failAfterFullAnalysis && marker) {
            throw new MojoExecutionException("Failing build after full analysis");
        }

    }

    // todo --> simplify
    private boolean checkFileContent(final File file) throws MojoExecutionException {
        boolean marker = false;
        try {
            final String fileContent = FileUtils.readFileToString(file, "utf-8");

            if (checkIfFileContainsSlingTypeAttrs(fileContent)) {
                final List<String> fileLines = Files.readAllLines(file.toPath(), Charset.defaultCharset());

                for (String forbiddenType : forbiddenTypes) {
                    if (forbiddenType != null) {

                        int linesCounter = 1;

                        for (String fileLine : fileLines) {
                            if (fileLine.contains(forbiddenType)) {
                                final String errorMsg =
                                        MessageFormat.format("File = [{0}] contains forbidden sling:resourceType = [{1}]. Line = [{2}].",
                                                file.getAbsolutePath(), forbiddenType, linesCounter);
                                getLog().error(errorMsg);

                                if (!failAfterFullAnalysis) {
                                    throw new MojoExecutionException(errorMsg);
                                } else {
                                    marker = true;
                                }
                            }
                            linesCounter++;
                        }
                    }
                }
            }

        } catch (IOException e) {
            getLog().warn("Could not get the content of the file --> " + file.getAbsolutePath() + ".", e);
        }

        return marker;
    }

    // todo -> might be a better approach
    private static boolean checkIfFileContainsSlingTypeAttrs(final String fileContent) {
        for (String attr : DESIRED_SLING_ATTRIBUTES) {
            if (fileContent.contains(attr)) {
                return true;
            }
        }
        return false;
    }
}
