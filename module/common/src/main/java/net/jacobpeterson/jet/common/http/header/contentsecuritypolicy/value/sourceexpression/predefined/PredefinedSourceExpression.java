package net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.sourceexpression.predefined;

import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.sourceexpression.SourceExpression;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static java.util.Arrays.stream;
import static java.util.Locale.ROOT;
import static java.util.function.Function.identity;

/**
 * {@link PredefinedSourceExpression} is an enum for predefined source expressions.
 *
 * @see
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy#fetch_directive_syntax">
 * developer.mozilla.org</a>
 */
@NullMarked
@RequiredArgsConstructor
public enum PredefinedSourceExpression implements SourceExpression {

    /**
     * Indicators that the specific resource type should be completely blocked.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy#fetch_directive_syntax">
     * developer.mozilla.org</a>
     */
    NONE("'none'"),

    /**
     * Resources of the given type may only be loaded from the same
     * <a href="https://developer.mozilla.org/en-US/docs/Glossary/Origin">origin</a> as the document.
     * <p>
     * Secure upgrades are allowed. For example:
     * <ul>
     * <li>If the document is served from <code>http://example.com</code>, then a CSP of <code>'self'</code> will also
     * permit resources from <code>https://example.com</code>.</li>
     * <li>If the document is served from <code>ws://example.org</code>, then a CSP of <code>'self'</code> will also
     * permit resources from <code>wss://example.org</code>.</li>
     * </ul>
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy#self">
     * developer.mozilla.org</a>
     */
    SELF("'self'"),

    /**
     * By default, if a CSP contains a <code>default-src</code> or a <code>script-src</code> directive, then JavaScript
     * functions which evaluate their arguments as JavaScript are disabled. This includes
     * <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/eval">
     * <code>eval()</code></a>, the
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/Window/setTimeout#code"><code>code</code></a> argument
     * to <a href="https://developer.mozilla.org/en-US/docs/Web/API/Window/setTimeout"><code>setTimeout()</code></a>, or
     * the <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Function/Function">
     * <code>Function()</code></a> constructor.
     * <p>
     * The <code>trusted-types-eval</code> keyword can be used to undo this protection, but only when
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/Trusted_Types_API">Trusted Types</a> are enforced and
     * passed to these functions instead of strings. This allows dynamic evaluation of strings as JavaScript, but only
     * after inputs have been passed through a transformation function before it is injected, which has the chance to
     * <a href="https://developer.mozilla.org/en-US/docs/Web/Security/Attacks/XSS#sanitization">sanitize</a> the input
     * to remove potentially dangerous markup.
     * <p>
     * The <code>trusted-types-eval</code> must be used instead of
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy#unsafe-eval">
     * <code>'unsafe-eval'</code></a> when using these methods with trusted types. This ensures that access to the
     * methods is blocked on browsers that don't support trusted types.
     * <p>
     * <strong>Note:</strong> Developers should avoid using <code>trusted-types-eval</code> or these methods unless
     * absolutely necessary. Trusted types ensure that the input passes through a transformation function — they don't
     * ensure that the transformation makes the input safe (and this can be very hard to get right).
     * <p>
     * See <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/CSP#eval_and_similar_apis">
     * <code>eval()</code> and similar APIs</a> in the CSP guide for more usage information.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy#trusted-types-eval">
     * developer.mozilla.org</a>
     */
    TRUSTED_TYPES_EVAL("'trusted-types-eval'"),

