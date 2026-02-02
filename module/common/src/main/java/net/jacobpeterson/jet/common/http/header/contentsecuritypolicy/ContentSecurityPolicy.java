package net.jacobpeterson.jet.common.http.header.contentsecuritypolicy;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder.SetMultimapBuilder;
import com.google.common.collect.SetMultimap;
import com.google.errorprone.annotations.Immutable;
import com.google.errorprone.annotations.concurrent.LazyInit;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.jacobpeterson.jet.common.http.header.Header;
import net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.key.PolicyDirectiveKey;
import net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.requiretrustedtypesfor.RequireTrustedTypesFor;
import net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.sandbox.SandboxFlag;
import net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.sourceexpression.SourceExpression;
import net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.sourceexpression.SourceExpressionContainer;
import net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.trustedtypes.TrustedTypesFlag;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static com.google.common.collect.ImmutableSetMultimap.flatteningToImmutableSetMultimap;
import static java.lang.String.join;
import static java.util.Locale.ROOT;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.joining;
import static lombok.AccessLevel.PRIVATE;
import static lombok.EqualsAndHashCode.CacheStrategy.LAZY;
import static net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.key.PolicyDirectiveKey.BASE_URI;
import static net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.key.PolicyDirectiveKey.BLOCK_ALL_MIXED_CONTENT;
import static net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.key.PolicyDirectiveKey.CHILD_SRC;
import static net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.key.PolicyDirectiveKey.CONNECT_SRC;
import static net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.key.PolicyDirectiveKey.DEFAULT_SRC;
import static net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.key.PolicyDirectiveKey.FENCED_FRAME_SRC;
import static net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.key.PolicyDirectiveKey.FONT_SRC;
import static net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.key.PolicyDirectiveKey.FORM_ACTION;
import static net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.key.PolicyDirectiveKey.FRAME_ANCESTORS;
import static net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.key.PolicyDirectiveKey.FRAME_SRC;
import static net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.key.PolicyDirectiveKey.IMG_SRC;
import static net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.key.PolicyDirectiveKey.MANIFEST_SRC;
import static net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.key.PolicyDirectiveKey.MEDIA_SRC;
import static net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.key.PolicyDirectiveKey.OBJECT_SRC;
import static net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.key.PolicyDirectiveKey.PREFETCH_SRC;
import static net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.key.PolicyDirectiveKey.REPORT_TO;
import static net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.key.PolicyDirectiveKey.REPORT_URI;
import static net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.key.PolicyDirectiveKey.REQUIRE_TRUSTED_TYPES_FOR;
import static net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.key.PolicyDirectiveKey.SANDBOX;
import static net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.key.PolicyDirectiveKey.SCRIPT_SRC;
import static net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.key.PolicyDirectiveKey.SCRIPT_SRC_ATTR;
import static net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.key.PolicyDirectiveKey.SCRIPT_SRC_ELEM;
import static net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.key.PolicyDirectiveKey.STYLE_SRC;
import static net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.key.PolicyDirectiveKey.STYLE_SRC_ATTR;
import static net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.key.PolicyDirectiveKey.STYLE_SRC_ELEM;
import static net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.key.PolicyDirectiveKey.TRUSTED_TYPES;
import static net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.key.PolicyDirectiveKey.UPGRADE_INSECURE_REQUESTS;
import static net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.key.PolicyDirectiveKey.WORKER_SRC;

/**
 * {@link ContentSecurityPolicy} is an immutable class that represents a standardized HTTP
 * {@link Header#CONTENT_SECURITY_POLICY}.
 * <p>
 * The HTTP <strong><code>Content-Security-Policy</code></strong> response header allows website administrators to
 * control resources the user agent is allowed to load for a given page. With a few exceptions, policies mostly involve
 * specifying server origins and script endpoints. This helps guard against
 * <a href="https://developer.mozilla.org/en-US/docs/Glossary/Cross-site_scripting">cross-site scripting</a> attacks.
 * <p>
 * See the <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/CSP">Content Security Policy (CSP)</a>
 * guide for details about how a CSP is delivered to the browser, what it looks like, along with use cases and
 * deployment strategies.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Content-Security-Policy">
 * developer.mozilla.org</a>
 * @see <a href="https://content-security-policy.com">content-security-policy.com</a>
 * @see
 * <a href="https://github.com/OWASP/CheatSheetSeries/blob/master/cheatsheets/Content_Security_Policy_Cheat_Sheet.md">
 * github.com/OWASP/.../Content_Security_Policy_Cheat_Sheet.md</a>
 * @see Header#CONTENT_SECURITY_POLICY
 */
@NullMarked
@Immutable
@RequiredArgsConstructor(access = PRIVATE) @EqualsAndHashCode(onlyExplicitlyIncluded = true, cacheStrategy = LAZY)
@SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "OptionalAssignedToNull"})
public final class ContentSecurityPolicy {

    /**
     * The {@link #getDirectives()} delimiter: <code>";"</code>
     */
    public static final String DIRECTIVE_DELIMITER = ";";

    private static final Splitter PARSE_DIRECTIVE_SPLITTER =
            Splitter.on(DIRECTIVE_DELIMITER).trimResults().omitEmptyStrings();
    private static final Splitter PARSE_DIRECTIVE_VALUES_SPLITTER =
            Splitter.on(' ').trimResults().omitEmptyStrings();

