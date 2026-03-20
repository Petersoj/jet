package net.jacobpeterson.jet.server;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.jacobpeterson.jet.common.http.header.Header;
import net.jacobpeterson.jet.common.http.header.cachecontrol.response.ResponseCacheControl;
import net.jacobpeterson.jet.server.handle.Handle;
import net.jacobpeterson.jet.server.handle.HandleFactory;
import net.jacobpeterson.jet.server.handle.HandleInternals;
import net.jacobpeterson.jet.server.handle.request.Request;
import net.jacobpeterson.jet.server.handle.request.multipart.MultipartConfig;
import net.jacobpeterson.jet.server.handle.response.Response;
import net.jacobpeterson.jet.server.handler.handler.Handler;
import net.jacobpeterson.jet.server.handler.throwable.ThrowableHandler;
import net.jacobpeterson.jet.server.handler.throwable.simple.SimpleThrowableHandler;
import net.jacobpeterson.jet.server.route.router.Router;
import net.jacobpeterson.jet.server.route.router.simple.SimpleRouter;
import net.jacobpeterson.jet.server.session.SessionStore;
import net.jacobpeterson.jet.server.session.simple.SimpleSessionStore;
import net.jacobpeterson.jet.server.session.unsupported.UnsupportedSessionStore;
import org.eclipse.jetty.io.EofException;
import org.eclipse.jetty.server.Handler.Abstract;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.GracefulHandler;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.thread.VirtualThreadPool;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Throwables.getCausalChain;
import static java.time.Duration.ofMinutes;
import static lombok.AccessLevel.PRIVATE;
import static net.jacobpeterson.jet.common.http.header.Header.CACHE_CONTROL;
import static net.jacobpeterson.jet.common.http.header.Header.X_CONTENT_TYPE_OPTIONS;
import static net.jacobpeterson.jet.common.http.header.cachecontrol.response.ResponseCacheControl.NO_CACHE;
import static net.jacobpeterson.jet.common.http.status.Status.INTERNAL_SERVER_ERROR_500;
import static org.eclipse.jetty.io.Content.Sink.asOutputStream;

