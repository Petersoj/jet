package net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.key;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.ContentSecurityPolicy;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;

import static java.util.Arrays.stream;
import static java.util.Locale.ROOT;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toUnmodifiableMap;
import static net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.key.PolicyDirectiveKeyType.DEPRECATED;
import static net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.key.PolicyDirectiveKeyType.DOCUMENT;
import static net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.key.PolicyDirectiveKeyType.FETCH;
import static net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.key.PolicyDirectiveKeyType.NAVIGATION;
import static net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.key.PolicyDirectiveKeyType.OTHER;
import static net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.key.PolicyDirectiveKeyType.REPORTING;

/**
 * {@link PolicyDirectiveKey} is an enum for a {@link ContentSecurityPolicy} directive key.
 *
 * @see
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy#directives">
 * developer.mozilla.org</a>
 */
@NullMarked
@RequiredArgsConstructor
public enum PolicyDirectiveKey {

    // BEGIN https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy#fetch_directives

    /**
     * Defines the valid sources for
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/Web_Workers_API">web workers</a> and nested browsing
     * contexts loaded using elements such as
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Elements/frame">
     * <code>&lt;frame&gt;</code></a> and
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Elements/iframe">
     * <code>&lt;iframe&gt;</code></a>.
     * <p>
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy#fallbacks">
     * Fallback</a> for <code>frame-src</code> and <code>worker-src</code>.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy/child-src">
     * developer.mozilla.org</a>
     * @see PolicyDirectiveKeyType#FETCH
     */
    CHILD_SRC("child-src", FETCH),

    /**
     * Restricts the URLs which can be loaded using script interfaces.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy/connect-src">
     * developer.mozilla.org</a>
     * @see PolicyDirectiveKeyType#FETCH
     */
    CONNECT_SRC("connect-src", FETCH),

    /**
     * Serves as a fallback for the other
     * <a href="https://developer.mozilla.org/en-US/docs/Glossary/Fetch_directive">fetch directives</a>.
     * <p>
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy#fallbacks">
     * Fallback</a> for all other fetch directives.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy/default-src">
     * developer.mozilla.org</a>
     * @see PolicyDirectiveKeyType#FETCH
     */
    DEFAULT_SRC("default-src", FETCH),

    /**
     * Specifies valid sources for nested browsing contexts loaded into
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Elements/fencedframe">
     * <code>&lt;fencedframe&gt;</code></a> elements.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy/fenced-frame-src">
     * developer.mozilla.org</a>
     * @see PolicyDirectiveKeyType#FETCH
     */
    FENCED_FRAME_SRC("fenced-frame-src", FETCH),

    /**
     * Specifies valid sources for fonts loaded using
     * <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/Reference/At-rules/@font-face">
     * <code>@font-face</code></a>.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy/font-src">
     * developer.mozilla.org</a>
     * @see PolicyDirectiveKeyType#FETCH
     */
    FONT_SRC("font-src", FETCH),

    /**
     * Specifies valid sources for nested browsing contexts loaded into elements such as
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Elements/frame">
     * <code>&lt;frame&gt;</code></a> and
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Elements/iframe">
     * <code>&lt;iframe&gt;</code></a>.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy/frame-src">
     * developer.mozilla.org</a>
     * @see PolicyDirectiveKeyType#FETCH
     */
    FRAME_SRC("frame-src", FETCH),

    /**
     * Specifies valid sources of images and favicons.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy/img-src">
     * developer.mozilla.org</a>
     * @see PolicyDirectiveKeyType#FETCH
     */
    IMG_SRC("img-src", FETCH),

    /**
     * Specifies valid sources of application manifest files.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy/manifest-src">
     * developer.mozilla.org</a>
     * @see PolicyDirectiveKeyType#FETCH
     */
    MANIFEST_SRC("manifest-src", FETCH),

    /**
     * Specifies valid sources for loading media using the
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Elements/audio">
     * <code>&lt;audio&gt;</code></a>,
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Elements/video">
     * <code>&lt;video&gt;</code></a> and
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Elements/track">
     * <code>&lt;track&gt;</code></a> elements.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy/media-src">
     * developer.mozilla.org</a>
     * @see PolicyDirectiveKeyType#FETCH
     */
    MEDIA_SRC("media-src", FETCH),