    /**
     * Parses the given {@link Header#CONTENT_SECURITY_POLICY} value {@link String} into a
     * {@link ContentSecurityPolicy}.
     *
     * @param contentSecurityPolicy the {@link Header#CONTENT_SECURITY_POLICY} value {@link String}
     *
     * @return the {@link ContentSecurityPolicy}
     *
     * @see #toString()
     */
    public static ContentSecurityPolicy parse(final String contentSecurityPolicy) {
        return new ContentSecurityPolicy(PARSE_DIRECTIVE_SPLITTER.splitToStream(contentSecurityPolicy)
                .map(PARSE_DIRECTIVE_VALUES_SPLITTER::splitToList)
                .collect(flatteningToImmutableSetMultimap(strings -> strings.getFirst().toLowerCase(ROOT),
                        keyValue -> keyValue.size() == 1 ? Stream.of("") :
                                keyValue.subList(1, keyValue.size()).stream())));
    }

    /**
     * @return {@link #builder(Multimap)} with <code>existingDirectives</code> set to <code>null</code>
     */
    public static Builder builder() {
        return builder(null);
    }

    /**
     * Creates a {@link Builder}.
     *
     * @param existingDirectives the {@link String} {@link Multimap} of existing directives, or <code>null</code>
     *
     * @return the {@link Builder}
     */
    public static Builder builder(final @Nullable Multimap<String, String> existingDirectives) {
        return new Builder(existingDirectives);
    }

    /**
     * {@link Builder} is a builder class for {@link ContentSecurityPolicy}.
     *
     * @see #builder()
     * @see #builder(Multimap)
     */
    public static final class Builder {

        private final SetMultimap<String, String> directives;

        private Builder(final @Nullable Multimap<String, String> existingDirectives) {
            this.directives = SetMultimapBuilder
                    .linkedHashKeys()
                    .linkedHashSetValues()
                    .build();
            if (existingDirectives != null) {
                existingDirectives.entries().forEach(entry -> putDirectiveValue(entry.getKey(), entry.getValue()));
            }
        }

        /**
         * @see #getDirectives()
         */
        public Builder putDirectiveValue(final String key, final String value) {
            directives.put(key.toLowerCase(ROOT), value);
            return this;
        }

        /**
         * @see #getDirectives()
         */
        public Builder putDirectiveValue(final PolicyDirectiveKey policyDirectiveKey, final String value) {
            directives.put(policyDirectiveKey.toString(), value);
            return this;
        }

        /**
         * @return {@link #putDirectiveValue(String, String)} with <code>value</code> set to an empty {@link String}
         */
        public Builder putDirectiveValueless(final String key) {
            return putDirectiveValue(key, "");
        }

        /**
         * @return {@link #putDirectiveValue(PolicyDirectiveKey, String)} with <code>value</code> set to an empty
         * {@link String}
         */
        public Builder putDirectiveValueless(final PolicyDirectiveKey policyDirectiveKey) {
            return putDirectiveValue(policyDirectiveKey, "");
        }

        /**
         * @return {@link #putDirectiveValue(PolicyDirectiveKey, String)} with {@link SourceExpression}
         * {@link Object#toString()}
         */
        public Builder putSourceExpression(final PolicyDirectiveKey policyDirectiveKey,
                final SourceExpression sourceExpression) {
            return putDirectiveValue(policyDirectiveKey, sourceExpression.toString());
        }

        /**
         * @return {@link #putSourceExpression(PolicyDirectiveKey, SourceExpression)}
         * {@link PolicyDirectiveKey#CHILD_SRC}
         *
         * @see #getChildSrc()
         */
        public Builder addChildSrc(final SourceExpression sourceExpression) {
            return putSourceExpression(CHILD_SRC, sourceExpression);
        }

        /**
         * @return {@link #putSourceExpression(PolicyDirectiveKey, SourceExpression)}
         * {@link PolicyDirectiveKey#CONNECT_SRC}
         *
         * @see #getConnectSrc()
         */
        public Builder addConnectSrc(final SourceExpression sourceExpression) {
            return putSourceExpression(CONNECT_SRC, sourceExpression);
        }

        /**
         * @return {@link #putSourceExpression(PolicyDirectiveKey, SourceExpression)}
         * {@link PolicyDirectiveKey#DEFAULT_SRC}
         *
         * @see #getDefaultSrc()
         */
        public Builder addDefaultSrc(final SourceExpression sourceExpression) {
            return putSourceExpression(DEFAULT_SRC, sourceExpression);
        }

        /**
         * @return {@link #putSourceExpression(PolicyDirectiveKey, SourceExpression)}
         * {@link PolicyDirectiveKey#FENCED_FRAME_SRC}
         *
         * @see #getFencedFrameSrc()
         */
        public Builder addFencedFrameSrc(final SourceExpression sourceExpression) {
            return putSourceExpression(FENCED_FRAME_SRC, sourceExpression);
        }

        /**
         * @return {@link #putSourceExpression(PolicyDirectiveKey, SourceExpression)}
         * {@link PolicyDirectiveKey#FONT_SRC}
         *
         * @see #getFontSrc()
         */
        public Builder addFontSrc(final SourceExpression sourceExpression) {
            return putSourceExpression(FONT_SRC, sourceExpression);
        }

