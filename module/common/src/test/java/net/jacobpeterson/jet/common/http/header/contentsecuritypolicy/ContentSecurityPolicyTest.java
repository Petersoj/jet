package net.jacobpeterson.jet.common.http.header.contentsecuritypolicy;

import com.google.common.collect.ImmutableSetMultimap;
import net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.key.PolicyDirectiveKey;
import net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.requiretrustedtypesfor.RequireTrustedTypesFor;
import net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.sandbox.SandboxFlag;
import net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.sourceexpression.SourceExpression;
import net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.sourceexpression.SourceExpressionContainer;
import net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.sourceexpression.hash.HashSourceExpression;
import net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.sourceexpression.hash.HashSourceExpressionAlgorithm;
import net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.sourceexpression.host.HostSourceExpression;
import net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.sourceexpression.nonce.NonceSourceExpression;
import net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.sourceexpression.predefined.PredefinedSourceExpression;
import net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.sourceexpression.scheme.SchemeSourceExpression;
import net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.trustedtypes.TrustedTypesFlag;
import net.jacobpeterson.jet.common.http.url.Scheme;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@NullMarked
public final class ContentSecurityPolicyTest {

    @Test
    public void parse() {
        assertEquals(ImmutableSetMultimap.of(), ContentSecurityPolicy.parse("").getDirectives());
        assertEquals(ImmutableSetMultimap.of(), ContentSecurityPolicy.parse(" ").getDirectives());
        assertEquals(ImmutableSetMultimap.of(), ContentSecurityPolicy.parse(" ;").getDirectives());
        assertEquals(ImmutableSetMultimap.of(), ContentSecurityPolicy.parse(" ; ; ").getDirectives());
        assertEquals(ImmutableSetMultimap.of("default-src", ""),
                ContentSecurityPolicy.parse("default-src").getDirectives());
        assertEquals(ImmutableSetMultimap.of("default-src", ""),
                ContentSecurityPolicy.parse("DEFAULT-SRC;").getDirectives());
        assertEquals(ImmutableSetMultimap.of("default-src", ""),
                ContentSecurityPolicy.parse("Default-Src; ;").getDirectives());
        assertEquals(ImmutableSetMultimap.of("default-src", ""),
                ContentSecurityPolicy.parse("default-src; ;  ;   ;").getDirectives());
        assertEquals(ImmutableSetMultimap.of("default-src", "https://a.com"),
                ContentSecurityPolicy.parse("default-src https://a.com").getDirectives());
        assertEquals(ImmutableSetMultimap.of("default-src", "https://a.com"),
                ContentSecurityPolicy.parse("default-src https://a.com;").getDirectives());
        assertEquals(ImmutableSetMultimap.of("default-src", "https://a.com", "script-src", "'none'"),
                ContentSecurityPolicy.parse("default-src https://a.com; script-src 'none'").getDirectives());
    }

    @Test
    public void builder() {
        assertEquals(ContentSecurityPolicy.builder().build(), ContentSecurityPolicy.builder().build());
        assertEquals(ContentSecurityPolicy.parse(""), ContentSecurityPolicy.builder().build());
    }

    @Test
    public void builderMultimap() {
        assertEquals(ImmutableSetMultimap.of(),
                ContentSecurityPolicy.builder(ImmutableSetMultimap.of()).build().getDirectives());
        assertEquals(ImmutableSetMultimap.of("", ""),
                ContentSecurityPolicy.builder(ImmutableSetMultimap.of("", "")).build().getDirectives());
        assertEquals(ImmutableSetMultimap.of("a", ""),
                ContentSecurityPolicy.builder(ImmutableSetMultimap.of("a", "")).build().getDirectives());
        assertEquals(ImmutableSetMultimap.of("a", "a"),
                ContentSecurityPolicy.builder(ImmutableSetMultimap.of("a", "a")).build().getDirectives());
        assertEquals(ImmutableSetMultimap.of("a", "a", "a", "b"),
                ContentSecurityPolicy.builder(ImmutableSetMultimap.of("a", "a", "a", "b")).build().getDirectives());

        assertEquals("", ContentSecurityPolicy.builder(ImmutableSetMultimap.of("", "", "", "")).build().toString());
        assertEquals("a", ContentSecurityPolicy.builder(ImmutableSetMultimap.of("a", "")).build().toString());
    }

    public static final class BuilderTest {

        @Test
        public void putDirectiveValue() {
            assertEquals(ContentSecurityPolicy.parse("a b; c d e;"), ContentSecurityPolicy.builder()
                    .putDirectiveValue("a", "b")
                    .putDirectiveValue("c", "d")
                    .putDirectiveValue("C", "e")
                    .build());
        }

        @Test
        public void putDirectiveValuePolicyDirectiveKey() {
            assertEquals(ContentSecurityPolicy.parse("default-src a"), ContentSecurityPolicy.builder()
                    .putDirectiveValue(PolicyDirectiveKey.DEFAULT_SRC, "a")
                    .build());
        }

