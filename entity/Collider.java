package entity;

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
    public CollisionBox getCollisionBox();
    public void updateCollisionBox();
    public Action intersects(Collider collider, Direction from);
}
