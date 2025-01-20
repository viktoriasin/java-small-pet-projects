package hometask.internal.util;

import hometask.exceptions.JsonParserException;
import hometask.internal.handlers.AnnotationHandler;

import java.lang.reflect.*;
import java.util.Collection;
import java.util.Map;

public class JsonToStringWriterUtil {
    public static String write(Object value) throws Exception {
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
        } else if (value.getClass().isRecord()) {
            return writeRecord(value);
        } else {
            return writeObject(value);
        }
    }

    public static String writeMap(Map<Object, Object> value) throws Exception {
        StringBuilder sb = new StringBuilder();
        processCollectionStringStart(sb, '{');
        for (Map.Entry<Object, Object> mapEntry : value.entrySet()) {
            sb.append(write(mapEntry.getKey())).append(":").append(write(mapEntry.getValue())).append(",");
        }
        processCollectionStringEnd(sb, '}');
        return sb.toString();
    }

    public static String writeRecord(Object object) throws Exception {
        RecordComponent[] components = object.getClass().getRecordComponents();
        StringBuilder sb = new StringBuilder();
        processCollectionStringStart(sb, '{');
        for (var comp : components) {
            Field field = object.getClass().getDeclaredField(comp.getName());
            String fieldJsonName = AnnotationHandler.resolveFieldName(field);
            String name = field.getName();
            if (!AnnotationHandler.isFieldIgnored(field)) {
                Object value = comp.getAccessor().invoke(object);
                sb.append(fieldJsonName).append(":").append(write(value)).append(",");
            }
        }
        processCollectionStringEnd(sb, '}');
        return sb.toString();
    }

    public static String writeObject(Object object) {
        Field[] fields = object.getClass().getDeclaredFields();
        StringBuilder sb = new StringBuilder();
        processCollectionStringStart(sb, '{');
        int count = 0;
        for (Field field : fields) {
            String fieldJsonName = AnnotationHandler.resolveFieldName(field);
            if (!AnnotationHandler.isFieldIgnored(field)) {
                try {
                    Object value;
                    if (object.getClass().isRecord()) {
                        value = getRecordField(field, object, count++);
                    } else {
                        value = getObjectField(field, object);
                    }
                    if (value != null) {
                        sb.append(fieldJsonName).append(":").append(write(value)).append(",");
                    }
                } catch (Exception e) {

                }
            }
        }
        processCollectionStringEnd(sb, '}');
        return sb.toString();
    }

    private static Object getRecordField(Field field, Object object, int idx) {
        RecordComponent[] rc = object.getClass().getRecordComponents();
        Object value = null;
        try {
            value = rc[idx].getAccessor().invoke(object);
            System.out.println(value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new JsonParserException(e.toString());
        }
        return value;
    }

    public static String writeArray(Object[] array) throws Exception {
        StringBuilder sb = new StringBuilder();
        processCollectionStringStart(sb, '[');
        for (Object object : array) {
            sb.append(write(object)).append(",");
        }
        processCollectionStringEnd(sb, ']');
        return sb.toString();
    }

    public static String writeCollection(Object collection) throws Exception {
        StringBuilder sb = new StringBuilder();
        processCollectionStringStart(sb, '[');
        Collection<?> collection1 = (Collection<?>) collection;
        for (Object object : collection1) {
            sb.append(write(object)).append(",");
        }
        processCollectionStringEnd(sb, ']');
        return sb.toString();
    }

    public static String writeString(Object string) {
        return "\"" + string + "\"";
    }

    public static String writeNull() {
        return "null";
    }

    public static void processCollectionStringEnd(StringBuilder sb, char symbolToAddAtTheEnd) {
        sb.deleteCharAt(sb.length() - 1);
        sb.append(symbolToAddAtTheEnd);
    }

    public static void processCollectionStringStart(StringBuilder sb, char symbolToAddAtTheStart) {
        sb.append(symbolToAddAtTheStart);
    }

    public static Object getObjectField(Field field, Object instance) throws Exception {
        Object value;
        if (!Modifier.isPublic(field.getModifiers())) {
            Method getterMethod = null;
            String fieldName = field.getName();
            getterMethod = instance.getClass().getMethod("get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1));
            value = getterMethod.invoke(instance);
        } else {
            value = field.get(instance);
        }
        return value;
    }
}