    /**
     * By default, if a CSP contains a <code>default-src</code> or a <code>script-src</code> directive, then JavaScript
     * functions which evaluate their arguments as JavaScript are disabled. This includes
     * <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/eval">
     * <code>eval()</code></a>, the
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/Window/setTimeout#code"><code>code</code></a> argument
     * to <a href="https://developer.mozilla.org/en-US/docs/Web/API/Window/setTimeout"><code>setTimeout()</code></a>, or
     * the <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Function/Function">
     * <code>Function()</code></a> constructor.
     * <p>
     * The <code>unsafe-eval</code> keyword can be used to undo this protection, allowing dynamic evaluation of strings
     * as JavaScript.
     * <p>
     * <strong>Warning:</strong> Developers should avoid <code>'unsafe-eval'</code>, because it defeats much of the
     * purpose of having a CSP.
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy#trusted-types-eval">
     * 'trusted-types-eval'</a> provides a "potentially" safer alternative if using these methods is necessary.
     * <p>
     * See <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/CSP#eval_and_similar_apis">
     * <code>eval()</code> and similar APIs</a> in the CSP guide for more usage information.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy#unsafe-eval">
     * developer.mozilla.org</a>
     */
    UNSAFE_EVAL("'unsafe-eval'"),

    /**
     * By default, if a CSP contains a <code>default-src</code> or a <code>script-src</code> directive, then a page
     * won't be allowed to compile WebAssembly using functions like
     * <a href="https://developer.mozilla.org/en-US/docs/WebAssembly/Reference/JavaScript_interface/compileStreaming_static">
     * <code>WebAssembly.compileStreaming()</code></a>.
     * <p>
     * The <code>wasm-unsafe-eval</code> keyword can be used to undo this protection. This is a much safer alternative
     * to <code>'unsafe-eval'</code>, since it does not enable general evaluation of JavaScript.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy#wasm-unsafe-eval">
     * developer.mozilla.org</a>
     */
    WASM_UNSAFE_EVAL("'wasm-unsafe-eval'"),

    /**
     * By default, if a CSP contains a <code>default-src</code> or a <code>script-src</code> directive, then inline
     * JavaScript is not allowed to execute. This includes:
     * <ul>
     * <li>inline <code>&lt;script&gt;</code> tags</li>
     * <li>inline event handler attributes</li>
     * <li><code>javascript:</code> URLs.</li>
     * </ul>
     * <p>
     * Similarly, if a CSP contains <code>default-src</code> or a <code>style-src</code> directive, then inline CSS will
     * not be loaded, including:
     * <ul>
     * <li>inline <code>&lt;style&gt;</code> tags</li>
     * <li><a href="https://developer.mozilla.org/en-US/docs/Web/API/HTMLElement/style"><code>style</code></a>
     * attributes.</li>
     * </ul>
     * <p>
     * The <code>unsafe-inline</code> keyword can be used to undo this protection, allowing all these forms to be
     * loaded.
     * <p>
     * <strong>Warning:</strong> Developers should avoid <code>'unsafe-inline'</code>, because it defeats much of the
     * purpose of having a CSP.
     * <p>
     * See <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/CSP#inline_javascript">
     * Inline JavaScript</a> in the CSP guide for more usage information.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy#unsafe-inline">
     * developer.mozilla.org</a>
     */
    UNSAFE_INLINE("'unsafe-inline'"),

    /**
     * By default, if a CSP contains a <code>default-src</code> or a <code>script-src</code> directive, then inline
     * event handler attributes like <code>onclick</code> and inline <code>style</code> attributes are not allowed to
     * execute.
     * <p>
     * The <code>'unsafe-hashes'</code> expression allows the browser to use
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy#hash_algorithm-hash_value">
     * hash expressions</a> for inline event handlers and <code>style</code> attributes. For example, a CSP might
     * contain a directive like this: <code>script-src 'unsafe-hashes' 'sha256-cd9827ad...'</code>
     * <p>
     * If the hash value matches the hash of an inline event handler attribute value or of a <code>style</code>
     * attribute value, then the code will be allowed to execute.
     * <p>
     * <strong>Warning:</strong> The <code>'unsafe-hashes'</code> value is unsafe.
     * <p>
     * In particular, it enables an attack in which the content of the inline event handler attribute is injected into
     * the document as an inline <code>&lt;script&gt;</code> element. Suppose the inline event handler is:
     * <code>&lt;button onclick="transferAllMyMoney()"&gt;Transfer all my money&lt;/button&gt;</code>
     * <p>
     * If an attacker can inject an inline <code>&lt;script&gt;</code> element containing this code, the CSP will allow
     * it to execute automatically.
     * <p>
     * However, <code>'unsafe-hashes'</code> is much safer than <code>'unsafe-inline'</code>.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy#unsafe-hashes">
     * developer.mozilla.org</a>
     */
    UNSAFE_HASHES("'unsafe-hashes'"),

