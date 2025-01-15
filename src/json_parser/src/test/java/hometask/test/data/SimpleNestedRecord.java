package hometask.test.data;

import hometask.annotations.JsonProperty;

public record SimpleNestedRecord(@JsonProperty("i_value") int i,
                                 @JsonProperty("inner_value") Inner inner) {
    public record Inner(String s) {
    }
}
