package net.jacobpeterson.jet.openapiannotations.plugin.util.gson;

import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@NullMarked
public final class GsonUtilTest {

    @Test
    public void combine() {
        assertEquals(JsonParser.parseString("null"),
                GsonUtil.combine(JsonParser.parseString("null"), JsonParser.parseString("null")));
        assertEquals(JsonParser.parseString("{}"),
                GsonUtil.combine(JsonParser.parseString("{}"), JsonParser.parseString("null")));
        assertEquals(JsonParser.parseString("{}"),
                GsonUtil.combine(JsonParser.parseString("null"), JsonParser.parseString("{}")));
        assertEquals(JsonParser.parseString("""
                        [
                          "a",
                          "b"
                        ]
                        """),
                GsonUtil.combine(JsonParser.parseString("""
                                ["a"]
                                """),
                        JsonParser.parseString("""
                                ["b"]
                                """)));
        assertEquals(JsonParser.parseString("""
                        {
                          "a": true,
                          "b": [
                            "a",
                            "a"
                          ],
                          "d": {
                            "e": true,
                            "f": false
                          }
                        }
                        """),
                GsonUtil.combine(JsonParser.parseString("""
                                {
                                  "a": true,
                                  "b": [
                                    "a"
                                  ],
                                  "d": {
                                    "f": false
                                  }
                                }
                                """),
                        JsonParser.parseString("""
                                {
                                  "b": [
                                    "a"
                                  ],
                                  "d": {
                                    "e": true
                                  }
                                }
                                """)));
    }

    @Test
    public void walk() {
        final var primitives = new ArrayList<JsonPrimitive>();
        GsonUtil.walk(JsonParser.parseString("""
                {
                  "a": true,
                  "b": {
                    "c": "d",
                    "b": {
                      "c": "d"
                    }
                  },
                  "e": [
                    "f"
                  ]
                }
                """), stack -> {
            final var top = requireNonNull(stack.peek()).getValue();
            if (top.isJsonPrimitive()) {
                primitives.add(top.getAsJsonPrimitive());
            }
            return true;
        });
        assertEquals(List.of(new JsonPrimitive(true), new JsonPrimitive("d"),
                new JsonPrimitive("d"), new JsonPrimitive("f")), primitives);
    }
}
