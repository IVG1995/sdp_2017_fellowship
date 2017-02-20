package vision.preProcessing;

import java.awt.image.BufferedImage;

/**
 * Created by nlfox on 2/5/17.
 */
public interface PreProcessor {
    BufferedImage process(BufferedImage image);
}
