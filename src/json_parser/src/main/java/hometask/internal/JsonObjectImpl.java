package hometask.internal;

import hometask.exceptions.JsonParserException;
import hometask.internal.util.JsonReaderUtil;
import hometask.internal.util.JsonToObjectWriterUtil;
import hometask.json.JsonObject;

import java.util.*;

public class JsonObjectImpl implements JsonObject {
    public final Map<String, Object> parsedJsonElements;

    public JsonObjectImpl(String jsonString) {
        this.parsedJsonElements = new JsonReaderUtil(jsonString).readObject(); // TODO
    }

    @Override
    public <T> T to(Class<T> targetType) throws JsonParserException {
        JsonToObjectWriterUtil jsonToObjectWriterUtil = new JsonToObjectWriterUtil();
        if (targetType.isRecord()) {
            return jsonToObjectWriterUtil.parseRecord(targetType, parsedJsonElements);
        }
        return jsonToObjectWriterUtil.parseObject(targetType, parsedJsonElements);
    }
}

