package entity;

import java.awt.Graphics2D;

import main.GamePanel;

abstract public class Entity implements Collider {
    protected int x, y, width, height, location;
    protected int speed;

    protected String resourcesPath;
    protected GamePanel gamePanel;
    protected CollisionBox collisionBox;

    public Entity(GamePanel gamePanel, String resourcesPath, int x, int y, int width, int height) {
        this.gamePanel = gamePanel;
        this.resourcesPath = resourcesPath;
        setSpatialProperties(x, y, width, height);
        collisionBox = new CollisionBox();
        updateCollisionBox();
        updateLocation();
    }

    public int getGridLocationX() {
        return location % gamePanel.getMaxScreenColumns();
    }

    public int getGridLocationY() {
        return location / gamePanel.getMaxScreenColumns();
    }

    public int getLocationX() {
        return (location % gamePanel.getMaxScreenColumns()) * gamePanel.getTileSize();
    }

    public int getLocationY() {
        return (location / gamePanel.getMaxScreenColumns()) * gamePanel.getTileSize();
    }

    public void setSpatialProperties(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    abstract public void update();

    abstract public void draw(Graphics2D graphics2d);

    public void updateLocation() {
        int column = collisionBox.x / gamePanel.getTileSize();
        int row = collisionBox.y / gamePanel.getTileSize();
        location = column + row * gamePanel.getMaxScreenColumns();
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
