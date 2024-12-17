package json_parser;

public class Pet {
    public String type;
    public Integer legs;

    @Override
    public String toString() {
        return "Pet{" +
                "type='" + type + '\'' +
                ", legs=" + legs +
                '}';
    }
}