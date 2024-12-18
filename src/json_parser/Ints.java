package json_parser;

import java.util.Arrays;
import java.util.List;

public record Ints(List<Integer> i) {
    @Override
    public String toString() {
        return "Ints{" +
                "i=" + i +
                '}';
    }
}
