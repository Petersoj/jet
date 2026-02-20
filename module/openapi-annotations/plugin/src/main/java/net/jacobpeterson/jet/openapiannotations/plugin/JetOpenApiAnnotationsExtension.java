package net.jacobpeterson.jet.openapiannotations.plugin;

import com.github.victools.jsonschema.module.jackson.JacksonSchemaModule;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApi;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiComponents;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiOperation;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiPathItem;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiSchema;
import net.jacobpeterson.jet.openapiannotations.plugin.schemagenerator.SchemaGeneratorConfigBuilderProvider;
import net.jacobpeterson.jet.openapiannotations.plugin.schemagenerator.module.enclosingclassname.EnclosingClassNameSchemaModule;
import net.jacobpeterson.jet.openapiannotations.plugin.schemagenerator.module.gson.GsonSchemaModule;
import net.jacobpeterson.jet.openapiannotations.plugin.schemagenerator.module.nullable.NullableSchemaModule;
import net.jacobpeterson.jet.openapiannotations.plugin.schemagenerator.module.schemaname.SchemaNameSchemaModule;
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
     * For the {@link JetOpenApiAnnotationsTask}, set to <code>true</code> to use the
     * {@link EnclosingClassNameSchemaModule}, set to <code>false</code> otherwise.
     * <p>
     * Defaults to <code>true</code>.
     */
    public abstract Property<Boolean> getSchemaGeneratorUseEnclosingClassNameModule();

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
     * For the {@link JetOpenApiAnnotationsTask}, set to <code>true</code> to generate an
     * {@link OpenApiOperation#operationId()}, if not already provided, using the {@link OpenApiPathItem.MapEntry#key()}
     * and {@link OpenApiOperation#tags()}, set to <code>false</code> otherwise.
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
     * output generated from {@link OpenApi} annotations using the {@link OpenApi#DEFAULT_$SCHEMA}, set to
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
