package entity;

import java.awt.Graphics2D;

import main.GamePanel;

abstract public class Entity implements Collider {
    protected int x, y, width, height;
    protected int speed;
    protected String resourcesPath;
    protected GamePanel gamePanel;
    protected CollisionBox collisionBox;


    public Entity(GamePanel gamePanel, String resourcesPath, int x, int y, int width, int height) {
        this.gamePanel = gamePanel;
        this.resourcesPath = resourcesPath;
        setSpatialProperties(x, y, width, height);
    }
    
    public void setSpatialProperties(int x, int y, int width, int height)
    {
        this.x = x; this.y = y; this.width = width; this.height = height;
    }

    abstract public void update();
    abstract public void draw(Graphics2D g);

}

