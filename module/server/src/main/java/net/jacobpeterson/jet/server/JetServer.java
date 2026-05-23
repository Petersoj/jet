package net.jacobpeterson.jet.server;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.errorprone.annotations.Immutable;
import dev.scheibelhofer.crypto.provider.JctProvider;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import net.jacobpeterson.jet.common.http.header.Header;
import net.jacobpeterson.jet.common.http.header.cachecontrol.response.ResponseCacheControl;
import net.jacobpeterson.jet.common.http.header.contenttype.ContentType;
import net.jacobpeterson.jet.common.http.header.etag.ETag;
import net.jacobpeterson.jet.common.http.header.headers.Headers;
import net.jacobpeterson.jet.server.JetServer.Builder.SslPem;
import net.jacobpeterson.jet.server.handle.Handle;
import net.jacobpeterson.jet.server.handle.HandleFactory;
import net.jacobpeterson.jet.server.handle.HandleInternals;
import net.jacobpeterson.jet.server.handle.request.Request;
import net.jacobpeterson.jet.server.handle.request.multipart.MultipartConfig;
import net.jacobpeterson.jet.server.handle.response.Response;
import net.jacobpeterson.jet.server.handle.response.compression.CompressionConfig;
import net.jacobpeterson.jet.server.handler.throwable.ThrowableHandler;
import net.jacobpeterson.jet.server.handler.throwable.simple.SimpleThrowableHandler;
import net.jacobpeterson.jet.server.route.router.Router;
import net.jacobpeterson.jet.server.route.router.simple.MutableSimpleRouter;
import net.jacobpeterson.jet.server.session.SessionStore;
import net.jacobpeterson.jet.server.session.simple.SimpleSessionStore;
import net.jacobpeterson.jet.server.session.unsupported.UnsupportedSessionStore;
import org.eclipse.jetty.alpn.server.ALPNServerConnectionFactory;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.http2.server.HTTP2CServerConnectionFactory;
import org.eclipse.jetty.http2.server.HTTP2ServerConnectionFactory;
import org.eclipse.jetty.io.EofException;
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
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

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
import static java.util.Locale.ROOT;
import static java.util.concurrent.ForkJoinPool.commonPool;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.joining;
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
import static net.jacobpeterson.jet.common.http.status.Status.INTERNAL_SERVER_ERROR_500;
import static org.eclipse.jetty.http.UriCompliance.UNSAFE;
import static org.eclipse.jetty.io.Content.Sink.asOutputStream;

