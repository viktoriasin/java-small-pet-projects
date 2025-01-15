package hometask.internal;

import json_parser.internal.util.JsonToStringWriterUtil;
import json_parser.json.JsonObject;
import json_parser.json.JsonParser;

public class JsonParserImpl implements JsonParser {
    public static String writeToString(Object value) throws Exception {
        return JsonToStringWriterUtil.writeObject(value);
    }

    private static JsonObject parse(String json) {
        return new JsonObjectImpl(json);
    }
}
