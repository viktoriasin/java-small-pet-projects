package json_parser.internal;

import json_parser.json.JsonObject;
import json_parser.json.JsonParser;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

public class JsonParserImpl implements JsonParser {
    public static String writeToString(Object value) throws IllegalAccessException {
        return writeObject(value);
    }

    static JsonObject parse(String json) {
        return new JsonObjectImpl(json);
    }

    private static String write(Object value) throws IllegalAccessException {
        if (value != null) {
            if (value.getClass().isArray()) {
                return writeArray((Object[]) value);
            } else if (Collection.class.isAssignableFrom(value.getClass())) {
                return writeCollection(value);
            } else if (Map.class.isAssignableFrom(value.getClass())) {
                return writeMap((Map<Object, Object>) value);
            } else if (value.getClass() == Boolean.class
                    || value.getClass() == Integer.class
                    || value.getClass() == Long.class || value.getClass() == Double.class
                    || value.getClass() == Byte.class || value.getClass() == Float.class
            ) {
                return value.toString();
            } else if (value.getClass() == String.class || value.getClass() == Character.class) {
                return writeString(value);
            } else {
                return writeObject(value);
            }
        } else {
            return writeNull();
        }
    }

    private static String writeMap(Map<Object, Object> value) throws IllegalAccessException {
        StringBuilder sb = new StringBuilder();
        processCollectionStringStart(sb, '{');
        for (Map.Entry<Object, Object> mapEntry : value.entrySet()) {
            sb.append(write(mapEntry.getKey())).append(":").append(write(mapEntry.getValue())).append(",");
        }
        processCollectionStringEnd(sb, '}');
        return sb.toString();
    }

    static String writeObject(Object object) throws IllegalAccessException {
        Field[] fields = object.getClass().getDeclaredFields();
        StringBuilder sb = new StringBuilder();
        processCollectionStringStart(sb, '{');
        for (Field field : fields) {
            String name = field.getName();
            Object value = field.get(object); // TODO Illegal access exception use getters
            sb.append(name).append(":").append(write(value)).append(",");
        }
        processCollectionStringEnd(sb, '}');
        return sb.toString();
    }

    static String writeArray(Object[] array) throws IllegalAccessException {
        StringBuilder sb = new StringBuilder();
        processCollectionStringStart(sb, '[');
        for (Object object : array) {
            sb.append(write(object)).append(",");
        }
        processCollectionStringEnd(sb, ']');
        return sb.toString();
    }

    static String writeCollection(Object collection) throws IllegalAccessException {
        StringBuilder sb = new StringBuilder();
        processCollectionStringStart(sb, '[');
        Collection<?> collection1 = (Collection<?>) collection;
        for (Object object : collection1) {
            sb.append(write(object)).append(",");
        }
        processCollectionStringEnd(sb, ']');
        return sb.toString();
    }

    static String writeString(Object string) {
        return "\"" + string + "\"";
    }

    static String writeNull() {
        return "null";
    }

    static void processCollectionStringEnd(StringBuilder sb, char symbolToAddAtTheEnd) {
        sb.deleteCharAt(sb.length() - 1);
        sb.append(symbolToAddAtTheEnd);
    }

    static void processCollectionStringStart(StringBuilder sb, char symbolToAddAtTheStart) {
        sb.append(symbolToAddAtTheStart);
    }
}
