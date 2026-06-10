package net.jacobpeterson.jet.server;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.errorprone.annotations.Immutable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import net.jacobpeterson.jet.common.http.header.Header;
import net.jacobpeterson.jet.common.http.header.cachecontrol.response.ResponseCacheControl;
import net.jacobpeterson.jet.common.http.header.contenttype.ContentType;
import net.jacobpeterson.jet.common.http.header.etag.ETag;
import net.jacobpeterson.jet.common.http.header.headers.Headers;
import net.jacobpeterson.jet.common.http.status.Status;
import net.jacobpeterson.jet.common.http.version.Version;
import net.jacobpeterson.jet.server.JetServer.Builder.SslPem;
import net.jacobpeterson.jet.server.handle.Handle;
import net.jacobpeterson.jet.server.handle.HandleFactory;
import net.jacobpeterson.jet.server.handle.HandleInternals;
import net.jacobpeterson.jet.server.handle.exception.BodyStreamException;
import net.jacobpeterson.jet.server.handle.response.Response;
import net.jacobpeterson.jet.server.handle.response.exception.StatusException;
import net.jacobpeterson.jet.server.handler.Handler;
import net.jacobpeterson.jet.server.router.Router;
import net.jacobpeterson.jet.server.router.simple.MutableSimpleRouter;
import net.jacobpeterson.jet.server.session.Session;
import net.jacobpeterson.jet.server.session.SessionStore;
import net.jacobpeterson.jet.server.session.simple.SimpleSessionStore;
import org.eclipse.jetty.alpn.server.ALPNServerConnectionFactory;
import org.eclipse.jetty.http2.server.HTTP2CServerConnectionFactory;
import org.eclipse.jetty.http2.server.HTTP2ServerConnectionFactory;
import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.Handler.Abstract;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.GracefulHandler;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.VirtualThreadPool;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Throwables.getCausalChain;
import static java.lang.Long.parseLong;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.nio.file.FileVisitOption.FOLLOW_LINKS;
import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.Files.readString;
import static java.nio.file.Files.walkFileTree;
import static java.time.Duration.ofDays;
import static java.time.Duration.ofMinutes;
import static java.time.Duration.ofSeconds;
import static java.util.Collections.synchronizedSet;
import static java.util.Locale.ROOT;
import static java.util.concurrent.ForkJoinPool.commonPool;
import static java.util.concurrent.TimeUnit.SECONDS;
import static lombok.AccessLevel.PRIVATE;
import static net.jacobpeterson.jet.common.http.header.Header.ACCEPT_ENCODING;
import static net.jacobpeterson.jet.common.http.header.Header.CACHE_CONTROL;
import static net.jacobpeterson.jet.common.http.header.Header.CONTENT_ENCODING;
import static net.jacobpeterson.jet.common.http.header.Header.CONTENT_LENGTH;
import static net.jacobpeterson.jet.common.http.header.Header.CONTENT_RANGE;
import static net.jacobpeterson.jet.common.http.header.Header.CONTENT_TYPE;
import static net.jacobpeterson.jet.common.http.header.Header.ETAG;
import static net.jacobpeterson.jet.common.http.header.Header.VARY;
import static net.jacobpeterson.jet.common.http.header.Header.X_CONTENT_TYPE_OPTIONS;
import static net.jacobpeterson.jet.common.http.header.cachecontrol.response.ResponseCacheControl.NO_CACHE;
import static net.jacobpeterson.jet.common.http.status.Status.BAD_REQUEST_400;
import static net.jacobpeterson.jet.common.http.status.Status.INTERNAL_SERVER_ERROR_500;
import static net.jacobpeterson.jet.common.http.status.Status.SERVICE_UNAVAILABLE_503;
import static net.jacobpeterson.jet.common.util.throwable.ThrowableUtil.accumulateThrowable;
import static net.jacobpeterson.jet.common.util.throwable.ThrowableUtil.throwCheckedOrUnchecked;
import static net.jacobpeterson.jet.server.handle.exception.BodyStreamException.asBodyStreamException;
import static org.eclipse.jetty.http.UriCompliance.UNSAFE;
import static org.eclipse.jetty.io.Content.Sink.asOutputStream;
import static org.slf4j.event.Level.DEBUG;
import static org.slf4j.event.Level.ERROR;

