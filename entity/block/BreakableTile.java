package entity.block;

import java.awt.Color;
import java.awt.Graphics2D;

import entity.Explodable;
import entity.Shape;
import entity.block.PowerUp.Type;
import main.GamePanel;
import main.Stage;
import util.Tint;

public class BreakableTile extends Block implements Explodable {

    private final static String spritePath = "resources/blocks/ExplodableBlock.png";
    private static Stage _stage;
    private State state;
    private int nTintLevel;
    private int nFrameCounter;
    private int nTintStep;
    private Type powerUpType;

    public BreakableTile(GamePanel gamePanel, int x, int y, Stage stage) {
        super(gamePanel, spritePath, x, y);
        _stage = stage;
        collisionBox.shape = Shape.solid;

        state = State.idle;
        nTintLevel = 0;
        nTintStep = 10;
        nFrameCounter = 0;

        powerUpType = null;
    }

    @Override
    public void update() {
        if (++nFrameCounter > 999)
            nFrameCounter = 0;
        if (state == State.finishedExploding) {
            if (powerUpType == null)
                _stage.getTiles()[location] = new BackgroundTile(gamePanel, x, y);
            else
                _stage.getTiles()[location] = new PowerUp(powerUpType, gamePanel, _stage, x, y);
        }
    }

    @Override
    public void draw(Graphics2D graphics2d) {
        if (state == State.idle)
            super.draw(graphics2d);
        else if (state == State.exploding) {
            if (nTintLevel >= 100)
                state = State.finishedExploding;
            else {
                Color color;
                if (nFrameCounter % 4 == 0)
                    nTintLevel += nTintStep;
                if (nTintLevel > 90)
                    color = Color.black;
                else
                    color = new Color(100, 0, 0, nTintLevel);
                sprite = Tint.tint(sprite, color);
                graphics2d.drawImage(sprite,
                        x, y, width, height, null);
            }
        } else {
        } // finished exploding. Don't draw.
    }

    @Override
    public State getState() {
        return state;
    }

    @Override
    public void setState(State state) {
        this.state = state;
    }

    @Override
    public void explode() {
        setState(State.exploding);
    }

    public Type getPowerUp() {
        return powerUpType;
    }

    public void setPowerUp(Type powerUp) {
        this.powerUpType = powerUp;
    }
}
