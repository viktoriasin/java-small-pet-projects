package json_parser.internal;

import json_parser.exceptions.JsonParserException;
import json_parser.json.JsonObject;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class JsonObjectImpl implements JsonObject {
    private final Map<String, Object> parsedJsonElements;

    public JsonObjectImpl(String jsonString) {
        this.parsedJsonElements = new JsonParserUtil(jsonString).parseObject(); // TODO
        System.out.println(parsedJsonElements);
    }

    @Override
    public <T> T to(Class<T> targetType) throws JsonParserException {
        Field[] targetTypeFields = targetType.getDeclaredFields();
        Object instance = null;
        try {
            instance = targetType.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new JsonParserException("Не получилось создать инстанс типа " + targetType.getName());
        }
        for (Field field : targetTypeFields) {
            if (!AnnotationHandler.isFieldIgnored(field)) {

                String fieldJsonName = AnnotationHandler.resolveFieldName(field);
                String ownFieldName = field.getName();

                if (!parsedJsonElements.containsKey(fieldJsonName)) {
                    throw new JsonParserException("Поля " + fieldJsonName + " нет в переданной json строке.");
                }

                if (!Modifier.isPublic(field.getModifiers())) {
                    Method setterMethod = null;
                    try {
                        setterMethod = targetType.getMethod("set" + ownFieldName.substring(0, 1).toUpperCase() + ownFieldName.substring(1), field.getType());
                    } catch (NoSuchMethodException e) {
                        throw new JsonParserException("У непубличного поля " + ownFieldName + " нет сеттера.");
                    }
                    try {
                        setterMethod.invoke(instance, parsedJsonElements.get(fieldJsonName));
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new JsonParserException("У поля " + ownFieldName + " неподходящий тип в json строке.");
                    }
                } else {
                    try {
                        field.set(instance, parsedJsonElements.get(fieldJsonName));
                    } catch (IllegalAccessException e) {
                        throw new JsonParserException("У поля " + ownFieldName + " неподходящий тип в json строке.");
                    }
                }
            }
        }

//
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
        return (T) instance;
    }

}