        @Test
        public void putDirectiveValueless() {
            assertEquals(ContentSecurityPolicy.parse("a"), ContentSecurityPolicy.builder()
                    .putDirectiveValueless("a")
                    .build());
        }

        @Test
        public void putDirectiveValuelessPolicyDirectiveKey() {
            assertEquals(ContentSecurityPolicy.parse("default-src"), ContentSecurityPolicy.builder()
                    .putDirectiveValueless(PolicyDirectiveKey.DEFAULT_SRC)
                    .build());
        }

        @Test
        public void putSourceExpression() {
            assertEquals(ContentSecurityPolicy.parse("default-src 'none'; script-src 'self'"),
                    ContentSecurityPolicy.builder()
                            .putSourceExpression(PolicyDirectiveKey.DEFAULT_SRC, PredefinedSourceExpression.NONE)
                            .putSourceExpression(PolicyDirectiveKey.SCRIPT_SRC, PredefinedSourceExpression.SELF)
                            .build());
        }

        @Test
        public void addChildSrc() {
            assertEquals("child-src 'self'", ContentSecurityPolicy.builder()
                    .addChildSrc(PredefinedSourceExpression.SELF)
                    .build().toString());
            assertEquals("child-src 'self' 'nonce-123'", ContentSecurityPolicy.builder()
                    .addChildSrc(PredefinedSourceExpression.SELF)
                    .addChildSrc(NonceSourceExpression.create("123"))
                    .build().toString());
        }

        @Test
        public void addConnectSrc() {
            assertEquals("connect-src wss:", ContentSecurityPolicy.builder()
                    .addConnectSrc(SchemeSourceExpression.forScheme(Scheme.WSS))
                    .build().toString());
            assertEquals("connect-src wss: 'self'", ContentSecurityPolicy.builder()
                    .addConnectSrc(SchemeSourceExpression.forScheme(Scheme.WSS))
                    .addConnectSrc(PredefinedSourceExpression.SELF)
                    .build().toString());
        }

        @Test
        public void addDefaultSrc() {
            assertEquals("default-src 'none'", ContentSecurityPolicy.builder()
                    .addDefaultSrc(PredefinedSourceExpression.NONE)
                    .build().toString());
            assertEquals("default-src 'nonce-123' 'nonce-456'", ContentSecurityPolicy.builder()
                    .addDefaultSrc(NonceSourceExpression.create("123"))
                    .addDefaultSrc(NonceSourceExpression.create("456"))
                    .build().toString());
        }

        @Test
        public void addFencedFrameSrc() {
            assertEquals("fenced-frame-src *", ContentSecurityPolicy.builder()
                    .addFencedFrameSrc(HostSourceExpression.WILDCARD)
                    .build().toString());
            assertEquals("fenced-frame-src * data: blob:", ContentSecurityPolicy.builder()
                    .addFencedFrameSrc(HostSourceExpression.WILDCARD)
                    .addFencedFrameSrc(SchemeSourceExpression.forScheme(Scheme.DATA))
                    .addFencedFrameSrc(SchemeSourceExpression.forScheme(Scheme.BLOB))
                    .build().toString());
        }

        @Test
        public void addFontSrc() {
            assertEquals("font-src https://*.a.com", ContentSecurityPolicy.builder()
                    .addFontSrc(HostSourceExpression.create("https://*.a.com"))
                    .build().toString());
            assertEquals("font-src 'sha512-123' 'nonce-123'", ContentSecurityPolicy.builder()
                    .addFontSrc(HashSourceExpression.create(HashSourceExpressionAlgorithm.SHA_512, "123"))
                    .addFontSrc(NonceSourceExpression.create("123"))
                    .build().toString());
        }

        @Test
        public void addFrameSrc() {
            assertEquals("frame-src 'self'", ContentSecurityPolicy.builder()
                    .addFrameSrc(PredefinedSourceExpression.SELF)
                    .build().toString());
            assertEquals("frame-src 'self' 'nonce-123'", ContentSecurityPolicy.builder()
                    .addFrameSrc(PredefinedSourceExpression.SELF)
                    .addFrameSrc(NonceSourceExpression.create("123"))
                    .build().toString());
        }

        @Test
        public void addImgSrc() {
            assertEquals("img-src 'sha256-pmWkWSBCL51Bfkhn79xPuKBKHz//H6B+mY6G9/eieuM='",
                    ContentSecurityPolicy.builder()
                            .addImgSrc(HashSourceExpression.forStringSource(HashSourceExpressionAlgorithm.SHA_256,
                                    "123"))
                            .build().toString());
            assertEquals("img-src 'self' *:", ContentSecurityPolicy.builder()
                    .addImgSrc(PredefinedSourceExpression.SELF)
                    .addImgSrc(SchemeSourceExpression.create("*"))
                    .build().toString());
        }

        @Test
        public void addManifestSrc() {
            assertEquals("manifest-src 'report-sample'", ContentSecurityPolicy.builder()
                    .addManifestSrc(PredefinedSourceExpression.REPORT_SAMPLE)
                    .build().toString());
            assertEquals("manifest-src 'self' 'unsafe-inline'", ContentSecurityPolicy.builder()
                    .addManifestSrc(PredefinedSourceExpression.SELF)
                    .addManifestSrc(PredefinedSourceExpression.UNSAFE_INLINE)
                    .build().toString());
        }