        /**
         * @return {@link #putSourceExpression(PolicyDirectiveKey, SourceExpression)}
         * {@link PolicyDirectiveKey#FRAME_SRC}
         *
         * @see #getFrameSrc()
         */
        public Builder addFrameSrc(final SourceExpression sourceExpression) {
            return putSourceExpression(FRAME_SRC, sourceExpression);
        }

        /**
         * @return {@link #putSourceExpression(PolicyDirectiveKey, SourceExpression)}
         * {@link PolicyDirectiveKey#IMG_SRC}
         *
         * @see #getImgSrc()
         */
        public Builder addImgSrc(final SourceExpression sourceExpression) {
            return putSourceExpression(IMG_SRC, sourceExpression);
        }

        /**
         * @return {@link #putSourceExpression(PolicyDirectiveKey, SourceExpression)}
         * {@link PolicyDirectiveKey#MANIFEST_SRC}
         *
         * @see #getManifestSrc()
         */
        public Builder addManifestSrc(final SourceExpression sourceExpression) {
            return putSourceExpression(MANIFEST_SRC, sourceExpression);
        }

        /**
         * @return {@link #putSourceExpression(PolicyDirectiveKey, SourceExpression)}
         * {@link PolicyDirectiveKey#MEDIA_SRC}
         *
         * @see #getMediaSrc()
         */
        public Builder addMediaSrc(final SourceExpression sourceExpression) {
            return putSourceExpression(MEDIA_SRC, sourceExpression);
        }

        /**
         * @return {@link #putSourceExpression(PolicyDirectiveKey, SourceExpression)}
         * {@link PolicyDirectiveKey#OBJECT_SRC}
         *
         * @see #getObjectSrc()
         */
        public Builder addObjectSrc(final SourceExpression sourceExpression) {
            return putSourceExpression(OBJECT_SRC, sourceExpression);
        }

        /**
         * @return {@link #putSourceExpression(PolicyDirectiveKey, SourceExpression)}
         * {@link PolicyDirectiveKey#PREFETCH_SRC}
         *
         * @see #getPrefetchSrc()
         */
        public Builder addPrefetchSrc(final SourceExpression sourceExpression) {
            return putSourceExpression(PREFETCH_SRC, sourceExpression);
        }

        /**
         * @return {@link #putSourceExpression(PolicyDirectiveKey, SourceExpression)}
         * {@link PolicyDirectiveKey#SCRIPT_SRC}
         *
         * @see #getScriptSrc()
         */
        public Builder addScriptSrc(final SourceExpression sourceExpression) {
            return putSourceExpression(SCRIPT_SRC, sourceExpression);
        }

        /**
         * @return {@link #putSourceExpression(PolicyDirectiveKey, SourceExpression)}
         * {@link PolicyDirectiveKey#SCRIPT_SRC_ELEM}
         *
         * @see #getScriptSrcElem()
         */
        public Builder addScriptSrcElem(final SourceExpression sourceExpression) {
            return putSourceExpression(SCRIPT_SRC_ELEM, sourceExpression);
        }

        /**
         * @return {@link #putSourceExpression(PolicyDirectiveKey, SourceExpression)}
         * {@link PolicyDirectiveKey#SCRIPT_SRC_ATTR}
         *
         * @see #getScriptSrcAttr()
         */
        public Builder addScriptSrcAttr(final SourceExpression sourceExpression) {
            return putSourceExpression(SCRIPT_SRC_ATTR, sourceExpression);
        }

        /**
         * @return {@link #putSourceExpression(PolicyDirectiveKey, SourceExpression)}
         * {@link PolicyDirectiveKey#STYLE_SRC}
         *
         * @see #getStyleSrc()
         */
        public Builder addStyleSrc(final SourceExpression sourceExpression) {
            return putSourceExpression(STYLE_SRC, sourceExpression);
        }

        /**
         * @return {@link #putSourceExpression(PolicyDirectiveKey, SourceExpression)}
         * {@link PolicyDirectiveKey#STYLE_SRC_ELEM}
         *
         * @see #getStyleSrcElem()
         */
        public Builder addStyleSrcElem(final SourceExpression sourceExpression) {
            return putSourceExpression(STYLE_SRC_ELEM, sourceExpression);
        }

        /**
         * @return {@link #putSourceExpression(PolicyDirectiveKey, SourceExpression)}
         * {@link PolicyDirectiveKey#STYLE_SRC_ATTR}
         *
         * @see #getStyleSrcAttr()
         */
        public Builder addStyleSrcAttr(final SourceExpression sourceExpression) {
            return putSourceExpression(STYLE_SRC_ATTR, sourceExpression);
        }

        /**
         * @return {@link #putSourceExpression(PolicyDirectiveKey, SourceExpression)}
         * {@link PolicyDirectiveKey#WORKER_SRC}
         *
         * @see #getWorkerSrc()
         */
        public Builder addWorkerSrc(final SourceExpression sourceExpression) {
            return putSourceExpression(WORKER_SRC, sourceExpression);
        }

        /**
         * @return {@link #putSourceExpression(PolicyDirectiveKey, SourceExpression)}
         * {@link PolicyDirectiveKey#BASE_URI}
         *
         * @see #getBaseUri()
         */
        public Builder addBaseUri(final SourceExpression sourceExpression) {
            return putSourceExpression(BASE_URI, sourceExpression);
        }

