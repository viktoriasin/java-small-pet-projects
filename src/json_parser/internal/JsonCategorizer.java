package json_parser.internal;

@FunctionalInterface
public interface JsonCategorizer {
    JsonElementCategory categorize(char ch);
}