        @Test
        public void addMediaSrc() {
            assertEquals("media-src a.com/a", ContentSecurityPolicy.builder()
                    .addMediaSrc(HostSourceExpression.create("a.com/a"))
                    .build().toString());
            assertEquals("media-src a.com/a a.com/b", ContentSecurityPolicy.builder()
                    .addMediaSrc(HostSourceExpression.create("a.com/a"))
                    .addMediaSrc(HostSourceExpression.create("a.com/b"))
                    .build().toString());
        }

        @Test
        public void addObjectSrc() {
            assertEquals("object-src *.com/a", ContentSecurityPolicy.builder()
                    .addObjectSrc(HostSourceExpression.create("*.com/a"))
                    .build().toString());
            assertEquals("object-src https://a.com:*/b", ContentSecurityPolicy.builder()
                    .addObjectSrc(HostSourceExpression.create("https://a.com:*/b"))
                    .addObjectSrc(HostSourceExpression.create("https://a.com:*/b"))
                    .build().toString());
        }

        @Test
        public void addPrefetchSrc() {
            assertEquals("prefetch-src 'nonce-123'", ContentSecurityPolicy.builder()
                    .addPrefetchSrc(NonceSourceExpression.create("123"))
                    .build().toString());
            assertEquals("prefetch-src 'nonce-123'", ContentSecurityPolicy.builder()
                    .addPrefetchSrc(NonceSourceExpression.create("123"))
                    .addPrefetchSrc(NonceSourceExpression.create("123"))
                    .build().toString());
        }

        @Test
        public void addScriptSrc() {
            assertEquals("script-src 'sha384-384'", ContentSecurityPolicy.builder()
                    .addScriptSrc(HashSourceExpression.create(HashSourceExpressionAlgorithm.SHA_384, "384"))
                    .build().toString());
            assertEquals("script-src 'unsafe-eval' 'strict-dynamic'", ContentSecurityPolicy.builder()
                    .addScriptSrc(PredefinedSourceExpression.UNSAFE_EVAL)
                    .addScriptSrc(PredefinedSourceExpression.STRICT_DYNAMIC)
                    .build().toString());
        }

        @Test
        public void addScriptSrcElem() {
            assertEquals("script-src-elem *", ContentSecurityPolicy.builder()
                    .addScriptSrcElem(HostSourceExpression.WILDCARD)
                    .build().toString());
            assertEquals("script-src-elem *", ContentSecurityPolicy.builder()
                    .addScriptSrcElem(HostSourceExpression.WILDCARD)
                    .addScriptSrcElem(HostSourceExpression.WILDCARD)
                    .build().toString());
        }

        @Test
        public void addScriptSrcAttr() {
            assertEquals("script-src-attr a:", ContentSecurityPolicy.builder()
                    .addScriptSrcAttr(SchemeSourceExpression.create("a"))
                    .build().toString());
            assertEquals("script-src-attr a:", ContentSecurityPolicy.builder()
                    .addScriptSrcAttr(SchemeSourceExpression.create("a"))
                    .addScriptSrcAttr(SchemeSourceExpression.create("a"))
                    .build().toString());
        }

        @Test
        public void addStyleSrc() {
            assertEquals("style-src a.com/a", ContentSecurityPolicy.builder()
                    .addStyleSrc(HostSourceExpression.create("a.com/a"))
                    .build().toString());
            assertEquals("style-src a.com/a", ContentSecurityPolicy.builder()
                    .addStyleSrc(HostSourceExpression.create("a.com/a"))
                    .addStyleSrc(HostSourceExpression.create("a.com/a"))
                    .build().toString());
        }

        @Test
        public void addStyleSrcElem() {
            assertEquals("style-src-elem 'self'", ContentSecurityPolicy.builder()
                    .addStyleSrcElem(PredefinedSourceExpression.SELF)
                    .build().toString());
            assertEquals("style-src-elem 'self'", ContentSecurityPolicy.builder()
                    .addStyleSrcElem(PredefinedSourceExpression.SELF)
                    .addStyleSrcElem(PredefinedSourceExpression.SELF)
                    .build().toString());
        }

        @Test
        public void addStyleSrcAttr() {
            assertEquals("style-src-attr 'sha512-512'", ContentSecurityPolicy.builder()
                    .addStyleSrcAttr(HashSourceExpression.create(HashSourceExpressionAlgorithm.SHA_512, "512"))
                    .build().toString());
            assertEquals("style-src-attr 'sha512-512'", ContentSecurityPolicy.builder()
                    .addStyleSrcAttr(HashSourceExpression.create(HashSourceExpressionAlgorithm.SHA_512, "512"))
                    .addStyleSrcAttr(HashSourceExpression.create(HashSourceExpressionAlgorithm.SHA_512, "512"))
                    .build().toString());
        }

