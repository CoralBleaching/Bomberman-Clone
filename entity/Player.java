package entity;

import main.GamePanel;
import main.InputHandler;
import main.Stage;
import main.collisionHandler;
import main.collisionHandler.Vector2D;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;

public class Player extends Character {
    private InputHandler inputHandler;

    private final static int nPlayerStartingSpeed = 3;
    private final static int nPlayerSpritesNumber = 8;
    private final static String sSpritesPath = "resources/player/"; 

    private static int nPlayerStartingX = 64;
    private static int nPlayerStartingY = 64;
    private static int nPlayerStartingWidth = 68;
    private static int nPlayerStartingHeight = 128;

    private final double fSqrt2 = Math.sqrt(2.);

    private boolean uponBomb;

    public Player(Stage stage, GamePanel gamePanel, InputHandler inputHandler)
    {
        super(stage, gamePanel, sSpritesPath, nPlayerSpritesNumber, gamePanel.getTileSize(), 0, nPlayerStartingWidth, nPlayerStartingHeight);
        nPlayerStartingX = gamePanel.getTileSize();
        nPlayerStartingY = 0;
        this.inputHandler = inputHandler;
        speed = nPlayerStartingSpeed;
        direction = Direction.DOWN;
        uponBomb = false;
        collisionBox.shape = Shape.through;
        collisionBox.width = width - 10 * gamePanel.getScale();
        collisionBox.height = height / 2 - 10 * gamePanel.getScale();
    }

    public void setDefaultValues()
    {
        setSpatialProperties(nPlayerStartingX, nPlayerStartingY, nPlayerStartingWidth, nPlayerStartingHeight);
        speed = nPlayerStartingSpeed;
        direction = Direction.DOWN;
    }

    @Override
    public void update()
    {
        nFrameCounter++;
        int oldx = x, oldy = y;
        if (inputHandler.bomb)
        {
            int centerLocation = getCenterLocation();
            if (stage.getBombs()[centerLocation] == null &&
                !uponBomb)
            {
                stage.getBombs()[centerLocation] = new Bomb(gamePanel, centerLocation, stage);
                uponBomb = true;
            }
        }
        if (inputHandler.up)
        {
            direction = Direction.UP;
            y -= speed;
            updateCollisionBox();
            checkForCollisions(oldx, oldy, Direction.UP);
        }
        if (inputHandler.down)
        {
            direction = Direction.DOWN;
            y += speed;
            updateCollisionBox();
            checkForCollisions(oldx, oldy, Direction.DOWN);
        }
        if (inputHandler.right)
        {
            direction = Direction.RIGHT;
            x += speed;
            updateCollisionBox();
            checkForCollisions(oldx, oldy, Direction.RIGHT);
        }
        if (inputHandler.left)
        {
            direction = Direction.LEFT;
            x -= speed;
            updateCollisionBox();
            checkForCollisions(oldx, oldy, Direction.LEFT);
        }
        updateLocation();
    }

