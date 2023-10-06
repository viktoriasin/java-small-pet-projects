package snake.v1;

import org.jline.terminal.Terminal;

public class GameSize {

    public final int terminalWidth;
    public final int terminalHeight;

    public final int gameWidth;
    public final int gameHeight;

    public final int gameScaleX = 2;
    public final int gameScaleY = 1;

    GameSize(Terminal terminal) {
        gameWidth = terminal.getWidth() / gameScaleX;
        gameHeight = terminal.getHeight() / gameScaleY;

        terminalWidth = terminal.getWidth();
        terminalHeight = terminal.getHeight();
    }
}
