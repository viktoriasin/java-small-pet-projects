package hometask.test.data;

import hometask.json.JsonParser;
import hometask.test.data.ClassWithCollections.StringWrapper;
import hometask.test.data.RecordWithCollections.NestedValue;
import org.junit.Test;


import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonParserNestedTest {
//
//    @Test
//    public void shouldParseJsonToSimpleNestedRecord() {
//        // given
//        var expected = new SimpleNestedRecord(42, new SimpleNestedRecord.Inner("value"));
//
//        // language=json
//        String json = """
//            {
//              "i_value": 42,
//              "inner_value": {
//                "s": "value"
//              }
//            }
//        """;
//
//        // when
//        SimpleNestedRecord parsed = JsonParser.parse(json).to(SimpleNestedRecord.class);
//
//        // then
//        assertEquals(expected, parsed);
//    }
//
//    @Test
//    public void shouldParseJsonToRecursiveRecord() {
//        // given
//        var expected = new RecursiveRecord(42,
//                new RecursiveRecord(43,
//                        new RecursiveRecord(44,
//                                new RecursiveRecord(45, null))));
//
//        // when
//        RecursiveRecord parsed = JsonParser.parse(nestedJson()).to(RecursiveRecord.class);
//
//        // then
//        assertEquals(expected, parsed);
//    }
//
//    @Test
//    public void shouldParseJsonToRecursiveClass() {
//        // given
//        var expected = new RecursiveClass(42,
//                new RecursiveClass(43,
//                        new RecursiveClass(44,
//                                new RecursiveClass(45, null))));
//
//        // when
//        RecursiveClass parsed = JsonParser.parse(nestedJson()).to(RecursiveClass.class);
//
//        // then
//        assertEquals(expected, parsed);
//    }
//
//    @Test
//    public void shouldParseJsonToRecordWithCollections() {
//        // given
//
//        // language=json
//        var json = """
//            {
//              "doubles": [2.0, 1.0],
//              "ints": [43, 42, 44],
//              "int_array": [12, 21],
//              "values": [{"s": "s1"}, {"s": "s2"}]
//            }
//        """;
//
//        // when
//        RecordWithCollections parsed = JsonParser.parse(json).to(RecordWithCollections.class);
//
//        // then
//        assertEquals(List.of(2.0, 1.0), parsed.doubles());
//        assertEquals(Set.of(42, 43, 44), parsed.ints());
//        assertArrayEquals(new int[]{12, 21}, parsed.intArray());
//        assertEquals(List.of(new NestedValue("s1"), new NestedValue("s2")), parsed.values());
//    }
//
//    @Test
//    public void shouldParseJsonToClassWithCollections() {
//        // given
//
//        // language=json
//        var json = """
//            {
//              "ints": [43, 42, 44],
//              "deep_nested_values": [[[[[{"s": "s1"}, {"s": "s2"}], [{"s": "s3"}]]]]]
//            }
//        """;
//
//        // when
//        ClassWithCollections parsed = JsonParser.parse(json).to(ClassWithCollections.class);
//
//        // then
//        assertEquals(List.of(43, 42, 44), parsed.ints);
//
//        List<List<StringWrapper>> forthLevel = parsed.deepNestedValues.getFirst().getFirst().getFirst();
//        assertEquals(List.of(new StringWrapper("s1"), new StringWrapper("s2")), forthLevel.getFirst());
//        assertEquals(List.of(new StringWrapper("s3")), forthLevel.getLast());
//    }
//
//    // language=json
//    private String nestedJson() {
//        return """
//            {
//              "i": 42,
//              "nested": {
//                "i": 43,
//                "nested": {
//                  "i": 44,
//                  "nested": {
//                    "i": 45
//                  }
//                }
//              }
//            }
//        """;
//    }
}
