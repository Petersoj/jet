package net.jacobpeterson.jet.common.http.header.authorization;

import com.google.common.base.Splitter;
import com.google.errorprone.annotations.Immutable;
import com.google.errorprone.annotations.concurrent.LazyInit;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.jacobpeterson.jet.common.http.header.Header;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static com.google.common.base.Preconditions.checkArgument;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Locale.ROOT;
import static lombok.EqualsAndHashCode.CacheStrategy.LAZY;

/**
 * {@link BasicAuthentication} is an immutable class that represents a standardized HTTP {@link Header#AUTHORIZATION}
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Authorization#basic_authentication">
 * basic authentication</a>.
 * <p>
 * The HTTP <strong><code>Authorization</code></strong>
 * <a href="https://developer.mozilla.org/en-US/docs/Glossary/Request_header">request header</a> can be used to provide
 * credentials that authenticate a user agent with a server, allowing access to protected resources.
 * <p>
 * The <code>Authorization</code> header is usually, but not always, sent after the user agent first attempts to request
 * a protected resource without credentials. The server responds with a
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/401"><code>401 Unauthorized</code></a>
 * message that includes at least one
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/WWW-Authenticate">
 * <code>WWW-Authenticate</code></a> header. This header indicates the authentication schemes that can be used to access
 * the resource and any additional information needed by the client to use them. The user-agent should select the most
 * secure authentication scheme that it supports from those offered, prompt the user for their credentials, and then
 * re-request the resource with the encoded credentials in the <code>Authorization</code> header.
 * <p>
 * This header is stripped from cross-origin redirects.
 * <p>
 * <strong>Note:</strong> This header is part of the
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Authentication#the_general_http_authentication_framework">
 * General HTTP authentication framework</a>. It can be used with a number of
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Authentication#authentication_schemes">
 * authentication schemes</a>.
 * <p>
 * For <code>Basic</code> authentication, the credentials are constructed by first combining the username and the
 * password with a colon (e.g., <code>aladdin:opensesame</code>), and then by encoding the resulting string in
 * <a href="https://developer.mozilla.org/en-US/docs/Glossary/Base64"><code>base64</code></a> (e.g.,
 * <code>YWxhZGRpbjpvcGVuc2VzYW1l</code>). <code>Authorization: Basic YWxhZGRpbjpvcGVuc2VzYW1l</code>
 * <p>
 * <strong>Warning:</strong> <a href="https://developer.mozilla.org/en-US/docs/Glossary/Base64">Base64</a>-encoding can
 * easily be reversed to obtain the original name and password, so <code>Basic</code> authentication offers no
 * cryptographic security. <a href="https://developer.mozilla.org/en-US/docs/Glossary/HTTPS">HTTPS</a> is always
 * recommended when using authentication, but is even more so when using <code>Basic</code> authentication.
 * <p>
 * See also <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Authentication">HTTP authentication</a>
 * for examples on how to configure Apache or Nginx servers to password protect your site with HTTP basic
 * authentication.
 *
 * @see
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Authorization#basic_authentication">developer.mozilla.org</a>.
 * @see Header#AUTHORIZATION
 */
@NullMarked
@Immutable
@EqualsAndHashCode(cacheStrategy = LAZY)
@SuppressWarnings("NullAway") // TODO remove once NullAway false positives are fixed
public final class BasicAuthentication {

    /**
     * The auth scheme: <code>"Basic"</code>
     */
    public static final String AUTH_SCHEME = "Basic";

    /**
     * {@link #AUTH_SCHEME} {@link String#toLowerCase()}.
     */
    public static final String AUTH_SCHEME_LOWERCASE = AUTH_SCHEME.toLowerCase(ROOT);

    /**
     * The {@link #getUsername()}-{@link #getPassword()} delimiter: <code>":"</code>
     */
    public static final String USERNAME_PASSWORD_DELIMITER = ":";

    private static final Splitter PARSE_USERNAME_PASSWORD_SPLITTER =
            Splitter.on(USERNAME_PASSWORD_DELIMITER).limit(2);

    /**
     * @return {@link #parse(String, Charset)} with <code>charset</code> set to {@link StandardCharsets#UTF_8}
     */
    public static BasicAuthentication parse(final String basicAuthentication) {
        return parse(basicAuthentication, UTF_8);
    }

    /**
     * Parses the given {@link Header#AUTHORIZATION} basic authentication value {@link String} into a
     * {@link BasicAuthentication}.
     *
     * @param basicAuthentication the {@link Header#AUTHORIZATION} basic authentication value {@link String}
     * @param charset             the {@link #getCharset()}
     *
     * @return the {@link BasicAuthentication}
     *
     * @throws IllegalArgumentException thrown upon parsing failure
     * @see #toString() #toString()
     */
    public static BasicAuthentication parse(final String basicAuthentication, final Charset charset)
            throws IllegalArgumentException {
        final var indexOfAuthScheme = basicAuthentication.toLowerCase(ROOT).indexOf(AUTH_SCHEME_LOWERCASE);
        checkArgument(indexOfAuthScheme != -1, "Invalid basic authentication: %s", basicAuthentication);
        final var usernamePasswordSplit = PARSE_USERNAME_PASSWORD_SPLITTER.splitToList(
                new String(Base64.getDecoder().decode(
                        basicAuthentication.substring(indexOfAuthScheme + AUTH_SCHEME.length()).trim()), charset));
        checkArgument(usernamePasswordSplit.size() == 2, "Invalid basic authentication: %s", basicAuthentication);
        return builder()
                .username(usernamePasswordSplit.getFirst())
                .password(usernamePasswordSplit.get(1))
                .charset(charset)
                .build();
    }

    /**
     * The username.
     */
    private final @Getter String username;

    /**
     * The password.
     */
    private final @Getter String password;

    /**
     * The {@link Charset} of {@link #getUsername()} and {@link #getPassword()}.
     */
    private final @Getter Charset charset;

    private @LazyInit @EqualsAndHashCode.Exclude @Nullable String string;

    /**
     * @param username the {@link #getUsername()}
     * @param password the {@link #getPassword()}
     * @param charset  the {@link #getCharset()}, or <code>null</code> for {@link StandardCharsets#UTF_8}
     */
    @lombok.Builder(toBuilder = true)
    private BasicAuthentication(final String username, final String password, final @Nullable Charset charset) {
        this.username = username;
        this.password = password;
        this.charset = charset == null ? UTF_8 : charset;
    }

    /**
     * @return internally-cached {@link String} value for {@link Header#AUTHORIZATION}
     *
     * @see #parse(String)
     */
    @Override
    public String toString() {
        if (string == null) {
            string = AUTH_SCHEME + " " + Base64.getEncoder().encodeToString(
                    (username + USERNAME_PASSWORD_DELIMITER + password).getBytes(charset));
        }
        return string;
    }
}
