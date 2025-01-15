package hometask.test.data;

public class FullClass {
    private boolean b;
    private int i;
    public double d;
    public Boolean boolWrapper;
    public Integer intWrapper;
    private Double doubleWrapper;
    private String s;

    public FullClass() {
    }

    public FullClass(int i) {
        this.i = i;
    }

    public boolean getB() {
        return b;
    }

    public int getI() {
        return i;
    }

    public Double getDoubleWrapper() {
        return doubleWrapper;
    }

    public String getS() {
        return s;
    }

    public void setB(boolean b) {
        this.b = b;
    }

    public void setI(int i) {
        this.i = i;
    }

    public void setDoubleWrapper(Double doubleWrapper) {
        this.doubleWrapper = doubleWrapper;
    }

    public void setS(String s) {
        this.s = s;
    }
}
