package net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.sourceexpression.host;

import com.google.errorprone.annotations.Immutable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.sourceexpression.SourceExpression;
import net.jacobpeterson.jet.common.http.url.Url;
import org.jspecify.annotations.NullMarked;

import static lombok.AccessLevel.PRIVATE;
import static lombok.EqualsAndHashCode.CacheStrategy.LAZY;

/**
 * {@link HostSourceExpression} is a {@link SourceExpression} for a host.
 * <p>
 * The <a href="https://developer.mozilla.org/en-US/docs/Web/URI">URL</a> or IP address of a
 * <a href="https://developer.mozilla.org/en-US/docs/Glossary/Host">host</a> that is a valid source for the resource.
 * <p>
 * The scheme, port number, and path are optional.
 * <p>
 * If the scheme is omitted, the scheme of the document's origin is used.
 * <p>
 * When matching schemes, secure upgrades are allowed. For example:
 * <ul>
 * <li><code>http://example.com</code> will also permit resources from <code>https://example.com</code></li>
 * <li><code>ws://example.org</code> will also permit resources from <code>wss://example.org</code>.</li>
 * </ul>
 * <p>
 * Wildcards (<code>'*'</code>) can be used for subdomains, host address, and port number, indicating that all legal
 * values of each are valid. For example:
 * <ul>
 * <li><code>http://*.example.com</code> permits resources from any subdomain of <code>example.com</code>, over HTTP or
 * HTTPS.</li>
 * </ul>
 * <p>
 * Paths that end in <code>/</code> match any path they are a prefix of. For example:
 * <ul>
 * <li><code>example.com/api/</code> will permit resources from <code>example.com/api/users/new</code>.</li>
 * </ul>
 * <p>
 * Paths that do not end in <code>/</code> are matched exactly. For example:
 * <ul>
 * <li><code>https://example.com/file.js</code> permits resources from <code>https://example.com/file.js</code> but not
 * <code>https://example.com/file.js/file2.js</code>.</li>
 * </ul>
 *
 * @see
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy#host-source">
 * developer.mozilla.org</a>
 */
@NullMarked
@Immutable
@RequiredArgsConstructor(access = PRIVATE) @EqualsAndHashCode(cacheStrategy = LAZY)
public final class HostSourceExpression implements SourceExpression {

    /**
     * The wildcard (<code>*</code>) token.
     */
    public static final String WILDCARD_TOKEN = "*";

    /**
     * A {@link HostSourceExpression} that allows any URL except <code>data:</code>, <code>blob:</code>, and
     * <code>filesystem:</code> schemes.
     */
    public static final HostSourceExpression WILDCARD = create(WILDCARD_TOKEN);

    /**
     * Create a {@link HostSourceExpression}.
     *
     * @param host the {@link #getHost()}
     *
     * @return the {@link HostSourceExpression}
     */
    public static HostSourceExpression create(final String host) {
        return new HostSourceExpression(host);
    }

    private final @Getter String host;

    /**
     * @return {@link #equals(Object)} {@link #WILDCARD}
     */
    public boolean isWildcard() {
        return equals(WILDCARD);
    }

    /**
     * @return {@link #getHost()} {@link String#contains(CharSequence)} {@link #WILDCARD_TOKEN}
     */
    public boolean hasWildcard() {
        return host.contains(WILDCARD_TOKEN);
    }

    /**
     * @return {@link Url#parse(String)} {@link #getHost()}
     */
    public Url toUrl() {
        return Url.parse(host);
    }

    @Override
    public String toString() {
        return host;
    }
}
