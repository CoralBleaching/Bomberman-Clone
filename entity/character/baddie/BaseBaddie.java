package entity.character.baddie;

import java.awt.Graphics2D;

import entity.Action;
import entity.Collider;
import entity.Direction;
import entity.Shape;
import entity.block.Block;
import entity.character.Character;
import main.GamePanel;
import main.Stage;
import util.CollisionHandler;

public class BaseBaddie extends Character {

    private static final String spritesPath = "resources/baddie/base_baddie/";

    public BaseBaddie(Stage stage, GamePanel gamePanel, String resourcesPath, int spritesNumber, int x, int y,
            int width, int height) {
        super(stage, gamePanel, spritesPath, spritesNumber, x, y, width, height);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void explode() {
        // TODO Auto-generated method stub

    }

    @Override
    public CollisionBox getCollisionBox() {
        return collisionBox;
    }

    @Override
    public void updateCollisionBox() {
        collisionBox.x = x;
        collisionBox.y = y;
        collisionBox.width = width;
        collisionBox.height = height;
    }

    @Override
    public Action intersects(Collider collider, Direction from) {
        return CollisionHandler.rectangularCollision(collisionBox, collider.getCollisionBox());
    }

    @Override
    public void update() {
        int ncols = gamePanel.getMaxScreenColumns();
        Direction[] directions = Direction.values();
        direction = directions[gamePanel.rng.nextInt(4)];

        Block currentTile = stage.getTiles()[getCenterLocation()];
        var otherCenter = currentTile.getCenter();
        var center = getCenter();
        switch (direction) {
            case DOWN:
                if (center.y >= otherCenter.y)
                    if (stage.getTiles()[location + ncols].getCollisionBox().shape == Shape.solid) {

                    }
                break;
            case LEFT:
                break;
            case RIGHT:
                break;
            case UP:
                break;
        }
    }

    @Override
    public void draw(Graphics2D graphics2d) {
        // TODO Auto-generated method stub

    }

}
