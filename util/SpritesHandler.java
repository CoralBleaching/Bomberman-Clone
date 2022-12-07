package util;

import java.awt.image.BufferedImage;
import java.util.Map;

import entity.Direction;
import entity.block.PowerUp.Type;

public class SpritesHandler {
    public static BufferedImage solidSprite;
    public static BufferedImage breakableSprite;
    public static BufferedImage groundSprite;
    public static BufferedImage[] bombSprites;
    public static BufferedImage[] flameSprites;
    public static Map<Direction, BufferedImage[]> playerSprites;
    public static Map<Direction, BufferedImage[]> basebaddieSprites;
    public static Map<Type, BufferedImage[]> powerUpSprites;

}
