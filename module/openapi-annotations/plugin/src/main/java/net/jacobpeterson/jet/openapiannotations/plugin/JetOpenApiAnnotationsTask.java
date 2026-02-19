package net.jacobpeterson.jet.openapiannotations.plugin;

import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.module.jackson.JacksonSchemaModule;
import com.google.common.collect.MultimapBuilder.ListMultimapBuilder;
import com.google.gson.GsonBuilder;
import com.networknt.schema.Schema;
import com.networknt.schema.SchemaRegistry;
import net.jacobpeterson.jet.common.http.header.Header;
import net.jacobpeterson.jet.common.http.method.Method;
import net.jacobpeterson.jet.common.http.status.Status;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApi;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiSchema;
import net.jacobpeterson.jet.openapiannotations.plugin.schemagenerator.SchemaGeneratorConfigProvider;
import net.jacobpeterson.jet.openapiannotations.plugin.schemagenerator.module.GsonSchemaModule;
import net.jacobpeterson.jet.openapiannotations.plugin.schemagenerator.module.JSpecifyAnnotationsSchemaModule;
import net.jacobpeterson.jet.openapiannotations.plugin.util.gson.GsonUtil;
import net.jacobpeterson.jet.openapiannotations.plugin.util.gson.serializer.AnnotationJsonSerializer;
import net.jacobpeterson.jet.openapiannotations.plugin.util.gson.serializer.EmptyStringIsNullJsonSerializer;
import net.jacobpeterson.jet.openapiannotations.plugin.util.gson.serializer.HeaderJsonSerializer;
import net.jacobpeterson.jet.openapiannotations.plugin.util.gson.serializer.MethodJsonSerializer;
import net.jacobpeterson.jet.openapiannotations.plugin.util.gson.serializer.OpenApiSchemaJsonSerializer;
import net.jacobpeterson.jet.openapiannotations.plugin.util.gson.serializer.StatusJsonSerializer;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectories;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.compile.JavaCompile;
import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Map.Entry;

import static com.github.victools.jsonschema.generator.Option.EXTRA_OPEN_API_FORMAT_VALUES;
import static com.github.victools.jsonschema.generator.Option.PLAIN_DEFINITION_KEYS;
import static com.github.victools.jsonschema.generator.OptionPreset.PLAIN_JSON;
import static com.github.victools.jsonschema.generator.SchemaVersion.DRAFT_2020_12;
import static com.google.common.base.Preconditions.checkArgument;
import static com.networknt.schema.InputFormat.JSON;
import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.Files.readString;
import static java.nio.file.Files.walkFileTree;
import static java.nio.file.Files.writeString;
import static java.util.Map.entry;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toUnmodifiableMap;
import static net.jacobpeterson.jet.openapiannotations.annotation.OpenApi.DEFAULT_ANNOTATION_GROUP_NAME;
import static net.jacobpeterson.jet.openapiannotations.annotation.OpenApi.DEFAULT_OPENAPI;
import static net.jacobpeterson.jet.openapiannotations.annotation.OpenApi.DEFAULT_SCHEMA;
import static org.gradle.api.tasks.PathSensitivity.RELATIVE;

/**
 * {@link JetOpenApiAnnotationsTask} is the {@link DefaultTask} for {@link JetOpenApiAnnotationsPlugin}.
 */
@NullMarked
@CacheableTask
public abstract class JetOpenApiAnnotationsTask extends DefaultTask {

    private static final @SuppressWarnings("IdentifierName") String JSON_KEY_$SCHEMA = "$schema";
    private static final String JSON_KEY_OPENAPI = "openapi";

    public JetOpenApiAnnotationsTask() {
        setGroup("Build");
        setDescription("A code-first OpenAPI specification annotations processor Gradle plugin.");
    }

    @InputFiles @PathSensitive(RELATIVE)
    public abstract SetProperty<JavaCompile> getJavaCompileTasks();

    @Input @Optional
    public abstract Property<SchemaGeneratorConfigProvider> getSchemaGeneratorConfig();

    @Input
    public abstract Property<Boolean> getSchemaGeneratorModuleJSpecifyAnnotations();

    @Input
    public abstract Property<Boolean> getSchemaGeneratorModuleGson();

    @Input
    public abstract Property<Boolean> getSchemaGeneratorModuleJackson();

    @Input
    public abstract Property<Boolean> getSchemaValidation();

    @OutputDirectories
    public abstract DirectoryProperty getOutputDirectory();