/**
 * {@link JetServer} is a simple, lightweight, modern, turnkey, Java web server library.
 * <p>
 * Note: this class is thread-safe.
 *
 * @see <a href="https://github.com/Petersoj/jet">github.com/Petersoj/jet</a>
 */
@NullMarked
@Slf4j
@RequiredArgsConstructor(access = PRIVATE)
public final class JetServer {

    /**
     * Creates a {@link Builder}.
     *
     * @return the {@link Builder}
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * {@link Builder} is a builder class for {@link JetServer}.
     *
     * @see #builder()
     */
    public static final class Builder {

        private final List<Supplier<List<SslPem>>> sslPemsSuppliers = new ArrayList<>();
        private @Nullable HandleFactory handleFactory;
        private @Nullable SessionStore sessionStore;
        private @Nullable Router router;
        private boolean preventMimeSniffing = true;
        private boolean preventAmbiguousResponseCacheControl = true;
        private @Nullable String host;
        private int httpPort = 8080;
        private int httpsPort = 8443;
        private boolean http2 = true;
        private @Nullable Duration reloadSslPeriod;
        private @Nullable Duration gracefulStopTimeout;
        private @Nullable Duration connectionIdleTimeout;
        private @Nullable Duration connectionIdleTimeoutWhenStopping;
        private boolean connectionIdleTimeoutWhenStoppingSet;

        /**
         * @see #getHandleFactory()
         */
        public Builder handleFactory(final HandleFactory handleFactory) {
            this.handleFactory = handleFactory;
            return this;
        }

        /**
         * Calls {@link #sessionStore(SessionStore)} with a new {@link SimpleSessionStore} instance.
         */
        public Builder sessionStore() {
            return sessionStore(new SimpleSessionStore());
        }

        /**
         * @see #getSessionStore()
         */
        public Builder sessionStore(final SessionStore sessionStore) {
            this.sessionStore = sessionStore;
            return this;
        }

        /**
         * @see #getRouter()
         */
        public Builder router(final Router router) {
            this.router = router;
            return this;
        }

        /**
         * @see #isPreventMimeSniffing()
         */
        public Builder preventMimeSniffing(final boolean preventMimeSniffing) {
            this.preventMimeSniffing = preventMimeSniffing;
            return this;
        }

        /**
         * @see #isPreventAmbiguousResponseCacheControl()
         */
        public Builder preventAmbiguousResponseCacheControl(final boolean preventAmbiguousResponseCacheControl) {
            this.preventAmbiguousResponseCacheControl = preventAmbiguousResponseCacheControl;
            return this;
        }

        /**
         * @see #getHost()
         */
        public Builder host(final String host) {
            this.host = host;
            return this;
        }

        /**
         * @see #getHttpPort()
         */
        public Builder httpPort(final int httpPort) {
            this.httpPort = httpPort;
            return this;
        }

        /**
         * @see #getHttpsPort()
         */
        public Builder httpsPort(final int httpsPort) {
            this.httpsPort = httpsPort;
            return this;
        }

        /**
         * @see #isHttp2()
         */
        public Builder http2(final boolean http2) {
            this.http2 = http2;
            return this;
        }

        /**
         * {@link SslPem} represents SSL certificate data in the PEM {@link String} format.
         */
        @Value
        @Immutable
        public static class SslPem {

            /**
             * The certificate chain {@link String}.
             */
            String certificateChain;

            /**
             * The private key {@link String}.
             */
            String privateKey;
        }

        /**
         * Calls {@link #reloadSslPeriod(Duration)} with a {@link Duration} of <code>1 day</code>. Calls
         * {@link #sslDirectory(Path, Predicate, Predicate)} with <code>sslDirectory</code> set to
         * <code>/etc/letsencrypt/live/</code> and <code>isCertificateChain</code> set to test for
         * <code>fullchain.pem</code> filename and <code>isPrivateKey</code> set to test for <code>privkey.pem</code>
         * filename.
         *
         * @return {@link #sslDirectory(Path, Predicate, Predicate)}
         */
        public Builder sslLetsEncrypt() {
            reloadSslPeriod(ofDays(1));
            return sslDirectory(Path.of("/etc/letsencrypt/live/"),
                    path -> path.getFileName().toString().equalsIgnoreCase("fullchain.pem"),
                    path -> path.getFileName().toString().equalsIgnoreCase("privkey.pem"));
        }

