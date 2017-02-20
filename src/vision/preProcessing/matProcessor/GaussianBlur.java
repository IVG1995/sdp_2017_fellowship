package vision.preProcessing.matProcessor;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import vision.VisionSettings;

/**
 * Created by nlfox on 2/6/17.
 */
public class GaussianBlur implements MatProcessor {
    @Override
    public Mat process(Mat mat) {
        if (VisionSettings.enableGaussianBlur) {
            Imgproc.GaussianBlur(mat, mat, new Size(3, 3), 0);
        }
        return mat;
    }
}
