package hometask.test.data;

import hometask.annotations.JsonProperty;

import java.util.List;
import java.util.Set;

public record RecordWithCollections(List<Double> doubles,
                                    Set<Integer> ints,
                                    @JsonProperty("int_array") int[] intArray,
                                    List<NestedValue> values) {
    public record NestedValue(String s) {
    }
}