        /**
         * @return {@link #putDirectiveValueless(PolicyDirectiveKey)} {@link PolicyDirectiveKey#SANDBOX}
         *
         * @see #isSandbox()
         */
        public Builder sandbox() {
            return putDirectiveValueless(SANDBOX);
        }

        /**
         * @return {@link #putDirectiveValue(PolicyDirectiveKey, String)} with {@link PolicyDirectiveKey#SANDBOX} and
         * {@link SandboxFlag#toString()}
         *
         * @see #getSandboxFlags()
         */
        public Builder addSandboxFlag(final SandboxFlag sandboxFlag) {
            return putDirectiveValue(SANDBOX, sandboxFlag.toString());
        }

        /**
         * @return {@link #putSourceExpression(PolicyDirectiveKey, SourceExpression)}
         * {@link PolicyDirectiveKey#FORM_ACTION}
         *
         * @see #getFormAction()
         */
        public Builder addFormAction(final SourceExpression sourceExpression) {
            return putSourceExpression(FORM_ACTION, sourceExpression);
        }

        /**
         * @return {@link #putSourceExpression(PolicyDirectiveKey, SourceExpression)}
         * {@link PolicyDirectiveKey#FRAME_ANCESTORS}
         *
         * @see #getFrameAncestors()
         */
        public Builder addFrameAncestors(final SourceExpression sourceExpression) {
            return putSourceExpression(FRAME_ANCESTORS, sourceExpression);
        }

        /**
         * @return {@link #putDirectiveValue(PolicyDirectiveKey, String)} {@link PolicyDirectiveKey#REPORT_TO}
         *
         * @see #getReportTo()
         */
        public Builder reportTo(final String reportTo) {
            return putDirectiveValue(REPORT_TO, reportTo);
        }

        /**
         * @return {@link #putDirectiveValue(PolicyDirectiveKey, String)} with
         * {@link PolicyDirectiveKey#REQUIRE_TRUSTED_TYPES_FOR} and {@link RequireTrustedTypesFor#toString()}
         *
         * @see #getRequireTrustedTypesFor()
         */
        public Builder addRequireTrustedTypesFor(final RequireTrustedTypesFor requireTrustedTypesFor) {
            return putDirectiveValue(REQUIRE_TRUSTED_TYPES_FOR, requireTrustedTypesFor.toString());
        }

        /**
         * @return {@link #putDirectiveValueless(PolicyDirectiveKey)} {@link PolicyDirectiveKey#TRUSTED_TYPES}
         *
         * @see #isTrustedTypes()
         */
        public Builder trustedTypes() {
            return putDirectiveValueless(TRUSTED_TYPES);
        }

        /**
         * @return {@link #putDirectiveValue(PolicyDirectiveKey, String)} {@link PolicyDirectiveKey#TRUSTED_TYPES}
         *
         * @see #getTrustedTypesPolicyNames()
         */
        public Builder addTrustedTypesPolicyName(final String trustedTypesPolicyName) {
            return putDirectiveValue(TRUSTED_TYPES, trustedTypesPolicyName);
        }

        /**
         * @return {@link #putDirectiveValue(PolicyDirectiveKey, String)} with {@link PolicyDirectiveKey#TRUSTED_TYPES}
         * and {@link TrustedTypesFlag#toString()}
         *
         * @see #getTrustedTypesFlags()
         */
        public Builder addTrustedTypesFlag(final TrustedTypesFlag trustedTypesFlag) {
            return putDirectiveValue(TRUSTED_TYPES, trustedTypesFlag.toString());
        }

        /**
         * @return {@link #putDirectiveValueless(PolicyDirectiveKey)}
         * {@link PolicyDirectiveKey#UPGRADE_INSECURE_REQUESTS}
         *
         * @see #isUpgradeInsecureRequests()
         */
        public Builder upgradeInsecureRequests() {
            return putDirectiveValueless(UPGRADE_INSECURE_REQUESTS);
        }

        /**
         * @return {@link #putDirectiveValueless(PolicyDirectiveKey)}
         * {@link PolicyDirectiveKey#BLOCK_ALL_MIXED_CONTENT}
         *
         * @see #isBlockAllMixedContent()
         */
        public Builder blockAllMixedContent() {
            return putDirectiveValueless(BLOCK_ALL_MIXED_CONTENT);
        }

        /**
         * @return {@link #putDirectiveValue(PolicyDirectiveKey, String)} {@link PolicyDirectiveKey#REPORT_URI}
         *
         * @see #getReportUri()
         */
        public Builder reportUri(final String reportUri) {
            return putDirectiveValue(REPORT_URI, reportUri);
        }

        /**
         * Builds this {@link Builder} into a new {@link ContentSecurityPolicy} instance.
         *
         * @return the built {@link ContentSecurityPolicy}
         */
        public ContentSecurityPolicy build() {
            return new ContentSecurityPolicy(ImmutableSetMultimap.copyOf(directives));
        }
    }

