package hometask.internal.util;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class JsonReaderUtil {
    private final char[] jsonChars;
    private int currentCharIndex = 0;

    public JsonReaderUtil(String jsonString) {
        this.jsonChars = jsonString.toCharArray();
    }

    public Map<String, Object> readObject() {

        if (isCurrentIndexOutOfArrayBounds()) return null;

        skipExtraSpaces();

        if (jsonChars[currentCharIndex] != '{') {
            return null;
        }

        Map<String, Object> parsedObjectMap = new HashMap<>();
        currentCharIndex++;

        while (true) {

            skipExtraSpaces();

            String key = readKey();

            if (key == null) {
                currentCharIndex++;
                return parsedObjectMap;
            }

            skipExtraSpaces();

            Object value = readValue();

            parsedObjectMap.put(key, value);

        }
    }

    public String readKey() {
        return readString();
    }

    public Object readValue() {
        if (isCurrentIndexOutOfArrayBounds()) return null;

        return Stream
                .<Function<JsonReaderUtil, Object>>of(
                        JsonReaderUtil::readBoolean,
                        JsonReaderUtil::readString,
                        JsonReaderUtil::readNumber,
                        JsonReaderUtil::readArray,
                        JsonReaderUtil::readObject,
                        JsonReaderUtil::readNullTerminal
                )
                .reduce(Optional.empty(), (acc, parser) -> acc.isPresent() ? acc : Optional.ofNullable(parser.apply(this)), (a, _) -> a)
                .orElse(null);
    }

    public String readString() {
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

    public Boolean readBoolean() {
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

    public Object[] readArray() {
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

            result.add(readValue());

            if (currentCharIndex < jsonChars.length && jsonChars[currentCharIndex] == ']') {
                currentCharIndex++;
                return result.toArray();
            }

            skipExtraSpaces();
        }
        return result.toArray();
    }

    public Number readNumber() {
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

    public Void readNullTerminal() {
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

    public void skipExtraSpaces() {
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

    public boolean isCurrentIndexOutOfArrayBounds() {
        return currentCharIndex >= jsonChars.length;
    }
}
