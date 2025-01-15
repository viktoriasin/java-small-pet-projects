package json_parser;

import java.util.Arrays;

public class RecordHandler {
     Ints i;
     int x;
     int[] o;

    public int[] getO() {
        return o;
    }

    public void setO(int[] o) {
        this.o = o;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public Ints getI() {
        return i;
    }

    public void setI(Ints i) {
        this.i = i;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    @Override
    public String toString() {
        return "RecordHandler{" +
                "i=" + i +
                ", x=" + x +
                ", o=" + Arrays.toString(o) +
                '}';
    }
}