    @Override
    public void draw(Graphics2D graphics)
    {
        if (nFrameCounter % 4 == 0 && inputHandler.moveKeyPressed()) nAnimationStep++;
        if (nFrameCounter > 999) { nFrameCounter = 0; }

        if (nAnimationStep >= nPlayerSpritesNumber)
        {
            nAnimationStep = 0;
        }

        BufferedImage image = null;
        switch (direction)
        {
            case UP:
                image = sprites.get(Direction.UP)[nAnimationStep];
                break;
            case DOWN:
                image = sprites.get(Direction.DOWN)[nAnimationStep];
                break;
            case RIGHT:
                image = sprites.get(Direction.RIGHT)[nAnimationStep];
                break;
            case LEFT:
                image = sprites.get(Direction.RIGHT)[nAnimationStep];
                break;
        }
        
        if (direction == Direction.LEFT)
        {
            graphics.drawImage(image, x + width, y, -width, height, null);
        }
        else { graphics.drawImage(image, x, y, width, height, null); }
        graphics.setColor(Color.red);
        graphics.drawRect(collisionBox.x, collisionBox.y, collisionBox.width, collisionBox.height);
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
    public Action intersects(Collider collider, Direction from) {
        CollisionBox box = collider.getCollisionBox();
        switch (box.shape)
        {
            case through:
                return collisionHandler.rectangularCollision(collisionBox, box, from);
            case solid:
                return collisionHandler.roundCollision(collisionBox, box, from);
            default:
                return Action.none;
        }
    }

    public void checkForCollisions(int oldx, int oldy, Direction towards)
    {
        for (int i = -1; i <= 1; i++)
        {
            int gridX = location / gamePanel.getMaxScreenRows() + i;
            if (gridX < 0) continue;
            else if (gridX >= gamePanel.getMaxScreenRows()) break;
            for (int j = -1; j <= 1; j++)
            {
                int gridY = location % gamePanel.getMaxScreenColumns() + j;
                if (gridY >= gamePanel.getMaxScreenColumns()) continue;
                if (gridY < 0) continue;

                int checkLocation = gridY + gridX * gamePanel.getMaxScreenColumns();

                Entity other = stage.getTiles()[checkLocation];
                solveCollision(other, towards, oldx, oldy, gridX, gridY);
                other = stage.getBombs()[checkLocation];
                if (other != null) solveCollision(other, towards, oldx, oldy, gridX, gridY);
            }
        }
    }

    private void solveCollision(Entity other, Direction towards, int oldx, int oldy, int gridX, int gridY)
    {
        CollisionBox otherBox = other.getCollisionBox();
        Direction from = collisionHandler.invertDirection(towards);
        Action action = intersects(other, from);
        switch (action)
        {
            case stop:
                if (otherBox.shape == Shape.solid)
                {
                    if (towards == Direction.LEFT || towards == Direction.RIGHT) 
                        { x = oldx; }
                    else y = oldy;
                }
                break;
            case push:   
                Vector2D difference = other.getCenter().minus(getCenter());
                Vector2D unitDifference = difference.scale(1 / difference.getLength());
                if (difference.getLength() < collisionBox.width + otherBox.width * fSqrt2 / 2)
                {
                    if (!isCorner(gridX, gridY, towards, unitDifference))
                    {
                        if (towards == Direction.LEFT || towards == Direction.RIGHT) 
                            { x = oldx; }
                        else y = oldy;
                    }
                    else
                    {
                        final int pushX = (int)Math.abs(speed * unitDifference.x);
                        final int pushY = (int)Math.abs(speed * unitDifference.y);
                        switch (towards)
                        {
                            case LEFT:
                                x = oldx - pushX;
                                if (difference.getAngleSign() > 0) y += pushY; // I'm going down on the screen
                                else y -= pushY;                               // I'm going up on the screen
                                break;
                            case RIGHT:
                                x = oldx + pushX;
                                if (difference.getAngleSign() > 0) y -= pushY; // I'm going up on the screen
                                else y += pushY;                               // I'm going down on the screen
                                break;
                            case DOWN:
                                y = oldy + pushY;
                                if (difference.getAngleSign() > 0) x -= pushX; // I'm going west; must be pushed westward
                                else  x += pushX;                              // I'm going east; must be pushed eastward
                                break;
                            case UP:
                                y = oldy - pushY;
                                if (difference.getAngleSign() > 0)  x += pushX; // I'm going east; must be pushed eastward
                                else  x -= pushX;                               // I'm going west; must be pushed westward
                                break;
                        }
                    }
                }
                break;
            case none:
                // no collision, nothing to do.
        }
    }

    private boolean isCorner(int blockRow, int blockCol, Direction towards, Vector2D difference)
    {
        int ncols = gamePanel.getMaxScreenColumns();
        Block nextTile;
        Direction push = null;
        int nextPos;
        switch (towards)
        {
            case LEFT:
                push = (difference.getAngle() > 0) ? Direction.DOWN : Direction.UP;
            case RIGHT:
                if (towards == Direction.RIGHT) push = (difference.getAngle() > 0) ? Direction.UP : Direction.DOWN;
                switch (push)
                {
                    case UP:
                        nextPos = blockCol + (blockRow - 1) * ncols;
                        nextTile = stage.getTiles()[nextPos];
                        if (nextTile.collisionBox.shape == Shape.solid) return false;
                        if (stage.getBombs()[nextPos] != null) return false;
                        else return true;
                    case DOWN:
                        nextPos = blockCol + (blockRow + 1) * ncols;
                        nextTile = stage.getTiles()[nextPos];
                        if (nextTile.collisionBox.shape == Shape.solid) return false;
                        if (stage.getBombs()[nextPos] != null) return false;
                        else return true;
                    default:
                }
                break;
            case UP:
                push = (difference.getAngle() > 0) ? Direction.RIGHT : Direction.LEFT;
            case DOWN:
                if (towards == Direction.DOWN) push = (difference.getAngle() > 0) ? Direction.LEFT : Direction.RIGHT;
                switch (push)
                {
                    case LEFT:
                        nextPos = blockCol - 1 + (blockRow) * ncols;
                        nextTile = stage.getTiles()[nextPos];
                        if (nextTile.collisionBox.shape == Shape.solid) return false;
                        if (stage.getBombs()[nextPos] != null) return false;
                        else return true;
                    case RIGHT:
                        nextPos = blockCol + 1 + (blockRow) * ncols;
                        nextTile = stage.getTiles()[nextPos];
                        if (nextTile.collisionBox.shape == Shape.solid) return false;
                        if (stage.getBombs()[nextPos] != null) return false;
                        else return true;
                    default:
                }
                break;
            default:         
        }
        return true;
    }

    public int getCenterLocation()
    {
        Vector2D center = getCenter();
        int gridY = (int)Math.floor(center.x / gamePanel.getTileSize());
        int gridX = (int)Math.floor(center.y / gamePanel.getTileSize());
        return gridY + gridX * gamePanel.getMaxScreenColumns();
    }

    public void setUponBomb(boolean value) { uponBomb = value; }
}
