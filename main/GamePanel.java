package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;


public class GamePanel extends JPanel implements Runnable
{
    private int nOriginalTileSize = 64; 
    private int nScale = 1;

    private int nTileSize = nOriginalTileSize * nScale;
    private int nMaxScreenColumns = 15;
    private int nMaxScreenRows = 15;

    private int nScreenWidth = nTileSize * nMaxScreenColumns;
    private int nScreenHeight = nTileSize * nMaxScreenRows;
 
    private int nFPS = 60;
    private int nOneSecondMs = 1000; // ms

    InputHandler inputHandler;
    Thread gameThread;
    Stage stage;

    public GamePanel()
    {
        inputHandler = new InputHandler();    
        stage = new Stage(this, inputHandler);    
        
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
        double delta = 0;
        long lastTime = System.currentTimeMillis();
        long currentTime;
        // long timer = 0;
        // int drawCount = 0;

        while (gameThread != null)
        {
            currentTime = System.currentTimeMillis();
            delta += (currentTime - lastTime) / drawInterval;
            // timer += (currentTime - lastTime);
            lastTime = currentTime;

            if (delta >= 1)
            {
                update();

                repaint();

                delta--;

                // drawCount++;
            }

            // if (timer > oneSecondMs)
            // {
            //     System.out.println("FPS: " + drawCount);
            //     drawCount = 0;
            //     timer = 0;
            // }
        }
        
    }

    public void update()
    {
        stage.update();
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D)g;

        stage.draw(g2d);

        g2d.dispose();
    }

    public int getGridLength() { return nMaxScreenColumns * nMaxScreenRows; }
    public int getTileSize() { return nTileSize; }
    public int getMaxScreenColumns() { return nMaxScreenColumns; }
    public int getMaxScreenRows() { return nMaxScreenRows; }
    public int getScale() { return nScale; }
}
