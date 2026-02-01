package net.jacobpeterson.jet.common.http.header.stricttransportsecurity;

import com.google.common.base.Splitter;
import com.google.errorprone.annotations.Immutable;
import com.google.errorprone.annotations.concurrent.LazyInit;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.jacobpeterson.jet.common.http.header.Header;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.Boolean.TRUE;
import static java.lang.Long.parseLong;
import static java.util.Locale.ROOT;
import static lombok.EqualsAndHashCode.CacheStrategy.LAZY;

/**
 * {@link StrictTransportSecurity} is an enum that represents a standardized HTTP
 * {@link Header#STRICT_TRANSPORT_SECURITY}.
 * <p>
 * The HTTP <strong><code>Strict-Transport-Security</code></strong>
 * <a href="https://developer.mozilla.org/en-US/docs/Glossary/Response_header">response header</a> (often abbreviated as
 * <a href="https://developer.mozilla.org/en-US/docs/Glossary/HSTS">HSTS</a>) informs browsers that the
 * <a href="https://developer.mozilla.org/en-US/docs/Glossary/Host">host</a> should only be accessed using HTTPS, and
 * that any future attempts to access it using HTTP should automatically be upgraded to HTTPS. Additionally, on future
 * connections to the host, the browser will not allow the user to bypass secure connection errors, such as an invalid
 * certificate. HSTS identifies a host by its domain name only.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Strict-Transport-Security">
 * developer.mozilla.org</a>
 * @see <a href="https://hstspreload.org">hstspreload.org</a>
 * @see Header#STRICT_TRANSPORT_SECURITY
 * @see #ONE_YEAR_INCLUDE_SUBDOMAINS_PRELOAD
 */
@NullMarked
@Immutable
@EqualsAndHashCode(cacheStrategy = LAZY)
@SuppressWarnings("NullAway") // TODO remove once NullAway false positives are fixed
public final class StrictTransportSecurity {

    /**
     * The max age key: <code>"max-age"</code>
     */
    public static final String MAX_AGE_KEY = "max-age";

    /**
     * The max age value delimiter: <code>"="</code>
     */
    public static final String MAX_AGE_VALUE_DELIMITER = "=";

    /**
     * The flag delimiter: <code>";"</code>
     */
    public static final String FLAG_DELIMITER = ";";

    /**
     * The <code>includeSubDomains</code> flag: <code>"includeSubDomains"</code>
     */
    public static final String INCLUDE_SUB_DOMAINS_FLAG = "includeSubDomains";
    private static final String INCLUDE_SUB_DOMAINS_FLAG_LOWERCASED = INCLUDE_SUB_DOMAINS_FLAG.toLowerCase(ROOT);

    /**
     * The <code>preload</code> flag: <code>"preload"</code>
     */
    public static final String PRELOAD_FLAG = "preload";

    /**
     * The number of seconds in one year: 31,536,000
     */
    public static final long SECONDS_IN_ONE_YEAR = 31536000;

    /**
     * A {@link StrictTransportSecurity} with {@link #getMaxAge()} set to {@link #SECONDS_IN_ONE_YEAR}.
     */
    public static final StrictTransportSecurity ONE_YEAR = StrictTransportSecurity.builder()
            .maxAge(SECONDS_IN_ONE_YEAR)
            .build();

    /**
     * A {@link StrictTransportSecurity} with {@link #getMaxAge()} set to {@link #SECONDS_IN_ONE_YEAR} and
     * {@link #isIncludeSubDomains()} set to <code>true</code>.
     */
    public static final StrictTransportSecurity ONE_YEAR_INCLUDE_SUBDOMAINS = StrictTransportSecurity.builder()
            .maxAge(SECONDS_IN_ONE_YEAR)
            .includeSubDomains(true)
            .build();

    /**
     * A {@link StrictTransportSecurity} with {@link #getMaxAge()} set to {@link #SECONDS_IN_ONE_YEAR},
     * {@link #isIncludeSubDomains()} set to <code>true</code>, and {@link #isPreload()} set to <code>true</code>.
     */
    public static final StrictTransportSecurity ONE_YEAR_INCLUDE_SUBDOMAINS_PRELOAD = StrictTransportSecurity.builder()
            .maxAge(SECONDS_IN_ONE_YEAR)
            .includeSubDomains(true)
            .preload(true)
            .build();

    private static final Splitter PARSE_MAX_AGE_VALUE_SPLITTER =
            Splitter.on(MAX_AGE_VALUE_DELIMITER).limit(2).trimResults().omitEmptyStrings();
    private static final Splitter PARSE_FLAG_SPLITTER =
            Splitter.on(FLAG_DELIMITER).trimResults().omitEmptyStrings();

