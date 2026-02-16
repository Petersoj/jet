package net.jacobpeterson.jet.openapiannotations.plugin;

import net.jacobpeterson.jet.openapiannotations.annotation.OpenApi;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jspecify.annotations.NullMarked;

/**
 * {@link OpenApiAnnotationsPlugin} is {@link Project} {@link Plugin} for {@link OpenApi} annotations.
 */
@NullMarked
public class OpenApiAnnotationsPlugin implements Plugin<Project> {

    @Override
    public void apply(final Project project) {
        project.getTasks().register("openApiAnnotations", OpenApiAnnotationsTask.class);
    }
}
