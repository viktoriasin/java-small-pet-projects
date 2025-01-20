package hometask.internal.util;

public class PrimitiveArrayHolder {
    private int[] intArray;
    private short[] shortArray;
    private byte[] byteArray;
    private long[] longArray;
    private float[] floatArray;
    private double[] doubleArray;
    private char[] charArray;

    public Object getArray() {
        if (intArray != null) {
            return intArray;
        } else if (byteArray != null) {
            return byteArray;
        } else if (shortArray != null) {
            return shortArray;
        } else if (doubleArray != null) {
            return doubleArray;
        } else if (floatArray != null) {
            return floatArray;
        } else if (longArray != null) {
            return longArray;
        } else if (charArray != null) {
            return charArray;
        } else  {
            return booleanArray;
        }
    }

    public byte[] getByteArray() {
        return byteArray;
    }

    public void setByteArray(byte[] byteArray) {
        this.byteArray = byteArray;
    }

    public long[] getLongArray() {
        return longArray;
    }

    public void setLongArray(long[] longArray) {
        this.longArray = longArray;
    }

    public float[] getFloatArray() {
        return floatArray;
    }

    public void setFloatArray(float[] floatArray) {
        this.floatArray = floatArray;
    }

    public char[] getCharArray() {
        return charArray;
    }

    public void setCharArray(char[] charArray) {
        this.charArray = charArray;
    }

    public double[] getDoubleArray() {
        return doubleArray;
    }

    public void setDoubleArray(double[] doubleArray) {
        this.doubleArray = doubleArray;
    }

    public boolean[] getBooleanArray() {
        return booleanArray;
    }

    public void setBooleanArray(boolean[] booleanArray) {
        this.booleanArray = booleanArray;
    }

    boolean[] booleanArray;

    public int[] getIntArray() {
        return intArray;
    }

    public void setIntArray(int[] intArray) {
        this.intArray = intArray;
    }

    public short[] getShortArray() {
        return shortArray;
    }

    public void setShortArray(short[] shortArray) {
        this.shortArray = shortArray;
    }
}
