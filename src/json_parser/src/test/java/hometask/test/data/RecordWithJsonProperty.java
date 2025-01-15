package hometask.test.data;

import hometask.annotations.JsonProperty;

public record RecordWithJsonProperty(@JsonProperty("i") int intValue,
                                     @JsonProperty("b") boolean boolValue,
                                     @JsonProperty("s") String stringValue) {
}
