package net.jacobpeterson.jet.openapiannotations.plugin.schemagenerator;

import com.github.victools.jsonschema.generator.SchemaGeneratorConfig;
import org.gradle.api.Task;
import org.gradle.api.tasks.Input;
import org.jspecify.annotations.NullMarked;

import java.io.Serializable;

/**
 * {@link SchemaGeneratorConfigProvider} is a {@link FunctionalInterface} to provide a custom
 * {@link SchemaGeneratorConfig}.
 * <p>
 * Note: {@link SchemaGeneratorConfigProvider} extends {@link Serializable} so that this {@link FunctionalInterface} can
 * be used as a {@link Task} {@link Input}, but if the return value from {@link #provide()} changes, it will not re-run
 * the {@link Task}. The Gradle <code>clean</code> task must be run in order to use the changed {@link #provide()}.
 */
@NullMarked
@FunctionalInterface
public interface SchemaGeneratorConfigProvider extends Serializable {

    /**
     * @return the {@link SchemaGeneratorConfig}
     */
    SchemaGeneratorConfig provide();
}
