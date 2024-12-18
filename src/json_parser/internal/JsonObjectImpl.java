package json_parser.internal;

import json_parser.exceptions.JsonParserException;
import json_parser.json.JsonObject;

import java.lang.reflect.*;
import java.util.*;

public class JsonObjectImpl implements JsonObject {
    public final Map<String, Object> parsedJsonElements;

    public JsonObjectImpl(String jsonString) {
        this.parsedJsonElements = new JsonParserUtil(jsonString).parseObject(); // TODO
        System.out.println(parsedJsonElements);
    }


    @SuppressWarnings("unchecked")
    public <T> T parseObject(Class<T> clazz, Map<String, Object> data) throws Exception {
        Field[] fields = clazz.getDeclaredFields();
        Object instance = clazz.getDeclaredConstructor().newInstance();
        for (Field field : fields) {
            String fieldName = field.getName();

            Class<?> fieldType = field.getType();

            Object fieldValue = data.get(fieldName);
            Type genericType = field.getGenericType();
            if (genericType instanceof ParameterizedType) {
                field.set(instance, parse(fieldType, fieldValue, ((ParameterizedType) genericType).getActualTypeArguments()));
            } else {
                field.set(instance, parse(fieldType, fieldValue, null));
            }
        }
        return (T) instance;
    }

    public Object[] parseArray(Class<?> arrayElementType, Object[] arrayData, ParameterizedType genericType) throws Exception {
        Object[] extractedObject = (Object[]) Array.newInstance(arrayElementType, arrayData.length);
        for (int i = 0; i < arrayData.length; i++) {
            extractedObject[i] = parse(arrayElementType, arrayData[i], null); // по условию задания внутри массивов без дженериков
        }
        return extractedObject;
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
        } else {
            return parseObject(clazz, (Map<String, Object>) data);
        }
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

    public Object parseCollection(Class<?> clazz, Object[] data,  Type[] genericTypes) throws Exception {
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

    @Override
    public <T> T to(Class<T> targetType) throws JsonParserException {
        return null;
    }
//
//    @Override
//    public <T> T to(Class<T> targetType) throws JsonParserException {
//        Field[] targetTypeFields = targetType.getDeclaredFields();
//        Object instance = null;
//        try {
//            instance = targetType.getDeclaredConstructor().newInstance();
//        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
//            throw new JsonParserException("Не получилось создать инстанс типа " + targetType.getName());
//        }
//        for (Field field : targetTypeFields) {
//            String fieldJsonName = AnnotationHandler.resolveFieldName(field);
//            String ownFieldName = field.getName();
//
//            if (!isFieldExist(fieldJsonName)) {
//                throw new JsonParserException("Поля " + fieldJsonName + " нет в переданной json строке.");
//            }
//            Object instanceValueFromJson = parsedJsonElements.get(fieldJsonName);
//            Type fieldType = field.getType();
//            if (!AnnotationHandler.isFieldIgnored(field)) {
//                Object result = to(instanceValueFromJson, fieldType.getClass(), field.getGenericType());
//            }
//        }
//        return (T) instance;
//    }
//
//    private Object to(Object instanceValueFromJson, Class<?> fieldType, Type genericFieldType) {
//        Object result;
//        if (fieldType.isArray()) {
//            result = toArray(instanceValueFromJson, fieldType, genericFieldType);
//        } else if (fieldType.isInstance(Collection.class)) {
//            result = toCollection(instanceValueFromJson, fieldType, genericFieldType);
//        } else if (fieldType.isInstance(Map.class)) {
//            result = toMap(instanceValueFromJson,  fieldType, genericFieldType);
//        } else if (fieldType.isInstance(Record.class)) {
//            result = toRecord(instanceValueFromJson, fieldType.getComponentType());
//        } else if (fieldType.isInstance(String.class)) {
//            result = toString(instanceValueFromJson, fieldType, field.getGenericType().getClass(), field);
//        } else if (fieldType.isInstance(Number.class)) {
//            result = toNumber(instanceValueFromJson, fieldType);
//        } else if (fieldType.isInstance(Boolean.class)) {
//            result = toBoolean(instanceValueFromJson, fieldType);
//        } else if (fieldType.isInstance(Character.class)) {
//            result = toCharacter(instanceValueFromJson, fieldType);
//        } else {
//            result = toObject(instanceValueFromJson, fieldType, field.getGenericType().getClass(), field);
//        }
//        return result;
//    }
//
//    private Object toMap(Object instanceValueFromJson, Class<?> mapType, Class<?> keyType, Class<?> valueType, Field field) {
//        Map<String, Object> objectArray = (Map<String, Object>) instanceValueFromJson;
//        Type[] typesOfMap = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()
//        try {
//            Map<String, Object> map = (Map<String, Object>) mapType.getDeclaredConstructor().newInstance();
//            for (Map.Entry<String, Object> o : objectArray.entrySet()) {
//                Object key = to(o.getKey(), keyType, field);
//                map.put(to(o, componentType, field));
//            }
//        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
//            throw new RuntimeException(e);
//        }
//
//
//        return null;
//    }
//
//    private Object toCollection(Object instanceValueFromJson, Class<?> collectionType, Type genericFieldType) {
//        Object[] objectArray = (Object[]) instanceValueFromJson;
//        Type[] typesOfMap = ((ParameterizedType) genericFieldType).getActualTypeArguments();
//        try {
//            Collection<Object> collection = (Collection<Object>) collectionType.getDeclaredConstructor().newInstance();
//            for (Object o : objectArray) {
//                collection.add(to(o, genericFieldType.getClass(), genericFieldType.getClass()));
//            }
//            return collection;
//        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
//            throw new RuntimeException(e);
//        }
//
//    }
//
//    private Object toArray(Object instanceValueFromJson, Class<?> fieldType, Type genericFieldType) {
//        Object[] objectArray = (Object[]) instanceValueFromJson;
//        Object[] extractedObject = new Object[objectArray.length];
//        for (int i = 0; i < objectArray.length; i++) {
//            extractedObject[i] = to(objectArray[i], componentType, field);
//        }
//        return objectArray;
//    }
//
//
//    private <T> void setPrimitive(Class<T> targetType, Object instance, Object valueToSet, Field field) {
//        String ownFieldName = field.getName();
//
//        if (!Modifier.isPublic(field.getModifiers())) {
//            Method setterMethod = null;
//            try {
//                setterMethod = targetType.getMethod("set" + ownFieldName.substring(0, 1).toUpperCase() + ownFieldName.substring(1), field.getType());
//            } catch (NoSuchMethodException e) {
//                throw new JsonParserException("У непубличного поля " + ownFieldName + " нет сеттера.");
//            }
//            try {
//                setterMethod.invoke(instance, valueToSet);
//            } catch (IllegalAccessException | InvocationTargetException e) {
//                throw new JsonParserException("У поля " + ownFieldName + " неподходящий тип в json строке.");
//            }
//        } else {
//            try {
//                field.set(instance, valueToSet);
//            } catch (IllegalAccessException e) {
//                throw new JsonParserException("У поля " + ownFieldName + " неподходящий тип в json строке.");
//            }
//        }
//    }
//
//
//    private boolean isFieldExist(String fieldJsonName) {
//        return parsedJsonElements.containsKey(fieldJsonName);
//    }

}

