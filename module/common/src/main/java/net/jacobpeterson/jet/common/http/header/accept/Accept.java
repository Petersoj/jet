package net.jacobpeterson.jet.common.http.header.accept;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.errorprone.annotations.Immutable;
import com.google.errorprone.annotations.concurrent.LazyInit;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.jacobpeterson.jet.common.http.header.Header;
import net.jacobpeterson.jet.common.http.header.contenttype.ContentType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.stream.Collectors.joining;
import static lombok.AccessLevel.PRIVATE;
import static lombok.EqualsAndHashCode.CacheStrategy.LAZY;

/**
 * {@link Accept} is an immutable class that represents a standardized HTTP {@link Header#ACCEPT}.
 * <p>
 * The HTTP <strong><code>Accept</code></strong>
 * <a href="https://developer.mozilla.org/en-US/docs/Glossary/Request_header">request</a> and
 * <a href="https://developer.mozilla.org/en-US/docs/Glossary/Response_header">response header</a> indicates which
 * content types, expressed as <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/MIME_types">MIME
 * types</a>, the sender is able to understand. In requests, the server uses
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Content_negotiation">content negotiation</a> to
 * select one of the proposals and informs the client of the choice with the
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Type">
 * <code>Content-Type</code></a> response header. In responses, it provides information about which content types the
 * server can understand in messages to the requested resource, so that the content type can be used in subsequent
 * requests to the resource.
 * <p>
 * Browsers set required values for this header based on the context of the request. For example, a browser uses
 * different values in a request when fetching a CSS stylesheet, image, video, or a script.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Accept">developer.mozilla.org</a>
 * @see Header#ACCEPT
 */
@NullMarked
@Immutable
@RequiredArgsConstructor(access = PRIVATE) @EqualsAndHashCode(onlyExplicitlyIncluded = true, cacheStrategy = LAZY)
public final class Accept {

    /**
     * The weight parameter key: <code>"q"</code>
     * <p>
     * A value in order of preference expressed using a relative
     * <a href="https://developer.mozilla.org/en-US/docs/Glossary/Quality_values">quality value</a> called the
     * <em>weight</em>.
     */
    public static final String WEIGHT_PARAMETER_KEY = "q";

    /**
     * The content types delimiter: <code>","</code>
     */
    public static final String CONTENT_TYPES_DELIMITER = ",";

    private static final Splitter PARSE_CONTENT_TYPES_SPLITTER =
            Splitter.on(CONTENT_TYPES_DELIMITER).trimResults().omitEmptyStrings();

    /**
     * Parses the given {@link Header#ACCEPT} value {@link String} into an {@link Accept}.
     *
     * @param accept the {@link Header#ACCEPT} value {@link String}
     *
     * @return the {@link Accept}
     *
     * @throws IllegalArgumentException thrown upon parsing failure
     */
    public static Accept parse(final String accept) throws IllegalArgumentException {
        return builder()
                .addAll(PARSE_CONTENT_TYPES_SPLITTER.splitToStream(accept)
                        .map(ContentType::parse)
                        .collect(toImmutableList()))
                .build();
    }

    /**
     * Creates a {@link Builder}.
     *
     * @return the {@link Builder}
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * {@link Builder} is a builder class for {@link Accept}.
     *
     * @see #builder()
     */
    public static final class Builder {

        private final ImmutableList.Builder<ContentType> contentTypes = ImmutableList.builder();

        /**
         * @see #getContentTypes()
         */
        public Builder add(final ContentType contentType) {
            contentTypes.add(contentType);
            return this;
        }

        /**
         * @see #getContentTypes()
         */
        public Builder addAll(final ContentType... contentType) {
            contentTypes.add(contentType);
            return this;
        }

        /**
         * @see #getContentTypes()
         */
        public Builder addAll(final Iterable<ContentType> contentType) {
            contentTypes.addAll(contentType);
            return this;
        }

        /**
         * Builds this {@link Builder} into a new {@link Accept} instance.
         *
         * @return the built {@link Accept}
         *
         * @throws IllegalArgumentException thrown if one of the {@link #add(ContentType)} overloads was never called
         */
        public Accept build() throws IllegalArgumentException {
            final var contentTypes = this.contentTypes.build();
            checkArgument(!contentTypes.isEmpty(), "`accept()` was never called");
            return new Accept(contentTypes);
        }
    }

    /**
     * The {@link ImmutableList} of {@link ContentType}s.
     */
    private final @Getter @EqualsAndHashCode.Include ImmutableList<ContentType> contentTypes;

    private @LazyInit @Nullable String string;

    /**
     * @return internally-cached {@link String} value for {@link Header#ACCEPT}
     *
     * @see #parse(String)
     */
    @Override
    public String toString() {
        if (string == null) {
            string = contentTypes.stream()
                    .map(ContentType::toString)
                    .collect(joining(CONTENT_TYPES_DELIMITER + " "));
        }
        return string;
    }
}
