package entity;

import main.GamePanel;
import main.InputHandler;
import main.Stage;
import main.collisionHandler;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;

public class Player extends Character {
    private InputHandler inputHandler;

    private final static int nPlayerSpritesNumber = 8;
    private final static String sSpritesPath = "resources/player/"; 

    private static int nPlayerStartingX = 64;
    private static int nPlayerStartingY = 64;
    private static int nPlayerStartingWidth = 68;
    private static int nPlayerStartingHeight = 128;

    public Player(Stage stage, GamePanel gamePanel, InputHandler inputHandler)
    {
        super(stage, gamePanel, sSpritesPath, nPlayerSpritesNumber, gamePanel.getTileSize(), 0, nPlayerStartingWidth, nPlayerStartingHeight);
        nPlayerStartingX = gamePanel.getTileSize();;
        nPlayerStartingY = 0;
        this.inputHandler = inputHandler;
        speed = 4;
        direction = Direction.DOWN;
        collisionBox = new CollisionBox();
        collisionBox.shape = Shape.rectangle;
        collisionBox.width = width - 10 * gamePanel.getScale();
        collisionBox.height = height / 2 - 10 * gamePanel.getScale();
    }

    public void setDefaultValues()
    {
        setSpatialProperties(nPlayerStartingX, nPlayerStartingY, nPlayerStartingWidth, nPlayerStartingHeight);
        speed = 4;
        direction = Direction.DOWN;
    }

    @Override
    public void update()
    {
        frameCounter++;
        int oldx = x, oldy = y;
        if (inputHandler.up)
        {
            direction = Direction.UP;
            y -= speed;
        }
        if (inputHandler.down)
        {
            direction = Direction.DOWN;
            y += speed;
        }
        updateCollisionBox();
        if (checkForCollisions()) {y = oldy; }
        if (inputHandler.right)
        {
            direction = Direction.RIGHT;
            x += speed;
        }
        if (inputHandler.left)
        {
            direction = Direction.LEFT;
            x -= speed;
        }
        updateCollisionBox();
        if (checkForCollisions()) {x = oldx; }
        updateLocation();
    }

    @Override
    public void draw(Graphics2D graphics)
    {
        if (frameCounter % 4 == 0 && inputHandler.moveKeyPressed()) kAnimationStep++;
        if (frameCounter > 999) { frameCounter = 0; }

        if (kAnimationStep >= nPlayerSpritesNumber)
        {
            kAnimationStep = 0;
        }

        BufferedImage image = null;
        switch (direction)
        {
            case UP:
                image = sprites.get(Direction.UP)[kAnimationStep];
                break;
            case DOWN:
                image = sprites.get(Direction.DOWN)[kAnimationStep];
                break;
            case RIGHT:
                image = sprites.get(Direction.RIGHT)[kAnimationStep];
                break;
            case LEFT:
                image = sprites.get(Direction.RIGHT)[kAnimationStep];
                break;
        }
        
        if (direction == Direction.LEFT)
        {
            graphics.drawImage(image, x + width, y, -width, height, null);
        }
        else { graphics.drawImage(image, x, y, width, height, null); }
        graphics.setColor(Color.red);
        graphics.drawRect(collisionBox.x, collisionBox.y, collisionBox.width, collisionBox.height);
        //System.out.println("x: " + (int)(location / gamePanel.getMaxScreenRows()) + " y: " + (int)(location % gamePanel.getMaxScreenColumns()));
    }

    @Override
    public void updateCollisionBox()
    {
        collisionBox.x = x + gamePanel.getScale() * 5;
        collisionBox.y = y + height / 2 + 5 * gamePanel.getScale();
    }

    @Override
    public CollisionBox getCollisionBox() {
        return collisionBox;
    }

    @Override
    public boolean intersects(Collider collider) {
        CollisionBox box = collider.getCollisionBox();
        switch (box.shape)
        {
            case rectangle:
                return collisionHandler.rectangularCollision(collisionBox, box);
            case rounded:
                return collisionHandler.roundCollision(collisionBox, box);
            default:
                return false;
        }
    }



}