        @Test
        public void addWorkerSrc() {
            assertEquals("worker-src 'nonce-'".length() + 8, ContentSecurityPolicy.builder()
                    .addWorkerSrc(NonceSourceExpression.generate(8))
                    .build().toString().length());
            assertEquals("worker-src 'nonce-' 'nonce-'".length() + 32 + 128, ContentSecurityPolicy.builder()
                    .addWorkerSrc(NonceSourceExpression.generate(32))
                    .addWorkerSrc(NonceSourceExpression.generate(128))
                    .build().toString().length());
        }

        @Test
        public void addBaseUri() {
            assertEquals("base-uri a.com", ContentSecurityPolicy.builder()
                    .addBaseUri(SourceExpression.parse("a.com"))
                    .build().toString());
            assertEquals("base-uri a.com", ContentSecurityPolicy.builder()
                    .addBaseUri(SourceExpression.parse("a.com"))
                    .addBaseUri(SourceExpression.parse("a.com"))
                    .build().toString());
        }

        @Test
        public void sandbox() {
            assertEquals(ContentSecurityPolicy.parse("sandbox"), ContentSecurityPolicy.builder()
                    .sandbox()
                    .build());
        }

        @Test
        public void addSandboxFlag() {
            assertEquals(ContentSecurityPolicy.parse("sandbox allow-popups"), ContentSecurityPolicy.builder()
                    .addSandboxFlag(SandboxFlag.ALLOW_POPUPS)
                    .build());
            assertEquals(ContentSecurityPolicy.parse("sandbox allow-downloads allow-popups"),
                    ContentSecurityPolicy.builder()
                            .addSandboxFlag(SandboxFlag.ALLOW_POPUPS)
                            .addSandboxFlag(SandboxFlag.ALLOW_DOWNLOADS)
                            .build());
        }

        @Test
        public void addFormAction() {
            assertEquals("form-action 'none'", ContentSecurityPolicy.builder()
                    .addFormAction(PredefinedSourceExpression.NONE)
                    .build().toString());
            assertEquals("form-action 'self'", ContentSecurityPolicy.builder()
                    .addFormAction(SourceExpression.parse("'self'"))
                    .addFormAction(PredefinedSourceExpression.SELF)
                    .build().toString());
        }

        @Test
        public void addFrameAncestors() {
            assertEquals("frame-ancestors 'self'", ContentSecurityPolicy.builder()
                    .addFrameAncestors(SourceExpression.parse("'self'"))
                    .build().toString());
            assertEquals("frame-ancestors a:", ContentSecurityPolicy.builder()
                    .addFrameAncestors(SourceExpression.parse("a:"))
                    .addFrameAncestors(SourceExpression.parse("a:"))
                    .build().toString());
        }

        @Test
        public void reportTo() {
            assertEquals(ContentSecurityPolicy.parse("report-to endpoint"), ContentSecurityPolicy.builder()
                    .reportTo("endpoint")
                    .build());
        }

        @Test
        public void addRequireTrustedTypesFor() {
            assertEquals(ContentSecurityPolicy.parse(" require-trusted-types-for   'script'   ;"),
                    ContentSecurityPolicy.builder()
                            .addRequireTrustedTypesFor(RequireTrustedTypesFor.SCRIPT)
                            .build());
        }

        @Test
        public void trustedTypes() {
            assertEquals(ContentSecurityPolicy.parse(" trusted-types   ;"), ContentSecurityPolicy.builder()
                    .trustedTypes()
                    .build());
        }

        @Test
        public void addTrustedTypesPolicyName() {
            assertEquals(ContentSecurityPolicy.parse(" trusted-types  a ;"), ContentSecurityPolicy.builder()
                    .addTrustedTypesPolicyName("a")
                    .build());
        }

        @Test
        public void addTrustedTypesFlag() {
            assertEquals(ContentSecurityPolicy.parse(" ;  ; trusted-types   'allow-duplicates' ; ;"),
                    ContentSecurityPolicy.builder()
                            .addTrustedTypesFlag(TrustedTypesFlag.ALLOW_DUPLICATES)
                            .build());
        }

        @Test
        public void upgradeInsecureRequests() {
            assertEquals(ContentSecurityPolicy.parse(";  upgrade-insecure-requests  "), ContentSecurityPolicy.builder()
                    .upgradeInsecureRequests()
                    .build());
        }

        @Test
        public void blockAllMixedContent() {
            assertEquals(ContentSecurityPolicy.parse(";;;block-all-mixed-content;;;"), ContentSecurityPolicy.builder()
                    .blockAllMixedContent()
                    .build());
        }

        @Test
        public void reportUri() {
            assertEquals(ContentSecurityPolicy.parse("report-uri https://a.com"), ContentSecurityPolicy.builder()
                    .reportUri("https://a.com")
                    .build());
        }
    }

    @Test
    public void containsKeyPolicyDirectiveKey() {
        assertTrue(ContentSecurityPolicy.parse("default-src").containsKey(PolicyDirectiveKey.DEFAULT_SRC));
    }

