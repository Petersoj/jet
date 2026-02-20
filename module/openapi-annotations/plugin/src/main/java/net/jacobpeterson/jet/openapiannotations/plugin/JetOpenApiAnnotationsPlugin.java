package net.jacobpeterson.jet.openapiannotations.plugin;

import net.jacobpeterson.jet.openapiannotations.annotation.OpenApi;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.jvm.tasks.Jar;
import org.jspecify.annotations.NullMarked;

/**
 * {@link JetOpenApiAnnotationsPlugin} is {@link Project} {@link Plugin} for {@link OpenApi} annotations.
 */
@NullMarked
public class JetOpenApiAnnotationsPlugin implements Plugin<Project> {

    /**
     * The name used for {@link JetOpenApiAnnotationsExtension}: <code>"jetOpenApiAnnotations"</code>
     */
    public static final String EXTENSION_NAME = "jetOpenApiAnnotations";

    /**
     * The name used for {@link JetOpenApiAnnotationsTask}: <code>"jetOpenApiAnnotations"</code>
     */
    public static final String TASK_NAME = "jetOpenApiAnnotations";

    /**
     * The build output default directory name {@link JetOpenApiAnnotationsTask#getOutputDirectory()}:
     * <code>"jet-openapi-annotations"</code>
     */
    public static final String BUILD_OUTPUT_DEFAULT_DIRECTORY_NAME = "jet-openapi-annotations";

    @Override
    public void apply(final Project project) {
        final var extension = project.getExtensions().create(EXTENSION_NAME, JetOpenApiAnnotationsExtension.class);
        extension.getJavaCompileTasks()
                .convention(project.getTasks().withType(JavaCompile.class));
        extension.getSchemaGeneratorUseNullableModule()
                .convention(true);
        extension.getSchemaGeneratorUseGsonModule()
                .convention(false);
        extension.getSchemaGeneratorUseJacksonModule()
                .convention(false);
        extension.getGenerateOperationId()
                .convention(true);
        extension.getMoveClassSchemasToComponents()
                .convention(true);
        extension.getSchemaValidation()
                .convention(true);
        extension.getOutputDirectory()
                .convention(project.getLayout().getBuildDirectory().dir(BUILD_OUTPUT_DEFAULT_DIRECTORY_NAME));
        extension.getOutputDirectoryIncludeInJar()
                .convention(true);
        final var registeredTask = project.getTasks().register(TASK_NAME, JetOpenApiAnnotationsTask.class, task -> {
            task.getJavaCompileTasks()
                    .set(extension.getJavaCompileTasks());
            task.getSchemaGeneratorConfigBuilderProvider()
                    .set(extension.getSchemaGeneratorConfigBuilderProvider());
            task.getSchemaGeneratorUseNullableModule()
                    .set(extension.getSchemaGeneratorUseNullableModule());
            task.getSchemaGeneratorUseGsonModule()
                    .set(extension.getSchemaGeneratorUseGsonModule());
            task.getSchemaGeneratorUseJacksonModule()
                    .set(extension.getSchemaGeneratorUseJacksonModule());
            task.getGenerateOperationId()
                    .set(extension.getGenerateOperationId());
            task.getMoveClassSchemasToComponents()
                    .set(extension.getMoveClassSchemasToComponents());
            task.getSchemaValidation()
                    .set(extension.getSchemaValidation());
            task.getOutputDirectory()
                    .set(extension.getOutputDirectory());
        });
        project.afterEvaluate(evaluatedProject -> {
            if (extension.getOutputDirectoryIncludeInJar().get()) {
                evaluatedProject.getTasks().withType(Jar.class).configureEach(jarTask -> {
                    jarTask.dependsOn(registeredTask);
                    final var task = registeredTask.get();
                    jarTask.from(task.getOutputDirectory(), copySpec ->
                            copySpec.into(project.getLayout().getBuildDirectory().get().getAsFile().toPath().relativize(
                                    task.getOutputDirectory().get().getAsFile().toPath()).toString()));
                });
            }
        });
    }
}
