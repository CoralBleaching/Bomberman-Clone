package entity;

enum Shape {
    none,
    rectangle,
    rounded
}


public interface Collider {
    // public enum From
    // {
    //     TOP, BOTTOM, LEFT, RIGHT, NONE
    // }
    public class CollisionBox {
        public Shape shape;
        public int x, y, width, height;
    }
    public CollisionBox getCollisionBox();
    public void updateCollisionBox();
    public boolean intersects(Collider collider);
}
