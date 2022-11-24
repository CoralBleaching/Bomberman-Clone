package main;

import java.awt.Graphics2D;

import entity.Player;
import entity.BackgroundTile;
import entity.Block;
import entity.Bomb;
import entity.BreakableTile;
import entity.Flame;
//import entity.BreakableTile;
import entity.SolidTile;

public class Stage {

    private GamePanel gamePanel;
    private InputHandler inputHandler;
    private String map;
    private Player player;

    private Bomb[] bombs;
    private Block[] tiles;
    private Flame[] flames;

    public Stage(GamePanel gamePanel_, InputHandler inputHandler_)
    {
        gamePanel = gamePanel_;
        inputHandler = inputHandler_;

        player = new Player(this, gamePanel, inputHandler);
        //String map = buildMap();
        map = testMap();

        int gridLength = gamePanel.getGridLength();
        tiles = new Block[gridLength];
        bombs = new Bomb[gridLength];     
        flames = new Flame[gridLength];
        for (int i = 0; i < gamePanel.getMaxScreenRows(); i++)
        {
            for (int j = 0; j < gamePanel.getMaxScreenColumns(); j++)
            {
                int pos = j + i * gamePanel.getMaxScreenColumns();
                if (map.charAt(pos) == 'w')
                {
                    tiles[pos] = new SolidTile(gamePanel, j * gamePanel.getTileSize(), i * gamePanel.getTileSize());
                }
                else if (map.charAt(pos) == 'o')
                {
                    tiles[pos] = new BreakableTile(gamePanel, j * gamePanel.getTileSize(), i * gamePanel.getTileSize(), this);
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
        for (int i = 0; i < gamePanel.getGridLength(); i++)
        {
            if (bombs[i] != null) bombs[i].update();
            if (BreakableTile.class.isInstance(tiles[i])) tiles[i].update();
            if (flames[i] != null) flames[i].update();
        }
        player.update();
    }

    public void draw(Graphics2D graphics)
    {
        for (int i = 0; i < gamePanel.getGridLength(); i++)
        {
            tiles[i].draw(graphics);
            if (flames[i] != null) flames[i].draw(graphics);
            if (bombs[i] != null) bombs[i].draw(graphics);
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

    public Bomb[] getBombs() { return bombs; }
    public Flame[] getFlames() { return flames; }
    public String getMap() { return map; }
    public Player getPlayer() { return player; }
    public Block[] getTiles() { return tiles; }

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
