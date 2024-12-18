package json_parser;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DifficultPerson {
    public String first_name;
    public String last_name;
    public boolean is_alive;
    public Integer age;
    public Integer[] array;
    public SimplePerson simplePerson;
    public List<Integer> listInt;
    public List<String> listString;
    public List<SimplePerson> listPeson;
    public Map<String, SimplePerson> mapSimplePerson;
    public Ints i;


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


    public String getLast_name() {
        return last_name;
    }

    public int getAge() {
        return age;
    }

    public boolean isIs_alive() {
        return is_alive;
    }

    public DifficultPerson() {
    }

    public DifficultPerson(String first_name, String last_name, boolean is_alive, List<Integer> listInt, Integer age, Integer[] array, SimplePerson simplePerson, List<String> listString, List<SimplePerson> listPeson, Map<String, SimplePerson> mapSimplePerson, Ints i) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.is_alive = is_alive;
        this.listInt = listInt;
        this.age = age;
        this.array = array;
        this.simplePerson = simplePerson;
        this.listString = listString;
        this.listPeson = listPeson;
        this.mapSimplePerson = mapSimplePerson;
        this.i = i;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setArray(Integer[] array) {
        this.array = array;
    }

    public void setListPeson(List<SimplePerson> listPeson) {
        this.listPeson = listPeson;
    }

    public void setMapSimplePerson(Map<String, SimplePerson> mapSimplePerson) {
        this.mapSimplePerson = mapSimplePerson;
    }

    public void setListString(List<String> listString) {
        this.listString = listString;
    }

    public void setListInt(List<Integer> listInt) {
        this.listInt = listInt;
    }

    public void setSimplePerson(SimplePerson simplePerson) {
        this.simplePerson = simplePerson;
    }

    @Override
    public String toString() {
        return "DifficultPerson{" +
                "first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", is_alive=" + is_alive +
                ", age=" + age +
                ", array=" + Arrays.toString(array) +
                '}';
    }
}
