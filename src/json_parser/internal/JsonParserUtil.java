package json_parser.internal;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class JsonParserUtil {
    private final char[] jsonChars;
    private int currentCharIndex = 0;

    public JsonParserUtil(String jsonString) {
        this.jsonChars = jsonString.toCharArray();
    }

    public Map<String, Object> parseObject() {

        if (isCurrentIndexOutOfArrayBounds()) return null;

        if (jsonChars[currentCharIndex] != '{') {
            return null;
        }

        Map<String, Object> parsedObjectMap = new HashMap<>();
        currentCharIndex++;

        while (true) {

            skipExtraSpaces();

            String key = parseKey();

            if (key == null) {
                currentCharIndex++;
                return parsedObjectMap;
            }

            skipExtraSpaces();

            Object value = parseValue();

            parsedObjectMap.put(key, value);

        }
    }

    private String parseKey() {
        return parseString();
    }

    private Object parseValue() {
        if (isCurrentIndexOutOfArrayBounds()) return null;

        return Stream
                .<Function<JsonParserUtil, Object>>of(
                        JsonParserUtil::parseBoolean,
                        JsonParserUtil::parseString,
                        JsonParserUtil::parseNumber,
                        JsonParserUtil::parseArray,
                        JsonParserUtil::parseObject,
                        JsonParserUtil::parseNullTerminal
                )
                .reduce(Optional.empty(), (acc, parser) -> acc.isPresent() ? acc : Optional.ofNullable(parser.apply(this)), (a, _) -> a)
                .orElse(null);
    }

    private String parseString() {
        if (isCurrentIndexOutOfArrayBounds()) return null;

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

    private Boolean parseBoolean() {
        if (isCurrentIndexOutOfArrayBounds()) return null;

        if (jsonChars[currentCharIndex] != 't' && jsonChars[currentCharIndex] != 'f') {
            return null;
        }
        if (jsonChars[currentCharIndex] == 't') {
            for (char ch : "true".toCharArray()) {
                if (currentCharIndex < jsonChars.length && jsonChars[currentCharIndex] != ch) {
                    return null;
                }
                currentCharIndex++;
            }
            return true;
        } else {
            for (char ch : "false".toCharArray()) {
                if (currentCharIndex < jsonChars.length && jsonChars[currentCharIndex] != ch) {
                    return null;
                }
                currentCharIndex++;
            }
            return false;
        }
    }

    private Object[] parseArray() {
        if (isCurrentIndexOutOfArrayBounds()) return null;

        if (jsonChars[currentCharIndex] != '[') {
            return null;
        }
        currentCharIndex++;
        List<Object> result = new ArrayList<>();

        while (currentCharIndex < jsonChars.length) {
//            if (arrayElement == null) {
//                return result.toArray();
//            }

            result.add(parseValue());

            if (currentCharIndex < jsonChars.length && jsonChars[currentCharIndex] == ']') {
                currentCharIndex++;
                return result.toArray();
            }

            skipExtraSpaces();
        }
        return result.toArray();
    }

    private Number parseNumber() {
        if (isCurrentIndexOutOfArrayBounds()) return null;

        char jsonChar = jsonChars[currentCharIndex];

        if (!Character.isDigit(jsonChar) && jsonChar != '+' && jsonChar != '-') {
            return null;
        }

        StringBuilder sb = new StringBuilder();

        while (Character.isDigit(jsonChar) || jsonChar == '.' || jsonChar == '+' || jsonChar == '-') {
            sb.append(jsonChar);
            if (currentCharIndex + 1 < jsonChars.length) {
                jsonChar = jsonChars[++currentCharIndex];
            } else {
                currentCharIndex++;
                break;
            }
        }

        String numberString = sb.toString();
        try { // TODO
            return Integer.parseInt(numberString);
        } catch (NumberFormatException e) {
            try {
                return Long.parseLong(numberString);
            } catch (NumberFormatException m) {
                try {
                    return Float.parseFloat(numberString);
                } catch (NumberFormatException ex) {
                    try {
                        return Double.parseDouble(numberString);
                    } catch (NumberFormatException exc) {
                        return null;
                    }
                }
            }
        }
    }

    private Void parseNullTerminal() {
        if (currentCharIndex >= jsonChars.length) {
            throw new IllegalArgumentException();
        }

        for (char ch : "null".toCharArray()) {
            if (currentCharIndex < jsonChars.length && jsonChars[currentCharIndex] != ch) {
                throw new IllegalArgumentException();
            }
            currentCharIndex++;
        }

        return null;
    }

    private void skipExtraSpaces() {
        while (currentCharIndex < jsonChars.length
                && (jsonChars[currentCharIndex] == ' '
                || jsonChars[currentCharIndex] == ':'
                || jsonChars[currentCharIndex] == ','
                || jsonChars[currentCharIndex] == '\n'
                || jsonChars[currentCharIndex] == '\t')
        ) {
            currentCharIndex++;
        }
    }

    private boolean isCurrentIndexOutOfArrayBounds() {
        return currentCharIndex >= jsonChars.length;
    }
}
