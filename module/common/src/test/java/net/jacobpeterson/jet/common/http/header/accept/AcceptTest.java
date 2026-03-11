package net.jacobpeterson.jet.common.http.header.accept;

import com.google.common.collect.ImmutableList;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static net.jacobpeterson.jet.common.http.header.accept.Accept.WEIGHT_PARAMETER_KEY;
import static net.jacobpeterson.jet.common.http.header.contenttype.ContentType.APPLICATION_XHTML;
import static net.jacobpeterson.jet.common.http.header.contenttype.ContentType.APPLICATION_XML;
import static net.jacobpeterson.jet.common.http.header.contenttype.ContentType.TEXT_HTML;
import static net.jacobpeterson.jet.common.http.header.contenttype.ContentType.WILDCARD_WILDCARD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@NullMarked
public final class AcceptTest {

    @Test
    public void parse() {
        {
            final var accept = Accept.parse("text/html, application/xhtml+xml, application/xml;q=0.9, */*;q=0.8");
            assertEquals(ImmutableList.of(
                            TEXT_HTML,
                            APPLICATION_XHTML,
                            APPLICATION_XML.withParameter(WEIGHT_PARAMETER_KEY, "0.9"),
                            WILDCARD_WILDCARD.withParameter(WEIGHT_PARAMETER_KEY, "0.8")),
                    accept.getContentTypes());
        }
        {
            final var accept = Accept.parse("   */* ");
            assertEquals(ImmutableList.of(WILDCARD_WILDCARD), accept.getContentTypes());
        }
        assertThrows(IllegalArgumentException.class, () -> Accept.parse(""));
        assertThrows(IllegalArgumentException.class, () -> Accept.parse(" "));
        assertThrows(IllegalArgumentException.class, () -> Accept.parse(" *"));
        assertThrows(IllegalArgumentException.class, () -> Accept.parse(" */"));
        assertThrows(IllegalArgumentException.class, () -> Accept.parse(" /*"));
    }

    @Test
    public void _toString() {
        assertEquals("text/html", Accept.builder()
                .add(TEXT_HTML)
                .build().toString());
        assertEquals("text/html, */*; q=0.9", Accept.builder()
                .addAll(TEXT_HTML, WILDCARD_WILDCARD.withParameter(WEIGHT_PARAMETER_KEY, "0.9"))
                .build().toString());
    }
}
