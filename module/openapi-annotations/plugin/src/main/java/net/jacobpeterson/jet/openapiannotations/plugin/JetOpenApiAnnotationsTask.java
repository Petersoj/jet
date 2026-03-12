package net.jacobpeterson.jet.openapiannotations.plugin;

import com.github.victools.jsonschema.generator.CustomDefinition;
import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.module.jackson.JacksonSchemaModule;
import com.google.common.collect.MultimapBuilder.ListMultimapBuilder;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.networknt.schema.Schema;
import com.networknt.schema.SchemaRegistry;
import net.jacobpeterson.jet.common.http.header.Header;
import net.jacobpeterson.jet.common.http.method.Method;
import net.jacobpeterson.jet.common.http.status.Status;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApi;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiOperation;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiPathItem;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiSchema;
import net.jacobpeterson.jet.openapiannotations.plugin.JetOpenApiAnnotationsExtension.GenerateOperationId;
import net.jacobpeterson.jet.openapiannotations.plugin.gson.GsonUtil;
import net.jacobpeterson.jet.openapiannotations.plugin.gson.serializer.annotation.AnnotationJsonSerializer;
import net.jacobpeterson.jet.openapiannotations.plugin.gson.serializer.annotation.OpenApiJsonSerializer;
import net.jacobpeterson.jet.openapiannotations.plugin.gson.serializer.annotation.OpenApiPathItemJsonSerializer;
import net.jacobpeterson.jet.openapiannotations.plugin.gson.serializer.annotation.OpenApiSchemaJsonSerializer;
import net.jacobpeterson.jet.openapiannotations.plugin.gson.serializer.commonenum.HeaderJsonSerializer;
import net.jacobpeterson.jet.openapiannotations.plugin.gson.serializer.commonenum.MethodJsonSerializer;
import net.jacobpeterson.jet.openapiannotations.plugin.gson.serializer.commonenum.StatusJsonSerializer;
import net.jacobpeterson.jet.openapiannotations.plugin.gson.serializer.string.EmptyStringIsNullJsonSerializer;
import net.jacobpeterson.jet.openapiannotations.plugin.schemagenerator.SchemaGeneratorConfigBuilderProvider;
import net.jacobpeterson.jet.openapiannotations.plugin.schemagenerator.module.gson.GsonSchemaModule;
import net.jacobpeterson.jet.openapiannotations.plugin.schemagenerator.module.nullable.NullableSchemaModule;
import net.jacobpeterson.jet.openapiannotations.plugin.schemagenerator.module.schemaname.SchemaNameSchemaModule;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.MapProperty;
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
import tools.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import static com.github.victools.jsonschema.generator.Option.DEFINITIONS_FOR_ALL_OBJECTS;
import static com.github.victools.jsonschema.generator.Option.DEFINITIONS_FOR_MEMBER_SUPERTYPES;
import static com.github.victools.jsonschema.generator.Option.DEFINITION_FOR_MAIN_SCHEMA;
import static com.github.victools.jsonschema.generator.Option.ENUM_KEYWORD_FOR_SINGLE_VALUES;
import static com.github.victools.jsonschema.generator.Option.EXTRA_OPEN_API_FORMAT_VALUES;
import static com.github.victools.jsonschema.generator.Option.PLAIN_DEFINITION_KEYS;
import static com.github.victools.jsonschema.generator.OptionPreset.PLAIN_JSON;
import static com.github.victools.jsonschema.generator.SchemaVersion.DRAFT_2020_12;
import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE;
import static com.google.common.base.Preconditions.checkArgument;
import static com.networknt.schema.InputFormat.JSON;
import static java.lang.Math.max;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.Files.walkFileTree;
import static java.nio.file.Files.writeString;
import static java.util.Locale.ROOT;
import static java.util.Map.entry;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toUnmodifiableMap;
import static net.jacobpeterson.jet.openapiannotations.annotation.OpenApi.DEFAULT_$SCHEMA;
import static net.jacobpeterson.jet.openapiannotations.annotation.OpenApi.DEFAULT_ANNOTATION_GROUP_NAME;
import static net.jacobpeterson.jet.openapiannotations.annotation.OpenApi.DEFAULT_OPENAPI;
import static net.jacobpeterson.jet.openapiannotations.plugin.JetOpenApiAnnotationsExtension.GenerateOperationId.BOTH;
import static net.jacobpeterson.jet.openapiannotations.plugin.JetOpenApiAnnotationsExtension.GenerateOperationId.DISABLED;
import static net.jacobpeterson.jet.openapiannotations.plugin.JetOpenApiAnnotationsExtension.GenerateOperationId.FROM_CLASS_METHOD_NAME;
import static net.jacobpeterson.jet.openapiannotations.plugin.JetOpenApiAnnotationsExtension.GenerateOperationId.FROM_METHOD_AND_PATH;
import static net.jacobpeterson.jet.openapiannotations.plugin.gson.GsonUtil.walk;
import static net.jacobpeterson.jet.openapiannotations.plugin.gson.serializer.annotation.AnnotationJsonSerializer.ANNOTATION_METHOD_CLASS_NAME_DELIMITER;
import static net.jacobpeterson.jet.openapiannotations.plugin.gson.serializer.annotation.AnnotationJsonSerializer.JSON_KEY_CLASS_TRACER;
import static net.jacobpeterson.jet.openapiannotations.plugin.gson.serializer.annotation.AnnotationJsonSerializer.JSON_KEY_METHOD_TRACER;
import static net.jacobpeterson.jet.openapiannotations.plugin.gson.serializer.annotation.AnnotationJsonSerializer.removeTracers;
import static org.gradle.api.tasks.PathSensitivity.RELATIVE;