    @Test
    public void containsKeyString() {
        assertTrue(ContentSecurityPolicy.parse("a").containsKey("a"));
    }

    @Test
    public void getStringFirstEntryPolicyDirectiveKey() {
        assertEquals("a", ContentSecurityPolicy.parse("default-src a b;")
                .getStringFirstEntry(PolicyDirectiveKey.DEFAULT_SRC).orElseThrow());
    }

    @Test
    public void getStringFirstEntryString() {
        assertEquals("b", ContentSecurityPolicy.parse("a b c;")
                .getStringFirstEntry("a").orElseThrow());
    }

    @Test
    public void parseSourceExpressionsPolicyDirectiveKey() {
        assertEquals(Set.of(SourceExpressionContainer.wrap(SourceExpression.parse("'none'"))),
                ContentSecurityPolicy.parse("default-src 'none'")
                        .parseSourceExpressions(PolicyDirectiveKey.DEFAULT_SRC));
    }

    @Test
    public void parseSourceExpressionsString() {
        assertEquals(Set.of(SourceExpressionContainer.wrap(SourceExpression.parse("'none'"))),
                ContentSecurityPolicy.parse("a 'none'")
                        .parseSourceExpressions("a"));
    }

    @Test
    public void getChildSrc() {
        assertEquals(ContentSecurityPolicy.parse("child-src 'self'").getChildSrc(),
                ContentSecurityPolicy.builder()
                        .addChildSrc(PredefinedSourceExpression.SELF)
                        .build().getChildSrc());
        assertEquals(ContentSecurityPolicy.parse("child-src 'self' 'nonce-123'").getChildSrc(),
                ContentSecurityPolicy.builder()
                        .addChildSrc(PredefinedSourceExpression.SELF)
                        .addChildSrc(NonceSourceExpression.create("123"))
                        .build().getChildSrc());
    }

    @Test
    public void getConnectSrc() {
        assertEquals(ContentSecurityPolicy.parse("connect-src wss:").getConnectSrc(),
                ContentSecurityPolicy.builder()
                        .addConnectSrc(SchemeSourceExpression.forScheme(Scheme.WSS))
                        .build().getConnectSrc());
        assertEquals(ContentSecurityPolicy.parse("connect-src wss: 'self'").getConnectSrc(),
                ContentSecurityPolicy.builder()
                        .addConnectSrc(SchemeSourceExpression.forScheme(Scheme.WSS))
                        .addConnectSrc(PredefinedSourceExpression.SELF)
                        .build().getConnectSrc());
    }

    @Test
    public void getDefaultSrc() {
        assertEquals(ContentSecurityPolicy.parse("default-src 'none'").getDefaultSrc(),
                ContentSecurityPolicy.builder()
                        .addDefaultSrc(PredefinedSourceExpression.NONE)
                        .build().getDefaultSrc());
        assertEquals(ContentSecurityPolicy.parse("default-src 'nonce-123' 'nonce-456'").getDefaultSrc(),
                ContentSecurityPolicy.builder()
                        .addDefaultSrc(NonceSourceExpression.create("123"))
                        .addDefaultSrc(NonceSourceExpression.create("456"))
                        .build().getDefaultSrc());
    }

    @Test
    public void getFencedFrameSrc() {
        assertEquals(ContentSecurityPolicy.parse("fenced-frame-src *").getFencedFrameSrc(),
                ContentSecurityPolicy.builder()
                        .addFencedFrameSrc(HostSourceExpression.WILDCARD)
                        .build().getFencedFrameSrc());
        assertEquals(ContentSecurityPolicy.parse("fenced-frame-src * data:").getFencedFrameSrc(),
                ContentSecurityPolicy.builder()
                        .addFencedFrameSrc(HostSourceExpression.WILDCARD)
                        .addFencedFrameSrc(SchemeSourceExpression.forScheme(Scheme.DATA))
                        .build().getFencedFrameSrc());
    }

    @Test
    public void getFontSrc() {
        assertEquals(ContentSecurityPolicy.parse("font-src https://*.a.com").getFontSrc(),
                ContentSecurityPolicy.builder()
                        .addFontSrc(HostSourceExpression.create("https://*.a.com"))
                        .build().getFontSrc());
        assertEquals(ContentSecurityPolicy.parse("font-src 'sha512-123' 'nonce-123'").getFontSrc(),
                ContentSecurityPolicy.builder()
                        .addFontSrc(HashSourceExpression.create(HashSourceExpressionAlgorithm.SHA_512, "123"))
                        .addFontSrc(NonceSourceExpression.create("123"))
                        .build().getFontSrc());
    }

    @Test
    public void getFrameSrc() {
        assertEquals(ContentSecurityPolicy.parse("frame-src 'self'").getFrameSrc(),
                ContentSecurityPolicy.builder()
                        .addFrameSrc(PredefinedSourceExpression.SELF)
                        .build().getFrameSrc());
        assertEquals(ContentSecurityPolicy.parse("frame-src 'self' 'nonce-123'").getFrameSrc(),
                ContentSecurityPolicy.builder()
                        .addFrameSrc(PredefinedSourceExpression.SELF)
                        .addFrameSrc(NonceSourceExpression.create("123"))
                        .build().getFrameSrc());
    }

