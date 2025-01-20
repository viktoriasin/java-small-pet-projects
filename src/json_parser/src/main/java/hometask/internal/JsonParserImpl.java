package hometask.internal;

import hometask.internal.util.JsonToStringWriterUtil;
import hometask.json.JsonObject;
import hometask.json.JsonParser;

public class JsonParserImpl implements JsonParser {
    public static String writeToString(Object value) {
        return JsonToStringWriterUtil.writeObject(value);
    }

    private static JsonObject parse(String json) {
        return new JsonObjectImpl(json);
    }
}
