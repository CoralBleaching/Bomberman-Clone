package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Window;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import main.GamePanel.GameState;
import util.InputHandler;


public class Menu {
    protected static GamePanel _gamePanel;
    protected static InputHandler _inputHandler;
    protected static enum ButtonType { start, exit, do_nothing }
    protected int nMaxButtonIndex = 3;
    protected ArrayList<MenuButton> buttons;
    protected int kIndexFocused;
    protected int nFrameCounter;

    public Menu(GamePanel gamePanel, InputHandler inputHandler)
    {
        _gamePanel = gamePanel;
        _inputHandler = inputHandler;
        buildMenuButtons();
    }

    protected void setMaxButtonIndex(int idx)
    {
        nMaxButtonIndex = idx;
    }

    protected void buildMenuButtons()
    {
        int scrWidth = _gamePanel.getScreenWidth();
        int scrHeight = _gamePanel.getScreenHeight();
        kIndexFocused = 0;
        buttons = new ArrayList<MenuButton>(nMaxButtonIndex);
        buttons.add(new MenuButton((int)(.30 * scrWidth),(int)(.3 * scrHeight),(int)(0.36 * scrWidth),(int)(.15 * scrHeight), "Start Game", true, Color.blue, Color.white, ButtonType.start));
        buttons.add(new MenuButton((int)(.30 * scrWidth),(int)(.5 * scrHeight),(int)(0.36 * scrWidth),(int)(.15 * scrHeight), "Exit", false, Color.blue, Color.white, ButtonType.exit));
        buttons.add(new MenuButton((int)(.30 * scrWidth),(int)(.7 * scrHeight),(int)(0.36 * scrWidth),(int)(.15 * scrHeight), "Do Nothing", false, Color.blue, Color.white, ButtonType.do_nothing));
    }

    public void update()
    {
        if (++nFrameCounter > 9999) nFrameCounter = 0;
        if (_inputHandler.down)
        {
            if (nFrameCounter % 8 != 0) return;
            buttons.get(kIndexFocused).setFocused(false);
            if (++kIndexFocused >= nMaxButtonIndex) kIndexFocused = 0;
            buttons.get(kIndexFocused).setFocused(true);
            nFrameCounter = 0;
        }
        if (_inputHandler.up)
        {
            if (nFrameCounter % 8 != 0) return;
            buttons.get(kIndexFocused).setFocused(false);
            if (--kIndexFocused < 0) kIndexFocused = nMaxButtonIndex - 1;
            buttons.get(kIndexFocused).setFocused(true);
            nFrameCounter = 0;
        }
        if (_inputHandler.bomb)
        {
            buttons.get(kIndexFocused).execute();
            nFrameCounter = 0;
        }
    }

    public void draw(Graphics2D graphics)
    {
        for (var button : buttons)
        {
            button.draw(graphics);
        }
    }

    protected class MenuButton {
        private ButtonType buttonType;
        private int x, y, width, height;
        private String text;
        private boolean bFocused;
        private Color bgColor, txtColor;

        public MenuButton(int x, int y, int width, int height, 
                          String text, boolean focused, Color bgColor, 
                          Color txtColor, ButtonType buttonType) {
            this.x = x; this.y = y; this.width = width; this.height = height;
            this.text = text; this.bFocused = focused; this.bgColor = bgColor;
            this.txtColor = txtColor; this.buttonType = buttonType;
        }

        public void draw(Graphics2D graphics)
        {   
            graphics.setFont(new Font("Sans Serif", Font.PLAIN, 26));
            if (bFocused) graphics.setColor(Color.white);
            else graphics.setColor(bgColor);
            graphics.fillRect(x, y, width, height);
            if (bFocused) graphics.setColor(Color.red);
            else graphics.setColor(txtColor);
            graphics.drawString(text, x + width / 4, y + height / 2);
        }
        
        public void execute()
        {
            switch (buttonType)
            {
                case start:
                    _gamePanel.setGameState(GameState.stage);
                    break;
                case exit:
                    _gamePanel.setExit(true);
                    JComponent component = (JComponent) _gamePanel.getParent();
                    Window window = SwingUtilities.getWindowAncestor(component);
                    window.dispose();
                    break;
                default:
            }
        }

        public void setFocused(boolean focused) { this.bFocused = focused; }
    }
}