package hometask.test.data;

import hometask.json.JsonObject;
import hometask.json.JsonParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsonParserTest {

    @Test
    void shouldParseJsonToRecord() {
        // given
        var expected = new FullRecord(42, true, 1.0, false, 2.0, 43, "value");

        // when
        FullRecord parsed = JsonParser.parse(allTypesJson()).to(FullRecord.class);

        // then
        assertEquals(expected, parsed);
    }

    @Test
    void shouldParseJsonToClass() {
        // when
        FullClass parsed = JsonParser.parse(allTypesJson()).to(FullClass.class);

        // then
        assertEquals(42, parsed.getI());
        assertEquals(43, parsed.intWrapper);
        assertEquals(1.0, parsed.d);
        assertEquals(2.0, parsed.getDoubleWrapper());
        assertTrue(parsed.getB());
        assertFalse(parsed.boolWrapper);
        assertEquals("value", parsed.getS());
    }

    @Test
    void shouldParseJsonSeveralTimesFromIR() {
        // given
        JsonObject ir = JsonParser.parse(allTypesJson());

        // when
        FullRecord firstParsed = ir.to(FullRecord.class);
        FullRecord secondParsed = ir.to(FullRecord.class);

        // then
        assertEquals(firstParsed, secondParsed);
    }

    @Test
    void shouldParseJsonToDifferentTypesFromIR() {
        // given
        JsonObject ir = JsonParser.parse(allTypesJson());

        // when
        FullRecord fullRecord = ir.to(FullRecord.class);
        FullClass fullClass = ir.to(FullClass.class);

        // then
        assertEquals(42, fullRecord.i());
        assertEquals(42, fullClass.getI());
    }

    @Test
    void shouldParseJsonIgnoringOtherKeys() {
        // when
        ShortRecord record = JsonParser.parse(allTypesJson()).to(ShortRecord.class);

        // then
        assertEquals(42, record.i());
        assertTrue(record.b());
    }

    @Test
    void shouldParseJsonToRecordUsingJsonProperty() {
        // when
        RecordWithJsonProperty record = JsonParser.parse(allTypesJson()).to(RecordWithJsonProperty.class);

        // then
        assertEquals(42, record.intValue());
        assertTrue(record.boolValue());
        assertEquals("value", record.stringValue());
    }

    @Test
    void shouldParseJsonToClassUsingJsonProperty() {
        // when
        ClassWithJsonProperty instance = JsonParser.parse(allTypesJson()).to(ClassWithJsonProperty.class);

        // then
        assertEquals(42, instance.getIntValue());
        assertTrue(instance.boolValue);
        assertEquals("value", instance.stringValue);
    }

    @Test
    void shouldParseEmptyJsonToClass() {
        // when
        ClassWithJsonProperty instance = JsonParser.parse("{}").to(ClassWithJsonProperty.class);

        // then
        assertEquals(0, instance.getIntValue());
        assertFalse(instance.boolValue);
        assertNull(instance.stringValue);
    }

    // language=json
    private String allTypesJson() {
        return """
            {
              "b": true,
              "i": 42,
              "d": 1.0,
              "boolWrapper": false,
              "intWrapper": 43,
              "doubleWrapper": 2.0,
              "s": "value"
            }
        """;
    }
}
