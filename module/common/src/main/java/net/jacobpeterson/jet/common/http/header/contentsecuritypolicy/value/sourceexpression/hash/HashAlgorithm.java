package net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.sourceexpression.hash;

import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import lombok.Getter;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.function.Function;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static com.google.common.hash.Hashing.sha256;
import static com.google.common.hash.Hashing.sha384;
import static com.google.common.hash.Hashing.sha512;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.stream;
import static java.util.Locale.ROOT;
import static java.util.function.Function.identity;

/**
 * {@link HashAlgorithm} is an enum for the algorithm of {@link HashSourceExpression}.
 */
@NullMarked
public enum HashAlgorithm {

    /**
     * The {@link Hashing#sha256() SHA-256} hash function.
     */
    SHA_256("sha256", sha256()),

    /**
     * The {@link Hashing#sha384() SHA-384} hash function.
     */
    SHA_384("sha384", sha384()),

    /**
     * The {@link Hashing#sha512() SHA-512} hash function.
     */
    SHA_512("sha512", sha512());

    private final String string;

    /**
     * The {@link Function} to return the Base64-encoded hash {@link String} a given <code>byte[]</code>.
     */
    private final @SuppressWarnings("ImmutableEnumChecker") @Getter Function<byte[], String> function;

    HashAlgorithm(final String string, final HashFunction hashFunction) {
        this.string = string;
        this.function = (bytes) -> Base64.getEncoder().encodeToString(hashFunction.hashBytes(bytes).asBytes());
    }

    /**
     * @return {@link #getFunction()} {@link Function#apply(Object)}
     */
    public String hashBytes(final byte[] bytes) {
        return function.apply(bytes);
    }

    /**
     * @return {@link #hashBytes(byte[])} {@link String#getBytes()} {@link StandardCharsets#UTF_8}
     */
    public String hashString(final String string) {
        return hashBytes(string.getBytes(UTF_8));
    }

    @Override
    public String toString() {
        return string;
    }

    /**
     * An {@link ImmutableMap} of lowercased {@link #toString()} mapped to {@link HashAlgorithm}.
     */
    public static final ImmutableMap<String, HashAlgorithm> VALUES_OF_LOWERCASED_STRINGS =
            stream(values()).collect(toImmutableMap(value -> value.toString().toLowerCase(ROOT), identity()));

    /**
     * Gets the {@link HashAlgorithm} for the given <code>string</code>.
     *
     * @param string the case-insensitive {@link #toString()}
     *
     * @return the {@link HashAlgorithm}, or <code>null</code> if no mapping exists
     */
    public static @Nullable HashAlgorithm forString(final String string) {
        return VALUES_OF_LOWERCASED_STRINGS.get(string.toLowerCase(ROOT));
    }
}
