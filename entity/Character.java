package entity;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import main.GamePanel;
import main.Stage;

enum Direction
{
    UP,
    DOWN,
    LEFT,
    RIGHT
}

abstract public class Character extends Entity {
    protected Map<Direction, BufferedImage[]> sprites;
    protected Direction direction;
    protected int spritesNumber;
    protected int kAnimationStep, frameCounter;
    protected Stage stage;
    protected int location;

    private Map<Direction, String> filePrefixes;

    public Character(Stage stage, GamePanel gamePanel, String resourcesPath, int spritesNumber, int x, int y, int width, int height)
    {
        super(gamePanel, resourcesPath, x, y, width, height);
        this.stage = stage;
        this.spritesNumber = spritesNumber;
        kAnimationStep = 0;
        frameCounter = 0;

        sprites = new HashMap<Direction, BufferedImage[]>();
        sprites.put(Direction.UP, new BufferedImage[spritesNumber]);
        sprites.put(Direction.DOWN, new BufferedImage[spritesNumber]);
        sprites.put(Direction.LEFT, new BufferedImage[spritesNumber]);
        sprites.put(Direction.RIGHT, new BufferedImage[spritesNumber]);

        filePrefixes = new HashMap<Direction, String>();
        filePrefixes.put(Direction.UP, "up_");
        filePrefixes.put(Direction.DOWN, "down_");
        filePrefixes.put(Direction.RIGHT, "right_");
        filePrefixes.put(Direction.LEFT, "left_");

        getCharacterImage();

    }

    public boolean checkForCollisions()
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

                if (intersects(stage.getTiles()[checkLocation]))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public void getCharacterImage()
    {
        try
        {
            for (int i = 0; i < spritesNumber; i++)
            {
                sprites.get(Direction.UP)[i] = ImageIO.read(new File(resourcesPath + filePrefixes.get(Direction.UP) + i + ".png"));
                sprites.get(Direction.DOWN)[i] = ImageIO.read(new File(resourcesPath + filePrefixes.get(Direction.DOWN) + i + ".png"));
                sprites.get(Direction.RIGHT)[i] = ImageIO.read(new File(resourcesPath + filePrefixes.get(Direction.RIGHT) + i + ".png"));
                // sprites.get(Direction.UP)[i] = ImageIO.read(getClass().getResourceAsStream(resourcesPath + filePrefixes.get(Direction.UP) + spritesNumber + ".png"));
                // sprites.get(Direction.DOWN)[i] = ImageIO.read(getClass().getResourceAsStream(resourcesPath + filePrefixes.get(Direction.DOWN) + spritesNumber + ".png"));
                // sprites.get(Direction.RIGHT)[i] = ImageIO.read(getClass().getResourceAsStream(resourcesPath + filePrefixes.get(Direction.RIGHT) + spritesNumber + ".png"));
            }
        }
        catch (IOException e) { e.printStackTrace(); }
    }

    public void updateLocation()
    {
        int gridY = collisionBox.x / gamePanel.getTileSize();
        int gridX = collisionBox.y / gamePanel.getTileSize();
        location = gridY + gridX * gamePanel.getMaxScreenColumns();
    }

}
