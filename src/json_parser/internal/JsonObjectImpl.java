package json_parser.internal;

import json_parser.exceptions.JsonParserException;
import json_parser.json.JsonObject;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class JsonObjectImpl implements JsonObject {
    private Map<String, Object> parsedJsonElements;

    public JsonObjectImpl(String jsonString) {
        this.parsedJsonElements = new JsonParserUtil(jsonString).parseObject(); // TODO
    }

    @Override
    public <T> T to(Class<T> targetType) throws JsonParserException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
//        Field[] targetTypeFields = targetType.getDeclaredFields();
//        Object instance = targetType.getDeclaredConstructor().newInstance();
//        for (Field field : targetTypeFields) {
//            boolean needToUseSetters = false;
//            Method setterMethod = null;
//            if (Modifier.isPrivate(field.getModifiers()) || Modifier.isProtected(field.getModifiers())) {
//                needToUseSetters = true;
//                try {
//                    setterMethod = targetType.getMethod("set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1));
//                } catch (NoSuchMethodException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//            String fieldJsonName;
//            if (field.getAnnotation(JsonProperty.class) != null) {
//                fieldJsonName = field.getAnnotation(JsonProperty.class).value();
//            } else {
//                fieldJsonName = field.getName();
//            }
//            Class<?> fieldType = field.getType();
//            if (dataSource.get(fieldJsonName) != null) {
//                String valueFromJson = dataSource.get(fieldJsonName);
//                if (!needToUseSetters) {
//                    if (fieldType.isInstance(String.class)) {
//                        field.set(instance, valueFromJson);
//                    } else if (fieldType.isInstance(Integer.class)) {
//                        field.setInt(instance, Integer.parseInt(valueFromJson));
//                    } else if (fieldType.isInstance(Byte.class)) {
//                        field.setByte(instance, Byte.parseByte(valueFromJson));
//                    } else if (fieldType.isInstance(Long.class)) {
//                        field.setLong(instance, Long.parseLong(valueFromJson));
//                    }
//                } else {
//                    if (fieldType.isInstance(String.class)) {
//                        setterMethod.invoke(instance, valueFromJson);
//                    } else if (fieldType.isInstance(Integer.class)) {
//                        setterMethod.invoke(instance, Integer.parseInt(valueFromJson));
//                    } else if (fieldType.isInstance(Byte.class)) {
//                        setterMethod.invoke(instance, Byte.parseByte(valueFromJson));
//                    } else if (fieldType.isInstance(Long.class)) {
//                        setterMethod.invoke(instance, Long.parseLong(valueFromJson));
//                    }
//                }
//            }
//        }
//        return (T) instance;
        return null;
    }

}

