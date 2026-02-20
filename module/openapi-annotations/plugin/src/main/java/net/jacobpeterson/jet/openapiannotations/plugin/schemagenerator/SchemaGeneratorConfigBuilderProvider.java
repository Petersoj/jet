package net.jacobpeterson.jet.openapiannotations.plugin.schemagenerator;

import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import org.gradle.api.Task;
import org.gradle.api.tasks.Input;
import org.jspecify.annotations.NullMarked;

import java.io.Serializable;

/**
 * {@link SchemaGeneratorConfigBuilderProvider} is a {@link FunctionalInterface} to provide a
 * {@link SchemaGeneratorConfigBuilder}.
 * <p>
 * Note: {@link SchemaGeneratorConfigBuilderProvider} extends {@link Serializable} so that this
 * {@link FunctionalInterface} can be used as a {@link Task} {@link Input}, but if the {@link #provide()} implementation
 * is changed, it will not re-run the {@link Task}. The Gradle <code>clean</code> task must be run in order to use the
 * update {@link #provide()} implementation.
 */
@NullMarked
@FunctionalInterface
public interface SchemaGeneratorConfigBuilderProvider extends Serializable {

    /**
     * @return the {@link SchemaGeneratorConfigBuilder}
     */
    SchemaGeneratorConfigBuilder provide();
}