    /**
     * A {@link SetMultimap} containing all directive key {@link String}s and space-separated directive value
     * {@link String}s. If a directive is present, but has no values in {@link Header#CONTENT_SECURITY_POLICY}, then
     * it maps to a single empty {@link String} in this {@link SetMultimap}.
     */
    private final @EqualsAndHashCode.Include @Getter ImmutableSetMultimap<String, String> directives;
    private @LazyInit @Nullable ImmutableSet<SourceExpressionContainer> childSrc;
    private @LazyInit @Nullable ImmutableSet<SourceExpressionContainer> connectSrc;
    private @LazyInit @Nullable ImmutableSet<SourceExpressionContainer> defaultSrc;
    private @LazyInit @Nullable ImmutableSet<SourceExpressionContainer> fencedFrameSrc;
    private @LazyInit @Nullable ImmutableSet<SourceExpressionContainer> fontSrc;
    private @LazyInit @Nullable ImmutableSet<SourceExpressionContainer> frameSrc;
    private @LazyInit @Nullable ImmutableSet<SourceExpressionContainer> imgSrc;
    private @LazyInit @Nullable ImmutableSet<SourceExpressionContainer> manifestSrc;
    private @LazyInit @Nullable ImmutableSet<SourceExpressionContainer> mediaSrc;
    private @LazyInit @Nullable ImmutableSet<SourceExpressionContainer> objectSrc;
    private @LazyInit @Nullable ImmutableSet<SourceExpressionContainer> prefetchSrc;
    private @LazyInit @Nullable ImmutableSet<SourceExpressionContainer> scriptSrc;
    private @LazyInit @Nullable ImmutableSet<SourceExpressionContainer> scriptSrcElem;
    private @LazyInit @Nullable ImmutableSet<SourceExpressionContainer> scriptSrcAttr;
    private @LazyInit @Nullable ImmutableSet<SourceExpressionContainer> styleSrc;
    private @LazyInit @Nullable ImmutableSet<SourceExpressionContainer> styleSrcElem;
    private @LazyInit @Nullable ImmutableSet<SourceExpressionContainer> styleSrcAttr;
    private @LazyInit @Nullable ImmutableSet<SourceExpressionContainer> workerSrc;
    private @LazyInit @Nullable ImmutableSet<SourceExpressionContainer> baseUri;
    private @LazyInit @Nullable Boolean sandbox;
    private @LazyInit @Nullable ImmutableSet<SandboxFlag> sandboxFlags;
    private @LazyInit @Nullable ImmutableSet<SourceExpressionContainer> formAction;
    private @LazyInit @Nullable ImmutableSet<SourceExpressionContainer> frameAncestors;
    private @LazyInit @Nullable Optional<String> reportTo;
    private @LazyInit @Nullable ImmutableSet<RequireTrustedTypesFor> requireTrustedTypesFor;
    private @LazyInit @Nullable Boolean trustedTypes;
    private @LazyInit @Nullable ImmutableSet<String> trustedTypesPolicyNames;
    private @LazyInit @Nullable ImmutableSet<TrustedTypesFlag> trustedTypesFlags;
    private @LazyInit @Nullable Boolean upgradeInsecureRequests;
    private @LazyInit @Nullable Boolean blockAllMixedContent;
    private @LazyInit @Nullable Optional<String> reportUri;
    private @LazyInit @Nullable String string;

    /**
     * @return {@link #containsDirectiveKey(String)} {@link PolicyDirectiveKey#toString()}
     */
    public Boolean containsDirectiveKey(final PolicyDirectiveKey policyDirectiveKey) {
        return containsDirectiveKey(policyDirectiveKey.toString());
    }

    /**
     * @return {@link #getDirectives()} {@link ImmutableSetMultimap#containsKey(Object)}
     */
    public Boolean containsDirectiveKey(final String policyDirectiveKey) {
        return directives.containsKey(policyDirectiveKey);
    }

    /**
     * @return {@link #getDirectiveFirstEntry(String)} {@link PolicyDirectiveKey#toString()}
     */
    public Optional<String> getDirectiveFirstEntry(final PolicyDirectiveKey policyDirectiveKey) {
        return getDirectiveFirstEntry(policyDirectiveKey.toString());
    }

    /**
     * @return {@link #getDirectives()} {@link ImmutableSetMultimap#get(Object)} first non-empty {@link String} entry
     */
    public Optional<String> getDirectiveFirstEntry(final String policyDirectiveKey) {
        return directives.get(policyDirectiveKey).stream().filter(not(String::isEmpty)).findFirst();
    }

    /**
     * @return {@link #parseDirectiveSourceExpressions(String)} {@link PolicyDirectiveKey#toString()}
     */
    public ImmutableSet<SourceExpressionContainer> parseDirectiveSourceExpressions(
            final PolicyDirectiveKey policyDirectiveKey) {
        return parseDirectiveSourceExpressions(policyDirectiveKey.toString());
    }

    /**
     * @return {@link #getDirectives()} {@link ImmutableSetMultimap#get(Object)} mapped to
     * {@link SourceExpression#parse(String)} and {@link SourceExpressionContainer#wrap(SourceExpression)}
     */
    public ImmutableSet<SourceExpressionContainer> parseDirectiveSourceExpressions(final String policyDirectiveKey) {
        return directives.get(policyDirectiveKey).stream()
                .filter(not(String::isEmpty))
                .map(SourceExpression::parse)
                .map(SourceExpressionContainer::wrap)
                .collect(toImmutableSet());
    }

