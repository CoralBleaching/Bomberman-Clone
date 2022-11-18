package entity;

import main.GamePanel;

public class BreakableTile extends Block {

    private final static String spritePath = "resources/blocks/ExplodableBlock.png";

    public BreakableTile(GamePanel gamePanel, int x, int y) {
        super(gamePanel, spritePath, x, y);
        collisionBox.shape = Shape.solid;
    }

    @Override
    public void update() {
        // TODO Auto-generated method stub
        
    }
    
}
