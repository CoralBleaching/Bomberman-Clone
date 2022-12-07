package entity.character.baddie;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import entity.Action;
import entity.Bomb;
import entity.Collider;
import entity.Direction;
import entity.Entity;
import entity.Flame;
import entity.Shape;
import entity.block.Block;
import entity.character.Character;
import main.GamePanel;
import main.Stage;
import util.CollisionHandler;
import util.Tint;

public class BaseBaddie extends Character {

    private static final String sSpritesPath = "resources/baddie/base_baddie/";
    private static final int nBaseBaddieSpritesNumber = 6;
    private static final float fBaseBaddieSpeed = 2f;
    private static final float fBaseChanceOfTakingTurn = 0.5f;

    protected float collisionBoxModifier = 0.7f;
    protected float collisionBoxOffsetPos = (1f - collisionBoxModifier) / 2f;
    protected int nTileCycleStep;
    protected int lastLocationChecked;
    protected boolean bIsTrapped;
    protected float speed;
    protected State state;
    protected int index;

    protected int nMaxShrinkSteps, nShrinkStep;
    protected float fMaxWidthShrinkCoeff = 0.4f;
    protected float fMaxXShrinkOffset = fMaxWidthShrinkCoeff / 2.f;
    protected float fMaxHeightShrinkCoeff = 0.2f;
    protected float fMaxYShrinkOffset = (1f - fMaxHeightShrinkCoeff);
    protected float fWidthShrinkStep, fHeightShrinkStep, fXShrinkStep, fYShrinkStep;

    public BaseBaddie(Stage stage, GamePanel gamePanel, int x, int y,
            int width, int height, int index) {
        super(stage, gamePanel, sSpritesPath, nBaseBaddieSpritesNumber, x, y, width, height);
        updateCollisionBox();
        speed = fBaseBaddieSpeed;
        bIsTrapped = false;
        state = State.idle;
        this.index = index;
        nTileCycleStep = 0;
        lastLocationChecked = -1;
        nShrinkStep = 0;
        nFinalTintLevel = 255;
        nMaxShrinkSteps = nFinalTintLevel / nTintStep;
        fWidthShrinkStep = fMaxWidthShrinkCoeff / nMaxShrinkSteps;
        fHeightShrinkStep = fMaxHeightShrinkCoeff / nMaxShrinkSteps;
        fXShrinkStep = fMaxXShrinkOffset / nMaxShrinkSteps;
        fYShrinkStep = fMaxYShrinkOffset / nMaxShrinkSteps;
        direction = Direction.values()[gamePanel.rng.nextInt(4)];
    }

    @Override
    public void explode() {
        state = State.exploding;
    }

    @Override
    public CollisionBox getCollisionBox() {
        return collisionBox;
    }

    @Override
    public void updateCollisionBox() {
        collisionBox.x = x + (int) (width * collisionBoxOffsetPos);
        collisionBox.y = y + (int) (height * collisionBoxOffsetPos);
        collisionBox.width = (int) (width * collisionBoxModifier);
        collisionBox.height = (int) (height * collisionBoxModifier);
    }

    @Override
    public Action intersects(Collider collider, Direction from) {
        return CollisionHandler.rectangularCollision(collisionBox, collider.getCollisionBox());
    }

    @Override
    public void updateLocation() {
        int column = x / gamePanel.getTileSize();
        int row = y / gamePanel.getTileSize();
        location = column + row * gamePanel.getMaxScreenColumns();
    }

    @Override
    public void update() {
        nFrameCounter = (nFrameCounter > 999) ? 0 : nFrameCounter + 1;

        if (state != State.idle)
            return;

        if (nFrameCounter % 4 == 0) {
            nAnimationStep++;
        }
        if (nAnimationStep >= nBaseBaddieSpritesNumber)
            nAnimationStep = 0;

        checkEntityCollisions();
        if (lastLocationChecked != location) {
            checkPerpendicularDirections();
            lastLocationChecked = location;
            nTileCycleStep++;
        }
        switch (direction) {
            case DOWN:
                y += speed;
                break;
            case LEFT:
                x -= speed;
                break;
            case RIGHT:
                x += speed;
                break;
            case UP:
                y -= speed;
                break;
        }
        updateCollisionBox();
        updateLocation();
        if (intersects(stage.getPlayer(), null) == Action.stop)
            stage.getPlayer().explode();

        checkFlameCollision();
    }

