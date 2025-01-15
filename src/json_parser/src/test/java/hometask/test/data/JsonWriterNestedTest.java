package hometask.test.data;

import hometask.json.JsonParser;
import hometask.test.data.ClassWithCollections.StringWrapper;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.List;

class JsonWriterNestedTest {

    @Test
    void shouldParseJsonToSimpleNestedRecord() throws JSONException {
        // given
        var record = new SimpleNestedRecord(42, new SimpleNestedRecord.Inner("value"));

        // language=json
        String expected = """
            {
              "i_value": 42,
              "inner_value": {
                "s": "value"
              }
            }
        """;

        // when
        String json = JsonParser.writeToString(record);

        // then
        JSONAssert.assertEquals(expected, json, true);
    }

    @Test
    void shouldParseJsonToClassWithCollections() throws JSONException {
        // given
        var classWithCollections = new ClassWithCollections();
        classWithCollections.ints = List.of(43, 42, 44);
        classWithCollections.deepNestedValues = List.of(List.of(List.of(List.of(
                List.of(new StringWrapper("s1"), new StringWrapper("s2")),
                List.of(new StringWrapper("s3"))))));

        // language=json
        var expected = """
            {
              "ints": [43, 42, 44],
              "deep_nested_values": [[[[[{"s": "s1"}, {"s": "s2"}], [{"s": "s3"}]]]]]
            }
        """;

        // when
        String json = JsonParser.writeToString(classWithCollections);

        // then
        JSONAssert.assertEquals(expected, json, true);
    }
}
