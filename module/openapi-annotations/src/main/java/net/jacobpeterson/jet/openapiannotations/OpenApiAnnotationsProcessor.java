package net.jacobpeterson.jet.openapiannotations;

import com.google.gson.GsonBuilder;
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
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApi.AnnotationOutputValidation;
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
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.AnnotationJsonSerializer;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationArrayIsNullableValue;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.header.HeaderJsonSerializer;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.method.MethodJsonSerializer;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.status.StatusJsonSerializer;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.string.EmptyStringIsNullJsonSerializer;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static com.networknt.schema.InputFormat.JSON;
import static java.nio.file.Files.readString;
import static java.util.Arrays.stream;
import static java.util.Map.entry;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Stream.concat;
import static javax.lang.model.SourceVersion.latestSupported;
import static javax.tools.StandardLocation.CLASS_OUTPUT;
import static net.jacobpeterson.jet.openapiannotations.annotation.OpenApi.AnnotationOutputValidation.ERROR;
import static net.jacobpeterson.jet.openapiannotations.annotation.OpenApi.AnnotationOutputValidation.NONE;
import static net.jacobpeterson.jet.openapiannotations.util.reflection.ReflectionUtil.getFullClassName;

/**
 * {@link OpenApiAnnotationsProcessor} is an {@link AbstractProcessor} for OpenAPI annotations.
 */
@NullMarked
public final class OpenApiAnnotationsProcessor extends AbstractProcessor {

    /**
     * The default value for {@link OpenApi#annotationGroupName()}: <code>""</code>
     * <p>
     * This {@link String} constant acts as a reference for the default annotation group, which is just an empty
     * {@link String}.
     */
    public static final String DEFAULT_ANNOTATION_GROUP_NAME = "";

    /**
     * The default value for {@link OpenApi#annotationOutputValidation()}:
     * <code>{@link AnnotationOutputValidation#ERROR}</code>
     */
    public static final AnnotationOutputValidation DEFAULT_ANNOTATION_OUTPUT_VALIDATION = ERROR;

    /**
     * The default value for {@link OpenApi#$schema()}:
     * <code>"https://spec.openapis.org/oas/3.2/schema/2025-09-17"</code>
     */
    @SuppressWarnings("JavadocLinkAsPlainText")
    public static final String DEFAULT_SCHEMA = "https://spec.openapis.org/oas/3.2/schema/2025-09-17";

