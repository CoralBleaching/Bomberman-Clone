package main;

import java.awt.Color;
import java.util.ArrayList;

import util.InputHandler;

public class GameOver extends Menu {

    public GameOver(GamePanel gamePanel, InputHandler inputHandler) {
        super(gamePanel, inputHandler);
        setMaxButtonIndex(1);
    }
    
    @Override
    protected void buildMenuButtons()
    {
        int scrWidth = _gamePanel.getScreenWidth();
        int scrHeight = _gamePanel.getScreenHeight();
        buttons = new ArrayList<MenuButton>(nMaxButtonIndex);
        buttons.add(new MenuButton((int)(.30 * scrWidth),(int)(.3 * scrHeight),(int)(0.36 * scrWidth),
                                   (int)(.15 * scrHeight), "Game Over", true, Color.blue, 
                                   Color.white, ButtonType.exit));
    }
}
