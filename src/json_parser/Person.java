package json_parser;

import json_parser.annotations.JsonProperty;

public class Person {
    String first_name;
    String last_name;
    @JsonProperty("alived")
    boolean is_alive;
    int age;

    public boolean isIs_alive() {
        return is_alive;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public int getAge() {
        return age;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public void setIs_alive(boolean is_alive) {
        this.is_alive = is_alive;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Person{" +
                "first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", is_alive=" + is_alive +
                ", age=" + age +
                '}';
    }
}