    /**
     * Parses the given {@link Header#STRICT_TRANSPORT_SECURITY} value {@link String} into a
     * {@link StrictTransportSecurity}.
     *
     * @param strictTransportSecurity the {@link Header#STRICT_TRANSPORT_SECURITY} value {@link String}
     *
     * @return the {@link StrictTransportSecurity}
     *
     * @throws IllegalArgumentException thrown upon parsing failure
     */
    public static StrictTransportSecurity parse(final String strictTransportSecurity) throws IllegalArgumentException {
        final var flagSplit = PARSE_FLAG_SPLITTER.splitToList(strictTransportSecurity.toLowerCase(ROOT));
        final var maxAgeFlag = flagSplit.stream()
                .filter(flag -> flag.startsWith(MAX_AGE_KEY))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid HSTS: " + strictTransportSecurity));
        final var maxAgeValueSplit = PARSE_MAX_AGE_VALUE_SPLITTER.splitToList(maxAgeFlag);
        checkArgument(maxAgeValueSplit.size() == 2, "Invalid HSTS: %s", strictTransportSecurity);
        final var builder = builder();
        try {
            builder.maxAge(parseLong(maxAgeValueSplit.get(1)));
        } catch (final NumberFormatException numberFormatException) {
            throw new IllegalArgumentException(numberFormatException);
        }
        return builder
                .includeSubDomains(flagSplit.contains(INCLUDE_SUB_DOMAINS_FLAG_LOWERCASED))
                .preload(flagSplit.contains(PRELOAD_FLAG))
                .build();
    }

    /**
     * {@link Builder} is a builder class for {@link StrictTransportSecurity}.
     *
     * @see #builder()
     */
    public static final class Builder {}

    /**
     * The time, in seconds, that the browser should remember that a host is only to be accessed using HTTPS.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Strict-Transport-Security#max-ageexpire-time">
     * developer.mozilla.org</a>
     */
    private final @Getter long maxAge;

    /**
     * If this directive is specified, the HSTS policy applies to all subdomains of the host's domain as well.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Strict-Transport-Security#includesubdomains">
     * developer.mozilla.org</a>
     */
    private final @Getter boolean includeSubDomains;

    /**
     * See
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Strict-Transport-Security#preloading_strict_transport_security">
     * Preloading Strict Transport Security</a> for details. When using <code>preload</code>, the <code>max-age</code>
     * directive must be at least <code>31536000</code> (1 year), and the <code>includeSubDomains</code> directive must
     * be present.
     * <p>
     * From <a href="https://hstspreload.org">hstspreload.org</a>:
     * <p>
     * In order to be accepted to the HSTS preload list through this form, your site must satisfy the following set of
     * requirements:
     * <ol>
     * <li>Serve a valid <strong>certificate</strong>.</li>
     * <li><strong>Redirect</strong> from HTTP to HTTPS on the same host, if you are listening on port 80.</li>
     * <li>Serve all <strong>subdomains</strong> over HTTPS.
     * <ul>
     * <li>In particular, you must support HTTPS for the <code>www</code> subdomain if a DNS record for that subdomain
     * exists.</li>
     * <li><strong>Note:</strong> HSTS preloading applies to <em>all</em> subdomains, including internal subdomains that
     * are not publicly accessible.</li>
     * </ul>
     * </li>
     * <li>Serve an <strong>HSTS header</strong> on the base domain for HTTPS requests:
     * <ul>
     * <li>The <code>max-age</code> must be at least <code>31536000</code> seconds (1 year).</li>
     * <li>The <code>includeSubDomains</code> directive must be specified.</li>
     * <li>The <code>preload</code> directive must be specified.</li>
     * <li>If you are serving an additional redirect from your HTTPS site, that redirect must still have the HSTS header
     * (rather than the page it redirects to).</li>
     * </ul>
     * </li>
     * </ol>
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Strict-Transport-Security#preload">
     * developer.mozilla.org</a>
     */
    private final @Getter boolean preload;

    private @LazyInit @EqualsAndHashCode.Exclude @Nullable String string;

    /**
     * @param maxAge            the {@link #getMaxAge()}
     * @param includeSubDomains the {@link #isIncludeSubDomains()}
     * @param preload           the {@link #isPreload()}
     */
    @lombok.Builder(toBuilder = true)
    private StrictTransportSecurity(final long maxAge, final @Nullable Boolean includeSubDomains,
            final @Nullable Boolean preload) {
        this.maxAge = maxAge;
        this.includeSubDomains = TRUE.equals(includeSubDomains);
        this.preload = TRUE.equals(preload);
    }

    /**
     * @return internally-cached {@link String} value for {@link Header#STRICT_TRANSPORT_SECURITY}
     *
     * @see #parse(String)
     */
    @Override
    public String toString() {
        if (string == null) {
            final var string = new StringBuilder();
            string.append(MAX_AGE_KEY).append(MAX_AGE_VALUE_DELIMITER).append(maxAge);
            if (includeSubDomains) {
                string.append(FLAG_DELIMITER).append(' ').append(INCLUDE_SUB_DOMAINS_FLAG);
            }
            if (preload) {
                string.append(FLAG_DELIMITER).append(' ').append(PRELOAD_FLAG);
            }
            this.string = string.toString();
        }
        return string;
    }
}
