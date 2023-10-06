package calculator.v2;

enum Operator {
    PLUS, MINUS, MULTIPLY, DIVIDE;

    public static Operator resolve(char ch) {
        switch (ch) {
            case '+':
                return Operator.PLUS;
            case '-':
                return Operator.MINUS;
            case '*':
                return Operator.MULTIPLY;
            case '/':
                return Operator.DIVIDE;
            default:
                return null;
        }
    }

    public int applyOperator(int a, int b) {
        switch (this) {
            case PLUS:
                return a + b;
            case MINUS:
                return a - b;
            case MULTIPLY:
                return a * b;
            case DIVIDE:
                return a / b;
            default:
                throw new IllegalArgumentException("Неподдерживаемый оператор!");
        }
    }
}
