package json_parser;

import json_parser.annotations.JsonProperty;

import java.util.Arrays;

public class Person {
    public String first_name;
    public Integer age;
    public Pet favorite_pet;
    public Pet[] pets;

    @Override
    public String toString() {
        return "Person{" +
                "first_name='" + first_name + '\'' +
                ", age=" + age +
                ", favorite_pet=" + favorite_pet +
                ", pets=" + Arrays.toString(pets) +
                '}';
    }
}
