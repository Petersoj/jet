package net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.requiretrustedtypesfor;

import lombok.RequiredArgsConstructor;
import net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.key.PolicyDirectiveKey;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;

import static java.util.Arrays.stream;
import static java.util.Locale.ROOT;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toUnmodifiableMap;

/**
 * {@link RequireTrustedTypesFor} is an enum for {@link PolicyDirectiveKey#REQUIRE_TRUSTED_TYPES_FOR} values.
 * <p>
 * The HTTP <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy">
 * <code>Content-Security-Policy</code></a> (CSP) <strong><code>require-trusted-types-for</code></strong> directive
 * instructs user agents to control the data passed to DOM XSS sink functions, like
 * <a href="https://developer.mozilla.org/en-US/docs/Web/API/Element/innerHTML"><code>Element.innerHTML</code></a>
 * setter.
 * <p>
 * When used, those functions only accept non-spoofable, typed values created by
 * <a href="https://developer.mozilla.org/en-US/docs/Web/API/Trusted_Types_API">Trusted Type</a> policies, and reject
 * strings. Together with the <strong>
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy/trusted-types">
 * <code>trusted-types</code></a></strong> directive, which guards creation of Trusted Type policies, this allows
 * authors to define rules guarding writing values to the DOM and thus reducing the DOM XSS attack surface to small,
 * isolated parts of the web application codebase, facilitating their monitoring and code review.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy/sandbox">
 * developer.mozilla.org</a>
 */
@NullMarked
@RequiredArgsConstructor
public enum RequireTrustedTypesFor {

    /**
     * Disallows using strings with DOM XSS injection sink functions, and requires matching types created by Trusted
     * Type policies.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy/require-trusted-types-for">
     * developer.mozilla.org</a>
     */
    SCRIPT("'script'");

    private final String string;

    @Override
    public String toString() {
        return string;
    }

    /**
     * An unmodifiable {@link Map} of lowercased {@link #toString()} mapped to {@link RequireTrustedTypesFor}.
     */
    public static final Map<String, RequireTrustedTypesFor> VALUES_OF_LOWERCASED_STRINGS = stream(values())
            .collect(toUnmodifiableMap(value -> value.toString().toLowerCase(ROOT), identity()));

    /**
     * Gets the {@link RequireTrustedTypesFor} for the given <code>string</code>.
     *
     * @param string the case-insensitive {@link #toString()}
     *
     * @return the {@link RequireTrustedTypesFor}, or <code>null</code> if no mapping exists
     */
    public static @Nullable RequireTrustedTypesFor forString(final String string) {
        return VALUES_OF_LOWERCASED_STRINGS.get(string.toLowerCase(ROOT));
    }
}
