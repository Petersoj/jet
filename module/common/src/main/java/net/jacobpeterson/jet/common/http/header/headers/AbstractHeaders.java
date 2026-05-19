package net.jacobpeterson.jet.common.http.header.headers;

import com.google.common.collect.ForwardingListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder.ListMultimapBuilder;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static com.google.common.collect.Multimaps.unmodifiableListMultimap;
import static java.lang.String.CASE_INSENSITIVE_ORDER;

/**
 * {@link AbstractHeaders} is a case-insensitive {@link String} {@link ListMultimap} that uses
 * {@link ListMultimapBuilder#treeKeys()} with {@link String#CASE_INSENSITIVE_ORDER}.
 */
@NullMarked
public sealed abstract class AbstractHeaders extends ForwardingListMultimap<String, String>
        permits Headers, ImmutableHeaders {

    private final ListMultimap<String, String> delegate;

    /**
     * Instantiates a new {@link AbstractHeaders}.
     *
     * @param headers non-<code>null</code> for {@link ImmutableHeaders}, <code>null</code> for {@link Headers}
     */
    public AbstractHeaders(final @Nullable Multimap<String, String> headers) {
        final var delegate = ListMultimapBuilder
                .treeKeys(CASE_INSENSITIVE_ORDER)
                .arrayListValues(1)
                .<String, String>build();
        if (headers == null) {
            this.delegate = delegate;
        } else {
            delegate.putAll(headers);
            this.delegate = unmodifiableListMultimap(delegate);
        }
    }

    @Override
    protected ListMultimap<String, String> delegate() {
        return delegate;
    }
}
