package json_parser;

public class DifficultPerson {
    String first_name;
    String last_name;
    boolean is_alive;
    int age;
    int[] array;

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

    public void setArray(int[] array) {
        this.array = array;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public void setAnotherPerson(Person anotherPerson) {
        this.anotherPerson = anotherPerson;
    }

    public String getFirst_name() {
        return first_name;
    }

    public Person getAnotherPerson() {
        return anotherPerson;
    }

    public double getSalary() {
        return salary;
    }

    public int[] getArray() {
        return array;
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

    double salary;
    Person anotherPerson;
}