        /**
         * Uses {@link Files#walkFileTree(Path, Set, int, FileVisitor)} to discover SSL certificate files in the given
         * <code>sslDirectory</code>.
         *
         * @param sslDirectory       the directory {@link Path} containing SSL certificate files
         * @param isCertificateChain the {@link Predicate} to test if a {@link Path} is
         *                           {@link SslPem#getCertificateChain()}
         * @param isPrivateKey       the {@link Predicate} to test if a {@link Path} is {@link SslPem#getPrivateKey()}
         *
         * @return {@link #sslPems(Supplier)}
         */
        public Builder sslDirectory(final Path sslDirectory, final Predicate<Path> isCertificateChain,
                final Predicate<Path> isPrivateKey) {
            return sslPems(() -> {
                final var certificateChains = new ArrayList<String>();
                final var privateKeys = new ArrayList<String>();
                try {
                    walkFileTree(sslDirectory, Set.of(FOLLOW_LINKS), Integer.MAX_VALUE, new SimpleFileVisitor<>() {
                        @Override
                        public FileVisitResult visitFile(final Path file,
                                final BasicFileAttributes attributes) throws IOException {
                            if (!attributes.isRegularFile()) {
                                return CONTINUE;
                            }
                            if (isCertificateChain.apply(file)) {
                                certificateChains.add(readString(file, US_ASCII));
                            }
                            if (isPrivateKey.apply(file)) {
                                privateKeys.add(readString(file, US_ASCII));
                            }
                            return CONTINUE;
                        }
                    });
                } catch (final IOException ioException) {
                    throw new UncheckedIOException(ioException);
                }
                final var certificateChainsSize = certificateChains.size();
                final var privateKeysSize = privateKeys.size();
                checkArgument(certificateChainsSize == privateKeysSize,
                        "Found %s certificates, but only %s private keys", certificateChainsSize, privateKeysSize);
                final var sslPems = new ArrayList<SslPem>(certificateChainsSize);
                for (var index = 0; index < certificateChainsSize; index++) {
                    sslPems.add(new SslPem(certificateChains.get(index), privateKeys.get(index)));
                }
                return sslPems;
            });
        }

        /**
         * @return {@link #sslPem(Supplier)} using {@link Files#readString(Path, Charset)}
         */
        public Builder sslPem(final Path certificateChain, final Path privateKey) {
            return sslPem(() -> {
                try {
                    return new SslPem(readString(certificateChain, US_ASCII), readString(privateKey, US_ASCII));
                } catch (final IOException ioException) {
                    throw new UncheckedIOException(ioException);
                }
            });
        }

        /**
         * @return {@link #sslPem(SslPem)} {@link SslPem#SslPem(String, String)}
         */
        public Builder sslPem(final String certificateChain, final String privateKey) {
            return sslPem(new SslPem(certificateChain, privateKey));
        }

        /**
         * @return {@link #sslPems(List)}
         */
        public Builder sslPem(final SslPem sslPem) {
            return sslPems(List.of(sslPem));
        }

        /**
         * @return {@link #sslPems(Supplier)}
         */
        public Builder sslPem(final Supplier<SslPem> sslPem) {
            return sslPems(() -> List.of(sslPem.get()));
        }

        /**
         * @return {@link #sslPems(Supplier)}
         */
        public Builder sslPems(final List<SslPem> sslPems) {
            return sslPems(() -> sslPems);
        }

        /**
         * @param sslPems the {@link SslPem} {@link List} {@link Supplier}
         */
        public Builder sslPems(final Supplier<List<SslPem>> sslPems) {
            sslPemsSuppliers.add(sslPems);
            return this;
        }

