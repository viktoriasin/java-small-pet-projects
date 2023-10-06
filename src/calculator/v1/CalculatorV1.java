package calculator.v1;

import java.util.InputMismatchException;
import java.util.Scanner;

public class CalculatorV1 {
    private static final String GREETINGS = "Здравствуйте! Вы используете калькулятор. Доступные действия: '+' = 1; '-' = 2";
    private static final String WRONG_COMMAND_ALERT = "Неизвестная команда! Доступные действия: '+' = 1; '-' = 2";
    private static final String WRONG_NUMBER_ALERT = "Нужно ввести число.";

    void run() {
        System.out.println(GREETINGS);

        Scanner console = new Scanner(System.in);

        mainLoop:
        while (true) {
            System.out.println("Введите команду. Если хотите выйти, нажмите 0");
            int command = readNumber(console);

            switch (command) {
                case 0:
                    break mainLoop;

                case 1:
                    System.out.println(readNumber(console) + readNumber(console));
                    break;

                case 2:
                    System.out.println(readNumber(console) - readNumber(console));
                    break;

                default:
                    System.out.println(WRONG_COMMAND_ALERT);
                    break;
            }
        }
    }

    private int readNumber(Scanner console) {
        while (true) {
            try {
                return console.nextInt();
            } catch (InputMismatchException e) {
                System.out.println(WRONG_NUMBER_ALERT);
                console.next();
            }
        }
    }
}
