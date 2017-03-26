package vision;

import org.deeplearning4j.nn.modelimport.keras.InvalidKerasConfigurationException;
import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.deeplearning4j.nn.modelimport.keras.UnsupportedKerasConfigurationException;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.opencv.highgui.Highgui.imread;
import static org.opencv.highgui.Highgui.imwrite;

public class Predictor {
    public MultiLayerNetwork network = null;
    public MultiLayerNetwork cnn = null;
    public static Predictor predictor = null;

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    Predictor() {

        try {
            network = KerasModelImport.importKerasSequentialModelAndWeights(System.getProperty("user.dir") + "/my_model.json", System.getProperty("user.dir") + "/my_model.weights", false);
            cnn = KerasModelImport.importKerasSequentialModelAndWeights(System.getProperty("user.dir") + "/cnn.json", System.getProperty("user.dir") + "/cnn.weights", false);
        } catch (IOException | InvalidKerasConfigurationException | UnsupportedKerasConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static Predictor getPredictor() {
        if (predictor == null) {
            predictor = new Predictor();
        }
        return predictor;
    }

    public int getPlateKind(Mat image) {
        INDArray data = Nd4j.create(getHistArr(image));
        return network.predict(data)[0];
    }

    public boolean isRobot(Mat image) {
        Mat gray = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.equalizeHist(gray, gray);
        Imgproc.resize(gray, gray, new Size(32.0, 32.0));
        double[] data = new double[32 * 32];
        for (int i = 0; i < 32; i++) {
            for (int j = 0; j < 32; j++) {
                data[i * 32 + j] = gray.get(i, j)[0]/255.0;
            }
        }
        INDArray r = Nd4j.create(data);
        r = r.reshape(1,1, 32, 32);
        return cnn.predict(r)[0] == 1;
    }

    public double[] getHistArr(Mat image) {

        List<Mat> images = new ArrayList<>();
        Mat yuv_img = new Mat();
        Imgproc.cvtColor(image, yuv_img, Imgproc.COLOR_BGR2Lab);
        List<Mat> channels = new ArrayList<>();
        Core.split(yuv_img, channels);
        Imgproc.equalizeHist(channels.get(0), channels.get(0));
        Core.merge(channels, yuv_img);
        Imgproc.cvtColor(yuv_img, image, Imgproc.COLOR_Lab2BGR);
        images.add(image);
        double[] arr = new double[768];
        for (int i = 1; i <= 3; i++) {
            Mat hist = new Mat();
            Imgproc.calcHist(images, new MatOfInt(i - 1), new Mat(), hist, new MatOfInt(256), new MatOfFloat(0f, 256f));
            for (int j = 0; j < hist.height(); j++) {
                arr[(i - 1) * 255 + j] = hist.get(j, 0)[0] / 100.0;
            }
        }
        return arr;

    }

    public static void main(String[] args) throws Exception {
        Predictor p = Predictor.getPredictor();
        System.out.println(p.isRobot(imread("/Users/nlfox/Desktop/Screen Shot 2017-03-20 at 6.27.43 PM.png")));
    }
}
