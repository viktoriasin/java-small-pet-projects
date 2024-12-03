package calculator.v2;

interface Token {
}

class TokenOperator implements Token {
    final Operator value;

    TokenOperator(Operator value) {
        this.value = value;
    }

    public String toString() {
        return value.toString();
    }
}

class TokenNumber implements Token {
    final double value;

    TokenNumber(double value) {
        this.value = value;
    }

    public String toString() {
        return String.format("%.2f", value);
    }
}