    /**
     * Specifies valid sources for the
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Elements/object">
     * <code>&lt;object&gt;</code></a> and
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Elements/embed">
     * <code>&lt;embed&gt;</code></a> elements.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy/object-src">
     * developer.mozilla.org</a>
     * @see PolicyDirectiveKeyType#FETCH
     */
    OBJECT_SRC("object-src", FETCH),

    /**
     * Specifies valid sources to be prefetched or prerendered.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy/prefetch-src">
     * developer.mozilla.org</a>
     * @see PolicyDirectiveKeyType#FETCH
     */
    PREFETCH_SRC("prefetch-src", FETCH),

    /**
     * Specifies valid sources for JavaScript and WebAssembly resources.
     * <p>
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy#fallbacks">
     * Fallback</a> for <code>script-src-elem</code> and <code>script-src-attr</code>.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy/script-src">
     * developer.mozilla.org</a>
     * @see PolicyDirectiveKeyType#FETCH
     */
    SCRIPT_SRC("script-src", FETCH),

    /**
     * Specifies valid sources for JavaScript
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Elements/script">
     * <code>&lt;script&gt;</code></a> elements.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy/script-src-elem">
     * developer.mozilla.org</a>
     * @see PolicyDirectiveKeyType#FETCH
     */
    SCRIPT_SRC_ELEM("script-src-elem", FETCH),

    /**
     * Specifies valid sources for JavaScript inline event handlers.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy/script-src-attr">
     * developer.mozilla.org</a>
     * @see PolicyDirectiveKeyType#FETCH
     */
    SCRIPT_SRC_ATTR("script-src-attr", FETCH),

    /**
     * Specifies valid sources for stylesheets.
     * <p>
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy#fallbacks">
     * Fallback</a> for <code>style-src-elem</code> and <code>style-src-attr</code>.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy/style-src">
     * developer.mozilla.org</a>
     * @see PolicyDirectiveKeyType#FETCH
     */
    STYLE_SRC("style-src", FETCH),

    /**
     * Specifies valid sources for stylesheets
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Elements/style">
     * <code>&lt;style&gt;</code></a> elements and
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Elements/link">
     * <code>&lt;link&gt;</code></a> elements with <code>rel="stylesheet"</code>.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy/style-src-elem">
     * developer.mozilla.org</a>
     * @see PolicyDirectiveKeyType#FETCH
     */
    STYLE_SRC_ELEM("style-src-elem", FETCH),

    /**
     * Specifies valid sources for inline styles applied to individual DOM elements.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy/style-src-attr">
     * developer.mozilla.org</a>
     * @see PolicyDirectiveKeyType#FETCH
     */
    STYLE_SRC_ATTR("style-src-attr", FETCH),

    /**
     * Specifies valid sources for
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/Worker"><code>Worker</code></a>,
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/SharedWorker"><code>SharedWorker</code></a>, or
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/ServiceWorker"><code>ServiceWorker</code></a> scripts.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy/worker-src">
     * developer.mozilla.org</a>
     * @see PolicyDirectiveKeyType#FETCH
     */
    WORKER_SRC("worker-src", FETCH),

    // END https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy#fetch_directives

    // BEGIN https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy#document_directives

    /**
     * Restricts the URLs which can be used in a document's
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Elements/base"><code>&lt;base&gt;</code></a>
     * element.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy/base-uri">
     * developer.mozilla.org</a>
     * @see PolicyDirectiveKeyType#DOCUMENT
     */
    BASE_URI("base-uri", DOCUMENT),

    /**
     * Enables a sandbox for the requested resource similar to the
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Elements/iframe">
     * <code>&lt;iframe&gt;</code></a>
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Elements/iframe#sandbox">
     * <code>sandbox</code></a> attribute.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy/sandbox">
     * developer.mozilla.org</a>
     * @see PolicyDirectiveKeyType#DOCUMENT
     */
    SANDBOX("sandbox", DOCUMENT),

    // END https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy#document_directives

    // BEGIN https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy#navigation_directives

    /**
     * Restricts the URLs which can be used as the target of a form submissions from a given context.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy/form-action">
     * developer.mozilla.org</a>
     * @see PolicyDirectiveKeyType#NAVIGATION
     */
    FORM_ACTION("form-action", NAVIGATION),

    /**
     * Specifies valid parents that may embed a page using
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Elements/frame">
     * <code>&lt;frame&gt;</code></a>,
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Elements/iframe">
     * <code>&lt;iframe&gt;</code></a>,
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Elements/object">
     * <code>&lt;object&gt;</code></a>, or
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Elements/embed">
     * <code>&lt;embed&gt;</code></a>.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy/frame-ancestors">
     * developer.mozilla.org</a>
     * @see PolicyDirectiveKeyType#NAVIGATION
     */
    FRAME_ANCESTORS("frame-ancestors", NAVIGATION),

