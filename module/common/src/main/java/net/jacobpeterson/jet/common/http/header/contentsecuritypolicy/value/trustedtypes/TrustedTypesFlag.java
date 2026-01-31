package net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.trustedtypes;

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
 * {@link TrustedTypesFlag} is an enum for {@link PolicyDirectiveKey#TRUSTED_TYPES} flags.
 * <p>
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy/sandbox">
 * developer.mozilla.org</a>
 */
@NullMarked
@RequiredArgsConstructor
public enum TrustedTypesFlag {

    /**
     * Disallows creating any Trusted Type policy (same as not specifying any <em>&lt;policyName&gt;</em>).
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy/trusted-types#none">
     * developer.mozilla.org</a>
     */
    NONE("'none'"),

    /**
     * Allows for creating policies with a name that was already used.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy/trusted-types#allow-duplicates">
     * developer.mozilla.org</a>
     */
    ALLOW_DUPLICATES("'allow-duplicates'");

    private final String string;

    @Override
    public String toString() {
        return string;
    }

    /**
     * An {@link ImmutableMap} of lowercased {@link #toString()} mapped to {@link TrustedTypesFlag}.
     */
    public static final ImmutableMap<String, TrustedTypesFlag> VALUES_OF_LOWERCASED_STRINGS = stream(values())
            .collect(toImmutableMap(value -> value.toString().toLowerCase(ROOT), identity()));

    /**
     * Gets the {@link TrustedTypesFlag} for the given <code>string</code>.
     *
     * @param string the case-insensitive {@link #toString()}
     *
     * @return the {@link TrustedTypesFlag}, or <code>null</code> if no mapping exists
     */
    public static @Nullable TrustedTypesFlag forString(final String string) {
        return VALUES_OF_LOWERCASED_STRINGS.get(string.toLowerCase(ROOT));
    }
}
