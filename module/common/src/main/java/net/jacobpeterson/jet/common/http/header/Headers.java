package net.jacobpeterson.jet.common.http.header;

import com.google.common.collect.ForwardingListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder.ListMultimapBuilder;
import org.jspecify.annotations.NullMarked;

import static java.lang.String.CASE_INSENSITIVE_ORDER;

/**
 * {@link Headers} is a case-insensitive {@link String} {@link ListMultimap} that uses
 * {@link ListMultimapBuilder#treeKeys()} with {@link String#CASE_INSENSITIVE_ORDER}.
 */
@NullMarked
public class Headers extends ForwardingListMultimap<String, String> {

    /**
     * Create a new {@link Headers} instance.
     *
     * @return the new {@link Headers} instance
     */
    public static Headers create() {
        return new Headers();
    }

    private final ListMultimap<String, String> delegate;

    private Headers() {
        delegate = ListMultimapBuilder
                .treeKeys(CASE_INSENSITIVE_ORDER)
                .arrayListValues(1)
                .build();
    }

    @Override
    protected ListMultimap<String, String> delegate() {
        return delegate;
    }
}
