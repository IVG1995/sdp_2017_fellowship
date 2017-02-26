package vision.preProcessing.matProcessor;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorMOG;
import org.opencv.video.BackgroundSubtractorMOG2;
import sun.security.provider.SHA;
import vision.ShapeObject;
import vision.VisionSettings;
import vision.preProcessing.PreProcessor;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by nlfox on 2/5/17.
 */
public class BgSubtractor implements MatProcessor {
    public static ArrayList<ShapeObject> objects;
    BackgroundSubtractorMOG2 backgroundSubtractorMOG;
    public static long cnt = 0;

    public BgSubtractor() {
        backgroundSubtractorMOG = new BackgroundSubtractorMOG2(50, 26, true);
        objects = new ArrayList<>();
    }

    public static RotatedRect getApproxContour(MatOfPoint thisContour) {

        MatOfPoint2f thisContour2f = new MatOfPoint2f();
        MatOfPoint approxContour = new MatOfPoint();
        MatOfPoint2f approxContour2f = new MatOfPoint2f();

        thisContour.convertTo(thisContour2f, CvType.CV_32FC2);

        //adjust the 0.02
        double approxDistance = Imgproc.arcLength(thisContour2f, true) * 0.02;

        Imgproc.approxPolyDP(thisContour2f, approxContour2f, approxDistance, true);
//        System.out.println(" contour size: " + thisContour.size() + " apprix size " + thisContour2f.size());
        approxContour2f.convertTo(approxContour, CvType.CV_32S);

        return Imgproc.minAreaRect(approxContour2f);
    }

    @Override
    public Mat process(Mat mat) {
        if (!VisionSettings.enableBgSub) {
            return mat;
        }
        cnt += 1;
        Mat fgMask = new Mat();
        if (cnt < 50) {
            backgroundSubtractorMOG.apply(mat, fgMask, 0.8);
        } else {
            backgroundSubtractorMOG.apply(mat, fgMask, 0);
        }
        Mat output = new Mat();
        Mat thresh = new Mat();
        Imgproc.threshold(fgMask, thresh, 128, 255, Imgproc.THRESH_BINARY);
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hier = new Mat();
        mat.copyTo(output, fgMask);
        Imgproc.findContours(fgMask, contours, hier, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        for (int i = 0; i < contours.size(); i++) {
            if (Imgproc.contourArea(contours.get(i)) > 200) {
                RotatedRect rotatedRect = getApproxContour(contours.get(i));
                // double angle = rotatedRect.angle; // angle
                Point[] rect_points = new Point[4];
                rotatedRect.points(rect_points);
                // read center of rotated rect
                Point center = rotatedRect.center; // center
                // add plate
                objects.add(new ShapeObject(center.x, center.y, ShapeObject.SQUARE));

//                Core.circle(mat,center,5,new Scalar(255,0,0));
//                // draw rotated rect
//                for (int j = 0; j < 4; ++j) {
//                    Core.line(mat, rect_points[j], rect_points[(j + 1) % 4], new Scalar(0, 255, 0));
//                }


            } else if (Imgproc.contourArea(contours.get(i)) > 100) {
                Point center = new Point();
                float[] radius = new float[1];
                MatOfPoint thisContour = contours.get(i);
                MatOfPoint2f thisContour2f = new MatOfPoint2f();
                thisContour.convertTo(thisContour2f, CvType.CV_32FC2);
                Imgproc.minEnclosingCircle(thisContour2f, center, radius);
                objects.add(new ShapeObject(center.x,center.y,ShapeObject.CIRCLE));
                //Core.circle(mat, center, Math.round(radius[0]), new Scalar(0, 255, 0));

            }
        }


        //update object list

        return output;


    }


}
