package calculator.v2;

import java.util.*;

public class CalculatorV2 {

    public void run() {
        System.out.println("Здравствуйте! Вы используете калькулятор.");

//        String entryLine = new Scanner(System.in).nextLine();
        String entryLine = "1 + 2/2*3-1+3 *4";

        List<Token> tokenList = parseEntryLine(entryLine);

        reduceTokens(tokenList);

        if (tokenList.size() == 1 && tokenList.get(0) instanceof TokenNumber) {
            System.out.println("Результат выражения: " + tokenList.get(0));
        } else {
            System.out.println("Введите выражение в корректном формате");
        }
    }

    private List<Token> parseEntryLine(String line) {
        List<Token> tokenList = new ArrayList<>();
        for (int i = 0; i < line.length(); i++) {
            if (Character.isDigit(line.charAt(i))) {
                int j = i;
                while (j < line.length() && Character.isDigit(line.charAt(j))) {
                    j++;
                }
                tokenList.add(new TokenNumber(Integer.parseInt(line.substring(i, j))));
                i = j - 1;
            } else if (line.charAt(i) != ' ') {
                Operator operator = Operator.resolve(line.charAt(i));
                if (operator == null) {
                    return Collections.emptyList();
                } else {
                    tokenList.add(new TokenOperator(operator));
                }
            }
        }
        return tokenList;
    }

    private void reduceTokens(List<Token> tokens) {
        reduceTokens(tokens, Operator.MULTIPLY, Operator.DIVIDE);
        reduceTokens(tokens, Operator.PLUS, Operator.MINUS);
    }

    private void reduceTokens(List<Token> tokens, Operator operator1, Operator operator2) {
        for (int i = 1; i < tokens.size() - 1; i = i + 2) {
            if (!(tokens.get(i) instanceof TokenOperator)) {
                return;
            }
            Operator operator = ((TokenOperator) tokens.get(i)).value;
            if (!(tokens.get(i - 1) instanceof TokenNumber) || !(tokens.get(i + 1) instanceof TokenNumber)) {
                return;
            }
            if (operator == operator1 || operator == operator2) {
                try {
                    tokens.set(i - 1, new TokenNumber(operator.applyOperator(((TokenNumber) tokens.get(i - 1)).value, ((TokenNumber) tokens.get(i + 1)).value)));
                } catch (Exception e) {
                    return;
                }
                tokens.remove(i);
                tokens.remove(i);
                i -= 2;
            }
        }
    }
}
