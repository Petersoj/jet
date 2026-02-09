package net.jacobpeterson.jet.openapiannotations;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.networknt.schema.Schema;
import com.networknt.schema.SchemaRegistry;
import lombok.Data;
import net.jacobpeterson.jet.openapiannotations.annotation.annotationsvalidation.AnnotationsValidationLevel;
import net.jacobpeterson.jet.openapiannotations.annotation.annotationsvalidation.OpenApiAnnotationsValidation;
import net.jacobpeterson.jet.openapiannotations.annotation.externaldoc.OpenApiExternalDoc;
import net.jacobpeterson.jet.openapiannotations.annotation.externaldoc.OpenApiExternalDocs;
import net.jacobpeterson.jet.openapiannotations.annotation.info.OpenApiInfo;
import net.jacobpeterson.jet.openapiannotations.annotation.info.OpenApiInfos;
import net.jacobpeterson.jet.openapiannotations.annotation.jsonschemadialect.OpenApiJsonSchemaDialect;
import net.jacobpeterson.jet.openapiannotations.annotation.jsonschemadialect.OpenApiJsonSchemaDialects;
import net.jacobpeterson.jet.openapiannotations.annotation.self.OpenApiSelf;
import net.jacobpeterson.jet.openapiannotations.annotation.self.OpenApiSelfs;
import net.jacobpeterson.jet.openapiannotations.annotation.server.OpenApiServer;
import net.jacobpeterson.jet.openapiannotations.annotation.server.OpenApiServers;
import net.jacobpeterson.jet.openapiannotations.annotation.tag.OpenApiTag;
import net.jacobpeterson.jet.openapiannotations.annotation.tag.OpenApiTags;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.AnnotationJsonSerializer;
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
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkState;
import static com.networknt.schema.InputFormat.JSON;
import static java.nio.file.Files.readString;
import static java.util.Objects.requireNonNull;
import static javax.lang.model.SourceVersion.latestSupported;
import static javax.tools.StandardLocation.CLASS_OUTPUT;
import static net.jacobpeterson.jet.openapiannotations.annotation.annotationsvalidation.AnnotationsValidationLevel.ERROR;
import static net.jacobpeterson.jet.openapiannotations.annotation.annotationsvalidation.AnnotationsValidationLevel.NONE;

/**
 * {@link OpenApiAnnotationsProcessor} is an {@link AbstractProcessor} for OpenAPI annotations.
 */
@NullMarked
public final class OpenApiAnnotationsProcessor extends AbstractProcessor {

    /**
     * The OpenAPI specification schema URL this {@link OpenApiAnnotationsProcessor} uses.
     *
     * @see <a href="https://spec.openapis.org/oas/3.2/schema/2025-09-17.html">spec.openapis.org</a>
     */
    public static final String OPENAPI_SPECIFICATION_SCHEMA_URL = "https://spec.openapis.org/oas/3.2/schema/2025-09-17";

    /**
     * The OpenAPI specification version this {@link OpenApiAnnotationsProcessor} uses.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html">spec.openapis.org</a>
     */
    public static final String OPENAPI_SPECIFICATION_VERSION = "3.2.0";

