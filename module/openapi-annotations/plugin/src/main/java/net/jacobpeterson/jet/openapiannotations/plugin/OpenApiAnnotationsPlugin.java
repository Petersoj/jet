package net.jacobpeterson.jet.openapiannotations.plugin;

import net.jacobpeterson.jet.openapiannotations.annotation.OpenApi;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.compile.JavaCompile;
import org.jspecify.annotations.NullMarked;

/**
 * {@link OpenApiAnnotationsPlugin} is {@link Project} {@link Plugin} for {@link OpenApi} annotations.
 */
@NullMarked
public class OpenApiAnnotationsPlugin implements Plugin<Project> {

    @Override
    public void apply(final Project project) {
        final var extension = project.getExtensions().create("openApiAnnotations", OpenApiAnnotationsExtension.class);
        project.getTasks().register("openApiAnnotations", OpenApiAnnotationsTask.class, task -> {
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
                            .convention(project.getLayout().getBuildDirectory().dir("openapi-annotations")));
        });
    }
}
