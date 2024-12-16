package json_parser;

import json_parser.internal.JsonObjectImpl;

public class Main {
    public static void main(String[] args) {
//        new JsonObjectImpl("{ \"first_name\": \"John\", \"last_name\": \"Smith\", \"is_alive\": true, \"age\": 27}");
        new JsonObjectImpl("{ \"first_name\": \"John\", \"last_name\": \"Smith\", \"is_alive\": true, \"age\": 27, \"array\" : [1, 2,  3], \"number\": 27.3, \"object\" : { \"first_name\": \"John\", \"last_name\": \"Smith\"}}");
        new JsonObjectImpl("{ \"first_name\": \"John\", \"last_name\": \"Smith\", \"is_alive\": true, \"age\": 27, \"array\" : [{ \"first_name\": \"x\", \"last_name\": \"y\"}, { \"first_name\": \"234!\", \"last_name\": \"'?sdsd\"}], \"number\": 27.3, \"object\" : { \"first_name\": \"John\", \"last_name\": \"Smith\"}}");
        new JsonObjectImpl("{ \"first_name\": \"John\", \"last_name\": \"Smith\", \"is_alive\": true, \"age\": 27, \"boolean\":true, \"string\": \"Hey!\", \"boolean2\":false}");
    }
}

class Person {
    String first_name;
    String last_name;
    boolean is_alive;
    int age;
}
