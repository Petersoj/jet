package net.jacobpeterson.jet.common.http.header;

import com.google.common.collect.ForwardingListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Multimaps;
import com.google.errorprone.annotations.Immutable;
import org.jspecify.annotations.NullMarked;

import static com.google.common.collect.Multimaps.unmodifiableListMultimap;
import static java.lang.String.CASE_INSENSITIVE_ORDER;

/**
 * {@link ImmutableHeaders} is identical to {@link Headers}, but made {@link Immutable} using
 * {@link Multimaps#unmodifiableListMultimap(ListMultimap)}.
 */
@NullMarked
@Immutable
public class ImmutableHeaders extends ForwardingListMultimap<String, String> {

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

    private final @SuppressWarnings("Immutable") ListMultimap<String, String> delegate;

    private ImmutableHeaders(final Multimap<String, String> headers) {
        final var mutableDelegate = MultimapBuilder.ListMultimapBuilder
                .treeKeys(CASE_INSENSITIVE_ORDER)
                .arrayListValues(1)
                .<String, String>build();
        mutableDelegate.putAll(headers);
        delegate = unmodifiableListMultimap(mutableDelegate);
    }

    @Override
    protected ListMultimap<String, String> delegate() {
        return delegate;
    }
}
