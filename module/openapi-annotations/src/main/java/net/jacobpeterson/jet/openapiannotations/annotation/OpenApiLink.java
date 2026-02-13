package net.jacobpeterson.jet.openapiannotations.annotation;

import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationArrayIsMap;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationArrayIsMapKey;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationArrayIsNullableValue;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationJsonIgnore;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationJsonObjectInline;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationJsonRawString;
import org.jspecify.annotations.NullMarked;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link OpenApiLink} is an annotation for the
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#link-object">OpenAPI Link Object</a>.
 * <p>
 * The Link Object represents a possible design-time link for a response. The presence of a link does not guarantee
 * the caller’s ability to successfully invoke it, rather it provides a known relationship and traversal mechanism
 * between responses and other operations.
 * <p>
 * Unlike <em>dynamic</em> links (i.e. links provided <strong>in</strong> the response payload), the OAS linking
 * mechanism does not require link information in the runtime response.
 * <p>
 * For computing links and providing instructions to execute them, a
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#runtime-expressions">runtime expression</a>
 * is used for accessing values in an operation and using them as parameters while invoking the linked operation.
 * <p>
 * A linked operation <em>MUST</em> be identified using either an <code>operationRef</code> or <code>operationId</code>.
 * The identified or referenced operation <em>MUST</em> be unique, and in the case of an <code>operationId</code>, it
 * <em>MUST</em> be resolved within the scope of the OpenAPI Description (OAD). Because of the potential for name
 * clashes, the <code>operationRef</code> syntax is preferred for multi-document OADs. However, because use of an
 * operation depends on its URL path template in the
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#paths-object">Paths Object</a>, operations from any
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#path-item-object">Path Item Object</a> that is referenced multiple
 * times within the OAD cannot be resolved unambiguously. In such ambiguous cases, the resulting behavior is
 * implementation-defined and <em>MAY</em> result in an error.
 * <p>
 * Note that it is not possible to provide a constant value to <code>parameters</code> that matches the syntax of a
 * runtime expression. It is possible to have ambiguous parameter names, e.g. <code>name: "id", in: "path"</code> and
 * <code>name: "path.id", in: "query"</code>; this is <em>NOT RECOMMENDED</em> and the behavior is
 * implementation-defined, however implementations <em>SHOULD</em> prefer the qualified interpretation
 * (<code>path.id</code> as a path parameter), as the names can always be qualified to disambiguate them (e.g. using
 * <code>query.path.id</code> for the query parameter).
 *
 * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#link-object">spec.openapis.org</a>
 */
@NullMarked
@Target({})
@Retention(RUNTIME)
public @interface OpenApiLink {

    /**
     * {@link MapEntry} is an annotation for an {@link OpenApiLink} entry in an {@link AnnotationArrayIsMap}
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
        OpenApiLink[] value() default {};
    }

    /**
     * A URI reference to an OAS operation. This field is mutually exclusive of the <code>operationId</code> field, and
     * <em>MUST</em> point to an <a href="https://spec.openapis.org/oas/v3.2.0.html#operation-object">Operation
     * Object</a>. Relative <code>operationRef</code> values <em>MAY</em> be used to locate an existing
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#operation-object">Operation Object</a> in the OpenAPI
     * Description.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#link-operation-ref">spec.openapis.org</a>
     */
    String operationRef() default "";

    /**
     * The name of an <em>existing</em>, resolvable OAS operation, as defined with a unique <code>operationId</code>.
     * This field is mutually exclusive of the <code>operationRef</code> field.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#link-operation-id">spec.openapis.org</a>
     */
    String operationId() default "";

    /**
     * A map representing parameters to pass to an operation as specified with <code>operationId</code> or identified
     * via <code>operationRef</code>. The key is the parameter name to be used (optionally qualified with the parameter
     * location, e.g. <code>path.id</code> for an <code>id</code> parameter in the path), whereas the value can be a
     * constant or an expression to be evaluated and passed to the linked operation.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#link-parameters">spec.openapis.org</a>
     */
    @AnnotationArrayIsMap
    Parameter[] parameters() default {};

    /**
     * {@link Parameter} is an annotation for the {@link #parameters()} map.
     */
    @Target({})
    @Retention(RUNTIME) //@formatter:off
    @interface Parameter { //@formatter:on

        /**
         * The map entry key.
         */
        @AnnotationJsonIgnore
        @AnnotationArrayIsMapKey
        String key() default "";

        /**
         * The map entry value.
         */
        @AnnotationJsonRawString
        @AnnotationJsonObjectInline
        String value() default "";
    }

    /**
     * A literal value or <a href="https://spec.openapis.org/oas/v3.2.0.html#runtime-expressions">{expression}</a> to
     * use as a request body when calling the target operation.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#link-request-body">spec.openapis.org</a>
     */
    @AnnotationJsonRawString
    String requestBody() default "";

    /**
     * A description of the link. <a href="https://spec.openapis.org/oas/v3.2.0.html#bib-commonmark">CommonMark</a>
     * syntax <em>MAY</em> be used for rich text representation.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#link-description">spec.openapis.org</a>
     */
    String description() default "";

    /**
     * A server object to be used by the target operation.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#link-server">spec.openapis.org</a>
     */
    OpenApiServer[] server() default {};
}
