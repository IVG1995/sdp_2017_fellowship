package vision.preProcessing;

import vision.VisionSettings;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by nlfox on 2/5/17.
 */
public class BrightnessProcessor implements PreProcessor {

    @Override
    public BufferedImage process(BufferedImage image) {
        Graphics g = image.getGraphics();
        float percentage = VisionSettings.brightness; // 50% bright - change this (or set dynamically) as you feel fit
        int brightness = (int) (256 - 256 * percentage);
        g.setColor(new Color(0, 0, 0, brightness));
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        return image;
    }
}
