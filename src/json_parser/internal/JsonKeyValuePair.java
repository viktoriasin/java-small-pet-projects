package json_parser.internal;

public record JsonKeyValuePair(JsonKey key, JsonValue value) implements JsonElement  {
}
