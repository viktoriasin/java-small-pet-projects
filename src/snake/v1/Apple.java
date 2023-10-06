package snake.v1;

import java.util.List;
import java.util.Random;

public class Apple {
    private static final Random rand = new Random();

    public final Point point;

    public Apple(GameSize gameSize, List<Point> forbiddenPoints) {
        Point p;
        do {
            int x = rand.nextInt(gameSize.gameWidth) + 1;
            int y = rand.nextInt(gameSize.gameHeight) + 1;
            p = new Point(x, y);
        } while (forbiddenPoints.contains(p)); // TODO: Optimize algorithm
        point = p;
    }
}
