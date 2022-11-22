package util;

// The following is protected under a Creative Commons Attribution-ShareAlike 4.0 International Public License
// source: https://stackoverflow.com/questions/21382966/colorize-a-picture-in-java

import java.awt.AlphaComposite;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Graphics2D;

public class Tint {
    private static BufferedImage tint(BufferedImage image, Color color)
    {
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage tinted = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = tinted.createGraphics();
        g.drawImage(image, 0,0, null);
        g.setComposite(AlphaComposite.SrcAtop);
        g.setColor(color);
        g.fillRect(0,0,w,h);
        g.dispose();
        return tinted;
    }
}