        /**
         * @see #getReloadSslPeriod()
         */
        public Builder reloadSslPeriod(final Duration reloadSslPeriod) {
            this.reloadSslPeriod = reloadSslPeriod;
            return this;
        }

        /**
         * @see #getGracefulStopTimeout()
         */
        public Builder gracefulStopTimeout(final Duration gracefulStopTimeout) {
            this.gracefulStopTimeout = gracefulStopTimeout;
            return this;
        }

        /**
         * @see #getConnectionIdleTimeout()
         */
        public Builder connectionIdleTimeout(final Duration connectionIdleTimeout) {
            this.connectionIdleTimeout = connectionIdleTimeout;
            return this;
        }

        /**
         * @see #getConnectionIdleTimeoutWhenStopping()
         */
        public Builder connectionIdleTimeoutWhenStopping(final Duration connectionIdleTimeoutWhenStopping) {
            this.connectionIdleTimeoutWhenStopping = connectionIdleTimeoutWhenStopping;
            connectionIdleTimeoutWhenStoppingSet = true;
            return this;
        }

        /**
         * Builds this {@link Builder} into a new {@link JetServer} instance.
         *
         * @return the built {@link JetServer} instance
         */
        public JetServer build() {
            final var jetServer = new JetServer(
                    handleFactory != null ? handleFactory : Handle::new,
                    sessionStore,
                    router != null ? router : new MutableSimpleRouter(),
                    preventMimeSniffing,
                    preventAmbiguousResponseCacheControl,
                    host,
                    httpPort,
                    httpsPort,
                    http2,
                    reloadSslPeriod,
                    gracefulStopTimeout != null ? gracefulStopTimeout : ofMinutes(1),
                    connectionIdleTimeout != null ? connectionIdleTimeout : ofMinutes(1),
                    connectionIdleTimeoutWhenStoppingSet ? connectionIdleTimeoutWhenStopping : ofSeconds(1),
                    ImmutableList.copyOf(sslPemsSuppliers));
            try {
                jetServer.start();
            } catch (final Throwable throwable) {
                try {
                    jetServer.stop();
                } catch (final Throwable stopThrowable) {
                    throwable.addSuppressed(stopThrowable);
                }
                throw throwable;
            }
            return jetServer;
        }
    }

    private static final Pattern SSL_PEM_PRIVATE_KEY_STRIP_PATTERN = Pattern.compile("\\s|-----.*-----");

    /**
     * The {@link HandleFactory}.
     * <p>
     * Defaults to <code>{@link Handle}::new</code>.
     */
    private final @Getter HandleFactory handleFactory;

    /**
     * The {@link SessionStore}, or <code>null</code> to disable {@link Session}s.
     * <p>
     * Defaults to <code>null</code>.
     */
    private final @Getter @Nullable SessionStore sessionStore;

    /**
     * The {@link Router}.
     * <p>
     * Defaults to {@link MutableSimpleRouter}.
     */
    private final @Getter Router router;

    /**
     * Whether to call {@link Response} {@link Headers#ensureEntryIgnoreCase(String, String)} with
     * {@link Header#X_CONTENT_TYPE_OPTIONS} and <code>"nosniff"</code>.
     * <p>
     * Defaults to <code>true</code>.
     */
    private final @Getter boolean preventMimeSniffing;

    /**
     * Whether to call {@link Response#setCacheControl(ResponseCacheControl)} with {@link ResponseCacheControl#NO_CACHE}
     * if {@link Header#CACHE_CONTROL} is not already set.
     * <p>
     * Defaults to <code>true</code>.
     */
    private final @Getter boolean preventAmbiguousResponseCacheControl;

    /**
     * The host address to bind to, or <code>null</code> for all addresses.
     * <p>
     * Defaults to <code>null</code>.
     */
    private final @Getter @Nullable String host;

    /**
     * The HTTP port.
     * <p>
     * Defaults to <code>8080</code>.
     */
    private final @Getter int httpPort;

    /**
     * The HTTPS port.
     * <p>
     * Defaults to <code>8443</code>.
     */
    private final @Getter int httpsPort;

