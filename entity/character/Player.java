package entity.character;

import main.GamePanel;
import main.Stage;
import main.GamePanel.GameState;
import util.InputHandler;
import util.Tint;
import util.CollisionHandler;
import util.CollisionHandler.Vector2D;

import java.awt.Color;
import java.awt.image.BufferedImage;

import entity.Action;
import entity.Bomb;
import entity.Collider;
import entity.Direction;
import entity.Entity;
import entity.Shape;
import entity.block.Block;
import entity.block.PowerUp;

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

    public final static int nPlayerMaxSpeed = 7;

    public Player(Stage stage, GamePanel gamePanel, InputHandler inputHandler) {
        super(stage, gamePanel, sSpritesPath, nPlayerSpritesNumber, gamePanel.getTileSize(), 0, nPlayerStartingWidth,
                nPlayerStartingHeight);
        nPlayerStartingX = gamePanel.getTileSize();
        nPlayerStartingY = 0;
        this.inputHandler = inputHandler;
        speed = nPlayerStartingSpeed * gamePanel.getScale();
        direction = Direction.DOWN;
        uponBomb = false;
        collisionBox.shape = Shape.through;
        collisionBox.width = width - 10 * gamePanel.getScale();
        collisionBox.height = height / 2 - 10 * gamePanel.getScale();
    }

    public void setDefaultValues() {
        setSpatialProperties(nPlayerStartingX * gamePanel.getScale(),
                nPlayerStartingY * gamePanel.getScale(),
                nPlayerStartingWidth * gamePanel.getScale(),
                nPlayerStartingHeight * gamePanel.getScale());
        speed = nPlayerStartingSpeed * gamePanel.getScale();
        direction = Direction.DOWN;
    }

    @Override
    public void update() {
        nFrameCounter++;
        if (state != State.idle)
            return;

        int oldx = x, oldy = y;
        if (inputHandler.bomb) {
            int centerLocation = getCenterLocation();
            if (stage.getBombs()[centerLocation] == null &&
                    !uponBomb) {
                stage.getBombs()[centerLocation] = new Bomb(gamePanel, centerLocation, stage);
                uponBomb = true;
            }
        }
        if (inputHandler.up) {
            direction = Direction.UP;
            y -= speed;
            updateCollisionBox();
            checkForCollisions(oldx, oldy, Direction.UP);
        }
        if (inputHandler.down) {
            direction = Direction.DOWN;
            y += speed;
            updateCollisionBox();
            checkForCollisions(oldx, oldy, Direction.DOWN);
        }
        if (inputHandler.right) {
            direction = Direction.RIGHT;
            x += speed;
            updateCollisionBox();
            checkForCollisions(oldx, oldy, Direction.RIGHT);
        }
        if (inputHandler.left) {
            direction = Direction.LEFT;
            x -= speed;
            updateCollisionBox();
            checkForCollisions(oldx, oldy, Direction.LEFT);
        }
        updateCollisionBox();
        updateLocation();
    }

    @Override
    public void draw(Graphics2D graphics) {
        if (state == State.finishedExploding)
            return;

        if (nFrameCounter % 4 == 0 &&
                inputHandler.moveKeyPressed() &&
                state == State.idle) {
            nAnimationStep++;
        }
        if (nFrameCounter > 999) {
            nFrameCounter = 0;
        }

        if (nAnimationStep >= nPlayerSpritesNumber) {
            nAnimationStep = 0;
        }

        BufferedImage image = null;
        switch (direction) {
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

        if (state == State.exploding) {
            nTintLevel += nTintStep;
            if (nTintLevel > 255)
                nTintLevel = 255;
            image = Tint.tint(image, new Color(255, 0, 0, nTintLevel));
            if (nTintLevel == 250) {
                state = State.finishedExploding;
                gamePanel.setGameState(GameState.game_over);
            }
        }

        if (direction == Direction.LEFT) {
            graphics.drawImage(image, x + width, y, -width, height, null);
        } else {
            graphics.drawImage(image, x, y, width, height, null);
        }
        graphics.setColor(Color.red);
        graphics.drawRect(collisionBox.x, collisionBox.y, collisionBox.width, collisionBox.height);
    }

    @Override
    public void updateCollisionBox() {
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
        switch (box.shape) {
            case through:
                return CollisionHandler.rectangularCollision(collisionBox, box, from);
            case solid:
                return CollisionHandler.roundCollision(collisionBox, box, from);
            default:
                return Action.none;
        }
    }

    public void checkForCollisions(int oldx, int oldy, Direction towards) {
        for (int j = -1; j <= 1; j++) {
            int column = getGridLocationX() + j;
            if (column < 0)
                continue;
            else if (column >= gamePanel.getMaxScreenColumns())
                break;
            for (int i = -1; i <= 1; i++) {
                int row = getGridLocationY() + i;
                if (row >= gamePanel.getMaxScreenRows())
                    continue;
                if (row < 0)
                    continue;

                int checkLocation = column + row * gamePanel.getMaxScreenColumns();

                Entity other = stage.getTiles()[checkLocation];
                solveCollision(other, towards, oldx, oldy, column, row);
                other = stage.getBombs()[checkLocation];
                if (other != null)
                    solveCollision(other, towards, oldx, oldy, column, row);
            }
        }
    }

    private void solveCollision(Entity other, Direction towards, int oldx, int oldy, int column, int row) {
        CollisionBox otherBox = other.getCollisionBox();
        Direction from = CollisionHandler.invertDirection(towards);
        Action action = intersects(other, from);
        switch (action) {
            case stop:
                if (otherBox.shape == Shape.solid) {
                    if (towards == Direction.LEFT || towards == Direction.RIGHT) {
                        x = oldx;
                    } else
                        y = oldy;
                } else if (otherBox.shape == Shape.through &&
                        PowerUp.class.isInstance(other)) {
                    PowerUp powerUp = (PowerUp) other;
                    powerUp.empowerAndVanish(this);
                }
                break;
            case push:
                Vector2D difference = other.getCenter().minus(getCenter());
                Vector2D unitDifference = difference.scale(1 / difference.getLength());
                if (difference.getLength() < collisionBox.width + otherBox.width * fSqrt2 / 2) {
                    if (!isCorner(row, column, towards, unitDifference)) {
                        if (towards == Direction.LEFT || towards == Direction.RIGHT) {
                            x = oldx;
                        } else
                            y = oldy;
                    } else {
                        final int pushX = (int) Math.abs(speed * unitDifference.x);
                        final int pushY = (int) Math.abs(speed * unitDifference.y);
                        switch (towards) {
                            case LEFT:
                                x = oldx - pushX;
                                if (difference.getAngleSign() > 0)
                                    y += pushY; // I'm going down on the screen
                                else
                                    y -= pushY; // I'm going up on the screen
                                break;
                            case RIGHT:
                                x = oldx + pushX;
                                if (difference.getAngleSign() > 0)
                                    y -= pushY; // I'm going up on the screen
                                else
                                    y += pushY; // I'm going down on the screen
                                break;
                            case DOWN:
                                y = oldy + pushY;
                                if (difference.getAngleSign() > 0)
                                    x -= pushX; // I'm going west; must be pushed westward
                                else
                                    x += pushX; // I'm going east; must be pushed eastward
                                break;
                            case UP:
                                y = oldy - pushY;
                                if (difference.getAngleSign() > 0)
                                    x += pushX; // I'm going east; must be pushed eastward
                                else
                                    x -= pushX; // I'm going west; must be pushed westward
                                break;
                        }
                    }
                }
                break;
            case none:
                // no collision, nothing to do.
        }
    }

    private boolean isCorner(int blockRow, int blockCol, Direction towards, Vector2D difference) {
        int ncols = gamePanel.getMaxScreenColumns();
        Block nextTile;
        Direction push = null;
        int nextPos;
        switch (towards) {
            case LEFT:
                push = (difference.getAngle() > 0) ? Direction.DOWN : Direction.UP;
            case RIGHT:
                if (towards == Direction.RIGHT)
                    push = (difference.getAngle() > 0) ? Direction.UP : Direction.DOWN;
                switch (push) {
                    case UP:
                        nextPos = blockCol + (blockRow - 1) * ncols;
                        if (nextPos < 0)
                            return false;
                        nextTile = stage.getTiles()[nextPos];
                        if (nextTile.getCollisionBox().shape == Shape.solid)
                            return false;
                        if (stage.getBombs()[nextPos] != null)
                            return false;
                        else
                            return true;
                    case DOWN:
                        nextPos = blockCol + (blockRow + 1) * ncols;
                        if (nextPos >= gamePanel.getGridLength())
                            return false;
                        nextTile = stage.getTiles()[nextPos];
                        if (nextTile.getCollisionBox().shape == Shape.solid)
                            return false;
                        if (stage.getBombs()[nextPos] != null)
                            return false;
                        else
                            return true;
                    default:
                }
                break;
            case UP:
                push = (difference.getAngleSign() > 0) ? Direction.RIGHT : Direction.LEFT;
            case DOWN:
                if (towards == Direction.DOWN)
                    push = (difference.getAngleSign() > 0) ? Direction.LEFT : Direction.RIGHT;
                switch (push) {
                    case LEFT:
                        nextPos = blockCol - 1 + (blockRow) * ncols;
                        if (nextPos % ncols == ncols || nextPos < 0)
                            return false;
                        nextTile = stage.getTiles()[nextPos];
                        if (nextTile.getCollisionBox().shape == Shape.solid)
                            return false;
                        if (stage.getBombs()[nextPos] != null)
                            return false;
                        else
                            return true;
                    case RIGHT:
                        nextPos = blockCol + 1 + (blockRow) * ncols;
                        if (nextPos % ncols == 0 || nextPos > gamePanel.getGridLength())
                            return false;
                        nextTile = stage.getTiles()[nextPos];
                        if (nextTile.getCollisionBox().shape == Shape.solid)
                            return false;
                        if (stage.getBombs()[nextPos] != null)
                            return false;
                        else
                            return true;
                    default:
                }
                break;
            default:
        }
        return true;
    }

    public int getCenterLocation() {
        Vector2D center = getCenter();
        int column = (int) (center.x / gamePanel.getTileSize());
        int row = (int) (center.y / gamePanel.getTileSize());
        return column + row * gamePanel.getMaxScreenColumns();
    }

    public void setUponBomb(boolean value) {
        uponBomb = value;
    }

    @Override
    public void explode() {
        setState(State.exploding);
    }

}
