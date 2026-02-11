package net.jacobpeterson.jet.openapiannotations;

import com.google.gson.GsonBuilder;
import com.networknt.schema.Schema;
import com.networknt.schema.SchemaRegistry;
import lombok.Value;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApi;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApis;
import net.jacobpeterson.jet.openapiannotations.annotation.component.OpenApiComponents;
import net.jacobpeterson.jet.openapiannotations.annotation.externaldoc.OpenApiExternalDoc;
import net.jacobpeterson.jet.openapiannotations.annotation.info.OpenApiInfo;
import net.jacobpeterson.jet.openapiannotations.annotation.path.OpenApiPath;
import net.jacobpeterson.jet.openapiannotations.annotation.securityrequirement.OpenApiSecurityRequirements;
import net.jacobpeterson.jet.openapiannotations.annotation.server.OpenApiServer;
import net.jacobpeterson.jet.openapiannotations.annotation.tag.OpenApiTag;
import net.jacobpeterson.jet.openapiannotations.annotation.webhook.OpenApiWebhook;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.AnnotationJsonSerializer;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.string.EmptyStringIsNullJsonSerializer;
import org.jspecify.annotations.NullMarked;

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
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static com.networknt.schema.InputFormat.JSON;
import static java.nio.file.Files.readString;
import static java.util.Objects.requireNonNull;
import static javax.lang.model.SourceVersion.latestSupported;
import static javax.tools.StandardLocation.CLASS_OUTPUT;
import static net.jacobpeterson.jet.openapiannotations.annotation.OpenApi.AnnotationsValidationLevel.ERROR;
import static net.jacobpeterson.jet.openapiannotations.annotation.OpenApi.AnnotationsValidationLevel.NONE;
import static net.jacobpeterson.jet.openapiannotations.annotation.OpenApi.DEFAULT_ANNOTATION_GROUP_NAME;
import static net.jacobpeterson.jet.openapiannotations.annotation.OpenApi.DEFAULT_OPENAPI;
import static net.jacobpeterson.jet.openapiannotations.annotation.OpenApi.DEFAULT_SCHEMA;
import static net.jacobpeterson.jet.openapiannotations.util.reflection.ReflectionUtil.getFullClassName;

/**
 * {@link OpenApiAnnotationsProcessor} is an {@link AbstractProcessor} for OpenAPI annotations.
 */
