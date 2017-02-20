package vision.preProcessing.matProcessor;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorMOG;
import org.opencv.video.BackgroundSubtractorMOG2;
import vision.VisionSettings;
import vision.preProcessing.PreProcessor;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;

/**
 * Created by nlfox on 2/5/17.
 */
public class BgSubtractor implements MatProcessor {
    public static ArrayList<ArrayList<Double>> objects;
    BackgroundSubtractorMOG2 backgroundSubtractorMOG;
    public static long cnt = 0;

    public BgSubtractor() {
        backgroundSubtractorMOG = new BackgroundSubtractorMOG2();
    }

    @Override
    public Mat process(Mat mat) {
        if (!VisionSettings.enableBgSub) {
            return mat;
        }
        cnt += 1;
        Mat fgMask = new Mat();
        if (cnt < 200) {
            backgroundSubtractorMOG.apply(mat, fgMask, 0.5);
        } else {
            backgroundSubtractorMOG.apply(mat, fgMask, 0);

        }
        Mat output = new Mat();
        mat.copyTo(output, fgMask);

        //update object list

        return output;


    }


}
