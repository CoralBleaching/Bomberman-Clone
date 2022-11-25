package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;


public class GamePanel extends JPanel implements Runnable
{
    public static enum GameState { menu, stage, game_over }
    private static GameState _gameState; 
    private int nOriginalTileSize = 64; 
    private int nScale = 1;

    private int nTileSize = nOriginalTileSize * nScale;
    private int nMaxScreenColumns = 15;
    private int nMaxScreenRows = 15;

    private int nScreenWidth = nTileSize * nMaxScreenColumns;
    private int nScreenHeight = nTileSize * nMaxScreenRows;
 
    private int nFPS = 60;
    private int nOneSecondMs = 1000; // ms
    private int drawCount = 0;
    private long timer = 0;
    private double delta = 0;
    private double lastFpsCount = -1;

    private boolean bExit = false;

    InputHandler inputHandler;
    Thread gameThread;
    Stage stage;
    Menu menu;
    GameOver gameOver;

    public GamePanel()
    {
        _gameState = GameState.menu;
        inputHandler = new InputHandler();    
        stage = new Stage(this, inputHandler);   
        menu = new Menu(this, inputHandler); 
        gameOver = new GameOver(this, inputHandler);
        
        setPreferredSize(new Dimension(nScreenWidth, nScreenHeight));
        setBackground(Color.black);
        setDoubleBuffered(true);
        addKeyListener(inputHandler);
        setFocusable(true);
    }

    public void startGameThread()
    {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = nOneSecondMs / nFPS;
        long lastTime = System.currentTimeMillis();
        long currentTime;

        while (gameThread != null && !bExit)
        {
            currentTime = System.currentTimeMillis();
            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;


            if (delta >= 1)
            {
                update();

                repaint();

                delta--;
                drawCount++;
            }

            if (timer > nOneSecondMs)
            {
                lastFpsCount = drawCount;
                drawCount = 0;
                timer = 0;
            }

        }
        return;
    }

    public void update()
    {
        switch (_gameState)
        {
            case menu:
                menu.update();
                break;
            case stage:
                stage.update();
                break;
            case game_over:
                gameOver.update();
                break;
        }
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D)g;

        switch (_gameState)
        {
            case game_over:
                gameOver.draw(g2d);
                break;
            case menu:
                menu.draw(g2d);
                break;
            case stage:
                stage.draw(g2d);
                break;
        }
        
        g2d.setColor(Color.white);
        g2d.setFont(new Font("sans serif", Font.PLAIN, 10));
        g2d.drawString("FPS: " + lastFpsCount, 10, 10);


        g2d.dispose();
    }

    public int getGridLength() { return nMaxScreenColumns * nMaxScreenRows; }
    public int getTileSize() { return nTileSize; }
    public int getMaxScreenColumns() { return nMaxScreenColumns; }
    public int getMaxScreenRows() { return nMaxScreenRows; }
    public int getScale() { return nScale; }
    public int getScreenWidth() { return nScreenWidth; }
    public int getScreenHeight() { return nScreenHeight; }
    public void setExit(boolean exit) { bExit = exit; }
    public void setGameState(GameState gameState) { _gameState = gameState; }
}