    /**
     * @return internally-cached {@link #parseDirectiveSourceExpressions(PolicyDirectiveKey)}
     * {@link PolicyDirectiveKey#CHILD_SRC}
     */
    public ImmutableSet<SourceExpressionContainer> getChildSrc() {
        if (childSrc == null) {
            childSrc = parseDirectiveSourceExpressions(CHILD_SRC);
        }
        return childSrc;
    }

    /**
     * @return internally-cached {@link #parseDirectiveSourceExpressions(PolicyDirectiveKey)}
     * {@link PolicyDirectiveKey#CONNECT_SRC}
     */
    public ImmutableSet<SourceExpressionContainer> getConnectSrc() {
        if (connectSrc == null) {
            connectSrc = parseDirectiveSourceExpressions(CONNECT_SRC);
        }
        return connectSrc;
    }

    /**
     * @return internally-cached {@link #parseDirectiveSourceExpressions(PolicyDirectiveKey)}
     * {@link PolicyDirectiveKey#DEFAULT_SRC}
     */
    public ImmutableSet<SourceExpressionContainer> getDefaultSrc() {
        if (defaultSrc == null) {
            defaultSrc = parseDirectiveSourceExpressions(DEFAULT_SRC);
        }
        return defaultSrc;
    }

    /**
     * @return internally-cached {@link #parseDirectiveSourceExpressions(PolicyDirectiveKey)}
     * {@link PolicyDirectiveKey#FENCED_FRAME_SRC}
     */
    public ImmutableSet<SourceExpressionContainer> getFencedFrameSrc() {
        if (fencedFrameSrc == null) {
            fencedFrameSrc = parseDirectiveSourceExpressions(FENCED_FRAME_SRC);
        }
        return fencedFrameSrc;
    }

    /**
     * @return internally-cached {@link #parseDirectiveSourceExpressions(PolicyDirectiveKey)}
     * {@link PolicyDirectiveKey#FONT_SRC}
     */
    public ImmutableSet<SourceExpressionContainer> getFontSrc() {
        if (fontSrc == null) {
            fontSrc = parseDirectiveSourceExpressions(FONT_SRC);
        }
        return fontSrc;
    }

    /**
     * @return internally-cached {@link #parseDirectiveSourceExpressions(PolicyDirectiveKey)}
     * {@link PolicyDirectiveKey#FRAME_SRC}
     */
    public ImmutableSet<SourceExpressionContainer> getFrameSrc() {
        if (frameSrc == null) {
            frameSrc = parseDirectiveSourceExpressions(FRAME_SRC);
        }
        return frameSrc;
    }

    /**
     * @return internally-cached {@link #parseDirectiveSourceExpressions(PolicyDirectiveKey)}
     * {@link PolicyDirectiveKey#IMG_SRC}
     */
    public ImmutableSet<SourceExpressionContainer> getImgSrc() {
        if (imgSrc == null) {
            imgSrc = parseDirectiveSourceExpressions(IMG_SRC);
        }
        return imgSrc;
    }

    /**
     * @return internally-cached {@link #parseDirectiveSourceExpressions(PolicyDirectiveKey)}
     * {@link PolicyDirectiveKey#MANIFEST_SRC}
     */
    public ImmutableSet<SourceExpressionContainer> getManifestSrc() {
        if (manifestSrc == null) {
            manifestSrc = parseDirectiveSourceExpressions(MANIFEST_SRC);
        }
        return manifestSrc;
    }

    /**
     * @return internally-cached {@link #parseDirectiveSourceExpressions(PolicyDirectiveKey)}
     * {@link PolicyDirectiveKey#MEDIA_SRC}
     */
    public ImmutableSet<SourceExpressionContainer> getMediaSrc() {
        if (mediaSrc == null) {
            mediaSrc = parseDirectiveSourceExpressions(MEDIA_SRC);
        }
        return mediaSrc;
    }

    /**
     * @return internally-cached {@link #parseDirectiveSourceExpressions(PolicyDirectiveKey)}
     * {@link PolicyDirectiveKey#OBJECT_SRC}
     */
    public ImmutableSet<SourceExpressionContainer> getObjectSrc() {
        if (objectSrc == null) {
            objectSrc = parseDirectiveSourceExpressions(OBJECT_SRC);
        }
        return objectSrc;
    }

    /**
     * @return internally-cached {@link #parseDirectiveSourceExpressions(PolicyDirectiveKey)}
     * {@link PolicyDirectiveKey#PREFETCH_SRC}
     */
    public ImmutableSet<SourceExpressionContainer> getPrefetchSrc() {
        if (prefetchSrc == null) {
            prefetchSrc = parseDirectiveSourceExpressions(PREFETCH_SRC);
        }
        return prefetchSrc;
    }

    /**
     * @return internally-cached {@link #parseDirectiveSourceExpressions(PolicyDirectiveKey)}
     * {@link PolicyDirectiveKey#SCRIPT_SRC}
     */
    public ImmutableSet<SourceExpressionContainer> getScriptSrc() {
        if (scriptSrc == null) {
            scriptSrc = parseDirectiveSourceExpressions(SCRIPT_SRC);
        }
        return scriptSrc;
    }

