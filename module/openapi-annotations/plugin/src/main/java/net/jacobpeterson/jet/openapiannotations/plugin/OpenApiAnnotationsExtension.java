package net.jacobpeterson.jet.openapiannotations.plugin;

import com.github.victools.jsonschema.module.jackson.JacksonSchemaModule;
import net.jacobpeterson.jet.openapiannotations.plugin.schemagenerator.SchemaGeneratorConfigProvider;
import net.jacobpeterson.jet.openapiannotations.plugin.schemagenerator.module.GsonSchemaModule;
import net.jacobpeterson.jet.openapiannotations.plugin.schemagenerator.module.JSpecifyAnnotationsSchemaModule;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.compile.JavaCompile;
import org.jspecify.annotations.NullMarked;

/**
 * {@link OpenApiAnnotationsExtension} is a Gradle extension for {@link OpenApiAnnotationsPlugin}.
 */
@NullMarked
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
     * For the {@link OpenApiAnnotationsTask}, set to <code>true</code> to use {@link JSpecifyAnnotationsSchemaModule}
     * if {@link #getSchemaGeneratorConfig()} is not provided, set to <code>false</code> otherwise.
     * <p>
     * Defaults to <code>false</code>.
     */
    public abstract Property<Boolean> getSchemaGeneratorModuleJSpecifyAnnotations();

    /**
     * For the {@link OpenApiAnnotationsTask}, set to <code>true</code> to use {@link GsonSchemaModule} if
     * {@link #getSchemaGeneratorConfig()} is not provided, set to <code>false</code> otherwise.
     * <p>
     * Defaults to <code>false</code>.
     */
    public abstract Property<Boolean> getSchemaGeneratorModuleGson();

    /**
     * For the {@link OpenApiAnnotationsTask}, set to <code>true</code> to use {@link JacksonSchemaModule} if
     * {@link #getSchemaGeneratorConfig()} is not provided, set to <code>false</code> otherwise.
     * <p>
     * Defaults to <code>false</code>.
     */
    public abstract Property<Boolean> getSchemaGeneratorModuleJackson();

    /**
     * The {@link OpenApiAnnotationsTask} output directory.
     */
    public abstract DirectoryProperty getOutputDirectory();
}
