package hometask.test.data;

import hometask.json.JsonParser;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

class JsonWriterTest {

    @Test
    void shouldWriteRecordToJson() throws JSONException {
        // given
        var record = new FullRecord(42, true, 1.1, false, 2.2, 43, "value");

        // when
        String json = JsonParser.writeToString(record);

        // then
        JSONAssert.assertEquals(allTypesJson(), json, true);
    }

    @Test
    void shouldWriteClassToJson() throws JSONException {
        // given
        var fullClass = new FullClass();
        fullClass.setI(42);
        fullClass.setB(true);
        fullClass.setDoubleWrapper(2.2);
        fullClass.setS("value");
        fullClass.intWrapper = 43;
        fullClass.boolWrapper = false;
        fullClass.d = 1.1;

        // when
        String json = JsonParser.writeToString(fullClass);

        // then
        JSONAssert.assertEquals(allTypesJson(), json, true);
    }

    @Test
    void shouldWriteRecordToJsonIgnoringNullValues() throws JSONException {
        // given
        var recordWithNulls = new FullRecord(42, true, 1.1, null, null, null, null);

        // when
        String json = JsonParser.writeToString(recordWithNulls);

        // then
        JSONAssert.assertEquals(shortJson(), json, true);
    }

    @Test
    void shouldWriteClassToJsonIgnoringNullValues() throws JSONException {
        // given
        var classWithNulls = new FullClass();
        classWithNulls.setI(42);
        classWithNulls.setB(true);
        classWithNulls.setDoubleWrapper(null);
        classWithNulls.setS(null);
        classWithNulls.intWrapper = null;
        classWithNulls.boolWrapper = null;
        classWithNulls.d = 1.1;

        // when
        String json = JsonParser.writeToString(classWithNulls);

        // then
        JSONAssert.assertEquals(shortJson(), json, true);
    }

    @Test
    void shouldWriteRecordToJsonUsingJsonProperty() throws JSONException {
        // given
        var record = new RecordWithJsonProperty(42, true, "value");

        // when
        String json = JsonParser.writeToString(record);

        // then
        JSONAssert.assertEquals(boolIntStringJson(), json, true);
    }

    @Test
    void shouldWriteClassToJsonUsingJsonProperty() throws JSONException {
        // given
        var instance = new ClassWithJsonProperty();
        instance.setIntValue(42);
        instance.boolValue = true;
        instance.stringValue = "value";

        // when
        String json = JsonParser.writeToString(instance);

        // then
        JSONAssert.assertEquals(boolIntStringJson(), json, true);
    }

    // language=json
    private String allTypesJson() {
        return """
            {
              "b": true,
              "i": 42,
              "d": 1.1,
              "boolWrapper": false,
              "intWrapper": 43,
              "doubleWrapper": 2.2,
              "s": "value"
            }
        """;
    }

    // language=json
    private String shortJson() {
        return """
            {
              "b": true,
              "i": 42,
              "d": 1.1
            }
        """;
    }

    // language=json
    private String boolIntStringJson() {
        return """
            {
              "b": true,
              "i": 42,
              "s": "value"
            }
        """;
    }
}
