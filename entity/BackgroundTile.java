package entity;

import main.GamePanel;

public class BackgroundTile extends Block 
{
    private final static String spritePath = "resources/blocks/BackgroundTile.png";

    public BackgroundTile(GamePanel gamePanel, int x, int y) {
        super(gamePanel, spritePath, x, y);
        collisionBox.shape = Shape.none;
    }

    @Override
    public void update() {
        // pass
    }
}