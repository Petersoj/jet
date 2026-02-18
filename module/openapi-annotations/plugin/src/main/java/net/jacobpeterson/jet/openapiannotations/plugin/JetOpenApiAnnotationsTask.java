package net.jacobpeterson.jet.openapiannotations.plugin;

import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.module.jackson.JacksonSchemaModule;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.networknt.schema.Schema;
import com.networknt.schema.SchemaRegistry;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.jacobpeterson.jet.common.http.header.Header;
import net.jacobpeterson.jet.common.http.method.Method;
import net.jacobpeterson.jet.common.http.status.Status;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApi;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiCallback;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiComponents;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiExample;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiExternalDoc;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiHeader;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiInfo;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiLink;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiMediaType;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiParameter;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiPathItem;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiPaths;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiReference;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiRequestBody;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiResponse;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiSchema;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiSecurityRequirement;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiSecurityScheme;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiServer;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiTag;
import net.jacobpeterson.jet.openapiannotations.annotation.meta.AnnotationArrayIsNullableValue;
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
import org.jspecify.annotations.Nullable;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static com.github.victools.jsonschema.generator.OptionPreset.PLAIN_JSON;
import static com.github.victools.jsonschema.generator.SchemaVersion.DRAFT_2020_12;
import static com.google.common.base.Preconditions.checkArgument;
import static com.networknt.schema.InputFormat.JSON;
import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.Files.readString;
import static java.nio.file.Files.walkFileTree;
import static java.nio.file.Files.writeString;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static net.jacobpeterson.jet.openapiannotations.annotation.OpenApi.DEFAULT_ANNOTATION_GROUP_NAME;
import static net.jacobpeterson.jet.openapiannotations.annotation.OpenApi.DEFAULT_ANNOTATION_OUTPUT_VALIDATION;
import static net.jacobpeterson.jet.openapiannotations.annotation.OpenApi.DEFAULT_OPENAPI;
import static net.jacobpeterson.jet.openapiannotations.annotation.OpenApi.DEFAULT_SCHEMA;
import static net.jacobpeterson.jet.openapiannotations.plugin.util.reflection.ReflectionUtil.getClassName;
import static org.gradle.api.tasks.PathSensitivity.RELATIVE;

/**
 * {@link JetOpenApiAnnotationsTask} is the {@link DefaultTask} for {@link JetOpenApiAnnotationsPlugin}.
 */