/**
 * {@link JetServer} is a simple, modern, turnkey, Java web server library.
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

        private @Nullable HandleFactory handleFactory;
        private int defaultRequestBodyBoundCount = 8 * 1024 * 1024;
        private @Nullable MultipartConfig defaultMultipartConfig;
        private boolean preventMimeSniffing = true;
        private boolean preventAmbiguousResponseCacheControl = true;
        private @Nullable SessionStore sessionStore;
        private @Nullable Router router;
        private @Nullable ThrowableHandler routerThrowableHandler;
        private @Nullable ThrowableHandler responseBodyThrowableHandler;
        private @Nullable Handler afterHandler;
        private @Nullable Duration serverGracefulStopTimeout;
        private @Nullable String serverBindAddress;
        private int serverHttpPort = 8080;
        private @Nullable Duration requestIdleTimeout;

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
         * @see #getRouterThrowableHandler()
         */
        public Builder routerThrowableHandler(final ThrowableHandler routerThrowableHandler) {
            this.routerThrowableHandler = routerThrowableHandler;
            return this;
        }

        /**
         * @see #getResponseBodyThrowableHandler()
         */
        public Builder responseBodyThrowableHandler(final ThrowableHandler responseBodyThrowableHandler) {
            this.responseBodyThrowableHandler = responseBodyThrowableHandler;
            return this;
        }

        /**
         * @see #getAfterHandler()
         */
        public Builder afterHandler(final Handler afterHandler) {
            this.afterHandler = afterHandler;
            return this;
        }

        /**
         * @see #getServerGracefulStopTimeout()
         */
        public Builder serverGracefulStopTimeout(final Duration serverGracefulStopTimeout) {
            this.serverGracefulStopTimeout = serverGracefulStopTimeout;
            return this;
        }

        /**
         * @see #getServerBindAddress()
         */
        public Builder serverBindAddress(final String serverBindAddress) {
            this.serverBindAddress = serverBindAddress;
            return this;
        }

        /**
         * @see #getServerHttpPort()
         */
        public Builder serverHttpPort(final int serverHttpPort) {
            this.serverHttpPort = serverHttpPort;
            return this;
        }

        /**
         * @see #getRequestIdleTimeout()
         */
        public Builder requestIdleTimeout(final Duration requestIdleTimeout) {
            this.requestIdleTimeout = requestIdleTimeout;
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
            return new JetServer(
                    handleFactory != null ? handleFactory : Handle::new,
                    defaultRequestBodyBoundCount,
                    defaultMultipartConfig != null ? defaultMultipartConfig : MultipartConfig.builder().build(),
                    preventMimeSniffing,
                    preventAmbiguousResponseCacheControl,
                    sessionStore != null ? sessionStore : new SimpleSessionStore(),
                    router != null ? router : new SimpleRouter(),
                    routerThrowableHandler != null ? routerThrowableHandler : new SimpleThrowableHandler(),
                    responseBodyThrowableHandler != null ? responseBodyThrowableHandler : new SimpleThrowableHandler(),
                    afterHandler,
                    serverGracefulStopTimeout != null ? serverGracefulStopTimeout : ofMinutes(1),
                    serverBindAddress,
                    serverHttpPort,
                    requestIdleTimeout != null ? requestIdleTimeout : ofMinutes(1));
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
     * Whether to call {@link Response#setHeader(Header, String)} with {@link Header#X_CONTENT_TYPE_OPTIONS} and
     * <code>"nosniff"</code> if {@link Header#X_CONTENT_TYPE_OPTIONS} is not already set.
     * <p>
     * Defaults to <code>true</code>.
     */
    private final @Getter boolean preventMimeSniffing;

    /**
     * Whether to call {@link Response#setHeader(Header, String)} with {@link Header#CACHE_CONTROL} and
     * {@link ResponseCacheControl#NO_CACHE} if {@link Header#CACHE_CONTROL} is not already set.
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
     * Defaults to {@link SimpleRouter}.
     */
    private final @Getter Router router;

    /**
     * The {@link ThrowableHandler} for {@link Throwable}s thrown by {@link Router#route(Handle)}.
     * <p>
     * Defaults to {@link SimpleThrowableHandler}.
     */
    private final @Getter ThrowableHandler routerThrowableHandler;

    /**
     * The {@link ThrowableHandler} for {@link Throwable}s thrown by writing a response body.
     * <p>
     * Defaults to {@link SimpleThrowableHandler}.
     */
    private final @Getter ThrowableHandler responseBodyThrowableHandler;

    /**
     * The {@link Handler} called after a {@link Handle} has been fully processed and the response body (if any) has
     * been written successfully or unsuccessfully.
     * <p>
     * Defaults to <code>null</code>.
     */
    private final @Getter @Nullable Handler afterHandler;

    /**
     * The {@link Duration} to wait for the server gracefully stop.
     * <p>
     * Defaults to <code>1 minute</code>.
     */
    private final @Getter Duration serverGracefulStopTimeout;

    /**
     * The internet address the server binds to, or <code>null</code> for all addresses.
     * <p>
     * Defaults to <code>null</code>.
     */
    private final @Getter @Nullable String serverBindAddress;

    /**
     * The HTTP port the server binds to.
     * <p>
     * Defaults to <code>8080</code>.
     */
    private final @Getter int serverHttpPort;

    /**
     * The {@link Duration} to wait for in-flight requests to complete.
     * <p>
     * Defaults to <code>1 minute</code>.
     */
    private final @Getter Duration requestIdleTimeout;

    private @Nullable Server server;

    /**
     * @return {@link #getRouter()} cast as {@link SimpleRouter}
     */
    public SimpleRouter getSimpleRouter() {
        return (SimpleRouter) router;
    }

    /**
     * Starts this server.
     */
    public synchronized void start() {
        server = new Server(new VirtualThreadPool(Integer.MAX_VALUE));
        server.setStopAtShutdown(true);
        server.setStopTimeout(serverGracefulStopTimeout.toMillis());

        final var httpConfiguration = new HttpConfiguration();
        httpConfiguration.setSendServerVersion(false);
        httpConfiguration.setIdleTimeout(requestIdleTimeout.toMillis());
        final var httpConnector = new ServerConnector(server, null, null, null, -1, -1,
                new HttpConnectionFactory(httpConfiguration));
        httpConnector.setHost(serverBindAddress);
        httpConnector.setPort(serverHttpPort);
        server.addConnector(httpConnector);

        // https://jetty.org/docs/jetty/12.1/programming-guide/server/http.html#handler-use-graceful
        server.setHandler(new GracefulHandler(new Abstract() {
            @Override
            public boolean handle(final org.eclipse.jetty.server.Request jettyRequest,
                    final org.eclipse.jetty.server.Response jettyResponse, final Callback callback) {
                Handle handle = null;
                final var closeResponseBodyInputStream = new AtomicBoolean(true);
                try {
                    handle = handleFactory.create(new HandleInternals(JetServer.this, jettyRequest, jettyResponse));
                    try {
                        router.route(handle);
                    } catch (final Throwable throwable) {
                        routerThrowableHandler.handle(handle, throwable);
                    }
                    final var response = handle.getResponse();
                    applyStatusAndHeaders(jettyResponse, response);
                    final var bodyInputStream = response.getBodyInputStream();
                    var bodyOutputStreamApplier = response.getBodyOutputStreamApplier();
                    if (bodyInputStream != null && bodyOutputStreamApplier != null) {
                        LOGGER.warn("`Response.getBodyInputStream()` and `Response.getBodyOutputStreamApplier()` " +
                                "should not both be set.");
                    }
                    if (bodyInputStream != null) {
                        bodyOutputStreamApplier = outputStream -> {
                            try (bodyInputStream) {
                                bodyInputStream.transferTo(outputStream);
                            } catch (final IOException ioException) {
                                throw new UncheckedIOException(ioException);
                            } finally {
                                closeResponseBodyInputStream.setPlain(false);
                            }
                        };
                    }
                    if (bodyOutputStreamApplier != null) {
                        final var bodyOutputStream = asOutputStream(jettyResponse);
                        try (bodyOutputStream) {
                            bodyOutputStreamApplier.accept(bodyOutputStream);
                        } catch (final Throwable throwable) {
                            // Do not call `responseBodyThrowableHandler` if `throwable` is due to client disconnect.
                            if (getCausalChain(throwable).stream().noneMatch(cause -> cause instanceof EofException)) {
                                try {
                                    responseBodyThrowableHandler.handle(handle, throwable);
                                } catch (final Throwable handleThrowable) {
                                    LOGGER.error("`Jet.getResponseBodyThrowableHandler().handle()` threw",
                                            handleThrowable);
                                }
                                if (!jettyResponse.isCommitted()) {
                                    applyStatusAndHeaders(jettyResponse, response);
                                }
                            } else if (LOGGER.isDebugEnabled()) {
                                // Above if-statement prevents superfluous `Object[]` creation from varargs.
                                final var request = handle.getRequest();
                                LOGGER.debug("Client disconnect for request: {} {} {}",
                                        request.getVersion(), request.getMethod(), request.getUrl());
                            }
                        }
                    }
                } catch (final Throwable throwable) {
                    LOGGER.error("Internal server error", throwable);
                    jettyResponse.setStatus(INTERNAL_SERVER_ERROR_500.getCode());
                }
                if (handle != null && closeResponseBodyInputStream.getPlain()) {
                    final var responseBodyInputStream = handle.getResponse().getBodyInputStream();
                    if (responseBodyInputStream != null) {
                        try {
                            responseBodyInputStream.close();
                        } catch (final Throwable throwable) {
                            LOGGER.error("`Response.getBodyInputStream().close()` threw", throwable);
                        }
                    }
                }
                if (handle != null && afterHandler != null) {
                    try {
                        afterHandler.handle(handle);
                    } catch (final Throwable throwable) {
                        LOGGER.error("`Jet.getAfterHandler().handle()` threw", throwable);
                    }
                }
                callback.succeeded();
                return true;
            }

            private void applyStatusAndHeaders(final org.eclipse.jetty.server.Response jettyResponse,
                    final Response response) {
                jettyResponse.setStatus(response.getStatusCode());
                if (preventMimeSniffing && !response.getHeaders().containsKey(X_CONTENT_TYPE_OPTIONS.toString())) {
                    response.setHeader(X_CONTENT_TYPE_OPTIONS, "nosniff");
                }
                if (preventAmbiguousResponseCacheControl &&
                        !response.getHeaders().containsKey(CACHE_CONTROL.toString())) {
                    response.setHeader(CACHE_CONTROL, NO_CACHE.toString());
                }
                jettyResponse.getHeaders().clear();
                response.getHeaders().forEach(jettyResponse.getHeaders()::add);
            }
        }));
        // Do not serve error info, as this causes information leakage and Jetty already logs errors.
        server.setErrorHandler(new Abstract() {
            @Override
            public boolean handle(final org.eclipse.jetty.server.Request request,
                    final org.eclipse.jetty.server.Response response, final Callback callback) {
                callback.succeeded();
                return true;
            }
        });
        try {
            server.start();
        } catch (final Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Stops this server.
     */
    public synchronized void stop() {
        if (server == null) {
            return;
        }
        try {
            server.stop();
        } catch (final Exception exception) {
            throw new RuntimeException(exception);
        }
        server = null;
    }
}
