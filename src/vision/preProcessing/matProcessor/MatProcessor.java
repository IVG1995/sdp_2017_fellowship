package vision.preProcessing.matProcessor;

import org.opencv.core.Mat;

import java.awt.image.BufferedImage;

/**
 * Created by nlfox on 2/6/17.
 */
public interface MatProcessor {
    Mat process(Mat mat);
}

