package net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.sandbox;

import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.key.PolicyDirectiveKey;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static java.util.Arrays.stream;
import static java.util.Locale.ROOT;
import static java.util.function.Function.identity;

/**
 * {@link SandboxFlag} is an enum for the {@link PolicyDirectiveKey#SANDBOX} flags.
 * <p>
 * The HTTP <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy">
 * <code>Content-Security-Policy</code></a> (CSP) <strong><code>sandbox</code></strong> directive enables a sandbox for
 * the requested resource similar to the
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Elements/iframe"><code>&lt;iframe&gt;</code></a>
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Elements/iframe#sandbox">
 * <code>sandbox</code></a> attribute. It applies restrictions to a page's actions including preventing popups,
 * preventing the execution of plugins and scripts, and enforcing a same-origin policy.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy/sandbox">
 * developer.mozilla.org</a>
 */
@NullMarked
@RequiredArgsConstructor
public enum SandboxFlag {

    /**
     * Allows downloading files through an
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Elements/a"><code>&lt;a&gt;</code></a> or
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Elements/area"><code>&lt;area&gt;</code></a>
     * element with the
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Elements/a#download">download</a> attribute,
     * as well as through the navigation that leads to a download of a file. This works regardless of whether the user
     * clicked on the link, or JS code initiated it without user interaction.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy/sandbox#allow-downloads">
     * developer.mozilla.org</a>
     */
    ALLOW_DOWNLOADS("allow-downloads"),

    /**
     * Allows the page to submit forms. If this keyword is not used, form will be displayed as normal, but submitting it
     * will not trigger input validation, sending data to a web server or closing a dialog.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy/sandbox#allow-forms">
     * developer.mozilla.org</a>
     */
    ALLOW_FORMS("allow-forms"),

    /**
     * Allows the page to open modal windows by
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/Window/alert"><code>Window.alert()</code></a>,
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/Window/confirm"><code>Window.confirm()</code></a>,
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/Window/print"><code>Window.print()</code></a> and
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/Window/prompt"><code>Window.prompt()</code></a>, while
     * opening a <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Elements/dialog">
     * <code>&lt;dialog&gt;</code></a> is allowed regardless of this keyword. It also allows the page to receive
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/BeforeUnloadEvent"><code>BeforeUnloadEvent</code></a>
     * event.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy/sandbox#allow-modals">
     * developer.mozilla.org</a>
     */
    ALLOW_MODALS("allow-modals"),

    /**
     * Lets the resource <a href="https://developer.mozilla.org/en-US/docs/Web/API/Screen/lockOrientation">lock the
     * screen orientation</a>.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy/sandbox#allow-orientation-lock">
     * developer.mozilla.org</a>
     */
    ALLOW_ORIENTATION_LOCK("allow-orientation-lock"),

    /**
     * Allows the page to use the
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/Pointer_Lock_API">Pointer Lock API</a>.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy/sandbox#allow-pointer-lock">
     * developer.mozilla.org</a>
     */
    ALLOW_POINTER_LOCK("allow-pointer-lock"),

    /**
     * Allows popups (created, for example, by
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/Window/open"><code>Window.open()</code></a> or
     * <code>target="_blank"</code>). If this keyword is not used, popup display will silently fail.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy/sandbox#allow-popups">
     * developer.mozilla.org</a>
     */
    ALLOW_POPUPS("allow-popups"),

    /**
     * Allows a sandboxed document to open new windows without forcing the sandboxing flags upon them. This will
     * allow, for example, a third-party advertisement to be safely sandboxed without forcing the same restrictions
     * upon the page the ad links to.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy/sandbox#allow-popups-to-escape-sandbox">
     * developer.mozilla.org</a>
     */
    ALLOW_POPUPS_TO_ESCAPE_SANDBOX("allow-popups-to-escape-sandbox"),

    /**
     * Allows embedders to have control over whether an iframe can start a
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/PresentationRequest">presentation session</a>.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy/sandbox#allow-presentation">
     * developer.mozilla.org</a>
     */
    ALLOW_PRESENTATION("allow-presentation"),

