package net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.sourceexpression.hash;

import com.google.errorprone.annotations.Immutable;
import com.google.errorprone.annotations.concurrent.LazyInit;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.sourceexpression.SourceExpression;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static lombok.AccessLevel.PRIVATE;
import static lombok.EqualsAndHashCode.CacheStrategy.LAZY;

/**
 * {@link HashSourceExpression} is a {@link SourceExpression} for a hash.
 * <p>
 * This value consists of a string identifying a hash algorithm, followed by <code>-</code>, followed by a hash value.
 * The hash value may use any of the characters from
 * <a href="https://developer.mozilla.org/en-US/docs/Glossary/Base64#base64_characters">Base64</a> or
 * <a href="https://developer.mozilla.org/en-US/docs/Glossary/Base64#url_and_filename_safe_base64">URL-safe Base64</a>.
 * <ul>
 * <li>The hash algorithm identifier must be one of <code>sha256</code>, <code>sha384</code>, or
 * <code>sha512</code>.</li>
 * <li>The hash value is the base64-encoded
 * <a href="https://developer.mozilla.org/en-US/docs/Glossary/Hash_function">hash</a> of a <code>&lt;script&gt;</code>
 * or <code>&lt;style&gt;</code> resource, calculated using one of the following hash functions: SHA-256, SHA-384, or
 * SHA-512.</li>
 * </ul>
 * <p>For example: <code>'sha256-cd9827ad...'</code>
 * <p>
 * When the browser receives the document, it hashes the contents of any <code>&lt;script&gt;</code> and
 * <code>&lt;style&gt;</code> elements, compares the result with any hashes in the CSP directive, and loads the resource
 * only if there is a match.
 * <p>
 * If the element loads an external resource (for example, using the
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Elements/script#src"><code>src</code></a>
 * attribute), then the element must also have the
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Elements/script#integrity">
 * <code>integrity</code></a> attribute set.
 * <p>
 * If a directive contains a hash and <code>unsafe-inline</code>, then the browser ignores <code>unsafe-inline</code>.
 * <p>
 * See <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/CSP#hashes">Hashes</a> in the CSP guide for
 * more usage information.
 * <p>
 * <strong>Note:</strong> Hash source expressions are only applicable to
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Elements/script"><code>&lt;script&gt;</code></a>
 * and
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Elements/style"><code>&lt;style&gt;</code></a>
 * elements.
 *
 * @see
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy#hash_algorithm-hash_value">
 * developer.mozilla.org</a>
 * @see HashAlgorithm
 */
@NullMarked
@Immutable
@RequiredArgsConstructor(access = PRIVATE) @EqualsAndHashCode(cacheStrategy = LAZY)
public final class HashSourceExpression implements SourceExpression {

    /**
     * @return {@link #create(HashAlgorithm, String)} with
     * {@link HashAlgorithm#hashBytes(byte[])}
     */
    public static HashSourceExpression forBytesSource(final HashAlgorithm algorithm, final byte[] bytes) {
        return create(algorithm, algorithm.hashBytes(bytes));
    }

    /**
     * @return {@link #create(HashAlgorithm, String)} with
     * {@link HashAlgorithm#hashString(String)}
     */
    public static HashSourceExpression forStringSource(final HashAlgorithm algorithm, final String string) {
        return create(algorithm, algorithm.hashString(string));
    }

    /**
     * Creates a {@link HashSourceExpression}.
     *
     * @param algorithm the {@link #getAlgorithm()}
     * @param hash      the {@link #getHash()}
     *
     * @return the {@link HashSourceExpression}
     */
    public static HashSourceExpression create(final HashAlgorithm algorithm, final String hash) {
        return new HashSourceExpression(algorithm, hash);
    }

    private final @Getter HashAlgorithm algorithm;
    /**
     * The Base64-encoded hash {@link String} (without {@link #getPrefix()}).
     */
    private final @Getter String hash;

    private @LazyInit @EqualsAndHashCode.Exclude @Nullable String string;

    /**
     * @return the concatenation of {@link #getAlgorithm()} and <code>-</code>
     */
    public String getPrefix() {
        return algorithm + "-";
    }

    /**
     * @return the internally-cached concatenation of <code>'</code>, {@link #getPrefix()}, {@link #getHash()}, and
     * <code>'</code>
     */
    @Override
    public String toString() {
        if (string == null) {
            string = "'" + getPrefix() + hash + "'";
        }
        return string;
    }
}
