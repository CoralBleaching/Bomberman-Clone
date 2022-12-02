package util;

import entity.Action;
import entity.Collider.CollisionBox;
import entity.Direction;

public class CollisionHandler {

    public static Action rectangularCollision(CollisionBox box1, CollisionBox box2, Direction from) {
        boolean result = false;
        switch (from) {
            case LEFT:
                result |= collideRectFromLeft(box1, box2);
                break;
            case DOWN:
                result |= collideRectFromTop(box2, box1);
                break;
            case RIGHT:
                result |= collideRectFromLeft(box2, box1);
                break;
            case UP:
                result |= collideRectFromTop(box1, box2);
                break;
        }
        return (result) ? Action.stop : Action.none;
    }

    public static Action rectangularCollision(CollisionBox box1, CollisionBox box2) {
        boolean result = false;
        result |= collideRectFromLeft(box1, box2);
        result |= collideRectFromTop(box2, box1);
        result |= collideRectFromLeft(box2, box1);
        result |= collideRectFromTop(box1, box2);
        return (result) ? Action.stop : Action.none;
    }

    public static boolean collideRectFromLeft(CollisionBox leftBox, CollisionBox rightBox) {
        if (leftBox.x + leftBox.width > rightBox.x && leftBox.x < rightBox.x) {
            if (leftBox.x > rightBox.x + rightBox.width)
                return false;
            else if (leftBox.y > rightBox.y + rightBox.height)
                return false;
            else if (leftBox.y + leftBox.height < rightBox.y)
                return false;
            else
                return true;
        } else
            return false;
    }

    public static boolean collideRectFromTop(CollisionBox topBox, CollisionBox bottomBox) {
        if (topBox.y + topBox.height > bottomBox.y && topBox.y < bottomBox.y) {
            if (topBox.y > bottomBox.y + bottomBox.height)
                return false;
            else if (topBox.x > bottomBox.x + bottomBox.width)
                return false;
            else if (topBox.x + topBox.width < bottomBox.x)
                return false;
            else
                return true;
        } else
            return false;
    }

    private static Action collideCircleFromLeft(CollisionBox leftBox, CollisionBox rightBox) {
        // Operating assumption: already touching rectangular boundary
        int leftCenterY = leftBox.y + leftBox.height / 2;

        if (leftCenterY < rightBox.y || leftCenterY > rightBox.y + rightBox.height)
            return Action.push;
        return Action.stop;
    }

    private static Action collideCircleFromRight(CollisionBox rightBox, CollisionBox leftBox) {
        int rightCenterY = rightBox.y + rightBox.height / 2;
        if (rightCenterY < leftBox.y || rightCenterY > leftBox.y + leftBox.height)
            return Action.push;
        return Action.stop;
    }

    private static Action collideCircleFromTop(CollisionBox topBox, CollisionBox bottomBox) {
        int topCenterX = topBox.x + topBox.width / 2;
        if (topCenterX < bottomBox.x || topCenterX > bottomBox.x + bottomBox.width)
            return Action.push;
        return Action.stop;
    }

    private static Action collideCircleFromBottom(CollisionBox bottomBox, CollisionBox topBox) {
        int bottomCenterX = bottomBox.x + bottomBox.width / 2;
        if (bottomCenterX < topBox.x || bottomCenterX > topBox.x + topBox.width)
            return Action.push;
        return Action.stop;
    }

    public static Action roundCollision(CollisionBox box1, CollisionBox box2, Direction from) {
        Action result = Action.none;
        Action aux = Action.none;
        switch (from) {
            case LEFT:
                if (collideRectFromLeft(box1, box2)) {
                    aux = collideCircleFromLeft(box1, box2);
                    result = (aux == Action.none) ? result : aux;
                }
                ;
                break;
            case RIGHT:
                if (collideRectFromLeft(box2, box1)) {
                    aux = collideCircleFromRight(box1, box2);
                    result = (aux == Action.none) ? result : aux;
                }
                break;
            case UP:
                if (collideRectFromTop(box1, box2)) {
                    aux = collideCircleFromTop(box1, box2);
                    result = (aux == Action.none) ? result : aux;
                }
                break;
            case DOWN:
                if (collideRectFromTop(box2, box1)) {
                    aux = collideCircleFromBottom(box1, box2);
                    result = (aux == Action.none) ? result : aux;
                }
                break;
        }
        return result;
    }

    public static class Vector2D {
        public double x, y;
        private double theta, rho, signTheta;

        public Vector2D(double x, double y) {
            this.x = x;
            this.y = y;
            rho = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
            theta = (x == 0) ? 0 : Math.atan(y / x);
            signTheta = (y * x > 0) ? 1 : -1;
        }

        public double getLength() {
            return rho;
        }

        public double getAngle() {
            return theta;
        }

        public double getAngleSign() {
            return signTheta;
        }

        public Vector2D plus(Vector2D other) {
            return new Vector2D(x + other.x, y + other.y);
        }

        public Vector2D minus(Vector2D other) {
            return new Vector2D(x - other.x, y - other.y);
        }

        public Vector2D scale(double a) {
            return new Vector2D(a * x, a * y);
        }
    }

    public static Direction invertDirection(Direction direction) {
        return (direction == Direction.LEFT) ? Direction.RIGHT
                : (direction == Direction.RIGHT) ? Direction.LEFT
                        : (direction == Direction.DOWN) ? Direction.UP : Direction.DOWN;
    }
}