@NullMarked
public final class OpenApiAnnotationsProcessor extends AbstractProcessor {

    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            return false;
        }
        final var openApiWrappersOfGroupNames = new HashMap<String, OpenApiWrapper>();
        for (final var entry : getElementsOfRepeatableAnnotation(roundEnv,
                OpenApi.class, OpenApis.class, OpenApis::value).entrySet()) {
            final var openApi = entry.getKey();
            final var groupName = openApi.annotationGroupName();
            if (openApiWrappersOfGroupNames.containsKey(groupName)) {
                processingEnv.getMessager().printError(
                        "Duplicate `@%s` annotation%s".formatted(getFullClassName(OpenApi.class),
                                groupName.equals(DEFAULT_ANNOTATION_GROUP_NAME) ? "" :
                                        " for annotation group \"%s\"".formatted(groupName)),
                        entry.getValue());
                continue;
            }
            openApiWrappersOfGroupNames.put(groupName, new OpenApiWrapper(openApi));
        }
        final var gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(Annotation.class, new AnnotationJsonSerializer())
                .registerTypeAdapter(String.class, new EmptyStringIsNullJsonSerializer())
                .create();
        Schema schema = null;
        for (final var openApiWrapperOfGroupName : openApiWrappersOfGroupNames.entrySet()) {
            final var groupName = openApiWrapperOfGroupName.getKey();
            final var openApiWrapper = openApiWrapperOfGroupName.getValue();
            final String openApiJson;
            try {
                openApiJson = gson.toJson(openApiWrapper);
            } catch (final Exception exception) {
                processingEnv.getMessager().printError(exception.toString());
                continue;
            }
            if (openApiWrapper.annotationsValidationLevel() != NONE) {
                if (!openApiWrapper.$schema().equals(DEFAULT_SCHEMA)) {
                    processingEnv.getMessager().printError("""
                            `@OpenApi.annotationsValidationLevel` is set to `%s`, but validation for custom \
                            `@OpenApi.$schema` of `%s` is unsupported. Set `@OpenApi.annotationsValidationLevel` to \
                            `NONE` or remove the custom `@OpenApi.$schema`."""
                            .formatted(openApiWrapper.annotationsValidationLevel(), openApiWrapper.$schema()));
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
                                openApiWrapper.annotationsValidationLevel() == ERROR ? Kind.ERROR : Kind.WARNING,
                                "OpenAPIv%s schema offense%s: %s".formatted(DEFAULT_OPENAPI,
                                        !groupName.equals(DEFAULT_ANNOTATION_GROUP_NAME) ?
                                                " in annotation group \"%s\"".formatted(groupName) : "",
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

    @Value
    @SuppressWarnings({"ClassExplicitlyAnnotation", "ImmutableAnnotationChecker"})
    private static class OpenApiWrapper implements OpenApi {

        OpenApi openApi;
        List<OpenApiTag> tags;

        private OpenApiWrapper(final OpenApi openApi) {
            this.openApi = openApi;
            tags = new ArrayList<>(List.of(openApi.tags()));
        }

        @Override
        public String annotationGroupName() {
            return openApi.annotationGroupName();
        }

        @Override
        public AnnotationsValidationLevel annotationsValidationLevel() {
            return openApi.annotationsValidationLevel();
        }

        @Override
        public String $schema() {
            return openApi.$schema();
        }

        @Override
        public String openapi() {
            return openApi.openapi();
        }

        @Override
        public String $self() {
            return openApi.$self();
        }

        @Override
        public OpenApiInfo[] info() {
            return openApi.info();
        }

        @Override
        public String jsonSchemaDialect() {
            return openApi.jsonSchemaDialect();
        }

        @Override
        public OpenApiServer[] servers() {
            return openApi.servers();
        }

        @Override
        public OpenApiPath[] paths() {
            return openApi.paths(); // TODO
        }

        @Override
        public OpenApiWebhook[] webhooks() {
            return openApi.webhooks(); // TODO
        }

        @Override
        public OpenApiComponents[] components() {
            return openApi.components(); // TODO
        }

        @Override
        public OpenApiSecurityRequirements[] security() {
            return openApi.security();
        }

        @Override
        public OpenApiTag[] tags() {
            return tags.toArray(OpenApiTag[]::new);
        }

        @Override
        public OpenApiExternalDoc[] externalDocs() {
            return openApi.externalDocs();
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return openApi.annotationType();
        }
    }

    @Value
    @SuppressWarnings("ClassExplicitlyAnnotation")
    private static class OpenApiTagGenerated implements OpenApiTag {

        String name;

        @Override
        public String name() {
            return name;
        }

        @Override
        public String summary() {
            return "";
        }

        @Override
        public String description() {
            return "";
        }

        @Override
        public OpenApiExternalDoc[] externalDocs() {
            return new OpenApiExternalDoc[0];
        }

        @Override
        public String parent() {
            return "";
        }

        @Override
        public String kind() {
            return "";
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return OpenApiTag.class;
        }
    }

    private <S extends Annotation, P extends Annotation> Map<S, Element> getElementsOfRepeatableAnnotation(
            final RoundEnvironment roundEnv, final Class<S> singularClass, final Class<P> pluralClass,
            final Function<P, S[]> pluralClassValue) {
        final var elementsOfRepeatableAnnotations = new HashMap<S, Element>();
        for (final var element : roundEnv.getElementsAnnotatedWith(singularClass)) {
            if (elementsOfRepeatableAnnotations.put(
                    requireNonNull(element.getAnnotation(singularClass)), element) != null) {
                processingEnv.getMessager().printError("Duplicate `@%s`"
                        .formatted(getFullClassName(singularClass)), element);
            }
        }
        for (final var element : roundEnv.getElementsAnnotatedWith(pluralClass)) {
            for (final var singular : pluralClassValue.apply(requireNonNull(element.getAnnotation(pluralClass)))) {
                if (elementsOfRepeatableAnnotations.put(singular, element) != null) {
                    processingEnv.getMessager().printError("Duplicate `@%s`"
                            .formatted(getFullClassName(singularClass)), element);
                }
            }
        }
        return elementsOfRepeatableAnnotations;
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