    @Test
    public void getImgSrc() {
        assertEquals(ContentSecurityPolicy.parse("img-src 'sha256-pmWkWSBCL51Bfkhn79xPuKBKHz//H6B+mY6G9/eieuM='")
                        .getImgSrc(),
                ContentSecurityPolicy.builder()
                        .addImgSrc(HashSourceExpression.forStringSource(HashSourceExpressionAlgorithm.SHA_256, "123"))
                        .build().getImgSrc());
        assertEquals(ContentSecurityPolicy.parse("img-src 'self' *:").getImgSrc(),
                ContentSecurityPolicy.builder()
                        .addImgSrc(PredefinedSourceExpression.SELF)
                        .addImgSrc(SchemeSourceExpression.create("*"))
                        .build().getImgSrc());
    }

    @Test
    public void getManifestSrc() {
        assertEquals(ContentSecurityPolicy.parse("manifest-src 'report-sample'").getManifestSrc(),
                ContentSecurityPolicy.builder()
                        .addManifestSrc(PredefinedSourceExpression.REPORT_SAMPLE)
                        .build().getManifestSrc());
        assertEquals(ContentSecurityPolicy.parse("manifest-src 'self' 'unsafe-inline'").getManifestSrc(),
                ContentSecurityPolicy.builder()
                        .addManifestSrc(PredefinedSourceExpression.SELF)
                        .addManifestSrc(PredefinedSourceExpression.UNSAFE_INLINE)
                        .build().getManifestSrc());
    }

    @Test
    public void getMediaSrc() {
        assertEquals(ContentSecurityPolicy.parse("media-src a.com/a").getMediaSrc(),
                ContentSecurityPolicy.builder()
                        .addMediaSrc(HostSourceExpression.create("a.com/a"))
                        .build().getMediaSrc());
        assertEquals(ContentSecurityPolicy.parse("media-src a.com/a a.com/b").getMediaSrc(),
                ContentSecurityPolicy.builder()
                        .addMediaSrc(HostSourceExpression.create("a.com/a"))
                        .addMediaSrc(HostSourceExpression.create("a.com/b"))
                        .build().getMediaSrc());
    }

    @Test
    public void getObjectSrc() {
        assertEquals(ContentSecurityPolicy.parse("object-src *.com/a").getObjectSrc(),
                ContentSecurityPolicy.builder()
                        .addObjectSrc(HostSourceExpression.create("*.com/a"))
                        .build().getObjectSrc());
        assertEquals(ContentSecurityPolicy.parse("object-src https://a.com:*/b").getObjectSrc(),
                ContentSecurityPolicy.builder()
                        .addObjectSrc(HostSourceExpression.create("https://a.com:*/b"))
                        .addObjectSrc(HostSourceExpression.create("https://a.com:*/b"))
                        .build().getObjectSrc());
    }

    @Test
    public void getPrefetchSrc() {
        assertEquals(ContentSecurityPolicy.parse("prefetch-src 'nonce-123'").getPrefetchSrc(),
                ContentSecurityPolicy.builder()
                        .addPrefetchSrc(NonceSourceExpression.create("123"))
                        .build().getPrefetchSrc());
        assertEquals(ContentSecurityPolicy.parse("prefetch-src 'nonce-123'").getPrefetchSrc(),
                ContentSecurityPolicy.builder()
                        .addPrefetchSrc(NonceSourceExpression.create("123"))
                        .addPrefetchSrc(NonceSourceExpression.create("123"))
                        .build().getPrefetchSrc());
    }

    @Test
    public void getScriptSrc() {
        assertEquals(ContentSecurityPolicy.parse("script-src 'sha384-384'").getScriptSrc(),
                ContentSecurityPolicy.builder()
                        .addScriptSrc(HashSourceExpression.create(HashSourceExpressionAlgorithm.SHA_384, "384"))
                        .build().getScriptSrc());
        assertEquals(ContentSecurityPolicy.parse("script-src 'unsafe-eval' 'strict-dynamic'").getScriptSrc(),
                ContentSecurityPolicy.builder()
                        .addScriptSrc(PredefinedSourceExpression.UNSAFE_EVAL)
                        .addScriptSrc(PredefinedSourceExpression.STRICT_DYNAMIC)
                        .build().getScriptSrc());
    }

    @Test
    public void getScriptSrcElem() {
        assertEquals(ContentSecurityPolicy.parse("script-src-elem *").getScriptSrcElem(),
                ContentSecurityPolicy.builder()
                        .addScriptSrcElem(HostSourceExpression.WILDCARD)
                        .build().getScriptSrcElem());
        assertEquals(ContentSecurityPolicy.parse("script-src-elem *").getScriptSrcElem(),
                ContentSecurityPolicy.builder()
                        .addScriptSrcElem(HostSourceExpression.WILDCARD)
                        .addScriptSrcElem(HostSourceExpression.WILDCARD)
                        .build().getScriptSrcElem());
    }

