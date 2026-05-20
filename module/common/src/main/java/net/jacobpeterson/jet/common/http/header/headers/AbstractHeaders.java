package net.jacobpeterson.jet.common.http.header.headers;

import com.google.common.collect.ForwardingListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder.ListMultimapBuilder;
import net.jacobpeterson.jet.common.util.string.StringUtil;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

import static com.google.common.collect.Multimaps.unmodifiableListMultimap;
import static java.lang.String.CASE_INSENSITIVE_ORDER;
import static java.lang.String.join;
import static net.jacobpeterson.jet.common.util.string.StringUtil.containsIgnoreCase;

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

    /**
     * @return {@link #get(Object)} {@link List#getFirst()} or <code>null</code>
     */
    public @Nullable String getFirst(final String key) {
        final var values = get(key);
        return values.isEmpty() ? null : values.getFirst();
    }

    /**
     * @return {@link #getDelimited(String, String)} with <code>","</code>
     */
    public @Nullable String getCommaDelimited(final String key) {
        return getDelimited(key, ",");
    }

    /**
     * @return {@link #getDelimited(String, String)} with <code>";"</code>
     */
    public @Nullable String getSemicolonDelimited(final String key) {
        return getDelimited(key, ";");
    }

    /**
     * @return {@link #get(Object)} {@link String#join(CharSequence, Iterable)} or <code>null</code>
     */
    public @Nullable String getDelimited(final String key, final String delimiter) {
        final var values = get(key);
        if (values.isEmpty()) {
            return null;
        }
        if (values.size() == 1) {
            return values.getFirst();
        }
        return join(delimiter, values);
    }

    /**
     * @return <code>true</code> if {@link #get(Object)} with the given <code>key</code> contains a value where
     * {@link String#equalsIgnoreCase(String)} is <code>true</code>, <code>false</code> otherwise
     */
    public boolean containsEntryIgnoreCase(final String key, final String value) {
        for (final var valueOfKey : get(key)) {
            if (valueOfKey.equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return <code>true</code> if {@link #get(Object)} with the given <code>key</code> contains a value where
     * {@link String#contains(CharSequence)} is <code>true</code>, <code>false</code> otherwise
     */
    public boolean containsEntryContaining(final String key, final String value) {
        for (final var valueOfKey : get(key)) {
            if (valueOfKey.contains(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return <code>true</code> if {@link #get(Object)} with the given <code>key</code> contains a value where
     * {@link StringUtil#containsIgnoreCase(String, String)} is <code>true</code>, <code>false</code> otherwise
     */
    public boolean containsEntryContainingIgnoreCase(final String key, final String value) {
        for (final var valueOfKey : get(key)) {
            if (containsIgnoreCase(valueOfKey, value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected ListMultimap<String, String> delegate() {
        return delegate;
    }
}