    /**
     * Whether to enable {@link Version#HTTP_2}.
     * <p>
     * Defaults to <code>true</code>.
     */
    private final @Getter boolean http2;

    /**
     * The period {@link Duration} to call {@link #reloadSsl()}, or <code>null</code> to disable.
     * <p>
     * Defaults to <code>null</code>.
     */
    private final @Getter @Nullable Duration reloadSslPeriod;

    /**
     * The maximum {@link Duration} to wait before closing existing connections after {@link #stop()} is called.
     * <p>
     * Defaults to <code>1 minute</code>.
     */
    private final @Getter Duration gracefulStopTimeout;

    /**
     * The maximum {@link Duration} to wait for network data to be sent or received when actively reading/writing a
     * request/response before closing the connection.
     * <p>
     * Defaults to <code>1 minute</code>.
     */
    private final @Getter Duration connectionIdleTimeout;

    /**
     * The {@link #getConnectionIdleTimeout()} to apply to existing connections after {@link #stop()} is called, or
     * <code>null</code> to not modify the {@link #getConnectionIdleTimeout()}.
     * <p>
     * Defaults to <code>1 second</code>.
     */
    private final @Getter @Nullable Duration connectionIdleTimeoutWhenStopping;

    private final ImmutableList<Supplier<List<SslPem>>> sslPemsSuppliers;
    private final Set<Runnable> stopListeners = synchronizedSet(new HashSet<>());
    private volatile boolean stopCalled;
    private @Nullable Server server;
    private SslContextFactory.@Nullable Server sslContextFactory;
    private @Nullable ScheduledFuture<?> reloadSslFuture;

