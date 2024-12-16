package json_parser.internal;

import json_parser.annotations.JsonProperty;
import json_parser.annotations.JsonPropertyIgnored;

import java.lang.reflect.Field;

public class AnnotationHandler {

    public static String resolveFieldName(Field field) {
        String fieldJsonName;
        if (field.getAnnotation(JsonProperty.class) != null) {
            fieldJsonName = field.getAnnotation(JsonProperty.class).value();
            System.out.println(fieldJsonName);
        } else {
            fieldJsonName = field.getName();
        }
        return fieldJsonName;
    }

    public static boolean isFieldIgnored(Field field) {
        boolean isIgnored;
        if (field.getAnnotation(JsonPropertyIgnored.class) != null) {
            return field.getAnnotation(JsonPropertyIgnored.class).value();
        }
        return false;
    }
}
