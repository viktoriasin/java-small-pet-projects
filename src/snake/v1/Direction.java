package snake.v1;

import java.util.HashMap;
import java.util.Map;

public enum Direction {
    UP(0, -1),
    LEFT(-1, 0),
    RIGHT(1, 0),
    DOWN(0, 1);

    private static final Map<Direction, Direction> oppositesMap = new HashMap<>();

    static {
        oppositesMap.put(UP, DOWN);
        oppositesMap.put(DOWN, UP);
        oppositesMap.put(RIGHT, LEFT);
        oppositesMap.put(LEFT, RIGHT);
    }

    public final int dx;
    public final int dy;

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public boolean isOppositeTo(Direction newDirection) {
        return oppositesMap.get(this) == newDirection;
    }
}
