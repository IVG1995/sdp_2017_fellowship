package vision.preProcessing.matProcessor;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorMOG2;
import vision.Vision;
import vision.VisionSettings;
import vision.preProcessing.BrightnessProcessor;
import vision.preProcessing.OpenCVProcessor;
import vision.shapeObject.CircleObject;
import vision.shapeObject.RectObject;
import vision.shapeObject.ShapeObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import static vision.preProcessing.OpenCVProcessor.img2Mat;

/**
 * Created by nlfox on 2/5/17.
 */
public class BgSubtractor implements MatProcessor {
    public static ArrayList<ShapeObject> objects;
    public static BackgroundSubtractorMOG2 backgroundSubtractorMOG = new BackgroundSubtractorMOG2(50, 12, true);
    public static long cnt = 0;

    public BgSubtractor() {
    }

    public static RotatedRect getApproxContour(MatOfPoint thisContour) {

        MatOfPoint2f thisContour2f = new MatOfPoint2f();
        MatOfPoint approxContour = new MatOfPoint();
        MatOfPoint2f approxContour2f = new MatOfPoint2f();

        thisContour.convertTo(thisContour2f, CvType.CV_32FC2);

        //adjust the 0.02
        double approxDistance = Imgproc.arcLength(thisContour2f, true) * 0.02;

        Imgproc.approxPolyDP(thisContour2f, approxContour2f, approxDistance, true);
        //System.out.println(" contour size: " + thisContour.size() + " apprix size " + thisContour2f.size());
        approxContour2f.convertTo(approxContour, CvType.CV_32S);

        return Imgproc.minAreaRect(approxContour2f);
    }


    @Override
    public Mat process(Mat mat) {
        Mat fgMask = new Mat();
        if (cnt < 50 && VisionSettings.trainFromStaticImage) {
            System.out.println(" Start training from static image");
            BrightnessProcessor brightnessProcessor = new BrightnessProcessor();
            OpenCVProcessor openCVProcessor = new OpenCVProcessor();
            openCVProcessor.matProcessors.add(new GaussianBlur());
            for (int i = 1; i < 50; i++) {
                BufferedImage bi = imread(String.format(VisionSettings.trainingImagePath + "/00000%1$03d.jpg", i));
                brightnessProcessor.process(bi);
                openCVProcessor.process(bi);
                backgroundSubtractorMOG.apply(img2Mat(bi), new Mat(), 0.8);
            }
            cnt = 50;
            System.out.println("Stop train from image");
        }

        cnt += 1;


        if (cnt < 50) {
            backgroundSubtractorMOG.apply(mat, fgMask, 0.8);
            //return mat;
        } else {
            backgroundSubtractorMOG.apply(mat, fgMask, 0);
        }
        Mat output = new Mat();
        // Maybe 128?
        Imgproc.threshold(fgMask, fgMask, 128, 255, Imgproc.THRESH_BINARY);
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hier = new Mat();
        mat.copyTo(output, fgMask);
        Imgproc.findContours(fgMask, contours, hier, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        objects = new ArrayList<>();
        for (int i = 0; i < contours.size(); i++) {
            if (Imgproc.contourArea(contours.get(i)) > 200) {
                RotatedRect rotatedRect = getApproxContour(contours.get(i));
                // double angle = rotatedRect.angle; // angle
                Point[] rect_points = new Point[4];

                rotatedRect.points(rect_points);
                // read center of rotated rect
                Point center = rotatedRect.center; // center
                // add plate

                Rect boundingRect = Imgproc.boundingRect(contours.get(i));
                objects.add(new RectObject(rotatedRect, boundingRect));
                Core.rectangle(output, boundingRect.tl(), boundingRect.br(), new Scalar(255, 255, 255));
                //Core.circle(output, center, 5, new Scalar(255, 255, 255));
                // draw rotated rect
//                for (int j = 0; j < 4; ++j) {
//                    Core.line(output, rect_points[j], rect_points[(j + 1) % 4], new Scalar(255, 255, 255));
//                }


            } else if (Imgproc.contourArea(contours.get(i)) > 80) {
                Point center = new Point();
                float[] radius = new float[1];
                MatOfPoint thisContour = contours.get(i);
                MatOfPoint2f thisContour2f = new MatOfPoint2f();
                thisContour.convertTo(thisContour2f, CvType.CV_32FC2);
                Imgproc.minEnclosingCircle(thisContour2f, center, radius);
                Rect boundingRect = Imgproc.boundingRect(thisContour);
                objects.add(new CircleObject(center, radius[0], boundingRect));
                Core.rectangle(output, boundingRect.tl(), boundingRect.br(), new Scalar(255, 255, 255));
            }
        }


        //update object list

        return output;


    }

    public static BufferedImage imread(String s) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(s));
        } catch (IOException e) {
        }
        return img;

    }

}
