package json_parser.internal.handlers;

import json_parser.annotations.JsonProperty;
import json_parser.annotations.JsonPropertyIgnored;

import java.lang.reflect.Field;
import java.lang.reflect.RecordComponent;

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

    public static String resolveFieldName(RecordComponent recordComponent) {
        String fieldJsonName;
        if (recordComponent.getAnnotation(JsonProperty.class) != null) {
            fieldJsonName = recordComponent.getAnnotation(JsonProperty.class).value();
            System.out.println(fieldJsonName);
        } else {
            fieldJsonName = recordComponent.getName();
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

    public static boolean isFieldIgnored(RecordComponent recordComponent) {
        boolean isIgnored;
        if (recordComponent.getAnnotation(JsonPropertyIgnored.class) != null) {
            return recordComponent.getAnnotation(JsonPropertyIgnored.class).value();
        }
        return false;
    }
}
