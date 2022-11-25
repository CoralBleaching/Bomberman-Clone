package entity;

import main.collisionHandler.Vector2D;

enum Shape {
    none,
    through,
    solid
}
public interface Collider {
    public class CollisionBox {
        public Shape shape;
        public int x, y, width, height;
    }
    public Vector2D getCenter();
    public CollisionBox getCollisionBox();
    public void updateCollisionBox();
    public Action intersects(Collider collider, Direction from);
}