    private void start() {
        LOGGER.info("Jet starting...");
        server = new Server(new VirtualThreadPool(Integer.MAX_VALUE));
        server.setStopAtShutdown(true);
        server.setStopTimeout(gracefulStopTimeout.toMillis());
        final var httpConfiguration = new HttpConfiguration();
        httpConfiguration.setSendServerVersion(false);
        httpConfiguration.setUriCompliance(UNSAFE);
        {
            final var http11Factory = new HttpConnectionFactory(httpConfiguration);
            final var httpFactories = !http2 ? new ConnectionFactory[]{http11Factory} :
                    new ConnectionFactory[]{http11Factory, new HTTP2CServerConnectionFactory(httpConfiguration)};
            server.addConnector(createServerConnector(server, httpFactories, httpPort));
        }
        if (!sslPemsSuppliers.isEmpty()) {
            final var httpsConfiguration = new HttpConfiguration(httpConfiguration);
            httpsConfiguration.addCustomizer(new SecureRequestCustomizer());
            sslContextFactory = new SslContextFactory.Server();
            setKeyStoreFromSslPemsSuppliers(sslContextFactory);
            final var http11Factory = new HttpConnectionFactory(httpsConfiguration);
            final ConnectionFactory[] httpsFactories;
            if (!http2) {
                httpsFactories = new ConnectionFactory[]{
                        new SslConnectionFactory(sslContextFactory, http11Factory.getProtocol()),
                        http11Factory};
            } else {
                final var alpnFactory = new ALPNServerConnectionFactory();
                alpnFactory.setDefaultProtocol(http11Factory.getProtocol());
                httpsFactories = new ConnectionFactory[]{
                        new SslConnectionFactory(sslContextFactory, alpnFactory.getProtocol()),
                        alpnFactory,
                        new HTTP2ServerConnectionFactory(httpsConfiguration),
                        http11Factory};
            }
            server.addConnector(createServerConnector(server, httpsFactories, httpsPort));
        }
        server.setHandler(new GracefulHandler(new Abstract() {
            @Override
            public boolean handle(final org.eclipse.jetty.server.Request jettyRequest,
                    final org.eclipse.jetty.server.Response jettyResponse, final Callback callback) {
                handle(jettyRequest, jettyResponse);
                callback.succeeded();
                return true;
            }

            private void handle(final org.eclipse.jetty.server.Request jettyRequest,
                    final org.eclipse.jetty.server.Response jettyResponse) {
                // This check is still racy with `stop()`, but it decreases the likelihood of `addStopListener()` calls
                // throwing after `stop()` is called.
                if (stopCalled) {
                    jettyResponse.setStatus(SERVICE_UNAVAILABLE_503.getCode());
                    return;
                }
                Handle handle = null;
                try {
                    handle = handleFactory.create(new HandleInternals(JetServer.this, jettyRequest, jettyResponse));
                    final var response = handle.getResponse();
                    try {
                        router.route(handle);
                        handleCompression(handle);
                    } catch (final Throwable throwable) {
                        response.getHeaders().clear();
                        final int statusCode;
                        final String statusString;
                        final boolean errorLog;
                        if (throwable instanceof final StatusException statusException) {
                            statusCode = statusException.getStatusCode();
                            final var status = Status.forCode(statusCode);
                            statusString = status == null ? statusCode + " Error" : status.toString();
                            errorLog = false;
                        } else if (getCausalChain(throwable).stream()
                                .anyMatch(cause -> cause instanceof BodyStreamException)) {
                            final var status = BAD_REQUEST_400;
                            statusCode = status.getCode();
                            statusString = status.toString();
                            errorLog = false;
                        } else {
                            final var status = INTERNAL_SERVER_ERROR_500;
                            statusCode = status.getCode();
                            statusString = status.toString();
                            errorLog = true;
                        }
                        handle.getResponse().responseText(statusCode, statusString);
                        LOGGER.atLevel(errorLog ? ERROR : DEBUG).log("Handler threw", throwable);
                    }
                    final var headers = response.getHeaders();
                    if (preventMimeSniffing) {
                        headers.ensureEntryIgnoreCase(X_CONTENT_TYPE_OPTIONS.toString(), "nosniff");
                    }
                    if (preventAmbiguousResponseCacheControl && !headers.containsKey(CACHE_CONTROL.toString())) {
                        response.setCacheControl(NO_CACHE);
                    }
                    jettyResponse.setStatus(response.getStatusCode());
                    headers.forEach(jettyResponse.getHeaders()::add);
                    final var bodyOutputStreamApplier = response.getBodyOutputStreamApplier();
                    if (bodyOutputStreamApplier != null) {
                        try (final var bodyOutputStream = asOutputStream(jettyResponse)) {
                            bodyOutputStreamApplier.accept(new OutputStream() {
                                @Override
                                public void write(final byte[] b) throws IOException {
                                    try {
                                        bodyOutputStream.write(b);
                                    } catch (final Exception exception) {
                                        throw asBodyStreamException(exception);
                                    }
                                }

                                @Override
                                public void write(final byte[] b, final int off, final int len) throws IOException {
                                    try {
                                        bodyOutputStream.write(b, off, len);
                                    } catch (final Exception exception) {
                                        throw asBodyStreamException(exception);
                                    }
                                }

                                @Override
                                public void flush() throws IOException {
                                    try {
                                        bodyOutputStream.flush();
                                    } catch (final Exception exception) {
                                        throw asBodyStreamException(exception);
                                    }
                                }

                                @Override
                                public void close() throws IOException {
                                    try {
                                        bodyOutputStream.close();
                                    } catch (final Exception exception) {
                                        throw asBodyStreamException(exception);
                                    }
                                }

                                @Override
                                public void write(final int b) throws IOException {
                                    try {
                                        bodyOutputStream.write(b);
                                    } catch (final Exception exception) {
                                        throw asBodyStreamException(exception);
                                    }
                                }
                            });
                        } catch (final Throwable throwable) {
                            if (getCausalChain(throwable).stream().anyMatch(cause ->
                                    cause instanceof BodyStreamException)) {
                                LOGGER.debug("Body stream exception", throwable);
                            } else {
                                throw throwable;
                            }
                        }
                    }
                } catch (final Throwable throwable) {
                    LOGGER.error("Internal handler threw", throwable);
                    if (!jettyResponse.isCommitted()) {
                        jettyResponse.setStatus(INTERNAL_SERVER_ERROR_500.getCode());
                        jettyResponse.getHeaders().clear();
                    }
                } finally {
                    if (handle != null) {
                        try {
                            handle.getResponse().runAfters();
                        } catch (final Throwable throwable) {
                            LOGGER.error("`Response.runAfters()` threw", throwable);
                        }
                    }
                }
            }

            private void handleCompression(final Handle handle) {
                final var response = handle.getResponse();
                final var compressionConfig = response.getCompressionConfig();
                if (compressionConfig == null) {
                    return;
                }
                final var headers = response.getHeaders();
                if (compressionConfig.isEnsureVaryHeader()) {
                    headers.ensureEntryContainingIgnoreCase(VARY.toString(), ACCEPT_ENCODING.toString());
                }
                final var bodyOutputStreamApplier = response.getBodyOutputStreamApplier();
                if (bodyOutputStreamApplier == null) {
                    return;
                }
                if (compressionConfig.isCheckContentEncoding() && headers.containsKey(CONTENT_ENCODING.toString())) {
                    return;
                }
                if (compressionConfig.isCheckContentRange() && headers.containsKey(CONTENT_RANGE.toString())) {
                    return;
                }
                final var minimumContentLength = compressionConfig.getMinimumContentLength();
                if (minimumContentLength != null) {
                    final var contentLength = headers.getFirst(CONTENT_LENGTH.toString());
                    if (contentLength != null && parseLong(contentLength) < minimumContentLength) {
                        return;
                    }
                }
                if (compressionConfig.isCheckContentType()) {
                    final var contentType = headers.getFirst(CONTENT_TYPE.toString());
                    if (contentType != null && ContentType.parse(contentType).isCompressed()) {
                        return;
                    }
                }
                final var acceptEncoding = handle.getRequest().getAcceptEncoding();
                if (acceptEncoding == null) {
                    return;
                }
                final var acceptEncodingTypes = acceptEncoding.getEntryTypes();
                final var compressionLevel = compressionConfig.getLevels().stream()
                        .filter(level -> acceptEncodingTypes.contains(level.getType()))
                        .findFirst()
                        .orElse(null);
                if (compressionLevel == null) {
                    return;
                }
                headers.set(CONTENT_ENCODING.toString(), compressionLevel.getType().toString());
                headers.removeAll(CONTENT_LENGTH.toString());
                if (compressionConfig.isModifyETag()) {
                    final var eTagValue = headers.getFirst(ETAG.toString());
                    if (eTagValue != null) {
                        final var eTag = ETag.parse(eTagValue);
                        headers.set(ETAG.toString(), eTag.toBuilder()
                                .value(eTag.getValueWithoutCompressionType(), compressionLevel.getType())
                                .build().toString());
                    }
                }
                response.setBodyOutputStreamApplier(bodyOutputStream -> {
                    try (final var compressedBodyOutputStream = compressionLevel.getType()
                            .compress(bodyOutputStream, compressionLevel.getLevel())) {
                        bodyOutputStreamApplier.accept(compressedBodyOutputStream);
                    } catch (final IOException ioException) {
                        throw new UncheckedIOException(ioException);
                    }
                });
            }
        }));
        server.setErrorHandler(new GracefulHandler(new Abstract() {
            @Override
            public boolean handle(final org.eclipse.jetty.server.Request request,
                    final org.eclipse.jetty.server.Response response, final Callback callback) {
                callback.succeeded();
                return true;
            }
        }));
        try {
            server.start();
        } catch (final Exception exception) {
            throw new RuntimeException(exception);
        }
        LOGGER.info("Jet started");
        if (reloadSslPeriod != null) {
            final var periodSeconds = reloadSslPeriod.toSeconds();
            reloadSslFuture = commonPool().scheduleAtFixedRate(() -> Thread.ofVirtual().start(() -> {
                try {
                    reloadSsl();
                } catch (final Throwable throwable) {
                    LOGGER.error("Reload SSL threw", throwable);
                }
            }), periodSeconds, periodSeconds, SECONDS);
            LOGGER.info("Reloading SSL every {}", reloadSslPeriod.toString().substring(2).toLowerCase(ROOT));
        }
    }

