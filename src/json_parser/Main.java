package json_parser;

import json_parser.internal.JsonObjectImpl;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        String jsonString = "{\n" +
                "  \"first_name\": \"John\",\n" +
                "  \"last_name\": \"Smith\",\n" +
                "  \"is_alive\": true,\n" +
                "  \"age\": 27,\n" +
                "  \"address\": {\n" +
                "    \"street_address\": \"21 2nd Street\",\n" +
                "    \"city\": \"New York\",\n" +
                "    \"state\": \"NY\",\n" +
                "    \"postal_code\": \"10021-3100\"\n" +
                "  },\n" +
                "  \"phone_numbers\": [\n" +
                "    {\n" +
                "      \"type\": \"home\",\n" +
                "      \"number\": \"212 555-1234\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"type\": \"office\",\n" +
                "      \"number\": \"646 555-4567\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"children\": [\n" +
                "    \"Catherine\",\n" +
                "    \"Thomas\",\n" +
                "    \"Trevor\"\n" +
                "  ],\n" +
                "  \"spouse\": null\n" +
                "}";
//        JsonObjectImpl jsonObject = new JsonObjectImpl("{ \"first_\"name\":\"John\", \"last_name\":\"Smith\", \"is_alive\":true}");
        System.out.println(Arrays.toString("{ \"first_name\":\"John\", \"last_name\":\"Smith\", \"is_alive\":true}".split("\".*?\"\s*:\s*\".*?\"")));
    }
}

class Person {
    String first_name;
    String last_name;
    boolean is_alive;
    int age;
}
