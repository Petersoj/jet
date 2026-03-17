package net.jacobpeterson.jet.common.http.header.etag;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingInputStream;
import com.google.errorprone.annotations.Immutable;
import com.google.errorprone.annotations.concurrent.LazyInit;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.jacobpeterson.jet.common.http.header.Header;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.hash.Hashing.murmur3_128;
import static com.google.common.io.ByteStreams.nullOutputStream;
import static java.nio.ByteBuffer.allocate;
import static lombok.AccessLevel.PRIVATE;
import static lombok.EqualsAndHashCode.CacheStrategy.LAZY;

/**
 * {@link ETag} is an immutable class that represents a standardized HTTP {@link Header#ETAG}.
 * <p>
 * The HTTP <strong><code>ETag</code></strong> (entity tag)
 * <a href="https://developer.mozilla.org/en-us/docs/Glossary/Response_header">response header</a> is an identifier for
 * a specific version of a resource. It lets <a href="https://developer.mozilla.org/en-us/docs/Web/HTTP/Guides/Caching">
 * caches</a> be more efficient and save bandwidth, as a web server does not need to resend a full response if the
 * content has not changed. Additionally, ETags help to prevent simultaneous updates of a resource from overwriting each
 * other
 * (<a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/ETag#avoiding_mid-air_collisions">
 * "mid-air collisions"</a>).
 * <p>
 * If the resource at a given URL changes, a new <code>ETag</code> value <em>must</em> be generated. A comparison of
 * them can determine whether two representations of a resource are the same.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/ETag">developer.mozilla.org</a>
 * @see Header#ETAG
 */
@NullMarked
@Immutable
@RequiredArgsConstructor(access = PRIVATE) @EqualsAndHashCode(onlyExplicitlyIncluded = true, cacheStrategy = LAZY)
public final class ETag {

    /**
     * The "weak" prefix: <code>"W/"</code>
     */
    public static final String WEAK_PREFIX = "W/";

    /**
     * Computes a strong {@link ETag} for the given entity content using {@link HashingInputStream} with
     * {@link Hashing#murmur3_128()}.
     *
     * @param content the content {@link InputStream} (always closed by this method)
     *
     * @return the strong {@link ETag}
     */
    @SuppressWarnings("UnstableApiUsage")
    public static ETag computeStrong(final InputStream content) {
        try (final var hashingInputStream = new HashingInputStream(murmur3_128(), content)) {
            hashingInputStream.transferTo(nullOutputStream());
            return builder()
                    .value(hashingInputStream.hash().toString())
                    .build();
        } catch (final IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    /**
     * Computes a weak {@link ETag} for the given entity attributes.
     *
     * @param name         the name
     * @param size         the size
     * @param lastModified the last modified epoch millis
     *
     * @return the weak {@link ETag}
     */
    public static ETag computeWeak(final String name, final long size, final long lastModified) {
        // Based on the same algorithm as `org.eclipse.jetty.http.EtagUtils.computeWeakEtag()`.
        final var hashBytes = allocate(Integer.BYTES + Long.BYTES + Long.BYTES);
        final var nameHashcode = name.hashCode();
        hashBytes.putInt(nameHashcode);
        hashBytes.putLong(size ^ nameHashcode);
        hashBytes.putLong(lastModified ^ nameHashcode);
        return builder()
                .weak()
                .value(Base64.getEncoder().withoutPadding().encodeToString(hashBytes.array()))
                .build();
    }

    /**
     * Parses the given {@link Header#ETAG} value {@link String} into an {@link ETag}.
     *
     * @param etag the {@link Header#ETAG} value {@link String}
     *
     * @return the {@link ETag}
     *
     * @throws IllegalArgumentException thrown upon parsing failure
     */
    public static ETag parse(final String etag) throws IllegalArgumentException {
        final var trimmed = etag.trim();
        final var builder = builder();
        if (trimmed.startsWith(WEAK_PREFIX)) {
            builder.weak();
        }
        final var firstQuoteIndex = trimmed.indexOf('"');
        checkArgument(firstQuoteIndex != -1, "Invalid ETag: %s", etag);
        final var lastQuoteIndex = trimmed.lastIndexOf('"');
        checkArgument(firstQuoteIndex != lastQuoteIndex, "Invalid ETag: %s", etag);
        builder.value(trimmed.substring(firstQuoteIndex + 1, lastQuoteIndex));
        return builder.build();
    }

    /**
     * Creates a {@link Builder}.
     *
     * @return the {@link Builder}
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * {@link Builder} is a builder class for {@link ETag}.
     *
     * @see #builder()
     */
    public static final class Builder {

        private boolean weak;
        private @Nullable String value;

        /**
         * @see #isWeak()
         */
        public Builder weak() {
            weak = true;
            return this;
        }

        /**
         * @see #getValue()
         */
        public Builder value(final String value) {
            this.value = value;
            return this;
        }

        /**
         * Builds this {@link Builder} into a new {@link ETag} instance.
         *
         * @return the built {@link ETag}
         *
         * @throws IllegalArgumentException thrown if {@link #value(String)} was never called
         */
        public ETag build() throws IllegalArgumentException {
            checkArgument(value != null, "`value()` was never called");
            return new ETag(weak, value);
        }
    }

    /**
     * <code>W/</code> (case-sensitive) indicates that a
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Conditional_requests#weak_validation">weak
     * validator</a> is used. Weak ETags are easy to generate, but are far less useful for comparisons. Strong
     * validators are ideal for comparisons but can be very difficult to generate efficiently. Weak <code>ETag</code>
     * values of two representations of the same resources might be semantically equivalent, but not byte-for-byte
     * identical. This means weak ETags prevent caching when
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Accept-Ranges">byte range
     * requests</a> are used, but strong ETags mean range requests can still be cached.
     */
    private final @Getter @EqualsAndHashCode.Include boolean weak;

    /**
     * The unquoted ETag value.
     * <p>
     * Entity tag that uniquely represents the requested resource. It is a string of
     * <a href="https://developer.mozilla.org/en-US/docs/Glossary/ASCII">ASCII</a> characters placed between double
     * quotes, like <code>"675af34563dc-tr34"</code>. The method by which <code>ETag</code> values are generated is not
     * specified. Typically, the ETag value is a hash of the content, a hash of the last modification timestamp, or just
     * a revision number. For example, a wiki engine can use a hexadecimal hash of the documentation article content.
     */
    private final @Getter @EqualsAndHashCode.Include String value;

    private @LazyInit @Nullable String string;

    /**
     * @return internally-cached {@link String} value for {@link Header#ETAG}
     *
     * @see #parse(String)
     */
    @Override
    public String toString() {
        if (string == null) {
            final var string = new StringBuilder();
            if (weak) {
                string.append(WEAK_PREFIX);
            }
            string.append('"').append(value).append('"');
            this.string = string.toString();
        }
        return string;
    }
}
