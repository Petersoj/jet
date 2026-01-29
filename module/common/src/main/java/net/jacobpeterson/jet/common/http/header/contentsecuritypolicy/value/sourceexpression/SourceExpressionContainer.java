package net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.sourceexpression;

import com.google.errorprone.annotations.Immutable;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.sourceexpression.hash.HashSourceExpression;
import net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.sourceexpression.host.HostSourceExpression;
import net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.sourceexpression.nonce.NonceSourceExpression;
import net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.sourceexpression.predefined.PredefinedSourceExpression;
import net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.sourceexpression.scheme.SchemeSourceExpression;
import org.jspecify.annotations.NullMarked;

import static lombok.AccessLevel.PRIVATE;
import static lombok.EqualsAndHashCode.CacheStrategy.LAZY;

/**
 * {@link SourceExpressionContainer} is a container for {@link SourceExpression} and provides convenience methods for
 * casting to {@link SourceExpression} subclasses.
 */
@NullMarked
@Immutable
@RequiredArgsConstructor(access = PRIVATE) @EqualsAndHashCode(cacheStrategy = LAZY)
public final class SourceExpressionContainer {

    /**
     * Wraps the given {@link SourceExpression} in a {@link SourceExpressionContainer}.
     *
     * @param sourceExpression the {@link SourceExpression}
     *
     * @return the {@link SourceExpressionContainer}
     */
    public static SourceExpressionContainer wrap(final SourceExpression sourceExpression) {
        return new SourceExpressionContainer(sourceExpression);
    }

    private final SourceExpression sourceExpression;

    /**
     * @return the wrapped {@link SourceExpression}
     */
    public SourceExpression unwrap() {
        return sourceExpression;
    }

    /**
     * @return <code>{@link #unwrap()} instanceof {@link NonceSourceExpression}</code>
     */
    public boolean isNonce() {
        return sourceExpression instanceof NonceSourceExpression;
    }

    /**
     * @return {@link #unwrap()} casted to {@link NonceSourceExpression}
     */
    public NonceSourceExpression asNonce() {
        return (NonceSourceExpression) sourceExpression;
    }

    /**
     * @return <code>{@link #unwrap()} instanceof {@link HashSourceExpression}</code>
     */
    public boolean isHash() {
        return sourceExpression instanceof HashSourceExpression;
    }

    /**
     * @return {@link #unwrap()} casted to {@link HashSourceExpression}
     */
    public HashSourceExpression asHash() {
        return (HashSourceExpression) sourceExpression;
    }

    /**
     * @return <code>{@link #unwrap()} instanceof {@link HostSourceExpression}</code>
     */
    public boolean isHost() {
        return sourceExpression instanceof HostSourceExpression;
    }

    /**
     * @return {@link #unwrap()} casted to {@link HostSourceExpression}
     */
    public HostSourceExpression asHost() {
        return (HostSourceExpression) sourceExpression;
    }

    /**
     * @return <code>{@link #unwrap()} instanceof {@link SchemeSourceExpression}</code>
     */
    public boolean isScheme() {
        return sourceExpression instanceof SchemeSourceExpression;
    }

    /**
     * @return {@link #unwrap()} casted to {@link SchemeSourceExpression}
     */
    public SchemeSourceExpression asScheme() {
        return (SchemeSourceExpression) sourceExpression;
    }

    /**
     * @return <code>{@link #unwrap()} instanceof {@link PredefinedSourceExpression}</code>
     */
    public boolean isPredefined() {
        return sourceExpression instanceof PredefinedSourceExpression;
    }

    /**
     * @return {@link #unwrap()} casted to {@link PredefinedSourceExpression}
     */
    public PredefinedSourceExpression asPredefined() {
        return (PredefinedSourceExpression) sourceExpression;
    }

    @Override
    public String toString() {
        return sourceExpression.toString();
    }
}
