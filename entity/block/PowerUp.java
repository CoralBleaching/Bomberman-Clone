package entity.block;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import entity.Shape;
import entity.character.Player;
import main.GamePanel;
import main.Stage;
import main.Sound.Sounditem;

public class PowerUp extends Block {
    public static enum Type {
        skates("skates"),
        bomb("bomb"),
        fire("fire");

        public final String label;

        private Type(String label) {
            this.label = label;
        }
    }

    private static Map<Type, BufferedImage> sprites = null;
    private static final String spritesPath = "resources/powerups/";

    final Type type;
    private Stage stage;

    public PowerUp(Type type, GamePanel gamePanel, Stage stage, int x, int y) {
        super(gamePanel, x, y);
        this.stage = stage;
        this.type = type;
        resourcesPath = spritesPath;
        if (sprites == null) {
            sprites = new HashMap<Type, BufferedImage>(Type.values().length);
            getSprites();
        }
        collisionBox.shape = Shape.through;
    }

    private void getSprites() {
        Type[] types = Type.values();
        for (Type type : types) {
            try {
                sprites.put(type, ImageIO.read(new File(resourcesPath + type.label + ".png")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void empowerAndVanish(Player player) {
        gamePanel.playSE(Sounditem.collect);
        switch (type) {
            case skates:
                var speed = player.getSpeed();
                if (speed <= Player.nPlayerMaxSpeed) {
                    player.setSpeed(speed + 1);
                }
                break;
            case bomb:
                player.setMaxBombs(player.getMaxBombs() + 1);
                break;
            case fire:
                player.setFlamePower(player.getFlamePower() + 1);
                break;
        }
        vanish();
    }

    public void vanish() {
        stage.getTiles()[location] = new BackgroundTile(gamePanel, x, y);
    }

    @Override
    public void update() {
        // TODO Auto-generated method stub

    }

    @Override
    public void draw(Graphics2D graphics) {
        graphics.drawImage(sprites.get(type), x, y, width, height, null);
    }
}
