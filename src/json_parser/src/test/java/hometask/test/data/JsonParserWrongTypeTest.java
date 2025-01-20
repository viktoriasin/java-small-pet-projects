package hometask.test.data;

import hometask.json.JsonObject;
import hometask.json.JsonParser;
import hometask.exceptions.JsonParserException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JsonParserWrongTypeTest {

    @Test
    void shouldThrowJsonParserExceptionWhenParsedBooleanAsInt() {
        // given
        String json = "{\"id\": true}";

        // when
        JsonObject ir = JsonParser.parse(json);

        // then
        assertThrows(JsonParserException.class, () -> ir.to(IntWrapper.class));
        assertEquals(new BooleanWrapper(true), ir.to(BooleanWrapper.class));
    }

    @Test
    void shouldThrowJsonParserExceptionWhenParsedIntAsAString() {
        // given
        String json = "{\"id\": \"42\"}";

        // when
        JsonObject ir = JsonParser.parse(json);

        // then
        assertThrows(JsonParserException.class, () -> ir.to(IntWrapper.class));
        assertEquals(new StringWrapper("42"), ir.to(StringWrapper.class));
    }

    @Test
    void shouldThrowJsonParserExceptionWhenParsedStringAsAnInteger() {
        // given
        String json = "{\"id\": 42}";

        // when
        JsonObject ir = JsonParser.parse(json);

        // then
        assertThrows(JsonParserException.class, () -> ir.to(StringWrapper.class));
        assertEquals(new IntWrapper(42), ir.to(IntWrapper.class));
    }

    public record BooleanWrapper(boolean id) {}
    public record IntWrapper(int id) {}
    public record StringWrapper(String id) {}
}
