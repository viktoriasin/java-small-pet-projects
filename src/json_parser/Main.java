package json_parser;

import json_parser.internal.JsonObjectImpl;

public class Main {
    public static void main(String[] args) {
        new JsonObjectImpl("{ \"first_name\": \"John\", \"last_name\": \"Smith\", \"is_alive\": true, \"age\": 27}");
    }
}

class Person {
    String first_name;
    String last_name;
    boolean is_alive;
    int age;
}
