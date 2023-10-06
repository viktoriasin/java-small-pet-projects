package snake.v1;

import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp;

public class Printer {

    private static final String GAME_OVER = "Game Over!";
    private static final String RETRY = "Do you want to retry? Y/N";

    private static final String SNAKE_SYMBOL = "██";
    private static final String SPACE_SYMBOL = "  ";
    private static final String BORDER_SYMBOL = "║";
    private static final String APPLE_SYMBOL = "\uF8FF";

    private final Terminal terminal;
    private final GameSize gameSize;

    public Printer(Terminal terminal, GameSize gameSize) {
        this.terminal = terminal;
        this.gameSize = gameSize;
    }

    public void prepare() {
        terminal.enterRawMode();
        terminal.puts(InfoCmp.Capability.cursor_invisible);
    }

    public void shutdown() {
        terminal.puts(InfoCmp.Capability.cursor_visible);
        clearScreen();
    }

    public void printGameBoard() {
        clearScreen();
        printBorder();
    }

    public void print(Snake snake) {
        for (Point point : snake.getBody()) {
            printGamePoint(point, SNAKE_SYMBOL);
        }
        flush();
    }

    public void print(Apple apple) {
        printGamePoint(apple.point, APPLE_SYMBOL);
        flush();
    }

    public void printSnakeMove(Point oldTail, Point newHead) {
        printGamePoint(oldTail, SPACE_SYMBOL);
        printGamePoint(newHead, SNAKE_SYMBOL);
        flush();
    }

    public void printGameOver() {
        printTerminalPoint(1, 1, GAME_OVER);
        printTerminalPoint(1, 2, RETRY);
        flush();
    }

    private void printTerminalPoint(int x, int y, String symbol) {
        terminal.writer().write(composeMoveCommand(x, y));
        terminal.writer().write(symbol);
    }

    private void printGamePoint(Point point, String symbol) {
        printTerminalPoint(gameToTerminalX(point.x), gameToTerminalY(point.y), symbol);
    }

    private void printBorder() {
        if (gameSize.terminalWidth % 2 != 0) {
            for (int y = 1; y <= gameSize.terminalHeight; y++) {
                printTerminalPoint(gameSize.terminalWidth, y, BORDER_SYMBOL);
            }
        }
        flush();
    }

    private void clearScreen() {
        terminal.writer().write("\033[H\033[2J");
        flush();
    }

    private void flush() {
        terminal.writer().flush();
    }

    private int gameToTerminalX(int gameX) {
        return gameX * gameSize.gameScaleX - (gameSize.gameScaleX - 1);
    }

    private int gameToTerminalY(int gameY) {
        return gameY * gameSize.gameScaleY - (gameSize.gameScaleY - 1);
    }

    private String composeMoveCommand(int x, int y) {
        return "\u001b[" + y + ";" + x + "H";
    }
}