/**
 * {@link JetServer} is a simple, modern, turnkey, Java web server library.
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
        private int defaultRequestBodyBoundCount = 8 * 1024 * 1024;
        private @Nullable MultipartConfig defaultMultipartConfig;
        private @Nullable CompressionConfig defaultCompressionConfig;
        private boolean preventMimeSniffing = true;
        private boolean preventAmbiguousResponseCacheControl = true;
        private @Nullable SessionStore sessionStore;
        private @Nullable Router router;
        private @Nullable ThrowableHandler throwableHandler;
        private @Nullable Router afterRouter;
        private @Nullable Duration gracefulStopTimeout;
        private @Nullable String host;
        private int httpPort = 8080;
        private int httpsPort = 8443;
        private boolean http2 = true;
        private @Nullable Duration reloadSslPeriod;
        private @Nullable Duration connectionIdleTimeout;

        /**
         * @see #getHandleFactory()
         */
        public Builder handleFactory(final HandleFactory handleFactory) {
            this.handleFactory = handleFactory;
            return this;
        }

        /**
         * @see #getDefaultRequestBodyBoundCount()
         */
        public Builder defaultRequestBodyBoundCount(final int defaultRequestBodyBoundCount) {
            this.defaultRequestBodyBoundCount = defaultRequestBodyBoundCount;
            return this;
        }

        /**
         * @see #getDefaultMultipartConfig()
         */
        public Builder defaultMultipartConfig(final MultipartConfig defaultMultipartConfig) {
            this.defaultMultipartConfig = defaultMultipartConfig;
            return this;
        }

        /**
         * @see #getDefaultCompressionConfig()
         */
        public Builder defaultCompressionConfig(final CompressionConfig defaultCompressionConfig) {
            this.defaultCompressionConfig = defaultCompressionConfig;
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
         * @see #getSessionStore()
         */
        public Builder sessionStore(final SessionStore sessionStore) {
            this.sessionStore = sessionStore;
            return this;
        }

        /**
         * Calls {@link #sessionStore(SessionStore)} with {@link UnsupportedSessionStore#INSTANCE}.
         */
        public Builder disableSessions() {
            return sessionStore(UnsupportedSessionStore.INSTANCE);
        }

        /**
         * @see #getRouter()
         */
        public Builder router(final Router router) {
            this.router = router;
            return this;
        }

        /**
         * @see #getThrowableHandler()
         */
        public Builder throwableHandler(final ThrowableHandler throwableHandler) {
            this.throwableHandler = throwableHandler;
            return this;
        }

        /**
         * @see #getAfterRouter()
         */
        public Builder afterRouter(final Router afterRouter) {
            this.afterRouter = afterRouter;
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
                                certificateChains.add(readSslFile(file));
                            }
                            if (isPrivateKey.apply(file)) {
                                privateKeys.add(readSslFile(file));
                            }
                            return CONTINUE;
                        }

                        private String readSslFile(final Path file) throws IOException {
                            return readString(file, US_ASCII);
                        }
                    });
                } catch (final IOException ioException) {
                    throw new UncheckedIOException(ioException);
                }
                final var certificateChainsSize = certificateChains.size();
                final var privateKeysSize = privateKeys.size();
                checkState(certificateChainsSize == privateKeysSize, "Found %s certificates, but only %s private keys",
                        certificateChainsSize, privateKeysSize);
                final var sslPems = new ArrayList<SslPem>(certificateChainsSize);
                for (var index = 0; index < certificateChainsSize; index++) {
                    sslPems.add(new SslPem(certificateChains.get(index), privateKeys.get(index)));
                }
                LOGGER.info("sslPems: {}", sslPems.size());
                return sslPems;
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
         * @see #getConnectionIdleTimeout()
         */
        public Builder connectionIdleTimeout(final Duration connectionIdleTimeout) {
            this.connectionIdleTimeout = connectionIdleTimeout;
            return this;
        }

        /**
         * Builds this {@link Builder} into a new {@link JetServer} instance.
         *
         * @return the built {@link JetServer} instance
         */
        public JetServer build() {
            checkArgument(defaultRequestBodyBoundCount >= 0,
                    "`defaultRequestBodyBoundCount` must be positive or zero");
            final var connectionIdleTimeout = this.connectionIdleTimeout != null ? this.connectionIdleTimeout :
                    ofMinutes(1);
            return new JetServer(
                    handleFactory != null ? handleFactory : Handle::new,
                    defaultRequestBodyBoundCount,
                    defaultMultipartConfig != null ? defaultMultipartConfig : MultipartConfig.builder().build(),
                    defaultCompressionConfig != null ? defaultCompressionConfig : CompressionConfig.builder().build(),
                    preventMimeSniffing,
                    preventAmbiguousResponseCacheControl,
                    sessionStore != null ? sessionStore : new SimpleSessionStore(),
                    router != null ? router : new MutableSimpleRouter(),
                    throwableHandler != null ? throwableHandler : SimpleThrowableHandler.INSTANCE,
                    afterRouter,
                    gracefulStopTimeout != null ? gracefulStopTimeout : connectionIdleTimeout.plusSeconds(10),
                    host,
                    httpPort,
                    httpsPort,
                    http2,
                    connectionIdleTimeout,
                    reloadSslPeriod,
                    ImmutableList.copyOf(sslPemsSuppliers));
        }
    }

    /**
     * The {@link HandleFactory}.
     * <p>
     * Defaults to <code>{@link Handle}::new</code>.
     */
    private final @Getter HandleFactory handleFactory;

    /**
     * The default bound count when reading a {@link Request} body without specifying a bound count.
     * <p>
     * Defaults to <code>8 MiB</code>.
     */
    private final @Getter int defaultRequestBodyBoundCount;

    /**
     * The default {@link MultipartConfig} when reading a multipart {@link Request} body without specifying a
     * {@link MultipartConfig}.
     * <p>
     * Defaults to {@link MultipartConfig.Builder#build()}.
     */
    private final @Getter MultipartConfig defaultMultipartConfig;

    /**
     * The default {@link CompressionConfig} to initially set for {@link Response#getCompressionConfig()}.
     * <p>
     * Defaults to {@link CompressionConfig.Builder#build()}.
     */
    private final @Getter CompressionConfig defaultCompressionConfig;

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
     * The {@link SessionStore}.
     * <p>
     * Defaults to {@link SimpleSessionStore}. Use {@link UnsupportedSessionStore#INSTANCE} to disable sessions.
     */
    private final @Getter SessionStore sessionStore;

    /**
     * The {@link Router}.
     * <p>
     * Defaults to {@link MutableSimpleRouter}.
     */
    private final @Getter Router router;

    /**
     * The {@link ThrowableHandler} for {@link Throwable}s thrown by {@link #getRouter()}.
     * <p>
     * Defaults to {@link SimpleThrowableHandler#INSTANCE}.
     */
    private final @Getter ThrowableHandler throwableHandler;

    /**
     * The {@link Router} to call after the response body (if any) has been written successfully or unsuccessfully.
     * <p>
     * Defaults to <code>null</code>.
     */
    private final @Getter @Nullable Router afterRouter;

    /**
     * The {@link Duration} to wait before closing active connections after {@link #stop()} is called.
     * <p>
     * Defaults to {@link #getConnectionIdleTimeout()} plus <code>10 seconds</code>.
     */
    private final @Getter Duration gracefulStopTimeout;

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
     * Whether to enable {@link HttpVersion#HTTP_2}.
     * <p>
     * Defaults to <code>true</code>.
     */
    private final @Getter boolean http2;

    /**
     * The {@link Duration} to wait for network data to be sent or received before closing the connection.
     * <p>
     * Defaults to <code>1 minute</code>.
     */
    private final @Getter Duration connectionIdleTimeout;

    /**
     * The {@link Duration} period to call {@link #reloadSsl()}, or <code>null</code> to disable.
     */
    private final @Getter @Nullable Duration reloadSslPeriod;

    private final ImmutableList<Supplier<List<SslPem>>> sslPemsSuppliers;
    private @Nullable Server server;
    private SslContextFactory.@Nullable Server sslContextFactory;
    private @Nullable ScheduledFuture<?> reloadSslFuture;

    /**
     * Starts this server.
     */
    public synchronized void start() {
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
            final var httpConnector = new ServerConnector(server, null, null, null, -1, -1, httpFactories);
            httpConnector.setHost(host);
            httpConnector.setPort(httpPort);
            final var connectionIdleTimeoutMillis = connectionIdleTimeout.toMillis();
            httpConnector.setIdleTimeout(connectionIdleTimeoutMillis);
            httpConnector.setShutdownIdleTimeout(connectionIdleTimeoutMillis);
            server.addConnector(httpConnector);
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
            final var httpsConnector = new ServerConnector(server, null, null, null, -1, -1, httpsFactories);
            httpsConnector.setHost(host);
            httpsConnector.setPort(httpsPort);
            final var connectionIdleTimeoutMillis = connectionIdleTimeout.toMillis();
            httpsConnector.setIdleTimeout(connectionIdleTimeoutMillis);
            httpsConnector.setShutdownIdleTimeout(connectionIdleTimeoutMillis);
            server.addConnector(httpsConnector);
        }
        server.setHandler(new GracefulHandler(new Abstract() {
            @Override
            public boolean handle(final org.eclipse.jetty.server.Request jettyRequest,
                    final org.eclipse.jetty.server.Response jettyResponse, final Callback callback) {
                Handle handle = null;
                try {
                    handle = handleFactory.create(new HandleInternals(JetServer.this, jettyRequest, jettyResponse));
                    Consumer<OutputStream> bodyOutputStreamApplier = null;
                    CompressionConfig.@Nullable Level compressionLevel = null;
                    try {
                        router.route(handle);
                        final var response = handle.getResponse();
                        final var headers = response.getHeaders();
                        if (preventMimeSniffing) {
                            headers.ensureEntryIgnoreCase(X_CONTENT_TYPE_OPTIONS.toString(), "nosniff");
                        }
                        if (preventAmbiguousResponseCacheControl && !headers.containsKey(CACHE_CONTROL.toString())) {
                            response.setCacheControl(NO_CACHE);
                        }
                        bodyOutputStreamApplier = response.getBodyOutputStreamApplier();
                        final var bodyInputStream = response.getBodyInputStream();
                        if (bodyInputStream != null) {
                            bodyOutputStreamApplier = outputStream -> {
                                try (bodyInputStream) {
                                    bodyInputStream.transferTo(outputStream);
                                } catch (final IOException ioException) {
                                    throw new UncheckedIOException(ioException);
                                }
                            };
                        }
                        compressionLevel = getCompressionLevel(handle, bodyOutputStreamApplier != null);
                    } catch (final Throwable throwable) {
                        final var response = handle.getResponse();
                        response.setStatusCode(INTERNAL_SERVER_ERROR_500.getCode());
                        response.getHeaders().clear();
                        response.setBodyInputStream(null);
                        response.setBodyOutputStreamApplier(null);
                        throwableHandler.handle(handle, throwable);
                    }
                    final var response = handle.getResponse();
                    jettyResponse.setStatus(response.getStatusCode());
                    jettyResponse.getHeaders().clear();
                    response.getHeaders().forEach(jettyResponse.getHeaders()::add);
                    if (bodyOutputStreamApplier != null) {
                        try (final var bodyOutputStream = asOutputStream(jettyResponse)) {
                            if (compressionLevel != null) {
                                try (final var compressedBodyOutputStream = compressionLevel.getType()
                                        .compress(bodyOutputStream, compressionLevel.getLevel())) {
                                    bodyOutputStreamApplier.accept(compressedBodyOutputStream);
                                }
                            } else {
                                bodyOutputStreamApplier.accept(bodyOutputStream);
                            }
                        } catch (final Throwable throwable) {
                            if (getCausalChain(throwable).stream().anyMatch(cause ->
                                    cause instanceof EofException || cause instanceof TimeoutException)) {
                                LOGGER.debug("Client early disconnect or idle timeout", throwable);
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
                        final var bodyInputStream = handle.getResponse().getBodyInputStream();
                        if (bodyInputStream != null) {
                            try {
                                bodyInputStream.close();
                            } catch (final Throwable throwable) {
                                LOGGER.error("`Response.getBodyInputStream().close()` threw", throwable);
                            }
                        }
                        if (afterRouter != null) {
                            try {
                                afterRouter.route(handle);
                            } catch (final Throwable throwable) {
                                LOGGER.error("`Jet.getAfterRouter().route()` threw", throwable);
                            }
                        }
                    }
                }
                callback.succeeded();
                return true;
            }

            private CompressionConfig.@Nullable Level getCompressionLevel(final Handle handle,
                    final boolean isBodyOutputStreamApplier) {
                final var response = handle.getResponse();
                final var compressionConfig = response.getCompressionConfig();
                if (compressionConfig == null) {
                    return null;
                }
                final var headers = response.getHeaders();
                if (compressionConfig.isEnsureVaryHeader()) {
                    headers.ensureEntryContainingIgnoreCase(VARY.toString(), ACCEPT_ENCODING.toString());
                }
                if (!isBodyOutputStreamApplier) {
                    return null;
                }
                if (compressionConfig.isCheckContentEncoding() && headers.containsKey(CONTENT_ENCODING.toString())) {
                    return null;
                }
                if (compressionConfig.isCheckContentRange() && headers.containsKey(CONTENT_RANGE.toString())) {
                    return null;
                }
                final var minimumContentLength = compressionConfig.getMinimumContentLength();
                if (minimumContentLength != null) {
                    final var contentLength = headers.getFirst(CONTENT_LENGTH.toString());
                    if (contentLength != null && parseLong(contentLength) < minimumContentLength) {
                        return null;
                    }
                }
                if (compressionConfig.isCheckContentType()) {
                    final var contentType = headers.getFirst(CONTENT_TYPE.toString());
                    if (contentType != null && ContentType.parse(contentType).isCompressed()) {
                        return null;
                    }
                }
                final var acceptEncoding = handle.getRequest().getAcceptEncoding();
                if (acceptEncoding == null) {
                    return null;
                }
                final var acceptEncodingTypes = acceptEncoding.getEntryTypes();
                final var compressionLevel = compressionConfig.getLevels().stream()
                        .filter(level -> acceptEncodingTypes.contains(level.getType()))
                        .findFirst()
                        .orElse(null);
                if (compressionLevel == null) {
                    return null;
                }
                headers.set(CONTENT_ENCODING.toString(), compressionLevel.getType().toString());
                headers.removeAll(CONTENT_LENGTH.toString());
                if (compressionConfig.isModifyETag()) {
                    final var eTagValue = headers.getFirst(ETAG.toString());
                    if (eTagValue != null) {
                        final var eTag = ETag.parse(eTagValue);
                        headers.put(ETAG.toString(), eTag.toBuilder()
                                .value(eTag.getValueWithoutCompressionType(), compressionLevel.getType())
                                .build().toString());
                    }
                }
                return compressionLevel;
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
        LOGGER.info("Jet started!");
        if (reloadSslPeriod != null) {
            final var periodSeconds = reloadSslPeriod.toSeconds();
            reloadSslFuture = commonPool().scheduleAtFixedRate(() -> Thread.ofVirtual().start(() -> {
                try {
                    reloadSsl();
                } catch (final Throwable throwable) {
                    LOGGER.error("Reload SSL threw", throwable);
                }
            }), periodSeconds, periodSeconds, SECONDS);
            LOGGER.info("Reloading SSL every {}.", reloadSslPeriod.toString().substring(2).toLowerCase(ROOT));
        }
    }

    private void setKeyStoreFromSslPemsSuppliers(final SslContextFactory sslContextFactory) {
        try {
            final var keyStore = KeyStore.getInstance("pem", JctProvider.getInstance());
            keyStore.load(new ByteArrayInputStream(sslPemsSuppliers.stream()
                    .flatMap(supplier -> supplier.get().stream())
                    .map(sslPem -> sslPem.privateKey + "\n" + sslPem.certificateChain)
                    .collect(joining()).getBytes(US_ASCII)), null);
            sslContextFactory.setKeyStore(keyStore);
        } catch (final KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException exception) {
            throw new RuntimeException(exception);
        }
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
        } catch (final Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Stops this server.
     */
    public synchronized void stop() {
        LOGGER.info("Jet stopping...");
        if (reloadSslFuture != null) {
            reloadSslFuture.cancel(false);
            reloadSslFuture = null;
        }
        sslContextFactory = null;
        if (server != null) {
            try {
                server.stop();
            } catch (final Exception exception) {
                throw new RuntimeException(exception);
            } finally {
                server = null;
            }
        }
        LOGGER.info("Jet stopped");
    }

    /**
     * @return <code>true</code> if stopped, <code>false</code> if started and running
     */
    public synchronized boolean isStopped() {
        return server == null || server.isStopped();
    }

    /**
     * @return <code>true</code> if HTTPS (SSL/TLS) is enabled, <code>false</code> otherwise
     */
    public boolean isHttps() {
        return sslContextFactory != null;
    }
}
