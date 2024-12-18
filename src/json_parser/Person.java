package json_parser;

import json_parser.annotations.JsonProperty;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Person {
    public String first_name;
    public Integer age;
    public Pet favorite_pet;
    public Pet[][][] pets;
    public List<Pet> petsList;
    public Map<String, Pet> petMap;
    public Set<Pet> petSet;
    public String nullString;



    @Override
    public String toString() {
        return "Person{" +
                "first_name='" + first_name + '\'' +
                ", age=" + age +
                ", favorite_pet=" + favorite_pet +
                ", pets=" + Arrays.toString(pets) +
                ", petsList=" + petsList +
                ", petMap=" + petMap +
                ", petSet=" + petSet +
                ", nullString='" + nullString + '\'' +
                '}';
    }
}
