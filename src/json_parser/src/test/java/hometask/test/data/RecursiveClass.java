package hometask.test.data;

import java.util.Objects;

public class RecursiveClass {
    public int i;
    public RecursiveClass nested;

    public RecursiveClass() {
    }

    public RecursiveClass(int i, RecursiveClass nested) {
        this.i = i;
        this.nested = nested;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RecursiveClass that)) return false;
        return i == that.i && Objects.equals(nested, that.nested);
    }

    @Override
    public int hashCode() {
        return Objects.hash(i, nested);
    }
}
