package json_parser.internal;

import json_parser.internal.handlers.AnnotationHandler;
import json_parser.internal.util.JsonReaderUtil;
import json_parser.internal.util.JsonToObjectWriterUtil;
import json_parser.json.JsonObject;

import java.lang.reflect.*;
import java.util.*;

public class JsonObjectImpl implements JsonObject {
    public final Map<String, Object> parsedJsonElements;

    public JsonObjectImpl(String jsonString) {
        this.parsedJsonElements = new JsonReaderUtil(jsonString).readObject(); // TODO
    }

    @Override
    public <T> T to(Class<T> targetType) throws Exception {
        JsonToObjectWriterUtil jsonToObjectWriterUtil = new JsonToObjectWriterUtil();
        if (targetType.isRecord()) {
            return jsonToObjectWriterUtil.parseRecord(targetType, parsedJsonElements);
        }
        return jsonToObjectWriterUtil.parseObject(targetType, parsedJsonElements);
    }
}