@NullMarked
@CacheableTask
public abstract class JetOpenApiAnnotationsTask extends DefaultTask {

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
            final var openApiWrappersOfGroupNames = new HashMap<String, OpenApiWrapper>();
            for (final var javaCompileClassName : javaCompileClassNames) {
                final Class<?> javaCompileClass;
                try {
                    javaCompileClass = javaCompileClassLoader.loadClass(javaCompileClassName);
                } catch (final ClassNotFoundException classNotFoundException) {
                    throw new RuntimeException(classNotFoundException);
                }
                for (final var openApi : javaCompileClass.getDeclaredAnnotationsByType(OpenApi.class)) {
                    openApiWrappersOfGroupNames.computeIfAbsent(openApi.annotationGroupName(), OpenApiWrapper::new)
                            .wrap(openApi, javaCompileClassName);
                }
                for (final var method : javaCompileClass.getDeclaredMethods()) {
                    for (final var openApi : method.getDeclaredAnnotationsByType(OpenApi.class)) {
                        openApiWrappersOfGroupNames.computeIfAbsent(openApi.annotationGroupName(), OpenApiWrapper::new)
                                .wrap(openApi, "%s.%s()".formatted(javaCompileClassName, method.getName()));
                    }
                }
            }
            final var annotationGson = new GsonBuilder()
                    .registerTypeHierarchyAdapter(Annotation.class, new AnnotationJsonSerializer())
                    .registerTypeHierarchyAdapter(OpenApiSchema.class, new OpenApiSchemaJsonSerializer(
                            new SchemaGenerator(getSchemaGeneratorConfig()
                                    .getOrElse((SchemaGeneratorConfigProvider) () -> {
                                        final var builder = new SchemaGeneratorConfigBuilder(DRAFT_2020_12, PLAIN_JSON);
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
            for (final var openApiWrapperOfGroupName : openApiWrappersOfGroupNames.entrySet()) {
                final var groupName = openApiWrapperOfGroupName.getKey();
                final var openApiWrapper = openApiWrapperOfGroupName.getValue();
                if (openApiWrapper.getAnnotationOutputValidation() == null) {
                    openApiWrapper.setAnnotationOutputValidation(DEFAULT_ANNOTATION_OUTPUT_VALIDATION);
                }
                if (openApiWrapper.get$schema() == null) {
                    openApiWrapper.set$schema(DEFAULT_SCHEMA);
                }
                if (openApiWrapper.getOpenapi() == null) {
                    openApiWrapper.setOpenapi(DEFAULT_OPENAPI);
                }
                final var openApiJson = annotationGson.toJson(openApiWrapper);
                if (requireNonNull(openApiWrapper.getAnnotationOutputValidation())) {
                    final var $schema = requireNonNull(openApiWrapper.get$schema());
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
                    final var errors = schema.validate(openApiJson, JSON, executionContext -> executionContext
                            .executionConfig(executionConfig -> executionConfig
                                    .formatAssertionsEnabled(true)
                                    .annotationCollectionEnabled(true)));
                    if (!errors.isEmpty()) {
                        throw new IllegalArgumentException("\n" + errors.stream()
                                .map(error -> "OpenAPIv%s schema offense%s: %s".formatted(DEFAULT_OPENAPI,
                                        getInAnnotationGroupErrorMessage(groupName), error.toString()))
                                .collect(joining("\n")));
                    }
                }
                try {
                    writeString(outputDirectory.resolve("openapi%s.json"
                            .formatted(!groupName.isEmpty() ? "-" + groupName : "")), openApiJson);
                } catch (final IOException ioException) {
                    throw new RuntimeException(ioException);
                }
            }
        } catch (final IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    @RequiredArgsConstructor @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    @SuppressWarnings({"ClassExplicitlyAnnotation", "ImmutableAnnotationChecker", "InconsistentCapitalization"})
    private final class OpenApiWrapper implements OpenApi {

        private final @Getter String annotationGroupName;
        private @Getter @Setter @Nullable Boolean annotationOutputValidation;
        private @Getter @Setter @Nullable String $schema;
        private @Getter @Setter @Nullable String openapi;
        private @Getter @Nullable String $self;
        private @Getter @Nullable OpenApiInfo info;
        private @Getter @Nullable String jsonSchemaDialect;
        private final @Getter List<OpenApiServer> servers = new ArrayList<>();
        private @Getter @Nullable OpenApiPathsWrapper paths;
        private final @Getter List<OpenApiPathItem.MapEntry> webhooks = new ArrayList<>();
        private @Getter @Nullable OpenApiComponentsWrapper components;
        private final @Getter List<OpenApiSecurityRequirement> security = new ArrayList<>();
        private final @Getter List<OpenApiTag> tags = new ArrayList<>();
        private @Getter @Nullable OpenApiExternalDoc externalDocs;
        private final List<String> rawJsons = new ArrayList<>();

        private void wrap(final OpenApi openApi, final String annotationUsageLocation) throws IllegalArgumentException {
            final var annotationOutputValidation = openApi.annotationOutputValidation();
            if (annotationOutputValidation.length > 1) {
                throwArrayIsNullableValue("annotationOutputValidation", annotationUsageLocation);
            } else if (annotationOutputValidation.length == 1) {
                if (this.annotationOutputValidation != null) {
                    throwDuplicate("annotationOutputValidation", annotationUsageLocation);
                } else {
                    this.annotationOutputValidation = annotationOutputValidation[0];
                }
            }

            final var $schema = openApi.$schema();
            if (!$schema.isEmpty()) {
                if (this.$schema != null) {
                    throwDuplicate("$schema", annotationUsageLocation);
                } else {
                    this.$schema = $schema;
                }
            }

            final var openapi = openApi.openapi();
            if (!openapi.isEmpty()) {
                if (this.openapi != null) {
                    throwDuplicate("openapi", annotationUsageLocation);
                } else {
                    this.openapi = openapi;
                }
            }

            final var $self = openApi.$self();
            if (!$self.isEmpty()) {
                if (this.$self != null) {
                    throwDuplicate("$self", annotationUsageLocation);
                } else {
                    this.$self = $self;
                }
            }

            final var info = openApi.info();
            if (info.length > 1) {
                throwArrayIsNullableValue("info", annotationUsageLocation);
            } else if (info.length == 1) {
                if (this.info != null) {
                    throwDuplicate("info", annotationUsageLocation);
                } else {
                    this.info = info[0];
                }
            }

            final var jsonSchemaDialect = openApi.jsonSchemaDialect();
            if (!jsonSchemaDialect.isEmpty()) {
                if (this.jsonSchemaDialect != null) {
                    throwDuplicate("jsonSchemaDialect", annotationUsageLocation);
                } else {
                    this.jsonSchemaDialect = jsonSchemaDialect;
                }
            }

            servers.addAll(List.of(openApi.servers()));

            final var paths = openApi.paths();
            if (paths.length > 1) {
                throwArrayIsNullableValue("paths", annotationUsageLocation);
            } else if (paths.length == 1) {
                if (this.paths == null) {
                    this.paths = new OpenApiPathsWrapper();
                }
                this.paths.wrap(paths[0]);
            }

            webhooks.addAll(List.of(openApi.webhooks()));

            final var components = openApi.components();
            if (components.length > 1) {
                throwArrayIsNullableValue("components", annotationUsageLocation);
            } else if (components.length == 1) {
                if (this.components == null) {
                    this.components = new OpenApiComponentsWrapper();
                }
                this.components.wrap(components[0]);
            }

            security.addAll(List.of(openApi.security()));

            tags.addAll(List.of(openApi.tags()));

            final var externalDocs = openApi.externalDocs();
            if (externalDocs.length > 1) {
                throwArrayIsNullableValue("externalDocs", annotationUsageLocation);
            } else if (externalDocs.length == 1) {
                if (this.externalDocs != null) {
                    throwDuplicate("externalDocs", annotationUsageLocation);
                } else {
                    this.externalDocs = externalDocs[0];
                }
            }

            final var rawJson = openApi.rawJson();
            if (!rawJson.isEmpty()) {
                rawJsons.add(rawJson);
            }
        }

        private void throwArrayIsNullableValue(final String methodName, final String annotationUsageLocation) {
            throw new IllegalArgumentException(("`%s`: `@OpenApi.%s` is annotated with `@%s`, but the array " +
                    "contains more than one element").formatted(annotationUsageLocation,
                    methodName, getClassName(AnnotationArrayIsNullableValue.class)));
        }

        private void throwDuplicate(final String methodName, final String annotationUsageLocation) {
            throw new IllegalArgumentException("`%s`: duplicate `@OpenApi.%s`%s".formatted(annotationUsageLocation,
                    methodName, getInAnnotationGroupErrorMessage(annotationGroupName)));
        }

        @EqualsAndHashCode.Include
        @Override
        public String annotationGroupName() {
            return annotationGroupName;
        }

        @EqualsAndHashCode.Include
        @Override
        public boolean[] annotationOutputValidation() {
            return annotationOutputValidation == null ? new boolean[]{} : new boolean[]{annotationOutputValidation};
        }

        @EqualsAndHashCode.Include
        @Override
        public String $schema() {
            return $schema == null ? "" : $schema;
        }

        @EqualsAndHashCode.Include
        @Override
        public String openapi() {
            return openapi == null ? "" : openapi;
        }

        @EqualsAndHashCode.Include
        @Override
        public String $self() {
            return $self == null ? "" : $self;
        }

        @EqualsAndHashCode.Include
        @Override
        public OpenApiInfo[] info() {
            return info == null ? new OpenApiInfo[]{} : new OpenApiInfo[]{info};
        }

        @EqualsAndHashCode.Include
        @Override
        public String jsonSchemaDialect() {
            return jsonSchemaDialect == null ? "" : jsonSchemaDialect;
        }

        @EqualsAndHashCode.Include
        @Override
        public OpenApiServer[] servers() {
            return servers.toArray(OpenApiServer[]::new);
        }

        @EqualsAndHashCode.Include
        @Override
        public OpenApiPaths[] paths() {
            return paths == null ? new OpenApiPaths[]{} : new OpenApiPaths[]{paths};
        }

        @EqualsAndHashCode.Include
        @Override
        public OpenApiPathItem.MapEntry[] webhooks() {
            return webhooks.toArray(OpenApiPathItem.MapEntry[]::new);
        }

        @EqualsAndHashCode.Include
        @Override
        public OpenApiComponents[] components() {
            return components == null ? new OpenApiComponents[]{} : new OpenApiComponents[]{components};
        }

        @EqualsAndHashCode.Include
        @Override
        public OpenApiSecurityRequirement[] security() {
            return security.toArray(OpenApiSecurityRequirement[]::new);
        }

        @EqualsAndHashCode.Include
        @Override
        public OpenApiTag[] tags() {
            return tags.toArray(OpenApiTag[]::new);
        }

        @EqualsAndHashCode.Include
        @Override
        public OpenApiExternalDoc[] externalDocs() {
            return externalDocs == null ? new OpenApiExternalDoc[]{} : new OpenApiExternalDoc[]{externalDocs};
        }

        @EqualsAndHashCode.Include
        @Override
        public String rawJson() {
            return combineRawJsons(rawJsons);
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return OpenApi.class;
        }
    }

    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    @SuppressWarnings({"ClassExplicitlyAnnotation", "ImmutableAnnotationChecker"})
    private final class OpenApiPathsWrapper implements OpenApiPaths {

        private final List<OpenApiPathItem.MapEntry> value = new ArrayList<>();
        private final List<String> rawJsons = new ArrayList<>();

        private void wrap(final OpenApiPaths openApiPaths) {
            value.addAll(List.of(openApiPaths.value()));

            final var rawJson = openApiPaths.rawJson();
            if (!rawJson.isEmpty()) {
                rawJsons.add(rawJson);
            }
        }

        @EqualsAndHashCode.Include
        @Override
        public OpenApiPathItem.MapEntry[] value() {
            return value.toArray(OpenApiPathItem.MapEntry[]::new);
        }

        @EqualsAndHashCode.Include
        @Override
        public String rawJson() {
            return combineRawJsons(rawJsons);
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return OpenApiPaths.class;
        }
    }

    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    @SuppressWarnings({"ClassExplicitlyAnnotation", "ImmutableAnnotationChecker"})
    private final class OpenApiComponentsWrapper implements OpenApiComponents {

        private final List<OpenApiSchema.MapEntry> schemas = new ArrayList<>();
        private final List<OpenApiResponse.MapEntry> responses = new ArrayList<>();
        private final List<OpenApiReference.MapEntry> responseReferences = new ArrayList<>();
        private final List<OpenApiParameter.MapEntry> parameters = new ArrayList<>();
        private final List<OpenApiReference.MapEntry> parameterReferences = new ArrayList<>();
        private final List<OpenApiExample.MapEntry> examples = new ArrayList<>();
        private final List<OpenApiReference.MapEntry> exampleReferences = new ArrayList<>();
        private final List<OpenApiRequestBody.MapEntry> requestBodies = new ArrayList<>();
        private final List<OpenApiReference.MapEntry> requestBodyReferences = new ArrayList<>();
        private final List<OpenApiHeader.MapEntry> headers = new ArrayList<>();
        private final List<OpenApiReference.MapEntry> headerReferences = new ArrayList<>();
        private final List<OpenApiSecurityScheme.MapEntry> securitySchemes = new ArrayList<>();
        private final List<OpenApiReference.MapEntry> securitySchemeReferences = new ArrayList<>();
        private final List<OpenApiLink.MapEntry> links = new ArrayList<>();
        private final List<OpenApiReference.MapEntry> linkReferences = new ArrayList<>();
        private final List<OpenApiCallback.MapEntry> callbacks = new ArrayList<>();
        private final List<OpenApiReference.MapEntry> callbackReferences = new ArrayList<>();
        private final List<OpenApiPathItem.MapEntry> pathItems = new ArrayList<>();
        private final List<OpenApiMediaType.MapEntry> mediaTypes = new ArrayList<>();
        private final List<OpenApiReference.MapEntry> mediaTypeReferences = new ArrayList<>();
        private final List<String> rawJsons = new ArrayList<>();

        private void wrap(final OpenApiComponents openApiComponents) {
            schemas.addAll(List.of(openApiComponents.schemas()));
            responses.addAll(List.of(openApiComponents.responses()));
            responseReferences.addAll(List.of(openApiComponents.responseReferences()));
            parameters.addAll(List.of(openApiComponents.parameters()));
            parameterReferences.addAll(List.of(openApiComponents.parameterReferences()));
            examples.addAll(List.of(openApiComponents.examples()));
            exampleReferences.addAll(List.of(openApiComponents.exampleReferences()));
            requestBodies.addAll(List.of(openApiComponents.requestBodies()));
            requestBodyReferences.addAll(List.of(openApiComponents.requestBodyReferences()));
            headers.addAll(List.of(openApiComponents.headers()));
            headerReferences.addAll(List.of(openApiComponents.headerReferences()));
            securitySchemes.addAll(List.of(openApiComponents.securitySchemes()));
            securitySchemeReferences.addAll(List.of(openApiComponents.securitySchemeReferences()));
            links.addAll(List.of(openApiComponents.links()));
            linkReferences.addAll(List.of(openApiComponents.linkReferences()));
            callbacks.addAll(List.of(openApiComponents.callbacks()));
            callbackReferences.addAll(List.of(openApiComponents.callbackReferences()));
            pathItems.addAll(List.of(openApiComponents.pathItems()));
            mediaTypes.addAll(List.of(openApiComponents.mediaTypes()));
            mediaTypeReferences.addAll(List.of(openApiComponents.mediaTypeReferences()));

            final var rawJson = openApiComponents.rawJson();
            if (!rawJson.isEmpty()) {
                rawJsons.add(rawJson);
            }
        }

        @EqualsAndHashCode.Include
        @Override
        public OpenApiSchema.MapEntry[] schemas() {
            return schemas.toArray(OpenApiSchema.MapEntry[]::new);
        }

        @EqualsAndHashCode.Include
        @Override
        public OpenApiResponse.MapEntry[] responses() {
            return responses.toArray(OpenApiResponse.MapEntry[]::new);
        }

        @EqualsAndHashCode.Include
        @Override
        public OpenApiReference.MapEntry[] responseReferences() {
            return responseReferences.toArray(OpenApiReference.MapEntry[]::new);
        }

        @EqualsAndHashCode.Include
        @Override
        public OpenApiParameter.MapEntry[] parameters() {
            return parameters.toArray(OpenApiParameter.MapEntry[]::new);
        }

        @EqualsAndHashCode.Include
        @Override
        public OpenApiReference.MapEntry[] parameterReferences() {
            return parameterReferences.toArray(OpenApiReference.MapEntry[]::new);
        }

        @EqualsAndHashCode.Include
        @Override
        public OpenApiExample.MapEntry[] examples() {
            return examples.toArray(OpenApiExample.MapEntry[]::new);
        }

        @EqualsAndHashCode.Include
        @Override
        public OpenApiReference.MapEntry[] exampleReferences() {
            return exampleReferences.toArray(OpenApiReference.MapEntry[]::new);
        }

        @EqualsAndHashCode.Include
        @Override
        public OpenApiRequestBody.MapEntry[] requestBodies() {
            return requestBodies.toArray(OpenApiRequestBody.MapEntry[]::new);
        }

        @EqualsAndHashCode.Include
        @Override
        public OpenApiReference.MapEntry[] requestBodyReferences() {
            return requestBodyReferences.toArray(OpenApiReference.MapEntry[]::new);
        }

        @EqualsAndHashCode.Include
        @Override
        public OpenApiHeader.MapEntry[] headers() {
            return headers.toArray(OpenApiHeader.MapEntry[]::new);
        }

        @EqualsAndHashCode.Include
        @Override
        public OpenApiReference.MapEntry[] headerReferences() {
            return headerReferences.toArray(OpenApiReference.MapEntry[]::new);
        }

        @EqualsAndHashCode.Include
        @Override
        public OpenApiSecurityScheme.MapEntry[] securitySchemes() {
            return securitySchemes.toArray(OpenApiSecurityScheme.MapEntry[]::new);
        }

        @EqualsAndHashCode.Include
        @Override
        public OpenApiReference.MapEntry[] securitySchemeReferences() {
            return securitySchemeReferences.toArray(OpenApiReference.MapEntry[]::new);
        }

        @EqualsAndHashCode.Include
        @Override
        public OpenApiLink.MapEntry[] links() {
            return links.toArray(OpenApiLink.MapEntry[]::new);
        }

        @EqualsAndHashCode.Include
        @Override
        public OpenApiReference.MapEntry[] linkReferences() {
            return linkReferences.toArray(OpenApiReference.MapEntry[]::new);
        }

        @EqualsAndHashCode.Include
        @Override
        public OpenApiCallback.MapEntry[] callbacks() {
            return callbacks.toArray(OpenApiCallback.MapEntry[]::new);
        }

        @EqualsAndHashCode.Include
        @Override
        public OpenApiReference.MapEntry[] callbackReferences() {
            return callbackReferences.toArray(OpenApiReference.MapEntry[]::new);
        }

        @EqualsAndHashCode.Include
        @Override
        public OpenApiPathItem.MapEntry[] pathItems() {
            return pathItems.toArray(OpenApiPathItem.MapEntry[]::new);
        }

        @EqualsAndHashCode.Include
        @Override
        public OpenApiMediaType.MapEntry[] mediaTypes() {
            return mediaTypes.toArray(OpenApiMediaType.MapEntry[]::new);
        }

        @EqualsAndHashCode.Include
        @Override
        public OpenApiReference.MapEntry[] mediaTypeReferences() {
            return mediaTypeReferences.toArray(OpenApiReference.MapEntry[]::new);
        }

        @EqualsAndHashCode.Include
        @Override
        public String rawJson() {
            return combineRawJsons(rawJsons);
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return OpenApiComponents.class;
        }
    }

    private String combineRawJsons(final List<String> rawJsons) {
        return rawJsons.stream()
                .map(JsonParser::parseString)
                .reduce(GsonUtil::combine)
                .map(JsonElement::toString)
                .orElse("");
    }

    private String getInAnnotationGroupErrorMessage(final String annotationGroupName) {
        return annotationGroupName.equals(DEFAULT_ANNOTATION_GROUP_NAME) ? "" :
                " in annotation group \"%s\"".formatted(annotationGroupName);
    }
}
