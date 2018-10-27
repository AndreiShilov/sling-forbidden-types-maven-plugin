# Simple plugin to violate you build against forbidden sling resource types.


## How to build

Just simple ```mvn clean install``` to install plugin to your local maven repository.

## Configuration

1. ```failAfterFullAnalysis``` - true or false
    * ```true``` - fail build only after full analysis
    * ```false``` - fail build immediately after first found violation  
2. ```forbiddenTypes``` - section where you can declare types you do not want to use
    * ```forbiddenType``` - type to check
    
## Configuration example

1. 

```xml
<build>
        <plugins>
            <plugin>
                <groupId>io.github.andreishilov</groupId>
                <artifactId>sling-forbidden-types-maven-plugin</artifactId>
                <version>0.0.1</version>
                <configuration>
                    <failAfterFullAnalysis>true</failAfterFullAnalysis>
                    <forbiddenTypes>
                        <forbiddenType>granite/ui/components/foundation/form/fileupload</forbiddenType>
                        <forbiddenType>granite/ui/components/foundation/form/textfield</forbiddenType>
                    </forbiddenTypes>
                </configuration>
                <executions>
                    <execution>
                        <goals>
                            <goal>forbidden-types</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
            ...
```          

2.

```xml
<build>
        <plugins>
            <plugin>
                <groupId>io.github.andreishilov</groupId>
                <artifactId>sling-forbidden-types-maven-plugin</artifactId>
                <version>0.0.1</version>
                <configuration>
                    <failAfterFullAnalysis>false</failAfterFullAnalysis>
                    <forbiddenTypes>
                        <forbiddenType>granite/ui/components/foundation/form/fileupload</forbiddenType>
                    </forbiddenTypes>
                </configuration>
                <executions>
                    <execution>
                        <goals>
                            <goal>forbidden-types</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
            ...
```  