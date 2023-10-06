package calculator.v2;

class Token {
}

class TokenOperator extends Token {
    Operator value;

    TokenOperator(Operator value) {
        this.value = value;
    }

    public String toString() {
        return value.toString();
    }
}

class TokenNumber extends Token {
    int value;

    TokenNumber(int value) {
        this.value = value;
    }

    public String toString() {
        return String.valueOf(value);
    }
}
