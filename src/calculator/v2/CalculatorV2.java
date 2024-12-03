package calculator.v2;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CalculatorV2 {

    public static void run(String[] args) {

        if (args.length == 0 || args[0].isEmpty()) {
            return;
        }

        List<Token> tokenList = parseEntryLine(args[0]);

        reduceTokens(tokenList);

        if (tokenList.size() == 1 && tokenList.get(0) instanceof TokenNumber) {
            System.out.println(tokenList.get(0));
        }
    }

    private static List<Token> parseEntryLine(String line) {
        List<Token> tokenList = new ArrayList<>();
        for (int i = 0; i < line.length(); i++) {
            if (Character.isDigit(line.charAt(i))) {
                int j = i;
                int dotsCounter = 0;
                while (j < line.length() && (Character.isDigit(line.charAt(j)) || (line.charAt(j) == '.' && dotsCounter <= 1))) {
                    if (line.charAt(j) == '.') {
                        dotsCounter++;
                    }
                    j++;
                }
                String parsedNumber = line.substring(i, j);
                try {
                    tokenList.add(new TokenNumber(Double.parseDouble(parsedNumber)));
                } catch (NumberFormatException e) {
                    return Collections.emptyList();
                }
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

    private static void reduceTokens(List<Token> tokens) {
        int firstIndex;
        int lastIndex;
        while (containsOperator(tokens, Operator.OPEN_BRACKET)) {
            firstIndex = findOpenedBracketIndex(tokens);
            lastIndex = findClosedBracketIndex(tokens, firstIndex);
            if (lastIndex == -1) {
                return;
            }
            List<Token> innerList = tokens.subList(firstIndex + 1, lastIndex);
            reduceTokens(innerList, Operator.MULTIPLY, Operator.DIVIDE);
            reduceTokens(innerList, Operator.PLUS, Operator.MINUS);

            firstIndex = findOpenedBracketIndex(tokens);
            tokens.remove(findClosedBracketIndex(tokens, firstIndex));
            tokens.remove(firstIndex);
        }
        reduceTokens(tokens, Operator.MULTIPLY, Operator.DIVIDE);
        reduceTokens(tokens, Operator.PLUS, Operator.MINUS);
    }

    public static int findOpenedBracketIndex(List<Token> tokens) {
        int lastIndexOfOpenBracket = -1;
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i) instanceof TokenOperator tokenOperator) {
                Operator tokenOperatorValue = tokenOperator.value;
                if (tokenOperatorValue == Operator.OPEN_BRACKET) {
                    lastIndexOfOpenBracket = i;
                } else if (tokenOperatorValue == Operator.CLOSED_BRACKET) {
                    return lastIndexOfOpenBracket;
                }
            }
        }
        return lastIndexOfOpenBracket;
    }

    public static int findClosedBracketIndex(List<Token> tokens, int startIndex) {
        for (int i = startIndex; i < tokens.size(); i++) {
            if (tokens.get(i) instanceof TokenOperator tokenOperator) {
                Operator tokenOperatorValue = tokenOperator.value;
                if (tokenOperatorValue == Operator.CLOSED_BRACKET) {
                    return i;
                }
            }
        }
        return -1;
    }

    private static boolean containsOperator(List<Token> tokens, Operator operator) {
        for (Token currentToken : tokens) {
            if (currentToken instanceof TokenOperator tokenOperator && tokenOperator.value == operator) {
                return true;
            }
        }
        return false;
    }

    private static void reduceTokens(List<Token> tokens, Operator operator1, Operator operator2) {

        // обрабатываем отдельно случай с -1 перед числом в начале
        if (tokens.size() > 2 && tokens.get(1) instanceof TokenNumber num && tokens.get(0) instanceof TokenOperator op) {
            if (op.value == Operator.MINUS) {
                tokens.removeFirst();
                tokens.set(0, new TokenNumber(-1 * num.value));
            }
        }

        for (int currentTokenIndex = 1; currentTokenIndex < tokens.size() - 1; currentTokenIndex = currentTokenIndex + 2) {

            // tokens[1 PLUS 2 MULTIPLY 3] -- ok; tokens[2 3 PLUS 3] - not ok
            if (!(tokens.get(currentTokenIndex) instanceof TokenOperator)) {
                return;
            }

            Operator currentTokenValue = ((TokenOperator) tokens.get(currentTokenIndex)).value;
            int nextTokenIndex = currentTokenIndex + 1;
            int previousTokenIndex = currentTokenIndex - 1;

            // обрабатываем случаи [2 PLUS MINUS 3]
            if ((currentTokenIndex < tokens.size() - 2
                    && tokens.get(nextTokenIndex) instanceof TokenOperator possibleMinusOperator
                    && tokens.get(currentTokenIndex + 2) instanceof TokenNumber possibleNumberToNegative)
            ) {
                if (possibleMinusOperator.value == Operator.MINUS) {
                    tokens.remove(currentTokenIndex);
                    tokens.set(currentTokenIndex, new TokenOperator(currentTokenValue));
                    tokens.set(nextTokenIndex, new TokenNumber(-1 * possibleNumberToNegative.value));
                }
            }
            // tokens[PLUS PLUS 2] - not ok; tokens[1 PLUS PLUS] - not ok
            if (!(tokens.get(previousTokenIndex) instanceof TokenNumber) || !(tokens.get(nextTokenIndex) instanceof TokenNumber)) {
                return;
            }

            if (currentTokenValue == operator1 || currentTokenValue == operator2) {
                try {
                    double previousTokenValue = ((TokenNumber) tokens.get(previousTokenIndex)).value;
                    double nextTokenValue = ((TokenNumber) tokens.get(nextTokenIndex)).value;
                    tokens.set(previousTokenIndex, new TokenNumber(currentTokenValue.applyOperator(previousTokenValue, nextTokenValue)));
                } catch (IllegalArgumentException e) {
                    return;
                }
                tokens.remove(currentTokenIndex);
                tokens.remove(currentTokenIndex);
                currentTokenIndex -= 2;
            }
        }
    }
}
