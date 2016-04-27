# swagger-jaxrs-maven-plugin

This maven plugin allows you to generate a swagger specification file via a maven goal.
It is only a thin wrapper around the swagger-jaxrs library, and uses its reader implementation.
For this reason please report any generation specific issues to the
[swagger-core issue tracker](https://github.com/swagger-api/swagger-core/issues) directly.


## Example usage

This is an example xml which shows a possible usage for using the swagger api.
It lists all possible configuration options with its default values:

```xml

<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <name>swagger-jaxrs usage example</name>
    <groupId>cc.catalysts</groupId>
    <artifactId>swagger-jaxrs-demo</artifactId>

    <version>1.0.0-SNAPSHOT</version>

    <build>
        <plugins>
            <plugin>
                <groupId>cc.catalysts</groupId>
                <artifactId>swagger-jaxrs-maven-plugin</artifactId>
                <version>1.0.0-SNAPSHOT</version>
                <configuration>
                    <ouputDirectory>${project.build.directory}/generated-resources/swagger-jaxrs</ouputDirectory>
                    <ouputFormats>json,yaml</ouputFormats>
                    <pretty>false</pretty>
                    <resourcePackages></resourcePackages>
                    <info></info>
                </configuration>
                <dependencies>
                    <dependency>
                        <groupId>cc.catalysts</groupId>
                        <artifactId>swagger-jaxrs-demo-api</artifactId>
                        <version>1.0.0</version>
                    </dependency>
                </dependencies>
                <executions>
                    <execution>
                        <phase>generate-resources</phase>
                        <goals>
                            <goal>swagger-jaxrs</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

## License

[MIT](https://opensource.org/licenses/MIT)