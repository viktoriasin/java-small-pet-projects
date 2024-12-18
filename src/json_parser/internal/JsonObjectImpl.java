package json_parser.internal;

import json_parser.json.JsonObject;

import java.lang.reflect.*;
import java.util.*;

public class JsonObjectImpl implements JsonObject {
    public final Map<String, Object> parsedJsonElements;

    public JsonObjectImpl(String jsonString) {
        this.parsedJsonElements = new JsonParserUtil(jsonString).parseObject(); // TODO
    }

    @SuppressWarnings("unchecked")
    public Object parse(Class<?> clazz, Object data, Type[] genericTypes) throws Exception {
        if (clazz == Boolean.class || clazz == String.class
                || clazz == Integer.class || clazz == Character.class
                || clazz == Long.class || clazz == Double.class
                || clazz == Byte.class || clazz == Float.class
        ) {
            return data;
        } else if (clazz.isArray()) {
            return parseArray(clazz.getComponentType(), (Object[]) data, null); // по условию задания внутри массивов без дженериков
        } else if (Collection.class.isAssignableFrom(clazz)) {
            return parseCollection(clazz, (Object[]) data, genericTypes);
        } else if (Map.class.isAssignableFrom(clazz)) {
            return parseMap(clazz, (Map<Object, Object>) data, genericTypes);
        } else if (clazz.isRecord()) {
            return parseRecord(clazz, (Map<String, Object>) data);
        } else {
            return parseObject(clazz, (Map<String, Object>) data);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T parseObject(Class<T> clazz, Map<String, Object> data) throws Exception {
        Field[] fields = clazz.getDeclaredFields();
        Object instance = clazz.getDeclaredConstructor().newInstance();
        for (Field field : fields) {
            String fieldJsonName = AnnotationHandler.resolveFieldName(field);
            if (data.containsKey(fieldJsonName) && !AnnotationHandler.isFieldIgnored(field)) {
                Class<?> fieldType = field.getType();

                Object fieldValue = data.get(fieldJsonName);
                Type genericType = field.getGenericType();
                Object value;
                if (genericType instanceof ParameterizedType) {
                    value = parse(fieldType, fieldValue, ((ParameterizedType) genericType).getActualTypeArguments());
                } else {
                    value = parse(fieldType, fieldValue, null);
                }
                setObjectField(field, instance, value, clazz);
            } // TODO добавить обработку случаев, если объекта по имени нет в мапе и ессли поле помечено аннотацией ignored
        }
        return (T) instance;
    }

    public <T> T parseRecord(Class<T> clazz, Map<String, Object> data) throws Exception {
        RecordComponent[] components = clazz.getRecordComponents();
        Object[] fieldValues = new Object[components.length];

        for (int i = 0; i < components.length; i++) {
            RecordComponent comp = components[i];
            String fieldJsonName = AnnotationHandler.resolveFieldName(comp);
            String recordComponentName = comp.getName();
            if (data.containsKey(fieldJsonName) && !AnnotationHandler.isFieldIgnored(comp)) {
                Class<?> fieldType = comp.getType();
                Object fieldValue = data.get(fieldJsonName);
                Object value;
                if (comp.getGenericType() instanceof ParameterizedType) {
                    value = parse(fieldType, fieldValue, ((ParameterizedType) comp.getGenericType()).getActualTypeArguments());
                } else {
                    value = parse(fieldType, fieldValue, null);
                }
                fieldValues[i] = value;
            }
        }
        return (T) canonicalConstructorOfRecord(clazz).newInstance(fieldValues);
    }

    static <T> Constructor<T> canonicalConstructorOfRecord(Class<T> recordClass)
            throws NoSuchMethodException, SecurityException {
        Class<?>[] componentTypes = Arrays.stream(recordClass.getRecordComponents())
                .map(rc -> rc.getType())
                .toArray(Class<?>[]::new);
        return recordClass.getDeclaredConstructor(componentTypes);
    }

    public Object[] parseArray(Class<?> arrayElementType, Object[] arrayData, ParameterizedType genericType) throws Exception {
        Object[] extractedObject = (Object[]) Array.newInstance(arrayElementType, arrayData.length);
        for (int i = 0; i < arrayData.length; i++) {
            extractedObject[i] = parse(arrayElementType, arrayData[i], null); // по условию задания внутри массивов без дженериков
        }
        return extractedObject;
    }

    public Object parseMap(Class<?> clazz, Map<Object, Object> data, Type[] genericTypes) throws Exception {
        Map<Object, Object> map = new HashMap<>(); // для упрощения берем базовые реализации. В идеале - надо проверять на конкретный тип
        for (Map.Entry<Object, Object> o : data.entrySet()) {
            Object key = parse((Class<?>) genericTypes[0], o.getKey(), null); // по условию задания внутри дженериков без дженериков
            Object value = parse((Class<?>) genericTypes[1], o.getValue(), null); // по условию задания внутри дженериков без дженериков
            map.put(key, value);
        }
        return map;
    }

    public Object parseCollection(Class<?> clazz, Object[] data, Type[] genericTypes) throws Exception {
        Collection<Object> collection;
        if (List.class.isAssignableFrom(clazz)) {
            collection = new ArrayList<>(); // для упрощения берем базовые реализации. В идеале - надо проверять на конкретный тип
        } else if (Set.class.isAssignableFrom(clazz)) {
            collection = new HashSet<>();
        } else {
            collection = new ArrayDeque<>();
        }
        for (Object collectionMember : data) {
            collection.add(parse((Class<?>) genericTypes[0], collectionMember, null)); // по условию задания внутри дженериков без дженериков
        }
        return collection;
    }

    public void setObjectField(Field field, Object instance, Object value, Class<?> clazz) throws Exception {
        if (!Modifier.isPublic(field.getModifiers())) {
            Method setterMethod = null;
            String fieldName = field.getName();
            setterMethod = clazz.getMethod("set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1), field.getType());
            setterMethod.invoke(instance, value);
        } else {
            field.set(instance, value);
        }
    }

    @Override
    public <T> T to(Class<T> targetType) throws Exception {
        if (targetType.isRecord()) {
            return parseRecord(targetType, parsedJsonElements);
        }
        return parseObject(targetType, parsedJsonElements);
    }
}

