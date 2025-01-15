package hometask.test.data;

import hometask.annotations.JsonProperty;

public class ClassWithJsonProperty {
    @JsonProperty("i")
    private int intValue;

    @JsonProperty("b")
    public boolean boolValue;

    @JsonProperty("s")
    public String stringValue;

    public int getIntValue() {
        return intValue;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }
}
