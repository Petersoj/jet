package net.jacobpeterson.jet.openapiannotations;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
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
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;
import static javax.lang.model.SourceVersion.latestSupported;
import static javax.tools.StandardLocation.CLASS_OUTPUT;

/**
 * {@link OpenApiAnnotationsProcessor} is an {@link AbstractProcessor} for OpenAPI annotations.
 */
@NullMarked
public final class OpenApiAnnotationsProcessor extends AbstractProcessor {

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
        for (final var specificationAnnotationsOfGroupName : specificationAnnotationsOfGroupNames.entrySet()) {
            final var groupName = specificationAnnotationsOfGroupName.getKey();
            try (final var openApiJsonWriter = processingEnv.getFiler().createResource(CLASS_OUTPUT,
                    getClass().getPackageName(),
                    "openapi%s.json".formatted(!groupName.isEmpty() ? "-" + groupName : "")).openWriter()) {
                gson.toJson(specificationAnnotationsOfGroupName.getValue(), openApiJsonWriter);
            } catch (final IOException ioException) {
                throw new RuntimeException(ioException);
            }
        }
        return false;
    }

    @Data
    private static final class SpecificationAnnotations {

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
