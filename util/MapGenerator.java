package util;

import java.util.ArrayList;
import java.util.Collections;

import entity.block.PowerUp.Type;
import main.GamePanel;

public class MapGenerator {
    private double frequency;
    private double xOffset, yOffset;
    private GamePanel gamePanel;

    long seed;
    double vald;
    double cutoff;
    int nPowerUps;
    MapElement val;
    ArrayList<Integer> breakableTilePos;

    public MapGenerator(GamePanel gamePanel, double frequency, double cutoff, long seed, int nPowerUps) {
        this.frequency = frequency;
        this.cutoff = cutoff;
        this.nPowerUps = nPowerUps;
        this.gamePanel = gamePanel;
        xOffset = 0;
        yOffset = 0;
        this.seed = seed;
        breakableTilePos = new ArrayList<Integer>();
    }

    public MapElement[] generateNoisyMap() {
        int width = gamePanel.getMaxScreenColumns();
        int height = gamePanel.getMaxScreenRows();
        MapElement[] map = new MapElement[width * height];
        for (int j = 0; j < height; j++) {
            xOffset = 0;
            for (int i = 0; i < width; i++) {
                int pos = i + j * width;
                if (i == 0 || i == width - 1 || j == 0 || j == height - 1 || (i % 2 == 0 && j % 2 == 0))
                    map[pos] = MapElement.solidTile;
                else if ((i == 1 && (j == 1 || j == 2)) || (i == 2 && j == 1))
                    map[pos] = MapElement.groundTile;
                else {
                    vald = OpenSimplex2S.noise2(seed, xOffset, yOffset);
                    vald = (vald + 1) / 2;
                    if (vald < cutoff) {
                        map[pos] = MapElement.breakableTile;
                        breakableTilePos.add(pos);
                    } else {
                        map[pos] = MapElement.groundTile;
                    }
                }
                xOffset += frequency;
            }
            yOffset += frequency;
        }
        return map;
    }

    public void populateMap(MapElement[] map) {
        Collections.shuffle(breakableTilePos);
        breakableTilePos
                .subList(0, nPowerUps)
                .forEach(i -> map[i] = MapElement.powerup);
    }

    public Type rafflePowerUp() {
        var val = Type.values();
        var len = val.length;
        var i = gamePanel.rng.nextInt(0, len);
        System.out.println(val[i]);
        return val[i];
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }
}