    /**
     * OpenAPI annotations can be grouped into different specification outputs according to their <em>annotation group
     * name</em>. This {@link String} constant acts as a reference for the default annotation group, which is just an
     * empty {@link String}.
     */
    public static final String DEFAULT_ANNOTATION_GROUP_NAME = "";

    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            return false;
        }
        final var specificationAnnotationsOfGroupNames = new HashMap<String, SpecificationAnnotations>();
        final Function<String, SpecificationAnnotations> specificationAnnotationsOfGroupNamesMap = groupName ->
                specificationAnnotationsOfGroupNames.computeIfAbsent(groupName, _ -> new SpecificationAnnotations());
        for (final var element : roundEnv.getElementsAnnotatedWith(OpenApiAnnotationsValidation.class)) {
            final var annotationsValidation = requireNonNull(element.getAnnotation(OpenApiAnnotationsValidation.class));
            specificationAnnotationsOfGroupNamesMap.apply(annotationsValidation.annotationGroupName())
                    .setValidationLevel(annotationsValidation.level());
        }
        for (final var entry : getElementsOfRepeatableAnnotation(roundEnv,
                OpenApiSelf.class, OpenApiSelfs.class, OpenApiSelfs::value).entrySet()) {
            final var openApiSelf = entry.getKey();
            final var specificationAnnotations = specificationAnnotationsOfGroupNamesMap
                    .apply(openApiSelf.annotationGroupName());
            // `getElementsOfRepeatableAnnotation()` already checks for duplicate `@OpenApiSelf` annotations.
            checkState(specificationAnnotations.getSelf() == null);
            specificationAnnotations.setSelf(openApiSelf);
        }
        for (final var entry : getElementsOfRepeatableAnnotation(roundEnv,
                OpenApiInfo.class, OpenApiInfos.class, OpenApiInfos::value).entrySet()) {
            final var openApiInfo = entry.getKey();
            final var specificationAnnotations = specificationAnnotationsOfGroupNamesMap
                    .apply(openApiInfo.annotationGroupName());
            if (specificationAnnotations.getInfo() != null) {
                processingEnv.getMessager().printError("Duplicate `@%s`"
                        .formatted(OpenApiInfo.class.getSimpleName()), entry.getValue());
            } else {
                specificationAnnotations.setInfo(openApiInfo);
            }
        }
        for (final var entry : getElementsOfRepeatableAnnotation(roundEnv,
                OpenApiJsonSchemaDialect.class, OpenApiJsonSchemaDialects.class, OpenApiJsonSchemaDialects::value)
                .entrySet()) {
            final var openApiJsonSchemaDialect = entry.getKey();
            final var specificationAnnotations = specificationAnnotationsOfGroupNamesMap
                    .apply(openApiJsonSchemaDialect.annotationGroupName());
            // `getElementsOfRepeatableAnnotation()` already checks for duplicate `@OpenApiJsonSchemaDialect`
            // annotations.
            checkState(specificationAnnotations.getJsonSchemaDialect() == null);
            specificationAnnotations.setJsonSchemaDialect(openApiJsonSchemaDialect);
        }
        for (final var entry : getElementsOfRepeatableAnnotation(roundEnv,
                OpenApiServer.class, OpenApiServers.class, OpenApiServers::value).entrySet()) {
            final var openApiServer = entry.getKey();
            final var specificationAnnotations = specificationAnnotationsOfGroupNamesMap
                    .apply(openApiServer.annotationGroupName());
            if (specificationAnnotations.getServers() == null) {
                specificationAnnotations.setServers(new ArrayList<>());
            }
            if (requireNonNull(specificationAnnotations.getServers()).stream().anyMatch(server ->
                    server.url().equals(openApiServer.url()) || server.name().equals(openApiServer.name()))) {
                processingEnv.getMessager().printError("Duplicate `@%s`"
                        .formatted(OpenApiServer.class.getSimpleName()), entry.getValue());
            } else {
                specificationAnnotations.getServers().add(openApiServer);
            }
        }
        for (final var entry : getElementsOfRepeatableAnnotation(roundEnv,
                OpenApiTag.class, OpenApiTags.class, OpenApiTags::value).entrySet()) {
            final var openApiTag = entry.getKey();
            final var specificationAnnotations = specificationAnnotationsOfGroupNamesMap
                    .apply(openApiTag.annotationGroupName());
            if (specificationAnnotations.getTags() == null) {
                specificationAnnotations.setTags(new ArrayList<>());
            }
            if (requireNonNull(specificationAnnotations.getTags()).stream().anyMatch(tag ->
                    tag.name().equals(openApiTag.name()))) {
                processingEnv.getMessager().printError("Duplicate `@%s`"
                        .formatted(OpenApiTag.class.getSimpleName()), entry.getValue());
            } else {
                specificationAnnotations.getTags().add(openApiTag);
            }
        }
        for (final var entry : getElementsOfRepeatableAnnotation(roundEnv,
                OpenApiExternalDoc.class, OpenApiExternalDocs.class, OpenApiExternalDocs::value).entrySet()) {
            final var openApiExternalDoc = entry.getKey();
            final var specificationAnnotations = specificationAnnotationsOfGroupNamesMap
                    .apply(openApiExternalDoc.annotationGroupName());
            if (specificationAnnotations.getExternalDocs() != null) {
                processingEnv.getMessager().printError("Duplicate `@%s`"
                        .formatted(OpenApiExternalDoc.class.getSimpleName()), entry.getValue());
            } else {
                specificationAnnotations.setExternalDocs(openApiExternalDoc);
            }
        }
        final var gson = new GsonBuilder()
                .enableComplexMapKeySerialization()
                .registerTypeHierarchyAdapter(Annotation.class, new AnnotationJsonSerializer())
                .registerTypeAdapter(String.class, new EmptyStringIsNullJsonSerializer())
                .create();
        Schema schema = null;
        for (final var specificationAnnotationsOfGroupName : specificationAnnotationsOfGroupNames.entrySet()) {
            final var groupName = specificationAnnotationsOfGroupName.getKey();
            final var specificationAnnotations = specificationAnnotationsOfGroupName.getValue();
            final var openApiJson = gson.toJson(specificationAnnotations);
            if (specificationAnnotations.getValidationLevel() != NONE) {
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
                    processingEnv.getMessager().printMessage(switch (specificationAnnotations.getValidationLevel()) {
                        case WARNING -> Kind.WARNING;
                        case ERROR -> Kind.ERROR;
                        default -> throw new IllegalStateException();
                    }, (!groupName.equals(DEFAULT_ANNOTATION_GROUP_NAME) ? "Annotation group \"" + groupName + "\": " :
                            "") + error.getMessage());
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

    @Data
    private static final class SpecificationAnnotations {

        private transient AnnotationsValidationLevel validationLevel = ERROR;
        private final @SerializedName("$schema") String schema = OPENAPI_SPECIFICATION_SCHEMA_URL;
        private final String openapi = OPENAPI_SPECIFICATION_VERSION;
        private @SerializedName("$self") @Nullable OpenApiSelf self;
        private @Nullable OpenApiInfo info;
        private @Nullable OpenApiJsonSchemaDialect jsonSchemaDialect;
        private @Nullable List<OpenApiServer> servers;
        private @Nullable List<OpenApiTag> tags;
        private @Nullable OpenApiExternalDoc externalDocs;
    }

    private <S extends Annotation, P extends Annotation> Map<S, Element> getElementsOfRepeatableAnnotation(
            final RoundEnvironment roundEnv, final Class<S> singularClass, final Class<P> pluralClass,
            final Function<P, S[]> pluralClassValue) {
        final var elementsOfRepeatableAnnotations = new HashMap<S, Element>();
        for (final var element : roundEnv.getElementsAnnotatedWith(singularClass)) {
            if (elementsOfRepeatableAnnotations
                    .put(requireNonNull(element.getAnnotation(singularClass)), element) != null) {
                processingEnv.getMessager().printError("Duplicate `@%s`"
                        .formatted(singularClass.getSimpleName()), element);
            }
        }
        for (final var element : roundEnv.getElementsAnnotatedWith(pluralClass)) {
            for (final var singular : pluralClassValue.apply(requireNonNull(element.getAnnotation(pluralClass)))) {
                if (elementsOfRepeatableAnnotations.put(singular, element) != null) {
                    processingEnv.getMessager().printError("Duplicate `@%s`"
                            .formatted(singularClass.getSimpleName()), element);
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
