package net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.key;

import org.jspecify.annotations.NullMarked;

/**
 * {@link PolicyDirectiveKeyType} is an enum for the type of a {@link PolicyDirectiveKey}.
 *
 * @see
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy#directives">
 * developer.mozilla.org</a>
 */
@NullMarked
public enum PolicyDirectiveKeyType {

    /**
     * Fetch directives control the locations from which certain resource types may be loaded.
     * <p>
     * All fetch directives may be specified the single value <code>'none'</code>, indicating that the specific resource
     * type should be completely blocked, or as one or more <em>source expression</em> values, indicating valid sources
     * for that resource type. See
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy#fetch_directive_syntax">
     * Fetch directive syntax</a> for more details.
     * <p>
     * Some fetch directives function as fallbacks for other more granular directives. This means that if the more
     * granular directive is not specified, then the fallback is used to provide a policy for that resource type.
     * <ul>
     * <li><code>default-src</code> is a fallback for all other fetch directives.</li>
     * <li><code>script-src</code> is a fallback for <code>script-src-attr</code> and <code>script-src-elem</code>.</li>
     * <li><code>style-src</code> is a fallback for <code>style-src-attr</code> and <code>style-src-elem</code>.</li>
     * <li><code>child-src</code> is a fallback for <code>frame-src</code> and <code>worker-src</code>.</li>
     * </ul>
     * For example:
     * <ul>
     * <li>If <code>img-src</code> is omitted but <code>default-src</code> is included, then the policy defined by
     * <code>default-src</code> will be applied to images.</li>
     * <li>If <code>script-src-elem</code> is omitted but <code>script-src</code> is included, then the policy defined
     * by <code>script-src</code> will be applied to <code>&lt;script&gt;</code> elements.</li>
     * <li>If <code>script-src-elem</code> and <code>script-src</code> are both omitted, but <code>default-src</code> is
     * included, then the policy defined by <code>default-src</code> will be applied to <code>&lt;script&gt;</code>
     * elements.</li>
     * </ul>
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy#fetch_directives">
     * developer.mozilla.org</a>
     */
    FETCH,

    /**
     * Document directives govern the properties of a document or
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/Web_Workers_API">worker</a> environment to which a
     * policy applies.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy#document_directives">
     * developer.mozilla.org</a>
     */
    DOCUMENT,

    /**
     * Navigation directives govern to which locations a user can navigate or submit a form, for example.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy#navigation_directives">
     * developer.mozilla.org</a>
     */
    NAVIGATION,

    /**
     * Reporting directives control the destination URL for CSP violation reports in
     * <code>Content-Security-Policy</code> and
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy-Report-Only">
     * <code>Content-Security-Policy-Report-Only</code></a>.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy#reporting_directives">
     * developer.mozilla.org</a>
     */
    REPORTING,

    /**
     * Other directives.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy#other_directives">
     * developer.mozilla.org</a>
     */
    OTHER,

    /**
     * Deprecated directives.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy#deprecated_directives">
     * developer.mozilla.org</a>
     */
    DEPRECATED
}
