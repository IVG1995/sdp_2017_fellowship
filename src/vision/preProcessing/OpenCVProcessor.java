package vision.preProcessing;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import vision.constants.Constants;
import vision.preProcessing.matProcessor.BgSubtractor;
import vision.preProcessing.matProcessor.GaussianBlur;
import vision.preProcessing.matProcessor.MatProcessor;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nlfox on 2/6/17.
 */
public class OpenCVProcessor implements PreProcessor {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    public OpenCVProcessor() {
    }

    public static BufferedImage mat2Img(Mat in) {
        Imgproc.cvtColor(in, in, Imgproc.COLOR_RGB2BGR);
        BufferedImage out;
        byte[] data = new byte[Constants.INPUT_WIDTH * Constants.INPUT_HEIGHT * (int) in.elemSize()];
        int type;
        in.get(0, 0, data);

        if (in.channels() == 1)
            type = BufferedImage.TYPE_BYTE_GRAY;
        else
            type = BufferedImage.TYPE_3BYTE_BGR;

        out = new BufferedImage(Constants.INPUT_WIDTH, Constants.INPUT_HEIGHT, type);

        out.getRaster().setDataElements(0, 0, Constants.INPUT_WIDTH, Constants.INPUT_HEIGHT, data);
        return out;
    }

    public static Mat img2Mat(BufferedImage image) {
        Mat mat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);

        byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        mat.put(0, 0, data);
        return mat;
    }

    public List<MatProcessor> matProcessors = new ArrayList<MatProcessor>() ;

    @Override
    public BufferedImage process(BufferedImage image) {
        Mat m = img2Mat(image);
        for (MatProcessor mp : matProcessors) {
            m = mp.process(m);
        }
        return mat2Img(m);
    }
}
