package main;

import java.awt.Graphics2D;

import entity.Bomb;
import entity.Flame;
import entity.block.BackgroundTile;
import entity.block.Block;
import entity.block.BreakableTile;
import entity.block.SolidTile;
import entity.character.Character;
import entity.character.Player;
import entity.character.baddie.BaseBaddie;
import main.Sound.Sounditem;
import util.InputHandler;
import util.MapElement;
import util.MapGenerator;

public class Stage {

    private GamePanel gamePanel;
    private Player player;
    private MapGenerator mapGenerator;

    private int nStartFrames, width, height, tileSize;
    private boolean bRoundStarted;
    private long seed = 2022;
    private double frequency;

    MapElement[] map;

    private Bomb[] bombs;
    private Block[] tiles;
    private Flame[] flames;
    private Character[] baddies;

    public Stage(GamePanel gamePanel, InputHandler inputHandler) {
        this.gamePanel = gamePanel;
        width = gamePanel.getMaxScreenColumns();
        height = gamePanel.getMaxScreenRows();
        tileSize = gamePanel.getTileSize();

        ///////////////////////////////////////
        frequency = 0.8;
        var cutoff = 0.4;
        var nPowerUps = 4;
        seed = (long) System.nanoTime();
        ///////////////////////////////////////

        mapGenerator = new MapGenerator(gamePanel, frequency, cutoff, seed, nPowerUps);
        map = mapGenerator.generateNoisyMap();
        mapGenerator.populateMap(map);

        player = new Player(this, gamePanel, inputHandler);

        int gridLength = gamePanel.getGridLength();
        tiles = new Block[gridLength];
        bombs = new Bomb[gridLength];
        flames = new Flame[gridLength];

        ///////////////////////////////////////
        baddies = new Character[6];
        int baddieIdx = 0;
        ///////////////////////////////////////

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                int pos = i + j * width;
                switch (map[pos]) {
                    case solidTile:
                        tiles[pos] = new SolidTile(gamePanel, i * tileSize, j * tileSize);
                        break;
                    case breakableTile:
                        tiles[pos] = new BreakableTile(gamePanel, i * tileSize,
                                j * tileSize,
                                this);
                        break;
                    case powerup:
                        tiles[pos] = new BreakableTile(gamePanel, i * tileSize,
                                j * tileSize,
                                this);
                        ((BreakableTile) tiles[pos]).setPowerUp(mapGenerator.rafflePowerUp());
                        break;
                    case baddie:
                        baddies[baddieIdx] = new BaseBaddie(this, gamePanel, i * tileSize, j * tileSize, tileSize,
                                tileSize, baddieIdx++);
                    case groundTile:
                        tiles[pos] = new BackgroundTile(gamePanel, i * tileSize,
                                j * tileSize);
                        break;
                }
            }
        }

        nStartFrames = 0;
        bRoundStarted = false;
    }

    public void update() {
        for (int i = 0; i < gamePanel.getGridLength(); i++) {
            if (bombs[i] != null)
                bombs[i].update();
            if (BreakableTile.class.isInstance(tiles[i]))
                tiles[i].update();
            if (flames[i] != null)
                flames[i].update();
        }
        for (var baddie : baddies) {
            if (baddie != null)
                baddie.update();
        }
        if (bRoundStarted)
            player.update();
        else if (++nStartFrames > 30)
            bRoundStarted = true;
    }

    public void draw(Graphics2D graphics) {
        for (int i = 0; i < gamePanel.getGridLength(); i++) {
            tiles[i].draw(graphics);
            if (flames[i] != null)
                flames[i].draw(graphics);
            if (bombs[i] != null)
                bombs[i].draw(graphics);
        }
        for (var baddie : baddies) {
            if (baddie != null)
                baddie.draw(graphics);
        }
        player.draw(graphics);
    }

    public Character[] getBaddies() {
        return baddies;
    }

    public Bomb[] getBombs() {
        return bombs;
    }

    public Flame[] getFlames() {
        return flames;
    }

    public MapElement[] getMap() {
        return map;
    }

    public Player getPlayer() {
        return player;
    }

    public Block[] getTiles() {
        return tiles;
    }

}
