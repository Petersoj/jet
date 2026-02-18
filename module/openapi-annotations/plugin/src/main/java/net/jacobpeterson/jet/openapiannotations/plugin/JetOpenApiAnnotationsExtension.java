package net.jacobpeterson.jet.openapiannotations.plugin;

import com.github.victools.jsonschema.module.jackson.JacksonSchemaModule;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApi;
import net.jacobpeterson.jet.openapiannotations.plugin.schemagenerator.SchemaGeneratorConfigProvider;
import net.jacobpeterson.jet.openapiannotations.plugin.schemagenerator.module.GsonSchemaModule;
import net.jacobpeterson.jet.openapiannotations.plugin.schemagenerator.module.JSpecifyAnnotationsSchemaModule;
import org.gradle.api.Task;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.jvm.tasks.Jar;
import org.jspecify.annotations.NullMarked;

/**
 * {@link JetOpenApiAnnotationsExtension} is a Gradle extension for {@link JetOpenApiAnnotationsPlugin}.
 */
@NullMarked
public abstract class JetOpenApiAnnotationsExtension {

    /**
     * The {@link JavaCompile} tasks that are used to read compiled class files and classpaths from in
     * {@link JetOpenApiAnnotationsTask}.
     */
    public abstract SetProperty<JavaCompile> getJavaCompileTasks();

    /**
     * The {@link SchemaGeneratorConfigProvider} for the {@link JetOpenApiAnnotationsTask}.
     */
    public abstract Property<SchemaGeneratorConfigProvider> getSchemaGeneratorConfig();

    /**
     * For the {@link JetOpenApiAnnotationsTask}, set to <code>true</code> to use
     * {@link JSpecifyAnnotationsSchemaModule} if {@link #getSchemaGeneratorConfig()} is not provided, set to
     * <code>false</code> otherwise.
     * <p>
     * Defaults to <code>false</code>.
     */
    public abstract Property<Boolean> getSchemaGeneratorModuleJSpecifyAnnotations();

    /**
     * For the {@link JetOpenApiAnnotationsTask}, set to <code>true</code> to use {@link GsonSchemaModule} if
     * {@link #getSchemaGeneratorConfig()} is not provided, set to <code>false</code> otherwise.
     * <p>
     * Defaults to <code>false</code>.
     */
    public abstract Property<Boolean> getSchemaGeneratorModuleGson();

    /**
     * For the {@link JetOpenApiAnnotationsTask}, set to <code>true</code> to use {@link JacksonSchemaModule} if
     * {@link #getSchemaGeneratorConfig()} is not provided, set to <code>false</code> otherwise.
     * <p>
     * Defaults to <code>false</code>.
     */
    public abstract Property<Boolean> getSchemaGeneratorModuleJackson();

    /**
     * For the {@link JetOpenApiAnnotationsTask}, set to <code>true</code> to validate the OpenAPI specification JSON
     * output generated from {@link OpenApi} annotations using the {@link OpenApi#DEFAULT_SCHEMA}, set to
     * <code>false</code> otherwise.
     * <p>
     * Defaults to <code>true</code>.
     */
    public abstract Property<Boolean> getSchemaValidation();

    /**
     * The {@link JetOpenApiAnnotationsTask} output directory.
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