    @TaskAction
    public void run() {
        final var classLoaderUris = new HashSet<URI>();
        final var javaCompileClassNames = new HashSet<String>();
        for (final var javaCompileTask : getJavaCompileTasks().get()) {
            for (final var classPath : javaCompileTask.getClasspath()) {
                if (!classPath.exists()) {
                    continue;
                }
                classLoaderUris.add(classPath.toURI());
            }
            for (final var javaCompileOutput : javaCompileTask.getOutputs().getFiles()) {
                if (!javaCompileOutput.exists()) {
                    continue;
                }
                classLoaderUris.add(javaCompileOutput.toURI());
                final var javaCompileOutputPath = javaCompileOutput.toPath();
                try {
                    walkFileTree(javaCompileOutputPath, new SimpleFileVisitor<>() {

                        private static final String CLASS_FILE_SUFFIX = ".class";

                        @Override
                        public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) {
                            if (file.getFileName().toString().endsWith(CLASS_FILE_SUFFIX)) {
                                final var packageClass = javaCompileOutputPath.relativize(file)
                                        .toString().replace(File.separatorChar, '.');
                                javaCompileClassNames.add(packageClass.substring(0,
                                        packageClass.length() - CLASS_FILE_SUFFIX.length()));
                            }
                            return CONTINUE;
                        }
                    });
                } catch (final IOException ioException) {
                    throw new RuntimeException(ioException);
                }
            }
        }
        try (final var javaCompileClassLoader = new URLClassLoader(classLoaderUris.stream().map(classLoaderUri -> {
            try {
                return classLoaderUri.toURL();
            } catch (final MalformedURLException malformedUrlException) {
                throw new RuntimeException(malformedUrlException);
            }
        }).toArray(URL[]::new), getClass().getClassLoader())) {
            final var openApisOfGroupNames = ListMultimapBuilder
                    .hashKeys()
                    .arrayListValues()
                    .<String, OpenApi>build();
            for (final var javaCompileClassName : javaCompileClassNames) {
                final Class<?> javaCompileClass;
                try {
                    javaCompileClass = javaCompileClassLoader.loadClass(javaCompileClassName);
                } catch (final ClassNotFoundException classNotFoundException) {
                    throw new RuntimeException(classNotFoundException);
                }
                for (final var openApi : javaCompileClass.getDeclaredAnnotationsByType(OpenApi.class)) {
                    openApisOfGroupNames.put(openApi.annotationGroupName(), openApi);
                }
                for (final var method : javaCompileClass.getDeclaredMethods()) {
                    for (final var openApi : method.getDeclaredAnnotationsByType(OpenApi.class)) {
                        openApisOfGroupNames.put(openApi.annotationGroupName(), openApi);
                    }
                }
            }
            final var annotationGson = new GsonBuilder()
                    .registerTypeHierarchyAdapter(Annotation.class, new AnnotationJsonSerializer())
                    .registerTypeHierarchyAdapter(OpenApiSchema.class, new OpenApiSchemaJsonSerializer(
                            new SchemaGenerator(getSchemaGeneratorConfig()
                                    .getOrElse((SchemaGeneratorConfigProvider) () -> {
                                        final var builder = new SchemaGeneratorConfigBuilder(DRAFT_2020_12, PLAIN_JSON)
                                                .with(EXTRA_OPEN_API_FORMAT_VALUES)
                                                .with(PLAIN_DEFINITION_KEYS);
                                        if (getSchemaGeneratorModuleJSpecifyAnnotations().get()) {
                                            builder.with(new JSpecifyAnnotationsSchemaModule());
                                        }
                                        if (getSchemaGeneratorModuleGson().get()) {
                                            builder.with(new GsonSchemaModule());
                                        }
                                        if (getSchemaGeneratorModuleJackson().get()) {
                                            builder.with(new JacksonSchemaModule());
                                        }
                                        return builder.build();
                                    }).provide())))
                    .registerTypeAdapter(String.class, new EmptyStringIsNullJsonSerializer())
                    .registerTypeAdapter(Method.class, new MethodJsonSerializer())
                    .registerTypeAdapter(Status.class, new StatusJsonSerializer())
                    .registerTypeAdapter(Header.class, new HeaderJsonSerializer())
                    .create();
            Schema schema = null;
            final var outputDirectory = getOutputDirectory().get().getAsFile().toPath();
            for (final var openApiJsonOfGroupName : openApisOfGroupNames.entries().stream()
                    .map(entry -> entry(entry.getKey(), annotationGson.toJsonTree(entry.getValue())))
                    .collect(toUnmodifiableMap(Entry::getKey, Entry::getValue, GsonUtil::combine))
                    .entrySet()) {
                final var groupName = openApiJsonOfGroupName.getKey();
                final var openApiJson = openApiJsonOfGroupName.getValue().getAsJsonObject();
                if (!openApiJson.has(JSON_KEY_$SCHEMA)) {
                    openApiJson.addProperty(JSON_KEY_$SCHEMA, DEFAULT_SCHEMA);
                }
                if (!openApiJson.has(JSON_KEY_OPENAPI)) {
                    openApiJson.addProperty(JSON_KEY_OPENAPI, DEFAULT_OPENAPI);
                }
                final var openApiJsonString = openApiJson.toString();
                if (getSchemaValidation().get()) {
                    final var $schema = openApiJson.get(JSON_KEY_$SCHEMA).getAsString();
                    checkArgument($schema.equals(DEFAULT_SCHEMA),
                            "Validation for custom `@OpenApi.$schema` of `%s` is unsupported.", $schema);
                    if (schema == null) {
                        try {
                            schema = SchemaRegistry.withDefaultDialectId(null, null)
                                    .getSchema(readString(Paths.get(requireNonNull(
                                            getClass().getResource("oas-3.2-schema-2025-09-17.json")).toURI())));
                        } catch (final IOException | URISyntaxException exception) {
                            throw new RuntimeException(exception);
                        }
                    }
                    final var errors = schema.validate(openApiJsonString, JSON, executionContext -> executionContext
                            .executionConfig(executionConfig -> executionConfig
                                    .formatAssertionsEnabled(true)
                                    .annotationCollectionEnabled(true)));
                    if (!errors.isEmpty()) {
                        throw new IllegalArgumentException("\n" + errors.stream()
                                .map(error -> "        OpenAPIv%s schema offense%s: %s".formatted(DEFAULT_OPENAPI,
                                        getInAnnotationGroupErrorMessage(groupName), error.toString()))
                                .collect(joining("\n")));
                    }
                }
                try {
                    writeString(outputDirectory.resolve("openapi%s.json"
                            .formatted(!groupName.isEmpty() ? "-" + groupName : "")), openApiJsonString);
                } catch (final IOException ioException) {
                    throw new RuntimeException(ioException);
                }
            }
        } catch (final IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    private String getInAnnotationGroupErrorMessage(final String annotationGroupName) {
        return annotationGroupName.equals(DEFAULT_ANNOTATION_GROUP_NAME) ? "" :
                " in annotation group \"%s\"".formatted(annotationGroupName);
    }
}
