package cc.catalysts.maven.swagger;

import com.fasterxml.jackson.databind.ObjectWriter;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.models.Info;
import io.swagger.models.Swagger;
import io.swagger.util.Json;
import io.swagger.util.Yaml;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Thomas Scheinecker, Catalysts GmbH
 */
@Mojo(name = "swagger-jaxrs",
        defaultPhase = LifecyclePhase.GENERATE_RESOURCES,
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class SwaggerJaxrsMojo extends AbstractMojo {
    public enum OutputFormat {
        json, yaml
    }

    /**
     * A swagger Info object containing base information for swagger
     */
    @Parameter(required = true)
    private Info info;

    /**
     * A list of packages to allow. If empty all packages are allowed.
     */
    @Parameter
    private List<String> resourcePackages;

    /**
     * The output directory for the generated spec files.
     */
    @Parameter(defaultValue = "${project.build.directory}/generated-resources/swagger-jaxrs")
    private String outputDirectory;

    /**
     * A set of output formats to generate.
     * Possible values: 'json' or 'yaml'
     */
    @Parameter(defaultValue = "json,yaml")
    private Set<OutputFormat> outputFormats = new HashSet<>(Arrays.asList(OutputFormat.values()));

    /**
     * Whether or not to pretty-print the generated output file.
     */
    @Parameter(defaultValue = "false")
    private boolean pretty;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Starting swagger specification generation");

        if (outputFormats.isEmpty()) {
            getLog().warn("No output format specified - skipping execution");
            return;
        }

        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setResourcePackage(StringUtils.join(resourcePackages, ","));

        if (getLog().isDebugEnabled()) {
            Set<Class<?>> classes = beanConfig.classes();

            getLog().debug("Scanning " + classes.size() + " classes:");
            for (Class<?> aClass : classes) {
                getLog().info(aClass.getName());
            }
        }

        beanConfig.scanAndRead();

        try {
            File swaggerSpecDir = new File(outputDirectory);

            for (OutputFormat ouputFormat : outputFormats) {
                ObjectWriter writer;
                File output;

                switch (ouputFormat) {
                    case json:
                        output = new File(swaggerSpecDir, "swagger.json");
                        if (pretty) {
                            writer = Json.pretty();
                        } else {
                            writer = Json.mapper().writer();
                        }
                        break;
                    case yaml:
                        output = new File(swaggerSpecDir, "swagger.yaml");
                        if (pretty) {
                            writer = Yaml.pretty();
                        } else {
                            writer = Yaml.mapper().writer();
                        }
                        break;
                    default:
                        throw new IllegalArgumentException(String.format("Illegal output format '%s' provided. Supported formats are 'json' and 'yaml'", ouputFormat));
                }

                if (output.exists()) {
                    Files.deleteIfExists(output.toPath());
                }

                Files.createDirectories(output.toPath().getParent());

                Swagger swagger = beanConfig.getSwagger();
                swagger.setInfo(info);
                writer.writeValue(output, swagger);
                getLog().debug("." + output.getPath() + " generated");
            }
        } catch (IOException e) {
            throw new MojoFailureException("Couldn't write swagger.json", e);
        }
        getLog().info("Swagger specification generation finished");
    }
}