/**
 * {@link JetOpenApiAnnotationsTask} is the {@link DefaultTask} for {@link JetOpenApiAnnotationsPlugin}.
 */
@NullMarked
@CacheableTask
public abstract class JetOpenApiAnnotationsTask extends DefaultTask {

    private static final @SuppressWarnings("IdentifierName") String JSON_KEY_$SCHEMA = "$schema";
    private static final @SuppressWarnings("IdentifierName") String JSON_KEY_$DEFS = "$defs";
    private static final @SuppressWarnings("IdentifierName") String JSON_KEY_$REF = "$ref";
    private static final @SuppressWarnings("IdentifierName") String JSON_KEY_$REF_STARTS_WITH_$DEFS = "#/$defs/";
    private static final String JSON_KEY_OPENAPI = "openapi";
    private static final String JSON_KEY_PATHS = "paths";
    private static final String JSON_KEY_OPERATION_ID = "operationId";
    private static final String JSON_KEY_TAGS = "tags";
    private static final String JSON_KEY_COMPONENTS = "components";
    private static final String JSON_KEY_SCHEMAS = "schemas";
    private static final Pattern NON_ALPHANUMERIC_PATTERN = Pattern.compile("[^a-zA-Z0-9]+");

    public JetOpenApiAnnotationsTask() {
        setGroup("Build");
        setDescription("A code-first OpenAPI specification annotations processor Gradle plugin.");
    }

    @InputFiles @PathSensitive(RELATIVE)
    public abstract SetProperty<JavaCompile> getJavaCompileTasks();

    @Input @Optional
    public abstract Property<SchemaGeneratorConfigBuilderProvider> getSchemaGeneratorConfigBuilderProvider();

    @Input
    public abstract Property<Boolean> getSchemaGeneratorUseNullableModule();

    @Input
    public abstract Property<Boolean> getSchemaGeneratorUseSchemaNameModule();

    @Input
    public abstract Property<Boolean> getSchemaGeneratorUseGsonModule();

    @Input
    public abstract Property<Boolean> getSchemaGeneratorUseJacksonModule();

    @Input @Optional
    public abstract MapProperty<String, String> getSchemaGeneratorSimpleTypeMappings();

