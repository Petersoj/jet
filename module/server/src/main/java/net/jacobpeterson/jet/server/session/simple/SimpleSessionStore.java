package net.jacobpeterson.jet.server.session.simple;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Getter;
import net.jacobpeterson.jet.common.http.header.cookie.Cookie;
import net.jacobpeterson.jet.common.http.header.cookie.CookieSameSite;
import net.jacobpeterson.jet.common.util.token.TokenUtil;
import net.jacobpeterson.jet.server.handle.Handle;
import net.jacobpeterson.jet.server.session.Session;
import net.jacobpeterson.jet.server.session.SessionStore;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static java.util.concurrent.TimeUnit.DAYS;
import static net.jacobpeterson.jet.common.http.header.cookie.CookieSameSite.LAX;
import static net.jacobpeterson.jet.common.util.token.TokenUtil.generateToken;

/**
 * {@link SimpleSessionStore} is a simple {@link SessionStore} implementation that uses a {@link Cache} to store
 * {@link SimpleSession}s.
 */
@NullMarked
@Getter
public class SimpleSessionStore implements SessionStore {

    /**
     * The {@link #getIdTokenLength()} default: <code>64</code>
     */
    public static final int DEFAULT_ID_TOKEN_LENGTH = 64;

    /**
     * The {@link #getCookieName()} default: <code>"session-id"</code>
     */
    public static final String DEFAULT_COOKIE_NAME = "session-id";

    /**
     * The {@link #getCookieModifier()} default: {@link Cookie.Builder#httpOnly()}, {@link CookieSameSite#LAX}, and
     * {@link Cookie.Builder#secure()}.
     */
    public static final Consumer<Cookie.Builder> DEFAULT_COOKIE_MODIFIER = builder -> builder
            .httpOnly()
            .sameSite(LAX)
            .secure();

    /**
     * The <code>length</code> to use for {@link TokenUtil#generateToken(int)} for the {@link Session#getId()} and
     * {@link #getCookieModifier()} {@link Cookie.Builder#value(String)}.
     */
    private final int idTokenLength;

    /** The session {@link Cookie#getName()}. */
    private final String cookieName;

    /**
     * The session {@link Cookie.Builder} {@link Consumer} to modify a new session {@link Cookie}.
     * <p>
     * Note: the {@link Cookie.Builder#name(String)} and {@link Cookie.Builder#value(String)} are already set.
     */
    private final @Nullable Consumer<Cookie.Builder> cookieModifier;

    /** The session {@link Cache}. */
    private final Cache<String, Session> sessionsOfIds;

    /**
     * Calls {@link #SimpleSessionStore(int, String, Consumer, Cache)} with {@link #DEFAULT_ID_TOKEN_LENGTH},
     * {@link #DEFAULT_COOKIE_NAME}, {@link #DEFAULT_COOKIE_MODIFIER}, and
     * {@link Caffeine#expireAfterAccess(long, TimeUnit)} set to three days
     */
    public SimpleSessionStore() {
        this(DEFAULT_ID_TOKEN_LENGTH, DEFAULT_COOKIE_NAME, DEFAULT_COOKIE_MODIFIER,
                Caffeine.newBuilder().expireAfterAccess(3, DAYS).build());
    }

    /**
     * Instantiates a new {@link SimpleSessionStore}.
     *
     * @param idTokenLength  the {@link #getIdTokenLength()}
     * @param cookieName     the {@link #getCookieName()}
     * @param cookieModifier the {@link #getCookieModifier()}
     * @param sessionsOfIds  the {@link #getSessionsOfIds()}
     */
    public SimpleSessionStore(final int idTokenLength, final String cookieName,
            final @Nullable Consumer<Cookie.Builder> cookieModifier, final Cache<String, Session> sessionsOfIds) {
        this.idTokenLength = idTokenLength;
        this.cookieName = cookieName;
        this.cookieModifier = cookieModifier;
        this.sessionsOfIds = sessionsOfIds;
    }

    @Override
    public Session getOrCreate(final Handle handle) {
        final var existingSession = get(handle);
        if (existingSession != null) {
            return existingSession;
        }
        final var id = generateToken(idTokenLength);
        final var cookie = Cookie.builder()
                .name(cookieName)
                .value(id);
        if (cookieModifier != null) {
            cookieModifier.accept(cookie);
        }
        handle.getResponse().addCookie(cookie.build());
        final var newSession = new SimpleSession(id);
        sessionsOfIds.put(id, newSession);
        return newSession;
    }

    @Override
    public @Nullable Session get(final Handle handle) {
        final var id = handle.getRequest().getCookie(cookieName);
        return id == null ? null : get(id);
    }

    @Override
    public @Nullable Session get(final String id) {
        return sessionsOfIds.getIfPresent(id);
    }
}
