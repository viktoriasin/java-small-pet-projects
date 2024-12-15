package json_parser.internal;

import json_parser.exceptions.JsonParserException;
import json_parser.json.JsonObject;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class JsonObjectImpl implements JsonObject {

    private Map<String, Object> keyValueMap;
    private final char[] jsonChars;
    private int currentCharIndex = 0;

    public JsonObjectImpl(String jsonString) {
        this.jsonChars = jsonString.toCharArray();
        this.keyValueMap = parseObject();
        System.out.println(keyValueMap);
    }

    public Map<String, Object> parseObject() {

        if (currentCharIndex >= jsonChars.length) {
            return null;
        }

        if (jsonChars[currentCharIndex] != '{') {
            return null;
        }

        Map<String, Object> parsedObjectMap = new HashMap<>();
        currentCharIndex++;

        while (true) {
            skipExtraSpaces();
            String key = parseKey();

            if (key == null) {
                return parsedObjectMap;
            }

            skipExtraSpaces();

            Object value = parseValue();

            parsedObjectMap.put(key, value);
        }
    }

    private Object parseValue() {
        if (currentCharIndex >= jsonChars.length) {
            return null;
        }

        return Stream
                .<Function<JsonObjectImpl, Object>>of(
                        JsonObjectImpl::parseBoolean,
                        JsonObjectImpl::parseString,
                        JsonObjectImpl::parseNumber,
                        JsonObjectImpl::parseArray,
                        JsonObjectImpl::parseObject,
                        JsonObjectImpl::parseNullTerminal
                )
                .reduce(Optional.empty(), (acc, parser) -> acc.isPresent() ? acc : Optional.ofNullable(parser.apply(this)), (a, _) -> a)
                .orElse(null);
    }

    private Boolean parseBoolean() {
        if (currentCharIndex >= jsonChars.length) {
            return false;
        }
        if (jsonChars[currentCharIndex] != 't' && jsonChars[currentCharIndex] != 'f') {
            return null;
        }
        int counter = 0;
        StringBuilder probablyBooleanLiteral = new StringBuilder();
        while (currentCharIndex < jsonChars.length && counter < 4) {
            counter++;
            probablyBooleanLiteral.append(jsonChars[currentCharIndex]);
            currentCharIndex++;
        }
        String resultBoolean = probablyBooleanLiteral.toString();
        if (resultBoolean.equals("true")) {
            return true;
        }
        if (currentCharIndex < jsonChars.length && (resultBoolean + jsonChars[currentCharIndex]).equals("false")) {
            currentCharIndex++;
            return false;
        }

        return null;
    }

    private Void parseNullTerminal() {
        if (currentCharIndex >= jsonChars.length) {
            throw new IllegalArgumentException();
        }
        int counter = 0;
        StringBuilder probablyNullLiteral = new StringBuilder();
        while (currentCharIndex < jsonChars.length && counter < 4) {
            counter++;
            probablyNullLiteral.append(jsonChars[currentCharIndex]);
            currentCharIndex++;
        }
        if (probablyNullLiteral.toString().equals("null")) {
            return null;
        }
        throw new IllegalArgumentException();
    }

    private Object[] parseArray() {
        if (currentCharIndex >= jsonChars.length) {
            return null;
        }

        if (jsonChars[currentCharIndex] != '[') {
            return null;
        }
        currentCharIndex++;
        List<Object> result = new ArrayList<>();

        while (currentCharIndex < jsonChars.length) {
            Object arrayElement = parseValue();
            if (arrayElement == null) {
                return result.toArray();
            }
            result.add(parseObject());
            skipExtraSpaces();
        }
        return result.toArray();
    }

    private Number parseNumber() { // todo: +/-
        if (currentCharIndex >= jsonChars.length) {
            return null;
        }

        StringBuilder result = new StringBuilder();

        char jsonChar = jsonChars[currentCharIndex];

        if (!Character.isDigit(jsonChar)) {
            return null;
        }

        while (currentCharIndex < jsonChars.length && (Character.isDigit(jsonChar) || jsonChar == '.')) {
            result.append(jsonChar);
            jsonChar = jsonChars[++currentCharIndex];
        }

        String numberString = result.toString();
        try {
            return Integer.parseInt(numberString);
        } catch (NumberFormatException e) {
            try {
                return Double.parseDouble(numberString);
            } catch (NumberFormatException m) {
                return null;
            }
        }
    }

    private void skipExtraSpaces() {
        while (currentCharIndex < jsonChars.length
                && (jsonChars[currentCharIndex] == ' '
                || jsonChars[currentCharIndex] == ':'
                || jsonChars[currentCharIndex] == ','
                || jsonChars[currentCharIndex] == '\n'
                || jsonChars[currentCharIndex] == '\t'
                || jsonChars[currentCharIndex] == '}' // TODO убрать в нужный метод?
                || jsonChars[currentCharIndex] == ']') // TODO убрать в нужный метод?
        ) {
            currentCharIndex++;
        }
    }

    private String parseKey() {
        return parseString();
    }

    private String parseString() {
        if (currentCharIndex >= jsonChars.length) {
            return null;
        }

        if (jsonChars[currentCharIndex] != '"') {
            return null;
        }

        currentCharIndex++;

        StringBuilder sb = new StringBuilder();

        while (currentCharIndex < jsonChars.length) {
            if (jsonChars[currentCharIndex] == '"' && currentCharIndex > 0 && jsonChars[currentCharIndex - 1] != '\\') {
                currentCharIndex++;
                return sb.toString();
            }
            sb.append(jsonChars[currentCharIndex++]);
        }

        // если дойдем сюда, то значит ключ не валидный (иначе встретилась бы вторая ")
        return null;
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

