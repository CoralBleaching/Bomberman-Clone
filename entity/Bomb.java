package entity;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import java.awt.Graphics2D;

import main.collisionHandler;
import main.collisionHandler.Vector2D;
import main.GamePanel;
import main.Stage;

public class Bomb extends Entity {
    private static final String sSpritesPath = "resources/bomb/";
    private static final int nSprites = 3;
    private static BufferedImage[] sprites;
    private static Stage stage;
    private Player player;
    private int nAnimationStep, nFrameCounter;
    private boolean shrinking, fusing;

    public Bomb(GamePanel gamePanel, int location, Stage stage_) {
        super(gamePanel, sSpritesPath, -1, -1, gamePanel.getTileSize(), gamePanel.getTileSize());
        this.location = location;
        x = getLocationX();
        y = getLocationY();
        updateCollisionBox();
        stage = stage_;
        player = stage.getPlayer();
        collisionBox.shape = Shape.through;
        shrinking = true; fusing = true;

        getBombSprites();
        nAnimationStep = 0; nFrameCounter = 0;
    }

    private void getBombSprites()
    {
        sprites = new BufferedImage[nSprites];
        for (int i = 0; i < nSprites; i++)
        {
            try 
            {
                sprites[i] =  ImageIO.read(new File(resourcesPath + "Bomb_" + i + ".png"));
            }
            catch (IOException e) { e.printStackTrace(); }
        }
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
        if (collisionBox.shape == Shape.through &&
            intersects(player, null) == Action.none)
        {
            collisionBox.shape = Shape.solid;
            player.setUponBomb(false);
        }
        nFrameCounter++;
    }

    @Override
    public void draw(Graphics2D graphics2d) {
        if (nFrameCounter % 22 == 0) nAnimationStep += (fusing) ? 1 : -1;
        if (nFrameCounter > 999) nFrameCounter = 0;
        if (nAnimationStep >= nSprites - 1) fusing = false;
        if (nAnimationStep <= 0) fusing = true;
        if (nFrameCounter % 44 == 0)
        {
            if (shrinking) { x += 2; y += 2; width -= 4; height -= 4; shrinking = false; }
            else { x -= 2; y -= 2; width += 4; height += 4; shrinking = true; }
        }
        graphics2d.drawImage(sprites[nAnimationStep], 
        x, y, width, height, null);        
    }
    
}