    @Test
    public void getScriptSrcAttr() {
        assertEquals(ContentSecurityPolicy.parse("script-src-attr a:").getScriptSrcAttr(),
                ContentSecurityPolicy.builder()
                        .addScriptSrcAttr(SchemeSourceExpression.create("a"))
                        .build().getScriptSrcAttr());
        assertEquals(ContentSecurityPolicy.parse("script-src-attr a:").getScriptSrcAttr(),
                ContentSecurityPolicy.builder()
                        .addScriptSrcAttr(SchemeSourceExpression.create("a"))
                        .addScriptSrcAttr(SchemeSourceExpression.create("a"))
                        .build().getScriptSrcAttr());
    }

    @Test
    public void getStyleSrc() {
        assertEquals(ContentSecurityPolicy.parse("style-src a.com/a").getStyleSrc(),
                ContentSecurityPolicy.builder()
                        .addStyleSrc(HostSourceExpression.create("a.com/a"))
                        .build().getStyleSrc());
        assertEquals(ContentSecurityPolicy.parse("style-src a.com/a").getStyleSrc(),
                ContentSecurityPolicy.builder()
                        .addStyleSrc(HostSourceExpression.create("a.com/a"))
                        .addStyleSrc(HostSourceExpression.create("a.com/a"))
                        .build().getStyleSrc());
    }

    @Test
    public void getStyleSrcElem() {
        assertEquals(ContentSecurityPolicy.parse("style-src-elem 'self'").getStyleSrcElem(),
                ContentSecurityPolicy.builder()
                        .addStyleSrcElem(PredefinedSourceExpression.SELF)
                        .build().getStyleSrcElem());
        assertEquals(ContentSecurityPolicy.parse("style-src-elem 'self'").getStyleSrcElem(),
                ContentSecurityPolicy.builder()
                        .addStyleSrcElem(PredefinedSourceExpression.SELF)
                        .addStyleSrcElem(PredefinedSourceExpression.SELF)
                        .build().getStyleSrcElem());
    }

    @Test
    public void getStyleSrcAttr() {
        assertEquals(ContentSecurityPolicy.parse("style-src-attr 'sha512-512'").getStyleSrcAttr(),
                ContentSecurityPolicy.builder()
                        .addStyleSrcAttr(HashSourceExpression.create(HashSourceExpressionAlgorithm.SHA_512, "512"))
                        .build().getStyleSrcAttr());
        assertEquals(ContentSecurityPolicy.parse("style-src-attr 'sha512-512'").getStyleSrcAttr(),
                ContentSecurityPolicy.builder()
                        .addStyleSrcAttr(HashSourceExpression.create(HashSourceExpressionAlgorithm.SHA_512, "512"))
                        .addStyleSrcAttr(HashSourceExpression.create(HashSourceExpressionAlgorithm.SHA_512, "512"))
                        .build().getStyleSrcAttr());
    }

    @Test
    public void getWorkerSrc() {
        assertEquals(1, ContentSecurityPolicy.builder()
                .addWorkerSrc(NonceSourceExpression.generate(8))
                .build().getWorkerSrc().size());
        assertEquals(2, ContentSecurityPolicy.builder()
                .addWorkerSrc(NonceSourceExpression.generate(32))
                .addWorkerSrc(NonceSourceExpression.generate(128))
                .build().getWorkerSrc().size());
    }

    @Test
    public void getBaseUri() {
        assertEquals(ContentSecurityPolicy.parse("base-uri a.com").getBaseUri(),
                ContentSecurityPolicy.builder()
                        .addBaseUri(SourceExpression.parse("a.com"))
                        .build().getBaseUri());
        assertEquals(ContentSecurityPolicy.parse("base-uri a.com").getBaseUri(),
                ContentSecurityPolicy.builder()
                        .addBaseUri(SourceExpression.parse("a.com"))
                        .addBaseUri(SourceExpression.parse("a.com"))
                        .build().getBaseUri());
    }

    @Test
    public void isSandbox() {
        assertTrue(ContentSecurityPolicy.parse("sandbox").isSandbox());
        assertTrue(ContentSecurityPolicy.builder()
                .sandbox()
                .build().isSandbox());
    }

    @Test
    public void getSandboxFlags() {
        assertEquals(ContentSecurityPolicy.parse("sandbox allow-popups").getSandboxFlags(),
                ContentSecurityPolicy.builder()
                        .addSandboxFlag(SandboxFlag.ALLOW_POPUPS)
                        .build().getSandboxFlags());
        assertEquals(ContentSecurityPolicy.parse("sandbox allow-downloads allow-popups").getSandboxFlags(),
                ContentSecurityPolicy.builder()
                        .addSandboxFlag(SandboxFlag.ALLOW_POPUPS)
                        .addSandboxFlag(SandboxFlag.ALLOW_DOWNLOADS)
                        .build().getSandboxFlags());
    }

