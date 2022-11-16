package main;

import java.awt.Graphics2D;

import entity.Player;
import entity.BackgroundTile;
import entity.Block;
import entity.BreakableTile;
//import entity.BreakableTile;
import entity.SolidTile;

public class Stage {

    private GamePanel gamePanel;
    private InputHandler inputHandler;
    private Player player;
    //private BackgroundTile[] backgroundTiles;
    //private BreakableTile[] breakableTiles;
    //private SolidTile[] solidTiles;
    private Block[] tiles;

    public Stage(GamePanel gamePanel_, InputHandler inputHandler_)
    {
        gamePanel = gamePanel_;
        inputHandler = inputHandler_;

        player = new Player(this, gamePanel, inputHandler);
        //String map = buildMap();
        String map = testMap();

        //backgroundTiles = new BackgroundTile[gamePanel.kMaxScreenRows * gamePanel.kMaxScreenColumns];
        tiles = new Block[gamePanel.getMaxScreenRows() * gamePanel.getMaxScreenColumns()];     
        for (int i = 0; i < gamePanel.getMaxScreenRows(); i++)
        {
            for (int j = 0; j < gamePanel.getMaxScreenColumns(); j++)
            {
                int pos = j + i * gamePanel.getMaxScreenColumns();
                //backgroundTiles[j + i * gamePanel.kMaxScreenColumns] = new BackgroundTile(gamePanel, i * gamePanel.kTileSize, j * gamePanel.kTileSize);
                if (map.charAt(pos) == 'w')
                {
                    tiles[pos] = new SolidTile(gamePanel, j * gamePanel.getTileSize(), i * gamePanel.getTileSize());
                }
                else if (map.charAt(pos) == 'o')
                {
                    tiles[pos] = new BreakableTile(gamePanel, j * gamePanel.getTileSize(), i * gamePanel.getTileSize());
                }
                else
                {
                    tiles[pos] = new BackgroundTile(gamePanel, j * gamePanel.getTileSize(), i * gamePanel.getTileSize());
                }

            }
        }

        //solidTiles = new SolidTile[24];
        //breakableTiles = new BreakableTile[24];
    }

    public void update()
    {
        player.update();
    }

    public void draw(Graphics2D graphics)
    {
        for (Block tile : tiles)
        {
            tile.draw(graphics);
        }
        player.draw(graphics);
    }
    
    private String buildMap()
    {
        String baseMap = String.join("", 
        "wwwwwwwwwwwwwww",
        "w             w",
        "w w w w w w w w",
        "w             w",
        "w w w w w w w w",
        "w             w",
        "w w w w w w w w",
        "w             w",
        "w w w w w w w w",
        "w             w",
        "w w w w w w w w",
        "w             w",
        "w w w w w w w w",
        "w             w",
        "wwwwwwwwwwwwwww");
        return baseMap;
    }

    public Block[] getTiles() {
        return tiles;
    }

    private String testMap()
    {
        String testMap = String.join("",
        "wwwwwwwwwwwwwww",
        "w oo          w",
        "w wow w w w w w",
        "w ooooo       w",
        "w w wow wow w w",
        "w    o   o    w",
        "w w wow wow w w",
        "w    oooooo   w",
        "w w wow w w w w",
        "w             w",
        "w w w w w w w w",
        "w             w",
        "w w w w w w w w",
        "w             w",
        "wwwwwwwwwwwwwww");
        return testMap;
    }
}
