package entity;

import java.awt.Graphics2D;

import main.GamePanel;

abstract public class Entity implements Collider {
    protected int x, y, width, height, location;
    protected int speed;
    protected String resourcesPath;
    protected static GamePanel gamePanel;
    protected CollisionBox collisionBox;


    public Entity(GamePanel gamePanel_, String resourcesPath, int x, int y, int width, int height) {
        gamePanel = gamePanel_;
        this.resourcesPath = resourcesPath;
        setSpatialProperties(x, y, width, height);
        collisionBox = new CollisionBox();
        updateCollisionBox();
        updateLocation();
    }
    
    public int getGridX()
    {
        return 0;
    }
    
    public int getLocationX()
    {
        return (location % gamePanel.getMaxScreenColumns()) * gamePanel.getTileSize();
    }

    public int getLocationY()
    {
        return (location / gamePanel.getMaxScreenRows()) * gamePanel.getTileSize();
    }

    public void setSpatialProperties(int x, int y, int width, int height)
    {
        this.x = x; this.y = y; this.width = width; this.height = height;
    }

    abstract public void update();
    abstract public void draw(Graphics2D graphics2d);

    public void updateLocation()
    {
        int gridY = collisionBox.x / gamePanel.getTileSize();
        int gridX = collisionBox.y / gamePanel.getTileSize();
        location = gridY + gridX * gamePanel.getMaxScreenColumns();
    }

}

