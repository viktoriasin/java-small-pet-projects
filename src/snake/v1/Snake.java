package snake.v1;

import java.util.LinkedList;
import java.util.List;

public class Snake {
    private final LinkedList<Point> body = new LinkedList<>();
    private final GameSize gameSize;

    private Direction direction = Direction.RIGHT;

    public Snake(GameSize gameSize) {
        this.gameSize = gameSize;

        for (int x = 1; x <= 5; x++) {
            body.addFirst(new Point(x, 1));
        }
    }

    public List<Point> getBody() {
        return body;
    }

    public Point getTail() {
        return body.getLast();
    }

    public Point getHead() {
        return body.getFirst();
    }

    public void setDirection(Direction direction) {
        if (direction == null || this.direction.isOppositeTo(direction)) {
            return;
        }
        this.direction = direction;
    }

    public boolean move(Apple apple) {
        Point head = body.getFirst();
        int x = head.x + direction.dx;
        int y = head.y + direction.dy;

        if (x < 1) {
            x = gameSize.gameWidth;
        }
        if (x > gameSize.gameWidth) {
            x = 1;
        }
        if (y < 1) {
            y = gameSize.gameHeight;
        }
        if (y > gameSize.gameHeight) {
            y = 1;
        }
        Point newHead = new Point(x, y);
        if (newHead.equals(apple.point)) {
            body.addFirst(newHead);
            return true;
        }
        body.removeLast();
        if (body.contains(newHead)) { // TODO: LinkedHashSet?
            return false;
        }
        body.addFirst(newHead);
        return true;
    }
}
