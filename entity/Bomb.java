package entity;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import util.Tint;
import util.CollisionHandler;
import util.CollisionHandler.Vector2D;
import entity.Flame.Type;
import entity.character.Player;
import main.GamePanel;
import main.Stage;
import main.Sound.Sounditem;

public class Bomb extends Entity implements Explodable {
    private static final String sSpritesPath = "resources/bomb/";
    private static final int nSprites = 3;
    private static final float fTimerMax = 4f;
    private static final float fChainFuseDelay = 0.95f * fTimerMax;
    private static BufferedImage[] sprites;
    private static Stage stage;
    private Player player;
    private int nAnimationStep, nFrameCounter;
    private int nTintLevel, nTintStep;
    private float fTimer;
    private boolean bShrinking, bFusing;
    private BufferedImage sprite;
    private State state;
    private int power;

    public Bomb(GamePanel gamePanel, int location, Stage stage, int power) {
        super(gamePanel, sSpritesPath, -1, -1, gamePanel.getTileSize(), gamePanel.getTileSize());
        this.location = location;
        x = getLocationX();
        y = getLocationY();
        updateCollisionBox();
        Bomb.stage = stage;
        player = stage.getPlayer();
        collisionBox.shape = Shape.through;
        bShrinking = true;
        bFusing = true;
        state = State.idle;

        getBombSprites();
        nAnimationStep = 0;
        nFrameCounter = 0;
        nTintLevel = 0;
        nTintStep = 15;
        this.power = power;
    }

    private void getBombSprites() {
        sprites = new BufferedImage[nSprites];
        for (int i = 0; i < nSprites; i++) {
            try {
                sprites[i] = ImageIO.read(new File(resourcesPath + "Bomb_" + i + ".png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
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
        if (state == State.idle) {
            if (collisionBox.shape == Shape.through &&
                    intersects(player, null) == Action.none) {
                collisionBox.shape = Shape.solid;
                player.setUponBomb(false);
            }
            nFrameCounter++;

            if (nFrameCounter % 6 == 0)
                fTimer += 0.1;
            if (fTimer >= fTimerMax) {
                collisionBox.shape = Shape.through;
                state = State.exploding;
            }
        } else if (state == State.exploding) {
            nFrameCounter++;
            // Player could be locked out of putting bombs if stood upon exploding
            if (player.getCenterLocation() == location &&
                    intersects(player, null) == Action.stop) {
                player.setUponBomb(false);
            }
        } else {
            explode();
            stage.getBombs()[location] = null;
        }

    }

    @Override
    public void draw(Graphics2D graphics2d) {
        if (state == State.idle) {
            if (nFrameCounter % 22 == 0)
                nAnimationStep += (bFusing) ? 1 : -1;
            if (nFrameCounter > 999)
                nFrameCounter = 0;
            if (nAnimationStep >= nSprites - 1)
                bFusing = false;
            if (nAnimationStep <= 0)
                bFusing = true;
            if (nFrameCounter % 44 == 0) {
                if (bShrinking) {
                    x += 2;
                    y += 2;
                    width -= 4;
                    height -= 4;
                    bShrinking = false;
                } else {
                    x -= 2;
                    y -= 2;
                    width += 4;
                    height += 4;
                    bShrinking = true;
                }
            }
            graphics2d.drawImage(sprites[nAnimationStep],
                    x, y, width, height, null);
        } else if (state == State.exploding) {
            if (nTintLevel >= 225)
                state = State.finishedExploding;
            else {
                Color color;
                nTintLevel += nTintStep;
                if (nTintLevel > 200)
                    color = Color.white;
                else
                    color = new Color(255, 0, 0, nTintLevel);
                sprite = Tint.tint(sprites[nAnimationStep], color);
                graphics2d.drawImage(sprite,
                        x, y, width, height, null);
            }
        } else {
        } // finished exploding. Don't draw.
    }

    public void explode() {
        player.setnBombs(player.getnBombs() - 1);
        gamePanel.playSE(Sounditem.explosion);

        Flame centerFlame = new Flame(gamePanel, x, y, width, height, stage, Type.center, null);
        stage.getFlames()[location] = centerFlame;
        // var characters = centerFlame.checkForCharacterCollisions();
        // for (var character : characters)
        // character.explode();

        int ncols = gamePanel.getMaxScreenColumns();
        int tileSize = gamePanel.getTileSize();
        Flame newFlame = null;
        int newLocation = -1;
        Type type;
        Direction[] directions = Direction.values();
        for (Direction direction : directions) {
            for (int i = 1; i <= power; i++) {
                if (i == power)
                    type = Type.edge;
                else
                    type = Type.middle;
                if (direction == Direction.DOWN) {
                    newLocation = location + ncols * i;
                    if (newLocation >= gamePanel.getGridLength())
                        break;
                    newFlame = new Flame(gamePanel, x, y + tileSize * i, width, height, stage, type, Direction.DOWN);
                } else if (direction == Direction.LEFT) {
                    newLocation = location - 1 * i;
                    if (newLocation % ncols == ncols - 1 || newLocation < 0)
                        break;
                    newFlame = new Flame(gamePanel, x - tileSize * i, y, width, height, stage, type, Direction.LEFT);
                } else if (direction == Direction.RIGHT) {
                    newLocation = location + 1 * i;
                    if (newLocation % ncols == 0 || newLocation > gamePanel.getGridLength())
                        break;
                    newFlame = new Flame(gamePanel, x + tileSize * i, y, width, height, stage, type, Direction.RIGHT);
                } else// if (direction == Direction.UP)
                {
                    newLocation = location - ncols * i;
                    if (newLocation < 0)
                        break;
                    newFlame = new Flame(gamePanel, x, y - tileSize * i, width, height, stage, type, Direction.UP);
                }
                if (!newFlame.checkForCollisions())
                    stage.getFlames()[newLocation] = newFlame;
                else
                    break;
            }
        }
    }

    public float getChainFuseDelay() {
        return fChainFuseDelay;
    }

    public float getTimer() {
        return fTimer;
    }

    public float getTimerMax() {
        return fTimerMax;
    }

    public void setTimer(float time) {
        fTimer = time;
    }

    @Override
    public State getState() {
        return state;
    }

    @Override
    public void setState(State state) {
        this.state = state;
    }
}
