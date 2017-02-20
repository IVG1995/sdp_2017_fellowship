package vision;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorMOG;
import org.opencv.video.BackgroundSubtractorMOG2;
import vision.constants.Constants;
import vision.preProcessing.matProcessor.GaussianBlur;
import vision.preProcessing.matProcessor.MatProcessor;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static java.lang.Thread.sleep;


/**
 * Created by nlfox on 2/4/17.
 */
public class testCV extends JFrame {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    static String prefix = "/Users/nlfox/SDP-2017-G-6/Fred_working/sdp/test/";

    static MatProcessor gb = new GaussianBlur();

    public static void main(String[] args) throws InterruptedException {
        JFrame test = null;
        Mat mask = new Mat(480, 640, CvType.CV_8UC1);
        BackgroundSubtractorMOG2 backgroundSubtractorMOG = new BackgroundSubtractorMOG2(50, 26, true);
        for (int i = 1; i < 50; i++) {
            backgroundSubtractorMOG.apply(img2Mat(imread(String.format(prefix+"shots/00000%1$03d.jpg",
                    i))), mask, 0.8);
        }
        for (int k = 1; k < 250; k++) {
            Mat img = img2Mat(imread(String.format(prefix + "shots/moving/00000%1$03d.jpg", k)));

            backgroundSubtractorMOG.apply(img.clone(), mask, 0);

            //Mat thresh = new Mat();
            Imgproc.threshold(mask, mask, 128, 255, Imgproc.THRESH_BINARY);
            List<MatOfPoint> contours = new ArrayList<>();
            Mat hier = new Mat();

            Mat res = new Mat();
            img.copyTo(res, mask);
            Mat res_masked = new Mat();
            //Imgproc.cvtColor(res,res_masked,Imgproc.COLOR_BGR2GRAY);
            Imgproc.findContours(mask, contours, hier, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

            for (int i = 0; i < contours.size(); i++) {
                if (Imgproc.contourArea(contours.get(i)) > 150) {

                    RotatedRect rotatedRect = getApproxContour(contours.get(i));
                    double angle = rotatedRect.angle; // angle
                    Point[] rect_points = new Point[4];
                    rotatedRect.points(rect_points);
                    // read center of rotated rect
                    Point center = rotatedRect.center; // center
                    Core.circle(res,center,5,new Scalar(255,0,0));
                    // draw rotated rect
                    for (int j = 0; j < 4; ++j) {
                        Core.line(res, rect_points[j], rect_points[(j + 1) % 4], new Scalar(0, 255, 0));
                    }

                    //Core.rectangle(res, rect.tl(), rect.br(), new Scalar(255, 0, 0),1, 8,0);
                    //Imgproc.drawContours(res, contours, i, new Scalar(0, 255, 0), 1);
                } else if (Imgproc.contourArea(contours.get(i)) > 100) {
                    Point center = new Point();
                    float[] radius = new float[1];
                    MatOfPoint thisContour = contours.get(i);
                    MatOfPoint2f thisContour2f = new MatOfPoint2f();
                    thisContour.convertTo(thisContour2f, CvType.CV_32FC2);
                    Imgproc.minEnclosingCircle(thisContour2f, center, radius);
                    Core.circle(res, center, Math.round(radius[0]), new Scalar(0, 255, 0));

                }
            }
            if (test == null){
                test  = new testCV(mat2Img(res));
                test.setVisible(true);
            }else {
                //TimeUnit.MILLISECONDS.sleep(200);
                label.setIcon(new ImageIcon(mat2Img(res)));
            }

        }
        //bitwise_and(img,mask,res);

    }



    public static RotatedRect getApproxContour(MatOfPoint thisContour) {

        MatOfPoint2f thisContour2f = new MatOfPoint2f();
        MatOfPoint approxContour = new MatOfPoint();
        MatOfPoint2f approxContour2f = new MatOfPoint2f();

        thisContour.convertTo(thisContour2f, CvType.CV_32FC2);


        double approxDistance = Imgproc.arcLength(thisContour2f, true) * 0.02;


        Imgproc.approxPolyDP(thisContour2f, approxContour2f, approxDistance, true);
        System.out.println(" contour size: " + thisContour.size() + " apprix size " + thisContour2f.size());
        approxContour2f.convertTo(approxContour, CvType.CV_32S);
        return Imgproc.minAreaRect(approxContour2f);
    }

    public static BufferedImage imread(String s) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(s));
        } catch (IOException e) {
        }
        return img;

    }
    private static JLabel label;

    private testCV(BufferedImage bi) {
        this.setSize(640, 480);
        JPanel panel = new JPanel();
        panel.setSize(640, 480);
        ImageIcon icon = new ImageIcon(bi);
        label = new JLabel();
        label.setIcon(icon);
        panel.add(label);
        this.getContentPane().add(panel);
    }

    public static BufferedImage mat2Img(Mat in) {
        int type;
        if (in.channels() == 1) {

            type = BufferedImage.TYPE_BYTE_GRAY;
        } else {
            Imgproc.cvtColor(in, in, Imgproc.COLOR_RGB2BGR);
            type = BufferedImage.TYPE_3BYTE_BGR;
        }

        BufferedImage out;
        byte[] data = new byte[Constants.INPUT_WIDTH * Constants.INPUT_HEIGHT * (int) in.elemSize()];

        in.get(0, 0, data);

        out = new BufferedImage(Constants.INPUT_WIDTH, Constants.INPUT_HEIGHT, type);

        out.getRaster().setDataElements(0, 0, Constants.INPUT_WIDTH, Constants.INPUT_HEIGHT, data);
        return out;
    }

    public static Mat img2Mat(BufferedImage image) {
        Mat mat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);

        byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        mat.put(0, 0, data);
        mat = gb.process(mat);
        return mat;
    }

    public static Mat normalize(Mat mat) {
        for (int i = 0; i < mat.rows(); i++) {
            for (int j = 0; j < mat.cols(); j++) {
                double[] rgb = mat.get(i, j);
                double sum = rgb[0] + rgb[1] + rgb[2];
                for (int k = 0; k < 3; k++) {
                    rgb[k] = rgb[k] / sum * 255;
                }
                mat.put(i, j, rgb);
            }
        }
        return mat;
    }


}
