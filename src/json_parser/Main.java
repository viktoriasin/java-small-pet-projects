package json_parser;

import json_parser.internal.JsonObjectImpl;

import java.lang.reflect.InvocationTargetException;

public class Main {
    public static void main(String[] args) throws Exception {
//        new JsonObjectImpl("{ \"first_name\": \"John\", \"last_name\": \"Smith\", \"is_alive\": true, \"age\": 27}");
//        new JsonObjectImpl("{ \"first_name\": \"John\", \"last_name\": \"Smith\", \"is_alive\": true, \"age\": 27, \"array\" : [1, 2,  3], \"number\": 27.3, \"object\" : { \"first_name\": \"John\", \"last_name\": \"Smith\"}}");
//        new JsonObjectImpl("{ \"first_name\": \"John\", \"last_name\": \"Smith\", \"is_alive\": true, \"age\": 27, \"array\" : [{ \"first_name\": \"x\", \"last_name\": \"y\"}, { \"first_name\": \"234!\", \"last_name\": \"'?sdsd\"}], \"number\": 27.3, \"object\" : { \"first_name\": \"John\", \"last_name\": \"Smith\"}}");
//        new JsonObjectImpl("{ \"first_name\": \"John\", \"last_name\": \"Smith\", \"is_alive\": true, \"age\": 27, \"boolean\":true, \"string\": \"Hey!\", \"boolean2\":false}");

//        JsonObjectImpl impl1 = new JsonObjectImpl("{ \"first_name\": \"John\", \"last_name\": \"Smith\", \"alived\": true, \"age\": 27");
        JsonObjectImpl impl1 = new JsonObjectImpl("""
                { "first_name": "John",  "age": 27, "favorite_pet": {"type": "dog", "legs":4}, "pets": [[[{"type": "cat", "legs":4}, {"type": "bird", "legs":2}]]], "petsList": [{"type": "cat", "legs":4}, {"type": "bird", "legs":2}], "petMap": {"kitty":{"type": "cat", "legs":4}, "oliver":{"type": "cat", "legs":4}},
                 "petSet":[{"type": "cat", "legs":4}, {"type": "bird", "legs":2}], "nullString": null}
                """);
//        JsonObjectImpl impl2 = new JsonObjectImpl("{ \"first_name\": \"John\", \"last_name\": \"Smith\", \"is_alive\": true, \"age\": 27, \"array\": [1,2,3], \"salary\": 234.234 , \"anotherPerson\": { \"first_name\": \"John\", \"last_name\": \"Smith\", \"is_alive\": true, \"age\": 27}}");
        System.out.println(impl1.parseObject(Person.class, impl1.parsedJsonElements));
        System.out.println(impl1.parsedJsonElements);

        Person p = impl1.parseObject(Person.class, impl1.parsedJsonElements);
        System.out.println(p.nullString);

    }
}