    /**
     * Allows a sandboxed resource to retain its
     * <a href="https://developer.mozilla.org/en-US/docs/Glossary/Origin">origin</a>. A sandboxed resource is otherwise
     * treated as being from an
     * <a href="https://developer.mozilla.org/en-US/docs/Glossary/Origin#opaque_origin">opaque origin</a>, which ensures
     * that it will always fail
     * <a href="https://developer.mozilla.org/en-US/docs/Glossary/Same-origin_policy">same-origin policy</a> checks, and
     * hence cannot access
     * <a href="https://developer.mozilla.org/en-US/docs/Web/Security/Defenses/Same-origin_policy#cross-origin_data_storage_access">
     * <code>localstorage</code> and <code>document.cookie</code></a> and some JavaScript APIs. The
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Origin"><code>Origin</code></a> of
     * sandboxed resources without the <code>allow-same-origin</code> keyword is <code>null</code>.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy/sandbox#allow-same-origin">
     * developer.mozilla.org</a>
     */
    ALLOW_SAME_ORIGIN("allow-same-origin"),

    /**
     * Allows the page to run scripts (but not create pop-up windows). If this keyword is not used, this operation is
     * not allowed.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy/sandbox#allow-scripts">
     * developer.mozilla.org</a>
     */
    ALLOW_SCRIPTS("allow-scripts"),

    /**
     * Lets the resource request access to the parent's storage capabilities with the
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/Storage_Access_API">Storage Access API</a>.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy/sandbox#allow-storage-access-by-user-activation">
     * developer.mozilla.org</a>
     */
    ALLOW_STORAGE_ACCESS_BY_USER_ACTIVATION("allow-storage-access-by-user-activation"),

    /**
     * Lets the resource navigate the top-level browsing context (the one named <code>_top</code>).
     * <p>
     * <strong>Note:</strong> The <code>allow-top-navigation</code> and related values only make sense for embedded
     * documents (such as child iframes). For standalone documents, these values have no effect, as the top-level
     * browsing context is the document itself.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy/sandbox#allow-top-navigation">
     * developer.mozilla.org</a>
     */
    ALLOW_TOP_NAVIGATION("allow-top-navigation"),

    /**
     * Lets the resource navigate the top-level browsing context, but only if initiated by a user gesture.
     * <p>
     * <strong>Note:</strong> The <code>allow-top-navigation</code> and related values only make sense for embedded
     * documents (such as child iframes). For standalone documents, these values have no effect, as the top-level
     * browsing context is the document itself.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy/sandbox#allow-top-navigation-by-user-activation">
     * developer.mozilla.org</a>
     */
    ALLOW_TOP_NAVIGATION_BY_USER_ACTIVATION("allow-top-navigation-by-user-activation"),

    /**
     * Allows navigations to non-<code>http</code> protocols built into browser or
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/Navigator/registerProtocolHandler">registered by a
     * website</a>. This feature is also activated by <code>allow-popups</code> or <code>allow-top-navigation</code>
     * keyword.
     * <p>
     * <strong>Note:</strong> The <code>allow-top-navigation</code> and related values only make sense for embedded
     * documents (such as child iframes). For standalone documents, these values have no effect, as the top-level
     * browsing context is the document itself.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy/sandbox#allow-top-navigation-to-custom-protocols">
     * developer.mozilla.org</a>
     */
    ALLOW_TOP_NAVIGATION_TO_CUSTOM_PROTOCOLS("allow-top-navigation-to-custom-protocols");

    private final String string;

    @Override
    public String toString() {
        return string;
    }

    /**
     * An {@link ImmutableMap} of lowercased {@link #toString()} mapped to {@link SandboxFlag}.
     */
    public static final ImmutableMap<String, SandboxFlag> VALUES_OF_LOWERCASED_STRINGS = stream(values())
            .collect(toImmutableMap(value -> value.toString().toLowerCase(ROOT), identity()));

    /**
     * Gets the {@link SandboxFlag} for the given <code>string</code>.
     *
     * @param string the case-insensitive {@link #toString()}
     *
     * @return the {@link SandboxFlag}, or <code>null</code> if no mapping exists
     */
    public static @Nullable SandboxFlag forString(final String string) {
        return VALUES_OF_LOWERCASED_STRINGS.get(string.toLowerCase(ROOT));
    }
}
