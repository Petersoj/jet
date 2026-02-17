package net.jacobpeterson.jet.openapiannotations.plugin;

import net.jacobpeterson.jet.openapiannotations.plugin.schemagenerator.SchemaGeneratorConfigProvider;
import net.jacobpeterson.jet.openapiannotations.plugin.schemagenerator.module.GsonModule;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.compile.JavaCompile;
import tools.jackson.databind.JacksonModule;

/**
 * {@link OpenApiAnnotationsExtension} is a Gradle extension for {@link OpenApiAnnotationsPlugin}.
 */
public abstract class OpenApiAnnotationsExtension {

    /**
     * The {@link JavaCompile} tasks that are used to read compiled class files and classpaths from in
     * {@link OpenApiAnnotationsTask}.
     */
    public abstract SetProperty<JavaCompile> getJavaCompileTasks();

    /**
     * The {@link SchemaGeneratorConfigProvider} for the {@link OpenApiAnnotationsTask}.
     */
    public abstract Property<SchemaGeneratorConfigProvider> getSchemaGeneratorConfig();

    /**
     * For the {@link OpenApiAnnotationsTask}, set to <code>true</code> to use {@link GsonModule} if
     * {@link #getSchemaGeneratorConfig()} is not provided, set to <code>false</code> otherwise.
     * <p>
     * Defaults to <code>false</code>.
     */
    public abstract Property<Boolean> getSchemaGeneratorGsonModule();

    /**
     * For the {@link OpenApiAnnotationsTask}, set to <code>true</code> to use {@link JacksonModule} if
     * {@link #getSchemaGeneratorConfig()} is not provided, set to <code>false</code> otherwise.
     * <p>
     * Defaults to <code>false</code>.
     */
    public abstract Property<Boolean> getSchemaGeneratorJacksonModule();

    /**
     * The {@link OpenApiAnnotationsTask} output directory.
     */
    public abstract DirectoryProperty getOutputDirectory();
}
