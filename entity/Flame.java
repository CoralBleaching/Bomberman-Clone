package entity;

import java.awt.Graphics2D;

import main.GamePanel;
import main.collisionHandler;
import main.collisionHandler.Vector2D;

public class Flame extends Entity
{

    public Flame(GamePanel gamePanel_, String resourcesPath, int x, int y, int width, int height) {
        super(gamePanel_, resourcesPath, x, y, width, height);
        //TODO Auto-generated constructor stub
    }

    @Override
    public Vector2D getCenter() {
        return new Vector2D(collisionBox.x + collisionBox.width / 2, collisionBox.y + collisionBox.height / 2);
    }

    @Override
    public CollisionBox getCollisionBox() {
        return collisionBox;
    }

    @Override
    public void updateCollisionBox() {
        collisionBox.x = x; collisionBox.y = y; 
        collisionBox.width = width; collisionBox.height = height;
    }

    @Override
    public Action intersects(Collider collider, Direction from) {
        Action result = Action.none;
        Direction[] directions = Direction.values();
        for (var direction : directions)
        {
            result = collisionHandler.rectangularCollision(collisionBox, collider.getCollisionBox(), direction);
            if (result == Action.stop) return result;
        }
        return result;
    }

    @Override
    public void update() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void draw(Graphics2D graphics2d) {
        // TODO Auto-generated method stub
        
    }

}