    @Input
    public abstract Property<GenerateOperationId> getGenerateOperationId();

    @Input
    public abstract Property<Boolean> getMoveClassSchemasToComponents();

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
            final var methodsOfOpenApis = new HashMap<OpenApi, java.lang.reflect.Method>();
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
                        methodsOfOpenApis.put(openApi, method);
                    }
                }
            }
            final var tracerClasses = new HashSet<Class<? extends Annotation>>();
            final var schemaGeneratorConfigBuilder = getSchemaGeneratorConfigBuilderProvider()
                    .getOrElse((SchemaGeneratorConfigBuilderProvider) () ->
                            new SchemaGeneratorConfigBuilder(DRAFT_2020_12, PLAIN_JSON)
                                    .with(EXTRA_OPEN_API_FORMAT_VALUES)
                                    .with(PLAIN_DEFINITION_KEYS)
                                    .with(ENUM_KEYWORD_FOR_SINGLE_VALUES))
                    .provide();
            if (getSchemaGeneratorUseNullableModule().get()) {
                schemaGeneratorConfigBuilder.with(new NullableSchemaModule());
            }
            if (getSchemaGeneratorUseSchemaNameModule().get()) {
                schemaGeneratorConfigBuilder.with(new SchemaNameSchemaModule());
            }
            if (getSchemaGeneratorUseGsonModule().get()) {
                schemaGeneratorConfigBuilder.with(new GsonSchemaModule());
            }
            if (getSchemaGeneratorUseJacksonModule().get()) {
                schemaGeneratorConfigBuilder.with(new JacksonSchemaModule());
            }
            final var simpleTypeMappings = getSchemaGeneratorSimpleTypeMappings().getOrNull();
            if (simpleTypeMappings != null) {
                schemaGeneratorConfigBuilder.forTypesInGeneral().withCustomDefinitionProvider((javaType, context) -> {
                    final var rawJson = simpleTypeMappings.get(javaType.getErasedType().getName());
                    return rawJson == null ? null : new CustomDefinition((ObjectNode)
                            context.getGeneratorConfig().getObjectMapper().readTree(rawJson), true);
                });
            }
            final var generateOperationId = getGenerateOperationId().get();
            if (generateOperationId != DISABLED) {
                tracerClasses.add(OpenApiOperation.class);
            }
            final var moveClassSchemasToComponents = getMoveClassSchemasToComponents().get();
            if (moveClassSchemasToComponents) {
                tracerClasses.add(OpenApiSchema.class);
                schemaGeneratorConfigBuilder
                        .with(DEFINITION_FOR_MAIN_SCHEMA)
                        .with(DEFINITIONS_FOR_ALL_OBJECTS)
                        .with(DEFINITIONS_FOR_MEMBER_SUPERTYPES);
            }
            final var annotationGson = new GsonBuilder()
                    .registerTypeHierarchyAdapter(Annotation.class, new AnnotationJsonSerializer(tracerClasses))
                    .registerTypeHierarchyAdapter(OpenApi.class, new OpenApiJsonSerializer(methodsOfOpenApis))
                    .registerTypeHierarchyAdapter(OpenApiSchema.class,
                            new OpenApiSchemaJsonSerializer(new SchemaGenerator(schemaGeneratorConfigBuilder.build())))
                    .registerTypeHierarchyAdapter(OpenApiPathItem.class, new OpenApiPathItemJsonSerializer())
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
                    openApiJson.addProperty(JSON_KEY_$SCHEMA, DEFAULT_$SCHEMA);
                }
                if (!openApiJson.has(JSON_KEY_OPENAPI)) {
                    openApiJson.addProperty(JSON_KEY_OPENAPI, DEFAULT_OPENAPI);
                }
                if (generateOperationId != DISABLED) {
                    walk(openApiJson, stack -> {
                        final var top = requireNonNull(stack.peek());
                        if (!top.getValue().isJsonObject()) {
                            return true;
                        }
                        final var topObject = top.getValue().getAsJsonObject();
                        final var tracerClass = topObject.get(JSON_KEY_CLASS_TRACER);
                        if (tracerClass == null ||
                                !tracerClass.getAsString().equals(OpenApiOperation.class.getCanonicalName())) {
                            return true;
                        }
                        if (topObject.has(JSON_KEY_OPERATION_ID)) {
                            return false;
                        }
                        String fromClassMethodName = null;
                        String fromMethodAndPath = null;
                        if (generateOperationId == FROM_CLASS_METHOD_NAME || generateOperationId == BOTH) {
                            final var methodTracer = topObject.get(JSON_KEY_METHOD_TRACER);
                            if (methodTracer != null && !methodTracer.isJsonNull()) {
                                final var methodTracerString = methodTracer.getAsString();
                                fromClassMethodName = methodTracerString.substring(
                                        methodTracerString.indexOf(ANNOTATION_METHOD_CLASS_NAME_DELIMITER) + 1);
                            }
                        }
                        if (generateOperationId == FROM_METHOD_AND_PATH || generateOperationId == BOTH) {
                            if (stack.size() >= 4 && stack.get(stack.size() - 2).getKey().equals(JSON_KEY_PATHS)) {
                                var path = stack.get(stack.size() - 3).getKey().toLowerCase(ROOT);
                                if (topObject.has(JSON_KEY_TAGS)) {
                                    var substringIndex = 0;
                                    for (final var tag : topObject.getAsJsonArray(JSON_KEY_TAGS)) {
                                        final var tagString = tag.getAsString().toLowerCase(ROOT);
                                        final var indexOfTag = path.indexOf(tagString);
                                        if (indexOfTag != -1) {
                                            substringIndex = max(substringIndex, indexOfTag + tagString.length());
                                        }
                                    }
                                    path = path.substring(substringIndex);
                                }
                                fromMethodAndPath = LOWER_UNDERSCORE.to(LOWER_CAMEL, NON_ALPHANUMERIC_PATTERN.matcher(
                                        top.getKey().toLowerCase(ROOT)).replaceAll("_") + "_" +
                                        NON_ALPHANUMERIC_PATTERN.matcher(path).replaceAll("_"));
                            }
                        }
                        if (generateOperationId == BOTH && fromClassMethodName != null && fromMethodAndPath != null) {
                            checkArgument(fromClassMethodName.equals(fromMethodAndPath),
                                    "Operation ID \"%s\" generated from class method name does not match " +
                                            "generated operation ID \"%s\" from @OpenApi annotation method and path",
                                    fromClassMethodName, fromMethodAndPath);
                        }
                        if (fromClassMethodName != null || fromMethodAndPath != null) {
                            topObject.addProperty(JSON_KEY_OPERATION_ID,
                                    fromClassMethodName != null ? fromClassMethodName : fromMethodAndPath);
                        }
                        return false;
                    });
                }
                if (moveClassSchemasToComponents) {
                    var components = openApiJson.get(JSON_KEY_COMPONENTS);
                    if (components == null || components.isJsonNull()) {
                        components = new JsonObject();
                        openApiJson.add(JSON_KEY_COMPONENTS, components);
                    }
                    final var componentsObject = components.getAsJsonObject();
                    var componentsSchemas = componentsObject.get(JSON_KEY_SCHEMAS);
                    if (componentsSchemas == null || componentsSchemas.isJsonNull()) {
                        componentsSchemas = new JsonObject();
                        componentsObject.add(JSON_KEY_SCHEMAS, componentsSchemas);
                    }
                    final var componentsSchemasObject = componentsSchemas.getAsJsonObject();
                    walk(openApiJson, stack -> {
                        final var topValue = requireNonNull(stack.peek()).getValue();
                        if (!topValue.isJsonObject()) {
                            return true;
                        }
                        final var topObject = topValue.getAsJsonObject();
                        final var tracerClass = topObject.get(JSON_KEY_CLASS_TRACER);
                        if (tracerClass == null ||
                                !tracerClass.getAsString().equals(OpenApiSchema.class.getCanonicalName())) {
                            return true;
                        }
                        walk(topObject, schemaStack -> {
                            final var schemaTopValue = requireNonNull(schemaStack.peek()).getValue();
                            if (!schemaTopValue.isJsonObject()) {
                                return true;
                            }
                            final var schemaTopObject = schemaTopValue.getAsJsonObject();
                            final var schema$Ref = schemaTopObject.get(JSON_KEY_$REF);
                            if (schema$Ref != null && !schema$Ref.isJsonNull()) {
                                final var schemaRefString = schema$Ref.getAsString();
                                if (schemaRefString.startsWith(JSON_KEY_$REF_STARTS_WITH_$DEFS)) {
                                    schemaTopObject.addProperty(JSON_KEY_$REF, "#/components/schemas/" +
                                            schemaRefString.substring(JSON_KEY_$REF_STARTS_WITH_$DEFS.length()));
                                }
                            }
                            return true;
                        });
                        final var $defs = topObject.get(JSON_KEY_$DEFS);
                        if ($defs != null && !$defs.isJsonNull()) {
                            for (final var $defsEntry : $defs.getAsJsonObject().entrySet()) {
                                final var className = $defsEntry.getKey();
                                final var classSchema = $defsEntry.getValue();
                                final var existingComponentSchema = componentsSchemasObject.get(className);
                                if (existingComponentSchema != null && !existingComponentSchema.isJsonNull()) {
                                    checkArgument(existingComponentSchema.equals(classSchema), """
                                                    The following different schemas share the same component name of \
                                                    "%s". Change the class name or use `@SchemaName`.
                                                    %s
                                                    %s""",
                                            className, existingComponentSchema, classSchema);
                                } else {
                                    componentsSchemasObject.add(className, classSchema);
                                }
                            }
                            topObject.remove(JSON_KEY_$DEFS);
                        }
                        topObject.remove(JSON_KEY_$SCHEMA);
                        return false;
                    });
                    if (componentsSchemasObject.isEmpty()) {
                        componentsObject.remove(JSON_KEY_SCHEMAS);
                    }
                    if (componentsObject.isEmpty()) {
                        openApiJson.remove(JSON_KEY_COMPONENTS);
                    }
                }
                if (!tracerClasses.isEmpty()) {
                    removeTracers(openApiJson);
                }
                final var openApiJsonString = openApiJson.toString();
                if (getSchemaValidation().get()) {
                    final var $schema = openApiJson.get(JSON_KEY_$SCHEMA).getAsString();
                    checkArgument($schema.equals(DEFAULT_$SCHEMA),
                            "Validation for custom `@OpenApi.$schema` of `%s` is unsupported.", $schema);
                    if (schema == null) {
                        schema = SchemaRegistry.withDefaultDialectId(null, null)
                                .getSchema(new String(requireNonNull(getClass()
                                        .getResourceAsStream("oas-3.2-schema-2025-09-17.json")).readAllBytes(), UTF_8));
                    }
                    final var errors = schema.validate(openApiJsonString, JSON, executionContext -> executionContext
                            .executionConfig(executionConfig -> executionConfig
                                    .formatAssertionsEnabled(true)
                                    .annotationCollectionEnabled(true)));
                    if (!errors.isEmpty()) {
                        throw new IllegalArgumentException("\n" + errors.stream()
                                .map(error -> "        OpenAPIv%s schema offense%s: %s".formatted(DEFAULT_OPENAPI,
                                        groupName.equals(DEFAULT_ANNOTATION_GROUP_NAME) ? "" :
                                                " in annotation group \"%s\"".formatted(groupName),
                                        error.toString()))
                                .collect(joining("\n")));
                    }
                }
                writeString(outputDirectory.resolve("openapi%s.json"
                        .formatted(!groupName.isEmpty() ? "-" + groupName : "")), openApiJsonString);
            }
        } catch (final IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }
}
