package net.jacobpeterson.jet.openapiannotations.plugin.util.gson;

import com.google.gson.JsonParser;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

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
}
