package snake.v1;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;

public class SnakeV1 {

    private static final int WAIT_TIME_BETWEEN_RENDERS = 10;

    private static final int CODE_Q = 113;
    private static final int CODE_Y_1 = 89;
    private static final int CODE_Y_2 = 121;
    private static final int CODE_N_1 = 78;
    private static final int CODE_N_2 = 110;

    private static Terminal terminal;
    private static Printer printer;
    private static GameSize gameSize;

    public static void main(String[] args) throws IOException, InterruptedException {
        terminal = TerminalBuilder.builder().system(true).build();
        gameSize = new GameSize(terminal);
        printer = new Printer(terminal, gameSize);
        printer.prepare();

        boolean retry;
        do {
            retry = launchGame();
        } while (retry);

        printer.shutdown();
    }

    private static boolean launchGame() throws InterruptedException, IOException {
        printer.printGameBoard();

        Snake snake = new Snake(gameSize);
        printer.print(snake);

        Apple apple = new Apple(gameSize, snake.getBody());
        printer.print(apple);

        boolean isDefeated = false;

        while (true) {
            wait(WAIT_TIME_BETWEEN_RENDERS);

            int input = terminal.reader().read(1);
            if (input == CODE_Q) {
                break;
            }

            Direction newDirection = detectDirection(input);
            snake.setDirection(newDirection);

            Point tail = snake.getTail();
            if (!snake.move(apple)) {
                isDefeated = true;
                break;
            }
            Point head = snake.getHead();
            printer.printSnakeMove(tail, head);

            if (snake.getHead().equals(apple.point)) {
                apple = new Apple(gameSize, snake.getBody());
                printer.print(apple);
            }
        }

        if (isDefeated) {
            printer.printGameOver();
            return retry();
        } else {
            return false;
        }
    }

    public static void wait(int time) throws InterruptedException {
        Thread.sleep(time);
    }

    private static Direction detectDirection(int directionRaw) {
        switch (directionRaw) {
            case 119:
                return Direction.UP;
            case 97:
                return Direction.LEFT;
            case 100:
                return Direction.RIGHT;
            case 115:
                return Direction.DOWN;
            default:
                return null;
        }
    }

    public static boolean retry() throws IOException {
        while (true) {
            switch (terminal.reader().read()) {
                case CODE_Y_1:
                case CODE_Y_2:
                    return true;
                case CODE_N_1:
                case CODE_N_2:
                    return false;
                default:
            }
        }
    }
}
