package net.jacobpeterson.jet.openapiannotationsplugin;

import com.github.victools.jsonschema.module.jackson.JacksonSchemaModule;
import net.jacobpeterson.jet.openapiannotations.OpenApi;
import net.jacobpeterson.jet.openapiannotations.OpenApiComponents;
import net.jacobpeterson.jet.openapiannotations.OpenApiOperation;
import net.jacobpeterson.jet.openapiannotations.OpenApiPathItem;
import net.jacobpeterson.jet.openapiannotations.OpenApiSchema;
import net.jacobpeterson.jet.openapiannotationsplugin.schemagenerator.SchemaGeneratorConfigBuilderProvider;
import net.jacobpeterson.jet.openapiannotationsplugin.schemagenerator.module.gson.GsonSchemaModule;
import net.jacobpeterson.jet.openapiannotationsplugin.schemagenerator.module.nullable.NullableSchemaModule;
import net.jacobpeterson.jet.openapiannotationsplugin.schemagenerator.module.schemaname.SchemaNameSchemaModule;
import org.gradle.api.Task;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.api.tasks.compile.JavaCompile;
import org.jspecify.annotations.NullMarked;

import java.util.Map;

/**
 * {@link JetOpenApiAnnotationsExtension} is a Gradle extension for {@link JetOpenApiAnnotationsPlugin}.
 */
@NullMarked
public abstract class JetOpenApiAnnotationsExtension {

    /**
     * For the {@link JetOpenApiAnnotationsTask}, the classpath files to load into a {@link ClassLoader} that should be
     * scanned for {@link OpenApi} annotations. Can be JAR files or a directories containing <code>.class</code> files,
     * but only <code>.class</code> files will be scanned for {@link OpenApi} annotations. The classes inside JAR files
     * will not be scanned.
     * <p>
     * Defaults to {@link JavaCompile#getOutputs()}.
     */
    public abstract ConfigurableFileCollection getAnnotatedClasspaths();

    /**
     * For the {@link JetOpenApiAnnotationsTask}, the classpaths to load into a {@link ClassLoader} for dependency
     * classes used in fields that {@link OpenApiSchema#fromClass()}. Can be JAR files or directories containing
     * <code>.class</code> files. These classpaths will not be scanned for {@link OpenApi} annotations.
     * <p>
     * Defaults to {@link JavaCompile#getClasspath()}.
     */
    public abstract ConfigurableFileCollection getClasspaths();

    /**
     * The {@link SchemaGeneratorConfigBuilderProvider} for the {@link JetOpenApiAnnotationsTask}.
     */
    public abstract Property<SchemaGeneratorConfigBuilderProvider> getSchemaGeneratorConfigBuilderProvider();

    /**
     * For the {@link JetOpenApiAnnotationsTask}, set to <code>true</code> to use the {@link NullableSchemaModule}, set
     * to <code>false</code> otherwise.
     * <p>
     * Defaults to <code>true</code>.
     */
    public abstract Property<Boolean> getSchemaGeneratorUseNullableModule();

    /**
     * For the {@link JetOpenApiAnnotationsTask}, set to <code>true</code> to use the {@link SchemaNameSchemaModule},
     * set to <code>false</code> otherwise.
     * <p>
     * Defaults to <code>true</code>.
     */
    public abstract Property<Boolean> getSchemaGeneratorUseSchemaNameModule();

    /**
     * For the {@link JetOpenApiAnnotationsTask}, set to <code>true</code> to use {@link GsonSchemaModule} the, set to
     * <code>false</code> otherwise.
     * <p>
     * Defaults to <code>false</code>.
     */
    public abstract Property<Boolean> getSchemaGeneratorUseGsonModule();

    /**
     * For the {@link JetOpenApiAnnotationsTask}, set to <code>true</code> to use the {@link JacksonSchemaModule}, set
     * to <code>false</code> otherwise.
     * <p>
     * Defaults to <code>false</code>.
     */
    public abstract Property<Boolean> getSchemaGeneratorUseJacksonModule();

    /**
     * For the {@link JetOpenApiAnnotationsTask}, a {@link Map} containing simple type mappings with the fully qualified
     * class name as the key and the raw JSON schema as the value.
     */
    public abstract MapProperty<String, String> getSchemaGeneratorSimpleTypeMappings();

    /**
     * For the {@link JetOpenApiAnnotationsTask}, set to <code>true</code> to, if not already provided, generate
     * {@link OpenApiOperation#operationId()} by concatenating {@link OpenApiPathItem#methods()} with the
     * lower-camelcase conversion of the path segments of {@link OpenApiPathItem.MapEntry#key()} after the index of
     * {@link OpenApiOperation#tags()}. For example, if method is <code>POST</code>, path item key is
     * <code>/account/create</code>, and tag is <code>account</code>, then the generated operation ID is
     * <code>postCreate</code>. For many OpenAPI generated clients, this results in intuitively-named method calls, for
     * example: <code>account.postCreate(...)</code>.
     * <p>
     * Defaults to <code>true</code>.
     */
    public abstract Property<Boolean> getGenerateOperationId();

    /**
     * For the {@link JetOpenApiAnnotationsTask}, set to <code>true</code> to move the JSON schema generated from
     * {@link OpenApiSchema#fromClass()} to {@link OpenApiComponents#schemas()}, set to <code>false</code> otherwise.
     * <p>
     * Defaults to <code>true</code>.
     */
    public abstract Property<Boolean> getMoveClassSchemasToComponents();

    /**
     * For the {@link JetOpenApiAnnotationsTask}, set to <code>true</code> to validate the OpenAPI specification JSON
     * output generated from {@link OpenApi} annotations using supported OpenAPI JSON schemas (see {@link OpenApi}
     * public constants starting with <code>$SCHEMA_</code>), set to <code>false</code> otherwise.
     * <p>
     * Defaults to <code>true</code>.
     */
    public abstract Property<Boolean> getSchemaValidation();

    /**
     * The {@link JetOpenApiAnnotationsTask} output directory.
     * <p>
     * Defaults to {@link JetOpenApiAnnotationsPlugin#BUILD_OUTPUT_DEFAULT_DIRECTORY_NAME}.
     */
    public abstract DirectoryProperty getOutputDirectory();

    /**
     * Set to <code>true</code> to include the {@link #getOutputDirectory()} in each {@link Jar} {@link Task}, set to
     * <code>false</code> otherwise.
     * <p>
     * Defaults to <code>true</code>.
     */
    public abstract Property<Boolean> getOutputDirectoryIncludeInJar();
}