    @Test
    public void getFormAction() {
        assertEquals(ContentSecurityPolicy.parse("form-action 'none'").getFormAction(),
                ContentSecurityPolicy.builder()
                        .addFormAction(PredefinedSourceExpression.NONE)
                        .build().getFormAction());
        assertEquals(ContentSecurityPolicy.parse("form-action 'self'").getFormAction(),
                ContentSecurityPolicy.builder()
                        .addFormAction(SourceExpression.parse("'self'"))
                        .addFormAction(PredefinedSourceExpression.SELF)
                        .build().getFormAction());
    }

    @Test
    public void getFrameAncestors() {
        assertEquals(ContentSecurityPolicy.parse("frame-ancestors 'self'").getFrameAncestors(),
                ContentSecurityPolicy.builder()
                        .addFrameAncestors(SourceExpression.parse("'self'"))
                        .build().getFrameAncestors());
        assertEquals(ContentSecurityPolicy.parse("frame-ancestors a:").getFrameAncestors(),
                ContentSecurityPolicy.builder()
                        .addFrameAncestors(SourceExpression.parse("a:"))
                        .addFrameAncestors(SourceExpression.parse("a:"))
                        .build().getFrameAncestors());
    }

    @Test
    public void getReportTo() {
        assertEquals("endpoint", ContentSecurityPolicy.parse("report-to endpoint").getReportTo());
        assertEquals("endpoint", ContentSecurityPolicy.builder()
                .reportTo("endpoint")
                .build().getReportTo());
    }

    @Test
    public void getRequireTrustedTypesFor() {
        assertEquals(Set.of(RequireTrustedTypesFor.SCRIPT),
                ContentSecurityPolicy.parse(" require-trusted-types-for   'script'   ;").getRequireTrustedTypesFor());
        assertEquals(Set.of(RequireTrustedTypesFor.SCRIPT), ContentSecurityPolicy.builder()
                .addRequireTrustedTypesFor(RequireTrustedTypesFor.SCRIPT)
                .build().getRequireTrustedTypesFor());
    }

    @Test
    public void isTrustedTypes() {
        assertTrue(ContentSecurityPolicy.parse(" trusted-types   ;").isTrustedTypes());
        assertTrue(ContentSecurityPolicy.builder()
                .trustedTypes()
                .build().isTrustedTypes());
    }

    @Test
    public void getTrustedTypesPolicyNames() {
        assertEquals(ContentSecurityPolicy.parse(" trusted-types  a ;").getTrustedTypesPolicyNames(),
                ContentSecurityPolicy.builder()
                        .addTrustedTypesPolicyName("a")
                        .build().getTrustedTypesPolicyNames());
    }

    @Test
    public void getTrustedTypesFlags() {
        assertEquals(ContentSecurityPolicy.parse(" ;  ; trusted-types   'allow-duplicates' ; ;").getTrustedTypesFlags(),
                ContentSecurityPolicy.builder()
                        .addTrustedTypesFlag(TrustedTypesFlag.ALLOW_DUPLICATES)
                        .build().getTrustedTypesFlags());
    }

    @Test
    public void isUpgradeInsecureRequests() {
        assertTrue(ContentSecurityPolicy.parse(";  upgrade-insecure-requests  ").isUpgradeInsecureRequests());
        assertTrue(ContentSecurityPolicy.builder()
                .upgradeInsecureRequests()
                .build().isUpgradeInsecureRequests());
    }

    @Test
    public void isBlockAllMixedContent() {
        assertTrue(ContentSecurityPolicy.parse(";;;block-all-mixed-content;;;").isBlockAllMixedContent());
        assertTrue(ContentSecurityPolicy.builder()
                .blockAllMixedContent()
                .build().isBlockAllMixedContent());
    }

    @Test
    public void getReportUri() {
        assertEquals("https://a.com", ContentSecurityPolicy.parse("report-uri https://a.com").getReportUri());
        assertEquals("https://a.com", ContentSecurityPolicy.builder()
                .reportUri("https://a.com")
                .build().getReportUri());
    }

    @Test
    public void toBuilder() {
        final var contentSecurityPolicy = ContentSecurityPolicy.parse("default-src 'none'; script-src 'nonce-123'");
        assertEquals(contentSecurityPolicy, contentSecurityPolicy.toBuilder().build());
        assertEquals("default-src 'none'; script-src 'nonce-123'; sandbox",
                contentSecurityPolicy.toBuilder().sandbox().build().toString());
    }

    @Test
    public void _toString() {
        assertEquals("default-src 'none'; sandbox allow-forms; script-src 'self' a.com 'strict-dynamic'",
                ContentSecurityPolicy.builder()
                        .addDefaultSrc(SourceExpression.parse("'none'"))
                        .addSandboxFlag(SandboxFlag.ALLOW_FORMS)
                        .addScriptSrc(PredefinedSourceExpression.SELF)
                        .addScriptSrc(HostSourceExpression.create("a.com"))
                        .addScriptSrc(PredefinedSourceExpression.STRICT_DYNAMIC)
                        .build().toString());
    }
}
