package net.jacobpeterson.jet.openapiannotations.annotation;

import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationArrayIsMap;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationArrayIsMapKey;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationArrayIsNullableValue;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationJsonIgnore;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationJsonObjectInline;
import org.jspecify.annotations.NullMarked;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link OpenApiTag} is an annotation for the
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#tag-object">OpenAPI Tag Object</a>.
 * <p>
 * A list of tags used by the OpenAPI Description with additional metadata. The order of the tags can be used to reflect
 * on their order by the parsing tools. Not all tags that are used by the
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#operation-object">Operation Object</a> must be declared. The tags
 * that are not declared <em>MAY</em> be organized randomly or based on the tools’ logic. Each tag name in the list
 * <em>MUST</em> be unique.
 *
 * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#tag-object">spec.openapis.org</a>
 */
@NullMarked
@Target({})
@Retention(RUNTIME)
public @interface OpenApiTag {

    /**
     * {@link MapEntry} is an annotation for an {@link OpenApiTag} entry in an {@link AnnotationArrayIsMap}
     * annotation method.
     */
    @Target({})
    @Retention(RUNTIME) //@formatter:off
    @interface MapEntry { //@formatter:on

        /**
         * The map entry key.
         */
        @AnnotationJsonIgnore
        @AnnotationArrayIsMapKey
        String key() default "";

        /**
         * The map entry value.
         */
        @AnnotationArrayIsNullableValue
        @AnnotationJsonObjectInline
        OpenApiTag[] value() default {};
    }

    /**
     * <strong><em>REQUIRED</em></strong>. The name of the tag. Use this value in the <code>tags</code> array of an
     * Operation.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#tag-name">spec.openapis.org</a>
     */
    String name() default "";

    /**
     * A short summary of the tag, used for display purposes.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#tag-summary">spec.openapis.org</a>
     */
    String summary() default "";

    /**
     * A description for the tag. <a href="https://spec.openapis.org/oas/v3.2.0.html#bib-commonmark">CommonMark</a>
     * syntax <em>MAY</em> be used for rich text representation.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#tag-description">spec.openapis.org</a>
     */
    String description() default "";

    /**
     * Additional external documentation for this tag.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#tag-external-docs">spec.openapis.org</a>
     */
    @AnnotationArrayIsNullableValue
    OpenApiExternalDoc[] externalDocs() default {};

    /**
     * The <code>name</code> of a tag that this tag is nested under. The named tag <em>MUST</em> exist in the API
     * description, and circular references between parent and child tags <em>MUST NOT</em> be used.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#tag-parent">spec.openapis.org</a>
     */
    String parent() default "";

    /**
     * A machine-readable string to categorize what sort of tag it is. Any string value can be used; common uses are
     * <code>nav</code> for Navigation, <code>badge</code> for visible badges, <code>audience</code> for APIs used by
     * different groups. A <a href="https://spec.openapis.org/registry/tag-kind/">registry of the most commonly used
     * values</a> is available.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#tag-kind">spec.openapis.org</a>
     */
    String kind() default "";
}
