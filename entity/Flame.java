package entity;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import entity.block.Block;
import entity.block.BreakableTile;
import entity.block.PowerUp;
import main.GamePanel;
import main.Stage;
import util.CollisionHandler;
import util.CollisionHandler.Vector2D;

public class Flame extends Entity {
    public static enum Type {
        center("center"),
        middle("middle"),
        edge("edge");

        public final String label;

        private Type(String label) {
            this.label = label;
        }
    }

    private Type type;
    private Direction direction;

    private static final String sSpritesPath = "resources/flame/";
    private final int nSprites = 5;
    private static Stage stage;
    private int nAnimationStep, nFrameCounter;
    private BufferedImage[] sprites;
    private boolean fullyGrown;

    public Flame(GamePanel gamePanel_, int x, int y, int width, int height, Stage stage, Type type,
            Direction direction) {
        super(gamePanel_, sSpritesPath, x, y, width, height);
        this.type = type;
        this.direction = direction;
        Flame.stage = stage;
        getFlameSprites();
        nAnimationStep = 0;
        nFrameCounter = 0;
        fullyGrown = false;
    }

    private void getFlameSprites() {
        sprites = new BufferedImage[nSprites];
        BufferedImage tmp;
        for (int i = 0; i < nSprites; i++) {
            try {
                tmp = ImageIO.read(new File(resourcesPath + type.label + "_" + i + ".png"));
                sprites[i] = rotateImageToDirection(tmp);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private BufferedImage rotateImageToDirection(BufferedImage image) {
        if (direction == null)
            return image;
        switch (direction) {
            case DOWN:
                return rotate(image, 270.);
            case RIGHT:
                return rotate(image, 180.);
            case UP:
                return rotate(image, 90.);
            default:
                return image;
        }
    }

    public boolean checkForCollisions() {
        // var characters = checkForCharacterCollisions();
        // for (var character : characters)
        // character.explode();

        Bomb bomb = stage.getBombs()[location];
        Block block = stage.getTiles()[location];
        if (bomb != null) {
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

    // public ArrayList<Character> checkForCharacterCollisions() {
    // var characters = new ArrayList<Character>();
    // if (intersects(stage.getPlayer(), null) == Action.stop)
    // characters.add(stage.getPlayer());
    // return characters;
    // }

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
        if (nFrameCounter > 9 * nSprites) {
            stage.getFlames()[location] = null;
        }
        if (fullyGrown == true) {
            if (nFrameCounter % nSprites == 0)
                nAnimationStep--;
        } else {
            if (nFrameCounter % nSprites == 0)
                nAnimationStep++;
        }
        if (nAnimationStep >= nSprites) {
            nAnimationStep--;
            fullyGrown = true;
        }
    }

    @Override
    public void draw(Graphics2D graphics2d) {
        graphics2d.drawImage(sprites[nAnimationStep],
                x, y, width, height, null);
    }

    // https://stackoverflow.com/questions/8639567/java-rotating-images
    private BufferedImage rotate(BufferedImage bimg, Double angle) {
        double sin = Math.abs(Math.sin(Math.toRadians(angle))),
                cos = Math.abs(Math.cos(Math.toRadians(angle)));
        int w = bimg.getWidth();
        int h = bimg.getHeight();
        int neww = (int) Math.floor(w * cos + h * sin),
                newh = (int) Math.floor(h * cos + w * sin);
        BufferedImage rotated = new BufferedImage(neww, newh, bimg.getType());
        Graphics2D graphic = rotated.createGraphics();
        graphic.translate((neww - w) / 2, (newh - h) / 2);
        graphic.rotate(Math.toRadians(angle), w / 2, h / 2);
        graphic.drawRenderedImage(bimg, null);
        graphic.dispose();
        return rotated;
    }

}