    protected void checkFlameCollision() {
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

                Flame flame = stage.getFlames()[checkLocation];
                if (flame == null)
                    continue;
                if (intersects(flame, null) == Action.stop) {
                    direction = Direction.DOWN;
                    explode();
                    break;
                }
            }
        }
    }

    @Override
    public void draw(Graphics2D graphics) {
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
            nShrinkStep++;
            nTintLevel += nTintStep;
            if (nTintLevel > 255)
                nTintLevel = 255;
            if (nTintLevel == 255) {
                state = State.finishedExploding;
                stage.getBaddies()[index] = null;
            }
            image = Tint.tint(image, new Color(255, 0, 0, nTintLevel));
            graphics.drawImage(image,
                    (int) (x - width * fXShrinkStep * nShrinkStep),
                    (int) (y + height * fYShrinkStep * nShrinkStep),
                    (int) (width * (1 + fWidthShrinkStep * nShrinkStep)),
                    (int) (height * (1 - fYShrinkStep * nShrinkStep)),
                    null);
        } else {

            if (direction == Direction.LEFT) {
                graphics.drawImage(image, x + width, y, -width, height, null);
            } else {
                graphics.drawImage(image, x, y, width, height, null);
            }
            graphics.setColor(Color.red);
            graphics.drawRect(collisionBox.x, collisionBox.y, collisionBox.width, collisionBox.height);
        }
    }

    protected boolean checkEntityCollisions() {
        int ncols = gamePanel.getMaxScreenColumns();
        Block currentTile = stage.getTiles()[getCenterLocation()];
        var otherCenter = currentTile.getCenter();
        var center = getCenter();
        var entity = getEntityAt(direction);
        if (isBombOrSolidBlock(entity)) {
            switch (direction) {
                case DOWN:
                    if (center.y >= otherCenter.y)
                        collide(Direction.DOWN, getCenterLocation() + ncols);
                    return true;
                case LEFT:
                    if (center.x <= otherCenter.x)
                        collide(Direction.LEFT, getCenterLocation() - 1);
                    return true;
                case RIGHT:
                    if (center.x >= otherCenter.x)
                        collide(Direction.RIGHT, getCenterLocation() + 1);
                    return true;
                case UP:
                    if (center.y <= otherCenter.y)
                        collide(Direction.UP, getCenterLocation() - ncols);
                    return true;
            }
        }
        return false;
    }

    protected void collide(Direction towards, int blockLocation) {
        Direction[] validDirections = new Direction[3];
        var directions = Direction.values();
        int idx = 0;
        for (Direction direction : directions) {
            if (direction == towards)
                continue;
            var entity = getEntityAt(direction);
            if (!isBombOrSolidBlock(entity)) {
                validDirections[idx++] = direction;
            }
        }
        if (idx == 0) {
            bIsTrapped = true;
            return;
        }
        bIsTrapped = false;
        int newDirectionIdx = gamePanel.rng.nextInt(0, idx);
        this.direction = validDirections[newDirectionIdx];
    }

    protected void checkPerpendicularDirections() {
        Direction dir1 = null, dir2 = null;
        Entity entity1 = null, entity2 = null;
        switch (direction) {
            case DOWN:
            case UP:
                dir1 = Direction.LEFT;
                dir2 = Direction.RIGHT;
                entity1 = getEntityAt(Direction.LEFT);
                entity2 = getEntityAt(Direction.RIGHT);
                break;
            case LEFT:
            case RIGHT:
                dir1 = Direction.UP;
                dir2 = Direction.DOWN;
                entity1 = getEntityAt(Direction.UP);
                entity2 = getEntityAt(Direction.DOWN);
                break;
        }
        int idx = 0;
        Direction[] viableDirections = new Direction[2];
        if (!isBombOrSolidBlock(entity1))
            viableDirections[idx++] = dir1;
        if (!isBombOrSolidBlock(entity2))
            viableDirections[idx++] = dir2;
        if (idx == 0)
            return;
        else if (gamePanel.rng.nextFloat() < fBaseChanceOfTakingTurn) {
            direction = viableDirections[gamePanel.rng.nextInt(0, idx)];
        }
    }

    protected Entity getEntityAt(Direction direction) {
        int ncols = gamePanel.getMaxScreenColumns();
        int entLocation = -1;
        switch (direction) {
            case DOWN:
                entLocation = getCenterLocation() + ncols;
                break;
            case LEFT:
                entLocation = getCenterLocation() - 1;
                break;
            case RIGHT:
                entLocation = getCenterLocation() + 1;
                break;
            case UP:
                entLocation = getCenterLocation() - ncols;
                break;
        }
        var bomb = stage.getBombs()[entLocation];
        if (bomb != null)
            return bomb;
        return stage.getTiles()[entLocation];
    }

    protected boolean isBombOrSolidBlock(Entity entity) {
        if (Bomb.class.isInstance(entity))
            return true;
        return entity.getCollisionBox().shape == Shape.solid;
    }

}
