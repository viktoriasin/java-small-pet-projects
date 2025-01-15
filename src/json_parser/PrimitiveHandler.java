package json_parser;

import java.util.Arrays;

public class PrimitiveHandler {
    public char i[];

    public char[] getI() {
        return i;
    }

    public void setI(char[] i) {
        this.i = i;
    }

    @Override
    public String toString() {
        return "PrimitiveHandler{" +
                "i=" + Arrays.toString(i) +
                '}';
    }
}
