package net.jacobpeterson.jet.common.http.header.headers;

import com.google.common.collect.Multimap;
import com.google.errorprone.annotations.Immutable;
import org.jspecify.annotations.NullMarked;

/**
 * {@link Headers} is the {@link Immutable} {@link AbstractHeaders} implementation.
 */
@NullMarked
@Immutable
@SuppressWarnings("Immutable")
public final class ImmutableHeaders extends AbstractHeaders {

    /**
     * Creates a new {@link ImmutableHeaders} instance.
     *
     * @param headers the existing headers {@link Multimap}
     *
     * @return the new {@link ImmutableHeaders} instance
     */
    public static ImmutableHeaders create(final Multimap<String, String> headers) {
        return new ImmutableHeaders(headers);
    }

    private ImmutableHeaders(final Multimap<String, String> headers) {
        super(headers);
    }
}
