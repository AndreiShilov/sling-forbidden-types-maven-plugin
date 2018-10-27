package io.github.andreishilov.maven.plugins.filters;

import java.io.File;
import java.util.Objects;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.AbstractFileFilter;

public class ExtensionFileFilter extends AbstractFileFilter {

    private final String extension;

    public ExtensionFileFilter(String extension) {
        Objects.requireNonNull(extension);
        this.extension = extension;
    }

    @Override
    public boolean accept(File file) {
        return this.extension.equals(FilenameUtils.getExtension(file.getAbsolutePath()));
    }

    @Override
    public boolean accept(File dir, String name) {
        return false;
    }
}
