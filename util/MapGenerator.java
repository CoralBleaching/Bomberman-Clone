package util;

import main.GamePanel;

public class MapGenerator {
    private double frequency;
    private double xOffset, yOffset;
    private int width, height;
    private GamePanel gamePanel;

    long seed;
    double vald;
    double cutoff;
    MapElement val;

    public MapGenerator(GamePanel gamePanel, double frequency, double cutoff, long seed) {
        this.frequency = frequency;
        this.cutoff = cutoff;
        this.gamePanel = gamePanel;
        xOffset = 0;
        yOffset = 0;
        width = gamePanel.getMaxScreenColumns();
        height = gamePanel.getMaxScreenRows();
        this.seed = seed;
    }

    public MapElement[] generateNoisyMap() {
        MapElement[] map = new MapElement[width * height];
        for (int j = 0; j < height; j++) {
            xOffset = 0;
            for (int i = 0; i < width; i++) {
                if (i == 0 ||
                        i == gamePanel.getMaxScreenColumns() - 1 ||
                        j == 0 ||
                        j == gamePanel.getMaxScreenRows() - 1 ||
                        (i % 2 == 0 && j % 2 == 0))
                    map[i + j * width] = MapElement.solidTile;
                else if ((i == 1 && (j == 1 || j == 2)) || (i == 2 && j == 2))
                    map[i + j * width] = MapElement.groundTile;
                else {
                    vald = OpenSimplex2S.noise2(seed, xOffset, yOffset);
                    vald = (vald + 1) / 2;
                    map[i + j * width] = (vald < cutoff) ? MapElement.breakableTile : MapElement.groundTile;
                }
                xOffset += frequency;
            }
            yOffset += frequency;
        }
        return map;
    }

    public void populateMap() {

    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }
}
