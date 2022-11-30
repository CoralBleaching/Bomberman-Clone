package entity.block;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import entity.Action;
import entity.Collider;
import entity.Direction;
import entity.Entity;
import main.GamePanel;
import util.CollisionHandler.Vector2D;

abstract public class Block extends Entity {
    protected BufferedImage sprite;

    public Block(GamePanel gamePanel, String resourcesPath, int x, int y) {
        super(gamePanel, resourcesPath, x, y, gamePanel.getTileSize(), gamePanel.getTileSize());
        try {
            sprite = ImageIO.read(new File(resourcesPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected Block(GamePanel gamePanel, int x, int y) {
        super(gamePanel, "", x, y, gamePanel.getTileSize(), gamePanel.getTileSize());
    }

    public void draw(Graphics2D graphics) {
        graphics.drawImage(sprite, x, y, width, height, null);
    }

    @Override
    public CollisionBox getCollisionBox() {
        return collisionBox;
    }

    @Override
    public Action intersects(Collider collider, Direction from) {
        return Action.none;
    }

    @Override
    public void updateCollisionBox() {
        collisionBox.x = x;
        collisionBox.y = y;
        collisionBox.width = width;
        collisionBox.height = height;
    }

    @Override
    public Vector2D getCenter() {
        return new Vector2D(collisionBox.x + collisionBox.width / 2, collisionBox.y + collisionBox.height / 2);
    }
}
