package entity;

import main.GamePanel;

public class SolidTile extends Block {

    private final static String spritePath = "resources/blocks/SolidBlock.png";

    public SolidTile(GamePanel gamePanel, int x, int y) {
        super(gamePanel, spritePath, x, y);
        collisionBox.shape = Shape.rectangle;
    }

    @Override
    public void update() {
        // TODO Auto-generated method stub
        
    }
    
}
