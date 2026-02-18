package net.jacobpeterson.jet.openapiannotations.plugin;

import net.jacobpeterson.jet.openapiannotations.annotation.OpenApi;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.compile.JavaCompile;
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
        project.getTasks().register(TASK_NAME, JetOpenApiAnnotationsTask.class, task -> {
            task.setGroup("Build");
            task.setDescription("A code-first OpenAPI specification annotations processor Gradle plugin.");
            task.getJavaCompileTasks()
                    .set(extension.getJavaCompileTasks()
                            .convention(project.getTasks().withType(JavaCompile.class)));
            task.getSchemaGeneratorConfig()
                    .set(extension.getSchemaGeneratorConfig());
            task.getSchemaGeneratorModuleJSpecifyAnnotations()
                    .set(extension.getSchemaGeneratorModuleJSpecifyAnnotations()
                            .convention(false));
            task.getSchemaGeneratorModuleGson()
                    .set(extension.getSchemaGeneratorModuleGson()
                            .convention(false));
            task.getSchemaGeneratorModuleJackson()
                    .set(extension.getSchemaGeneratorModuleJackson()
                            .convention(false));
            task.getOutputDirectory()
                    .set(extension.getOutputDirectory()
                            .convention(project.getLayout().getBuildDirectory()
                                    .dir(BUILD_OUTPUT_DEFAULT_DIRECTORY_NAME)));
        });
    }
}