    /**
     * @return internally-cached {@link #parseDirectiveSourceExpressions(PolicyDirectiveKey)}
     * {@link PolicyDirectiveKey#SCRIPT_SRC_ELEM}
     */
    public ImmutableSet<SourceExpressionContainer> getScriptSrcElem() {
        if (scriptSrcElem == null) {
            scriptSrcElem = parseDirectiveSourceExpressions(SCRIPT_SRC_ELEM);
        }
        return scriptSrcElem;
    }

    /**
     * @return internally-cached {@link #parseDirectiveSourceExpressions(PolicyDirectiveKey)}
     * {@link PolicyDirectiveKey#SCRIPT_SRC_ATTR}
     */
    public ImmutableSet<SourceExpressionContainer> getScriptSrcAttr() {
        if (scriptSrcAttr == null) {
            scriptSrcAttr = parseDirectiveSourceExpressions(SCRIPT_SRC_ATTR);
        }
        return scriptSrcAttr;
    }

    /**
     * @return internally-cached {@link #parseDirectiveSourceExpressions(PolicyDirectiveKey)}
     * {@link PolicyDirectiveKey#STYLE_SRC}
     */
    public ImmutableSet<SourceExpressionContainer> getStyleSrc() {
        if (styleSrc == null) {
            styleSrc = parseDirectiveSourceExpressions(STYLE_SRC);
        }
        return styleSrc;
    }

    /**
     * @return internally-cached {@link #parseDirectiveSourceExpressions(PolicyDirectiveKey)}
     * {@link PolicyDirectiveKey#STYLE_SRC_ELEM}
     */
    public ImmutableSet<SourceExpressionContainer> getStyleSrcElem() {
        if (styleSrcElem == null) {
            styleSrcElem = parseDirectiveSourceExpressions(STYLE_SRC_ELEM);
        }
        return styleSrcElem;
    }

    /**
     * @return internally-cached {@link #parseDirectiveSourceExpressions(PolicyDirectiveKey)}
     * {@link PolicyDirectiveKey#STYLE_SRC_ATTR}
     */
    public ImmutableSet<SourceExpressionContainer> getStyleSrcAttr() {
        if (styleSrcAttr == null) {
            styleSrcAttr = parseDirectiveSourceExpressions(STYLE_SRC_ATTR);
        }
        return styleSrcAttr;
    }

    /**
     * @return internally-cached {@link #parseDirectiveSourceExpressions(PolicyDirectiveKey)}
     * {@link PolicyDirectiveKey#WORKER_SRC}
     */
    public ImmutableSet<SourceExpressionContainer> getWorkerSrc() {
        if (workerSrc == null) {
            workerSrc = parseDirectiveSourceExpressions(WORKER_SRC);
        }
        return workerSrc;
    }

    /**
     * @return internally-cached {@link #parseDirectiveSourceExpressions(PolicyDirectiveKey)}
     * {@link PolicyDirectiveKey#BASE_URI}
     */
    public ImmutableSet<SourceExpressionContainer> getBaseUri() {
        if (baseUri == null) {
            baseUri = parseDirectiveSourceExpressions(BASE_URI);
        }
        return baseUri;
    }

    /**
     * @return internally-cached {@link #containsDirectiveKey(PolicyDirectiveKey)}
     * {@link PolicyDirectiveKey#SANDBOX}
     */
    public boolean isSandbox() {
        if (sandbox == null) {
            sandbox = containsDirectiveKey(SANDBOX);
        }
        return sandbox;
    }

    /**
     * @return internally-cached {@link ImmutableSet} of {@link SandboxFlag}s parsed from {@link #getDirectives()}
     * {@link PolicyDirectiveKey#SANDBOX}
     */
    public ImmutableSet<SandboxFlag> getSandboxFlags() {
        if (sandboxFlags == null) {
            final var sandboxFlags = ImmutableSet.<SandboxFlag>builder();
            for (final var value : directives.get(SANDBOX.toString())) {
                if (value.isEmpty()) {
                    continue;
                }
                final var sandboxFlag = SandboxFlag.forString(value);
                checkArgument(sandboxFlag != null, "Invalid %s flag: %s", SANDBOX, value);
                sandboxFlags.add(sandboxFlag);
            }
            this.sandboxFlags = sandboxFlags.build();
        }
        return sandboxFlags;
    }

    /**
     * @return internally-cached {@link #parseDirectiveSourceExpressions(PolicyDirectiveKey)}
     * {@link PolicyDirectiveKey#FORM_ACTION}
     */
    public ImmutableSet<SourceExpressionContainer> getFormAction() {
        if (formAction == null) {
            formAction = parseDirectiveSourceExpressions(FORM_ACTION);
        }
        return formAction;
    }

    /**
     * @return internally-cached {@link #parseDirectiveSourceExpressions(PolicyDirectiveKey)}
     * {@link PolicyDirectiveKey#FRAME_ANCESTORS}
     */
    public ImmutableSet<SourceExpressionContainer> getFrameAncestors() {
        if (frameAncestors == null) {
            frameAncestors = parseDirectiveSourceExpressions(FRAME_ANCESTORS);
        }
        return frameAncestors;
    }

    /**
     * @return internally-cached {@link #getDirectiveFirstEntry(PolicyDirectiveKey)}
     * {@link PolicyDirectiveKey#REPORT_TO}
     */
    public @Nullable String getReportTo() {
        if (reportTo == null) {
            reportTo = getDirectiveFirstEntry(REPORT_TO);
        }
        return reportTo.orElse(null);
    }

