package entity;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import entity.block.Block;
import entity.block.BreakableTile;
import entity.block.PowerUp;
import entity.character.Character;
import main.GamePanel;
import main.Stage;
import util.CollisionHandler;
import util.CollisionHandler.Vector2D;

public class Flame extends Entity {
    private static final String sSpritesPath = "resources/flame/";
    private final int nSprites = 5;
    private static Stage _stage;
    private int nAnimationStep, nFrameCounter;
    private BufferedImage[] sprites;

    public Flame(GamePanel gamePanel_, int x, int y, int width, int height, Stage stage) {
        super(gamePanel_, sSpritesPath, x, y, width, height);
        _stage = stage;
        getFlameSprites();
        nAnimationStep = 0;
        nFrameCounter = 0;
    }

    private void getFlameSprites() {
        sprites = new BufferedImage[nSprites];
        for (int i = 0; i < nSprites; i++) {
            try {
                sprites[i] = ImageIO.read(new File(resourcesPath + "Flame_" + i + ".png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean checkForCollisions() {
        var characters = checkForCharacterCollisions();
        for (var character : characters)
            character.explode();

        Bomb bomb = _stage.getBombs()[location];
        Block block = _stage.getTiles()[location];
        if (bomb != null) {
            // if (bomb.getTimer() < bomb.getChainFuseDelay())
            // bomb.setTimer(bomb.getChainFuseDelay());
            bomb.setTimer(bomb.getTimerMax());
            return true;
        } else if (block.getCollisionBox().shape == Shape.solid) {
            if (BreakableTile.class.isInstance(block))
                ((BreakableTile) block).explode();
            return true;
        } else if (PowerUp.class.isInstance(block)) {
            ((PowerUp) block).vanish();
        }
        return false;
    }

    public ArrayList<Character> checkForCharacterCollisions() {
        var characters = new ArrayList<Character>();
        if (intersects(_stage.getPlayer(), null) == Action.stop)
            characters.add(_stage.getPlayer());
        return characters;
    }

    @Override
    public Vector2D getCenter() {
        return new Vector2D(collisionBox.x + collisionBox.width / 2,
                collisionBox.y + collisionBox.height / 2);
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
        Action result = Action.none;
        Direction[] directions = Direction.values();
        for (var direction : directions) {
            result = CollisionHandler.rectangularCollision(collisionBox, collider.getCollisionBox(), direction);
            if (result == Action.stop)
                return result;
        }
        return result;
    }

    @Override
    public void update() {
        if (++nFrameCounter > 999)
            nFrameCounter = 0;
        if (nFrameCounter > 40) {
            _stage.getFlames()[location] = null;
        }
    }

    @Override
    public void draw(Graphics2D graphics2d) {
        if (nFrameCounter % 4 == 0)
            nAnimationStep++;
        if (nAnimationStep >= nSprites)
            nAnimationStep = 0;
        graphics2d.drawImage(sprites[nAnimationStep],
                x, y, width, height, null);
    }

}