    /**
     * The default value for {@link OpenApi#openapi()}: <code>"3.2.0"</code>
     */
    public static final String DEFAULT_OPENAPI = "3.2.0";

    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            return false;
        }
        final var openApiWrappersOfGroupNames = new HashMap<String, OpenApiWrapper>();
        concat(roundEnv.getElementsAnnotatedWith(OpenApi.class).stream()
                        .map(element -> entry(requireNonNull(
                                element.getAnnotation(OpenApi.class)), element)),
                roundEnv.getElementsAnnotatedWith(OpenApi.RepeatableAnnotation.class).stream()
                        .flatMap(element -> stream(requireNonNull(
                                element.getAnnotation(OpenApi.RepeatableAnnotation.class)).value())
                                .map(openApi -> entry(openApi, element))))
                .forEach(entry -> openApiWrappersOfGroupNames
                        .computeIfAbsent(entry.getKey().annotationGroupName(), OpenApiWrapper::new)
                        .wrap(entry.getKey(), entry.getValue()));
        final var annotationGson = new GsonBuilder()
                .registerTypeHierarchyAdapter(Annotation.class, new AnnotationJsonSerializer())
                .registerTypeAdapter(String.class, new EmptyStringIsNullJsonSerializer())
                .registerTypeAdapter(Method.class, new MethodJsonSerializer())
                .registerTypeAdapter(Status.class, new StatusJsonSerializer())
                .registerTypeAdapter(Header.class, new HeaderJsonSerializer())
                .create();
        Schema schema = null;
        for (final var openApiWrapperOfGroupName : openApiWrappersOfGroupNames.entrySet()) {
            final var groupName = openApiWrapperOfGroupName.getKey();
            final var openApiWrapper = openApiWrapperOfGroupName.getValue();
            if (openApiWrapper.getAnnotationOutputValidation() == null) {
                openApiWrapper.setAnnotationOutputValidation(DEFAULT_ANNOTATION_OUTPUT_VALIDATION);
            }
            if (openApiWrapper.get$schema().isEmpty()) {
                openApiWrapper.set$schema(DEFAULT_SCHEMA);
            }
            if (openApiWrapper.getOpenapi().isEmpty()) {
                openApiWrapper.setOpenapi(DEFAULT_OPENAPI);
            }
            final String openApiJson;
            try {
                openApiJson = annotationGson.toJson(openApiWrapper);
            } catch (final Exception exception) {
                processingEnv.getMessager().printError(exception.toString());
                continue;
            }
            if (openApiWrapper.getAnnotationOutputValidation() != NONE) {
                if (!openApiWrapper.get$schema().equals(DEFAULT_SCHEMA)) {
                    processingEnv.getMessager().printError("""
                            `@OpenApi.annotationOutputValidation` is set to `%s`, but validation for custom \
                            `@OpenApi.$schema` of `%s` is unsupported. Set `@OpenApi.annotationOutputValidation` to \
                            `NONE` or remove the custom `@OpenApi.$schema`."""
                            .formatted(openApiWrapper.getAnnotationOutputValidation(), openApiWrapper.get$schema()));
                } else {
                    if (schema == null) {
                        try {
                            schema = SchemaRegistry.withDefaultDialectId(null, null)
                                    .getSchema(readString(Paths.get(requireNonNull(
                                            getClass().getResource("oas-3.2-schema-2025-09-17.json")).toURI())));
                        } catch (final IOException | URISyntaxException exception) {
                            throw new RuntimeException(exception);
                        }
                    }
                    for (final var error : schema.validate(openApiJson, JSON, executionContext -> executionContext
                            .executionConfig(executionConfig -> executionConfig
                                    .formatAssertionsEnabled(true)
                                    .annotationCollectionEnabled(true)))) {
                        processingEnv.getMessager().printMessage(
                                openApiWrapper.getAnnotationOutputValidation() == ERROR ? Kind.ERROR : Kind.WARNING,
                                "OpenAPIv%s schema offense%s: %s".formatted(DEFAULT_OPENAPI,
                                        getInAnnotationGroupErrorMessage(groupName),
                                        error.toString()));
                    }
                }
            }
            try (final var openApiJsonWriter = processingEnv.getFiler().createResource(CLASS_OUTPUT,
                    getClass().getPackageName(),
                    "openapi%s.json".formatted(!groupName.isEmpty() ? "-" + groupName : "")).openWriter()) {
                openApiJsonWriter.write(openApiJson);
            } catch (final IOException ioException) {
                throw new RuntimeException(ioException);
            }
        }
        return false;
    }

    @RequiredArgsConstructor @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    @SuppressWarnings({"ClassExplicitlyAnnotation", "ImmutableAnnotationChecker", "InconsistentCapitalization"})
    private class OpenApiWrapper implements OpenApi {

        private final @Getter String annotationGroupName;
        private @Getter @Setter @Nullable AnnotationOutputValidation annotationOutputValidation;
        private @Getter @Setter String $schema = "";
        private @Getter @Setter String openapi = "";
        private @Getter String $self = "";
        private @Getter @Nullable OpenApiInfo info;
        private @Getter String jsonSchemaDialect = "";
        private final @Getter List<OpenApiServer> servers = new ArrayList<>();
        private @Getter @Nullable OpenApiPathsWrapper paths;
        private final @Getter List<OpenApiPathItem.MapEntry> webhooks = new ArrayList<>();
        private @Getter @Nullable OpenApiComponentsWrapper components;
        private final @Getter List<OpenApiSecurityRequirement> security = new ArrayList<>();
        private final @Getter List<OpenApiTag> tags = new ArrayList<>();
        private @Getter @Nullable OpenApiExternalDoc externalDocs;

        private void wrap(final OpenApi openApi, final Element element) {
            final var annotationOutputValidation = openApi.annotationOutputValidation();
            if (annotationOutputValidation.length > 1) {
                printErrorArrayIsNullableValue("annotationOutputValidation", element);
            } else if (annotationOutputValidation.length == 1) {
                if (this.annotationOutputValidation != null) {
                    printErrorDuplicate("annotationOutputValidation", element);
                } else {
                    this.annotationOutputValidation = annotationOutputValidation[0];
                }
            }

            final var $schema = openApi.$schema();
            if (!$schema.isEmpty()) {
                if (!this.$schema.isEmpty()) {
                    printErrorDuplicate("$schema", element);
                } else {
                    this.$schema = $schema;
                }
            }

            final var openapi = openApi.openapi();
            if (!openapi.isEmpty()) {
                if (!this.openapi.isEmpty()) {
                    printErrorDuplicate("openapi", element);
                } else {
                    this.openapi = openapi;
                }
            }

            final var $self = openApi.$self();
            if (!$self.isEmpty()) {
                if (!this.$self.isEmpty()) {
                    printErrorDuplicate("$self", element);
                } else {
                    this.$self = $self;
                }
            }

            final var info = openApi.info();
            if (info.length > 1) {
                printErrorArrayIsNullableValue("info", element);
            } else if (info.length == 1) {
                if (this.info != null) {
                    printErrorDuplicate("info", element);
                } else {
                    this.info = info[0];
                }
            }

            final var jsonSchemaDialect = openApi.jsonSchemaDialect();
            if (!jsonSchemaDialect.isEmpty()) {
                if (!this.jsonSchemaDialect.isEmpty()) {
                    printErrorDuplicate("jsonSchemaDialect", element);
                } else {
                    this.jsonSchemaDialect = jsonSchemaDialect;
                }
            }

            servers.addAll(List.of(openApi.servers()));

            final var paths = openApi.paths();
            if (paths.length > 1) {
                printErrorArrayIsNullableValue("paths", element);
            } else if (paths.length == 1) {
                if (this.paths == null) {
                    this.paths = new OpenApiPathsWrapper();
                }
                this.paths.wrap(paths[0]);
            }

            webhooks.addAll(List.of(openApi.webhooks()));

            final var components = openApi.components();
            if (components.length > 1) {
                printErrorArrayIsNullableValue("components", element);
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
                printErrorArrayIsNullableValue("externalDocs", element);
            } else if (externalDocs.length == 1) {
                if (this.externalDocs != null) {
                    printErrorDuplicate("externalDocs", element);
                } else {
                    this.externalDocs = externalDocs[0];
                }
            }
        }

        private void printErrorArrayIsNullableValue(final String methodName, final Element element) {
            processingEnv.getMessager().printError(("`@OpenApi.%s` is annotated with `@%s`, but the array " +
                    "contains more than one element").formatted(methodName,
                    getFullClassName(AnnotationArrayIsNullableValue.class)), element);
        }

        private void printErrorDuplicate(final String methodName, final Element element) {
            processingEnv.getMessager().printError("Duplicate `@OpenApi.%s`%s".formatted(methodName,
                    getInAnnotationGroupErrorMessage(annotationGroupName)), element);
        }

        @EqualsAndHashCode.Include
        @Override
        public String annotationGroupName() {
            return annotationGroupName;
        }

        @EqualsAndHashCode.Include
        @Override
        public AnnotationOutputValidation[] annotationOutputValidation() {
            return annotationOutputValidation == null ? new AnnotationOutputValidation[]{} :
                    new AnnotationOutputValidation[]{annotationOutputValidation};
        }

        @EqualsAndHashCode.Include
        @Override
        public String $schema() {
            return $schema;
        }

        @EqualsAndHashCode.Include
        @Override
        public String openapi() {
            return openapi;
        }

        @EqualsAndHashCode.Include
        @Override
        public String $self() {
            return $self;
        }

        @EqualsAndHashCode.Include
        @Override
        public OpenApiInfo[] info() {
            return info == null ? new OpenApiInfo[]{} : new OpenApiInfo[]{info};
        }

        @EqualsAndHashCode.Include
        @Override
        public String jsonSchemaDialect() {
            return jsonSchemaDialect;
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

        @Override
        public Class<? extends Annotation> annotationType() {
            return OpenApi.class;
        }
    }

    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    @SuppressWarnings({"ClassExplicitlyAnnotation", "ImmutableAnnotationChecker"})
    private static class OpenApiPathsWrapper implements OpenApiPaths {

        private final List<OpenApiPathItem.MapEntry> value = new ArrayList<>();

        private void wrap(final OpenApiPaths openApiPaths) {
            value.addAll(List.of(openApiPaths.value()));
        }

        @EqualsAndHashCode.Include
        @Override
        public OpenApiPathItem.MapEntry[] value() {
            return value.toArray(OpenApiPathItem.MapEntry[]::new);
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return OpenApiPaths.class;
        }
    }

    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    @SuppressWarnings({"ClassExplicitlyAnnotation", "ImmutableAnnotationChecker"})
    private static class OpenApiComponentsWrapper implements OpenApiComponents {

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

        @Override
        public Class<? extends Annotation> annotationType() {
            return OpenApiComponents.class;
        }
    }

    private String getInAnnotationGroupErrorMessage(final String annotationGroupName) {
        return annotationGroupName.equals(DEFAULT_ANNOTATION_GROUP_NAME) ? "" :
                " in annotation group \"%s\"".formatted(annotationGroupName);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(getClass().getPackage().getName() + ".*");
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return latestSupported();
    }
}
