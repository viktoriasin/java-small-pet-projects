package hometask.test.data;

import hometask.annotations.JsonProperty;

import java.util.List;

public class ClassWithCollections {
    public List<Integer> ints;

    @JsonProperty("deep_nested_values")
    public List<List<List<List<List<StringWrapper>>>>> deepNestedValues;

    public record StringWrapper(String s) {
    }
}