    private ServerConnector createServerConnector(final Server server, final ConnectionFactory[] factories,
            final int port) {
        final var serverConnector = new ServerConnector(server, null, null, null, -1, -1, factories);
        serverConnector.setHost(host);
        serverConnector.setPort(port);
        serverConnector.setIdleTimeout(connectionIdleTimeout.toMillis());
        serverConnector.setShutdownIdleTimeout(connectionIdleTimeoutWhenStopping == null ? -1 :
                connectionIdleTimeoutWhenStopping.toMillis());
        return serverConnector;
    }

    private void setKeyStoreFromSslPemsSuppliers(final SslContextFactory sslContextFactory) {
        try {
            final var keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            final var certificateFactory = CertificateFactory.getInstance("X.509");
            for (final var sslPemSupplier : sslPemsSuppliers) {
                for (final var sslPem : sslPemSupplier.get()) {
                    final var certificates = certificateFactory.generateCertificates(new ByteArrayInputStream(
                            sslPem.certificateChain.getBytes(US_ASCII))).toArray(Certificate[]::new);
                    final var privateKey = KeyFactory.getInstance(certificates[0].getPublicKey().getAlgorithm())
                            .generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(
                                    SSL_PEM_PRIVATE_KEY_STRIP_PATTERN.matcher(sslPem.privateKey).replaceAll(""))));
                    keyStore.setKeyEntry(String.valueOf(keyStore.size()), privateKey, null, certificates);
                }
            }
            sslContextFactory.setKeyStore(keyStore);
        } catch (final CertificateException | NoSuchAlgorithmException | InvalidKeySpecException | KeyStoreException |
                IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * @return <code>true</code> if HTTPS (SSL/TLS) is enabled, <code>false</code> otherwise
     */
    public boolean isHttps() {
        return sslContextFactory != null;
    }

    /**
     * Reloads SSL (hot-swap).
     */
    public synchronized void reloadSsl() {
        if (sslContextFactory == null) {
            return;
        }
        try {
            sslContextFactory.reload(this::setKeyStoreFromSslPemsSuppliers);
            LOGGER.debug("Reloaded SSL");
        } catch (final Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Adds the given {@link Runnable} to a list of {@link Runnable}s that are guaranteed to run when {@link #stop()} is
     * called.
     *
     * @return <code>true</code> if added, <code>false</code> otherwise
     *
     * @throws IllegalStateException thrown if {@link #stop()} has already been called
     */
    public boolean addStopListener(final Runnable stopListener) throws IllegalStateException {
        synchronized (stopListeners) {
            checkState(!stopCalled, "`stop()` has already been called");
            return stopListeners.add(stopListener);
        }
    }

    /**
     * Removes a {@link Runnable} added from {@link #addStopListener(Runnable)}.
     *
     * @return <code>true</code> if removed, <code>false</code> otherwise
     */
    public boolean removeStopListener(final Runnable stopListener) {
        return stopListeners.remove(stopListener);
    }

    /**
     * Calls all {@link Runnable} from {@link #addStopListener(Runnable)}, cancels {@link #getReloadSslPeriod()}, and
     * stops the server by rejecting new connections and waiting for {@link #getGracefulStopTimeout()} to elapse before
     * closing existing connections. Subsequent calls to this method are ignored.
     * <p>
     * Note: this method should never be called on the {@link Thread} of a {@link Handler}.
     */
    public synchronized void stop() {
        if (stopCalled) {
            return;
        }
        stopCalled = true;
        LOGGER.info("Jet stopping...");
        Throwable throwables = null;
        for (final var stopListener : stopListeners.toArray(Runnable[]::new)) {
            try {
                stopListener.run();
            } catch (final Throwable throwable) {
                throwables = accumulateThrowable(throwables, throwable);
            }
        }
        if (reloadSslFuture != null) {
            try {
                reloadSslFuture.cancel(false);
            } catch (final Throwable throwable) {
                throwables = accumulateThrowable(throwables, throwable);
            }
        }
        sslContextFactory = null;
        if (server != null) {
            try {
                server.stop();
            } catch (final Throwable throwable) {
                throwables = accumulateThrowable(throwables, throwable);
            }
        }
        throwCheckedOrUnchecked(throwables);
        LOGGER.info("Jet stopped");
    }
}
