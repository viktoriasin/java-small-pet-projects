package calculator.v2;

enum Operator {
    PLUS, MINUS, MULTIPLY, DIVIDE, OPEN_BRACKET, CLOSED_BRACKET;

    public static Operator resolve(char ch) {
        return switch (ch) {
            case '+' -> Operator.PLUS;
            case '-' -> Operator.MINUS;
            case '*' -> Operator.MULTIPLY;
            case '/' -> Operator.DIVIDE;
            case '(' -> Operator.OPEN_BRACKET;
            case ')' -> Operator.CLOSED_BRACKET;
            default -> null;
        };
    }

    public double applyOperator(double a, double b) {
        return switch (this) {
            case PLUS -> a + b;
            case MINUS -> a - b;
            case MULTIPLY -> a * b;
            case DIVIDE -> a / b;
            default -> throw new IllegalArgumentException("Неподдерживаемый оператор!");
        };
    }
}