    /**
     * By default, if a CSP contains a <code>default-src</code> or a <code>script-src</code> directive, then inline
     * JavaScript is not allowed to execute. The <code>'inline-speculation-rules'</code> allows the browser to load
     * inline <code>&lt;script&gt;</code> elements that have a
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Elements/script/type"><code>type</code></a>
     * attribute of
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Elements/script/type/speculationrules">
     * <code>speculationrules</code></a>.
     * <p>
     * See the <a href="https://developer.mozilla.org/en-US/docs/Web/API/Speculation_Rules_API">
     * Speculation Rules API</a> for more information.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy#inline-speculation-rules">
     * developer.mozilla.org</a>
     */
    INLINE_SPECULATION_RULES("'inline-speculation-rules'"),

    /**
     * The <code>'strict-dynamic'</code> keyword makes the trust conferred on a script by a
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy#nonce-nonce_value">
     * nonce</a> or a
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy#hash_algorithm-hash_value">
     * hash</a> extend to scripts that this script dynamically loads, for example by creating new
     * <code>&lt;script&gt;</code> tags using
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/Document/createElement">
     * <code>Document.createElement()</code></a> and then inserting them into the document using
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/Node/appendChild"><code>Node.appendChild()</code></a>.
     * <p>
     * If this keyword is present in a directive, then the following source expression values are all ignored:
     * <ul>
     * <li><a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy#host-source">
     * &lt;host-source&gt;</a></li>
     * <li><a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy#scheme-source">
     * &lt;scheme-source&gt;</a></li>
     * <li><a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy#self">
     * <code>'self'</code></a></li>
     * <li><a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy#unsafe-inline">
     * <code>'unsafe-inline'</code></a></li>
     * </ul>
     * <p>
     * See <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/CSP#the_strict-dynamic_keyword">The
     * <code>strict-dynamic</code> keyword</a> in the CSP guide for more usage information.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy#strict-dynamic">
     * developer.mozilla.org</a>
     */
    STRICT_DYNAMIC("'strict-dynamic'"),

    /**
     * If this expression is included in a directive controlling scripts or styles, and the directive causes the browser
     * to block any inline scripts, inline styles, or event handler attributes, then the
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/CSP#violation_reporting">violation report</a>
     * that the browser generates will contain a
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/CSPViolationReportBody/sample"><code>sample</code></a>
     * property containing the first 40 characters of the blocked resource.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy#report-sample">
     * developer.mozilla.org</a>
     */
    REPORT_SAMPLE("'report-sample'");

    private final String string;

    @Override
    public String toString() {
        return string;
    }

    /**
     * An {@link ImmutableMap} of lowercased {@link #toString()} mapped to {@link PredefinedSourceExpression}.
     */
    public static final ImmutableMap<String, PredefinedSourceExpression> VALUES_OF_LOWERCASED_STRINGS = stream(values())
            .collect(toImmutableMap(value -> value.toString().toLowerCase(ROOT), identity()));

    /**
     * Gets the {@link PredefinedSourceExpression} for the given <code>string</code>.
     *
     * @param string the case-insensitive {@link #toString()}
     *
     * @return the {@link PredefinedSourceExpression}, or <code>null</code> if no mapping exists
     */
    public static @Nullable PredefinedSourceExpression forString(final String string) {
        return VALUES_OF_LOWERCASED_STRINGS.get(string.toLowerCase(ROOT));
    }
}
