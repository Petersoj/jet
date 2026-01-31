package net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.sourceexpression.nonce;

import com.google.errorprone.annotations.Immutable;
import com.google.errorprone.annotations.concurrent.LazyInit;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.sourceexpression.SourceExpression;
import net.jacobpeterson.jet.common.util.token.TokenUtil;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static lombok.AccessLevel.PRIVATE;
import static lombok.EqualsAndHashCode.CacheStrategy.LAZY;
import static net.jacobpeterson.jet.common.util.token.TokenUtil.generateToken;

/**
 * {@link NonceSourceExpression} is a {@link SourceExpression} for a nonce.
 * <p>
 * This value consists of the string <code>nonce-</code> followed by a
 * <a href="https://developer.mozilla.org/en-US/docs/Glossary/Nonce">nonce</a> value. The nonce value may use any of the
 * characters from <a href="https://developer.mozilla.org/en-US/docs/Glossary/Base64#base64_characters">Base64</a> or
 * <a href="https://developer.mozilla.org/en-US/docs/Glossary/Base64#url_and_filename_safe_base64">URL-safe Base64</a>.
 * <p>
 * This string is a random value that the server generates for every HTTP response. For example:
 * <code>'nonce-416d1177-4d12-4e3b-b7c9-f6c409789fb8'</code>
 * <p>
 * The server can then include the same value as the value of the <code>nonce</code> attribute of any
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Elements/script"><code>&lt;script&gt;</code></a>
 * or
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Elements/style"><code>&lt;style&gt;</code></a>
 * resources that they intend to load from the document.
 * <p>
 * The browser compares the value from the CSP directive against the value in the element attribute, and loads the
 * resource only if they match.
 * <p>
 * If a directive contains a nonce and <code>unsafe-inline</code>, then the browser ignores <code>unsafe-inline</code>.
 * <p>
 * See <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/CSP#nonces">Nonces</a> in the CSP guide for
 * more usage information.
 * <p>
 * <strong>Note:</strong> Nonce source expressions are only applicable to
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Elements/script"><code>&lt;script&gt;</code></a>
 * and
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Elements/style"><code>&lt;style&gt;</code></a>
 * elements.
 *
 * @see
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy#nonce-nonce_value">
 * developer.mozilla.org</a>
 */
@NullMarked
@Immutable
@RequiredArgsConstructor(access = PRIVATE) @EqualsAndHashCode(cacheStrategy = LAZY)
public final class NonceSourceExpression implements SourceExpression {

    /**
     * The prefix: <code>"nonce-"</code>
     */
    public static final String PREFIX = "nonce-";

    /**
     * @return {@link #create(String)} with a <code>length</code> of <code>16</code>
     */
    public static NonceSourceExpression generate() {
        return generate(16);
    }

    /**
     * @return {@link #create(String)} {@link TokenUtil#generateToken(int)}
     */
    public static NonceSourceExpression generate(final int length) {
        return create(generateToken(length));
    }

    /**
     * Creates a {@link NonceSourceExpression}.
     *
     * @param nonce the {@link #getNonce()}
     *
     * @return the {@link NonceSourceExpression}
     */
    public static NonceSourceExpression create(final String nonce) {
        return new NonceSourceExpression(nonce);
    }

    /** The nonce value {@link String} (without {@link #PREFIX}). */
    private final @Getter String nonce;
    private @LazyInit @EqualsAndHashCode.Exclude @Nullable String string;

    /**
     * @return the internally-cached concatenation of <code>'</code>, {@link #PREFIX}, {@link #getNonce()}, and
     * <code>'</code>
     */
    @Override
    public String toString() {
        if (string == null) {
            string = "'" + PREFIX + nonce + "'";
        }
        return string;
    }
}