    /**
     * @return internally-cached {@link ImmutableSet} of {@link RequireTrustedTypesFor}s parsed from
     * {@link #getDirectives()} {@link PolicyDirectiveKey#REQUIRE_TRUSTED_TYPES_FOR}
     */
    public ImmutableSet<RequireTrustedTypesFor> getRequireTrustedTypesFor() {
        if (requireTrustedTypesFor == null) {
            final var requireTrustedTypesFor = ImmutableSet.<RequireTrustedTypesFor>builder();
            for (final var value : directives.get(REQUIRE_TRUSTED_TYPES_FOR.toString())) {
                if (value.isEmpty()) {
                    continue;
                }
                final var requireTrustedTypesForEnum = RequireTrustedTypesFor.forString(value);
                checkArgument(requireTrustedTypesForEnum != null,
                        "Invalid %s value: %s", REQUIRE_TRUSTED_TYPES_FOR, value);
                requireTrustedTypesFor.add(requireTrustedTypesForEnum);
            }
            this.requireTrustedTypesFor = requireTrustedTypesFor.build();
        }
        return requireTrustedTypesFor;
    }

    /**
     * @return internally-cached {@link #containsDirectiveKey(PolicyDirectiveKey)}
     * {@link PolicyDirectiveKey#TRUSTED_TYPES}
     */
    public boolean isTrustedTypes() {
        if (trustedTypes == null) {
            trustedTypes = containsDirectiveKey(TRUSTED_TYPES);
        }
        return trustedTypes;
    }

    /**
     * @return internally-cached {@link ImmutableSet} of non-{@link TrustedTypesFlag} {@link String}s from
     * {@link #getDirectives()} {@link PolicyDirectiveKey#TRUSTED_TYPES}
     */
    @SuppressWarnings("NullAway")
    public ImmutableSet<String> getTrustedTypesPolicyNames() {
        if (trustedTypesPolicyNames == null) {
            parseTrustedTypes();
        }
        return trustedTypesPolicyNames;
    }

    /**
     * @return internally-cached {@link ImmutableSet} of {@link TrustedTypesFlag}s from {@link #getDirectives()}
     * {@link PolicyDirectiveKey#TRUSTED_TYPES}
     */
    @SuppressWarnings("NullAway")
    public ImmutableSet<TrustedTypesFlag> getTrustedTypesFlags() {
        if (trustedTypesFlags == null) {
            parseTrustedTypes();
        }
        return trustedTypesFlags;
    }

    private void parseTrustedTypes() {
        final var trustedTypesPolicyNames = ImmutableSet.<String>builder();
        final var trustedTypesFlags = ImmutableSet.<TrustedTypesFlag>builder();
        for (final var value : directives.get(TRUSTED_TYPES.toString())) {
            if (value.isEmpty()) {
                continue;
            }
            if (value.startsWith("'") && value.endsWith("'")) {
                final var trustedTypesFlag = TrustedTypesFlag.forString(value);
                checkArgument(trustedTypesFlag != null, "Invalid %s flag: %s", TRUSTED_TYPES, value);
                trustedTypesFlags.add(trustedTypesFlag);
            } else {
                trustedTypesPolicyNames.add(value);
            }
        }
        this.trustedTypesPolicyNames = trustedTypesPolicyNames.build();
        this.trustedTypesFlags = trustedTypesFlags.build();
    }

    /**
     * @return internally-cached {@link #containsDirectiveKey(PolicyDirectiveKey)}
     * {@link PolicyDirectiveKey#UPGRADE_INSECURE_REQUESTS}
     */
    public boolean isUpgradeInsecureRequests() {
        if (upgradeInsecureRequests == null) {
            upgradeInsecureRequests = containsDirectiveKey(UPGRADE_INSECURE_REQUESTS);
        }
        return upgradeInsecureRequests;
    }

    /**
     * @return internally-cached {@link #containsDirectiveKey(PolicyDirectiveKey)}
     * {@link PolicyDirectiveKey#BLOCK_ALL_MIXED_CONTENT}
     */
    public boolean isBlockAllMixedContent() {
        if (blockAllMixedContent == null) {
            blockAllMixedContent = containsDirectiveKey(BLOCK_ALL_MIXED_CONTENT);
        }
        return blockAllMixedContent;
    }

    /**
     * @return internally-cached {@link #getDirectiveFirstEntry(PolicyDirectiveKey)}
     * {@link PolicyDirectiveKey#REPORT_URI}
     */
    public @Nullable String getReportUri() {
        if (reportUri == null) {
            reportUri = getDirectiveFirstEntry(REPORT_URI);
        }
        return reportUri.orElse(null);
    }

    /**
     * @return this {@link ContentSecurityPolicy} copied into a new {@link Builder} instance
     */
    public Builder toBuilder() {
        return new Builder(directives);
    }

    /**
     * @return internally-cached {@link String} value for {@link Header#CONTENT_SECURITY_POLICY}
     */
    @Override
    public String toString() {
        if (string == null) {
            string = directives.keySet().stream()
                    .map(key -> {
                        final var value = join(" ", directives.get(key));
                        return !value.isEmpty() ? key + " " + value : key;
                    }).collect(joining(DIRECTIVE_DELIMITER + " "));
        }
        return string;
    }
}
