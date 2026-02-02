package net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.sourceexpression;

import com.google.errorprone.annotations.Immutable;
import net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.ContentSecurityPolicy;
import net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.sourceexpression.hash.HashSourceExpression;
import net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.sourceexpression.hash.HashAlgorithm;
import net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.sourceexpression.host.HostSourceExpression;
import net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.sourceexpression.nonce.NonceSourceExpression;
import net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.sourceexpression.predefined.PredefinedSourceExpression;
import net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.sourceexpression.scheme.SchemeSourceExpression;
import org.jspecify.annotations.NullMarked;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * {@link SourceExpression} is an interface for source expression types.
 *
 * @see ContentSecurityPolicy
 */
@NullMarked
@Immutable
public interface SourceExpression {

    /**
     * Parses the given source expression {@link String} into a {@link SourceExpression} subclass instance.
     *
     * @param sourceExpression the source expression {@link String}
     *
     * @return the {@link SourceExpression}
     *
     * @throws IllegalArgumentException thrown upon parsing failure
     * @see SourceExpressionContainer
     */
    static SourceExpression parse(final String sourceExpression) throws IllegalArgumentException {
        if (sourceExpression.startsWith("'") && sourceExpression.endsWith("'")) {
            final var predefined = PredefinedSourceExpression.forString(sourceExpression);
            if (predefined != null) {
                return predefined;
            }
            final var unquoted = sourceExpression.substring(1, sourceExpression.length() - 1);
            if (unquoted.startsWith(NonceSourceExpression.PREFIX)) {
                return NonceSourceExpression.create(unquoted.substring(NonceSourceExpression.PREFIX.length()));
            }
            final var hyphenIndex = unquoted.indexOf('-');
            checkArgument(hyphenIndex != -1, "Invalid source expression: %s", sourceExpression);
            final var hashAlgorithm = HashAlgorithm.forString(unquoted.substring(0, hyphenIndex));
            checkArgument(hashAlgorithm != null, "Invalid source expression: %s", sourceExpression);
            return HashSourceExpression.create(hashAlgorithm, unquoted.substring(hyphenIndex + 1));
        }
        if (sourceExpression.endsWith(SchemeSourceExpression.SUFFIX)) {
            return SchemeSourceExpression.create(sourceExpression.substring(0, sourceExpression.length() - 1));
        }
        return HostSourceExpression.create(sourceExpression);
    }
}