    // END https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy#navigation_directives

    // BEGIN https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy#reporting_directives

    /**
     * Provides the browser with a token identifying the reporting endpoint or group of endpoints to send CSP violation
     * information to. The endpoints that the token represents are provided through other HTTP headers, such as
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Reporting-Endpoints">
     * <code>Reporting-Endpoints</code></a> and
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Report-To">
     * <code>Report-To</code></a>.
     * <p>
     * <strong>Warning:</strong> This directive is intended to replace
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy/report-uri">
     * <code>report-uri</code></a>; in browsers that support <code>report-to</code>, the <code>report-uri</code>
     * directive is ignored. However until <code>report-to</code> is broadly supported you should specify both headers
     * as shown (where <code>endpoint_name</code> is the name of a separately provided endpoint):
     * <code>Content-Security-Policy: …; report-uri https://endpoint.example.com; report-to endpoint_name</code>
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy/report-to">
     * developer.mozilla.org</a>
     * @see PolicyDirectiveKeyType#REPORTING
     */
    REPORT_TO("report-to", REPORTING),

    // END https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy#reporting_directives

    // BEGIN https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy#other_directives

    /**
     * Enforces <a href="https://developer.mozilla.org/en-US/docs/Web/API/Trusted_Types_API">Trusted Types</a> at the
     * DOM XSS injection sinks.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy/require-trusted-types-for">
     * developer.mozilla.org</a>
     * @see PolicyDirectiveKeyType#OTHER
     */
    REQUIRE_TRUSTED_TYPES_FOR("require-trusted-types-for", OTHER),

    /**
     * Used to specify an allowlist of
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/Trusted_Types_API">Trusted Types</a> policies. Trusted
     * Types allows applications to lock down DOM XSS injection sinks to only accept non-spoofable, typed values in
     * place of strings.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy/trusted-types">
     * developer.mozilla.org</a>
     * @see PolicyDirectiveKeyType#OTHER
     */
    TRUSTED_TYPES("trusted-types", OTHER),

    /**
     * Instructs user agents to treat all of a site's insecure URLs (those served over HTTP) as though they have been
     * replaced with secure URLs (those served over HTTPS). This directive is intended for websites with large numbers
     * of insecure legacy URLs that need to be rewritten.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy/upgrade-insecure-requests">
     * developer.mozilla.org</a>
     * @see PolicyDirectiveKeyType#OTHER
     */
    UPGRADE_INSECURE_REQUESTS("upgrade-insecure-requests", OTHER),

    // END https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy#other_directives

    // BEGIN https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy#deprecated_directives

    /**
     * Prevents loading any assets using HTTP when the page is loaded using HTTPS.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy/block-all-mixed-content">
     * developer.mozilla.org</a>
     * @see PolicyDirectiveKeyType#DEPRECATED
     */
    BLOCK_ALL_MIXED_CONTENT("block-all-mixed-content", DEPRECATED),

    /**
     * Provides the browser with a URL where CSP violation reports should be sent. This has been superseded by the
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy/report-to">
     * <code>report-to</code></a> directive.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy/report-uri">
     * developer.mozilla.org</a>
     * @see PolicyDirectiveKeyType#DEPRECATED
     */
    REPORT_URI("report-uri", DEPRECATED);

    // END https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy#deprecated_directives

    private final String string;
    private final @Getter PolicyDirectiveKeyType type;

    @Override
    public String toString() {
        return string;
    }

    /**
     * An unmodifiable {@link Map} of lowercased {@link #toString()} mapped to {@link PolicyDirectiveKey}.
     */
    public static final Map<String, PolicyDirectiveKey> VALUES_OF_LOWERCASED_STRINGS = stream(values())
            .collect(toUnmodifiableMap(value -> value.toString().toLowerCase(ROOT), identity()));

    /**
     * Gets the {@link PolicyDirectiveKey} for the given <code>string</code>.
     *
     * @param string the case-insensitive {@link #toString()}
     *
     * @return the {@link PolicyDirectiveKey}, or <code>null</code> if no mapping exists
     */
    public static @Nullable PolicyDirectiveKey forString(final String string) {
        return VALUES_OF_LOWERCASED_STRINGS.get(string.toLowerCase(ROOT));
    }
}
