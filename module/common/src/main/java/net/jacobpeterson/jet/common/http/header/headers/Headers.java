package net.jacobpeterson.jet.common.http.header.headers;

import org.jspecify.annotations.NullMarked;

/**
 * {@link Headers} is the mutable {@link AbstractHeaders} implementation.
 */
@NullMarked
public final class Headers extends AbstractHeaders {

    /**
     * Create a new {@link Headers} instance.
     *
     * @return the new {@link Headers} instance
     */
    public static Headers create() {
        return new Headers();
    }

    private Headers() {
        super(null);
    }
}
