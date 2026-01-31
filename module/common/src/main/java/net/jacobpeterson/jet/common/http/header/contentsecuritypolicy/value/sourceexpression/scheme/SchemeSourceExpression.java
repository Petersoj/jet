package net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.sourceexpression.scheme;

import com.google.errorprone.annotations.Immutable;
import com.google.errorprone.annotations.concurrent.LazyInit;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.sourceexpression.SourceExpression;
import net.jacobpeterson.jet.common.http.url.Scheme;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static lombok.AccessLevel.PRIVATE;
import static lombok.EqualsAndHashCode.CacheStrategy.LAZY;

/**
 * {@link SchemeSourceExpression} is a {@link SourceExpression} for a scheme.
 * <p>
 * A <a href="https://developer.mozilla.org/en-US/docs/Web/URI/Reference/Schemes">scheme</a>, such as
 * <code>https:</code>. The colon is required.
 * <p>
 * Secure upgrades are allowed, so:
 * <ul>
 * <li><code>http:</code> will also permit resources loaded using HTTPS</li>
 * <li><code>ws:</code> will also permit resources loaded using WSS.</li>
 * </ul>
 *
 * @see
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy#scheme-source">
 * developer.mozilla.org</a>
 */
@NullMarked
@Immutable
@RequiredArgsConstructor(access = PRIVATE) @EqualsAndHashCode(cacheStrategy = LAZY)
public final class SchemeSourceExpression implements SourceExpression {

    /**
     * The suffix: <code>":"</code>
     */
    public static final String SUFFIX = ":";

    /**
     * @return {@link #create(String)} {@link Scheme#toString()}
     *
     * @see #toScheme()
     */
    public static SchemeSourceExpression forScheme(final Scheme scheme) {
        return create(scheme.toString());
    }

    /**
     * Creates a {@link SchemeSourceExpression}.
     *
     * @param scheme the {@link #getScheme()}
     *
     * @return the {@link SchemeSourceExpression}
     */
    public static SchemeSourceExpression create(final String scheme) {
        return new SchemeSourceExpression(scheme);
    }

    /** The scheme value {@link String} (without {@link #SUFFIX}). */
    private final @Getter String scheme;
    private @LazyInit @EqualsAndHashCode.Exclude @Nullable String string;

    /**
     * @return {@link Scheme#forString(String)} {@link #getScheme()}
     */
    public @Nullable Scheme toScheme() {
        return Scheme.forString(scheme);
    }

    /**
     * @return the internally-cached concatenation of {@link #getScheme()} and {@link #SUFFIX}
     */
    @Override
    public String toString() {
        if (string == null) {
            string = scheme + SUFFIX;
        }
        return string;
    }
}
