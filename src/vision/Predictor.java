package vision;

import org.deeplearning4j.nn.modelimport.keras.InvalidKerasConfigurationException;
import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.deeplearning4j.nn.modelimport.keras.UnsupportedKerasConfigurationException;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.opencv.highgui.Highgui.imread;

public class Predictor {
    public MultiLayerNetwork network = null;
    public static Predictor predictor = null;
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    Predictor() {

        try {
            network = KerasModelImport.importKerasSequentialModelAndWeights(System.getProperty("user.dir")+"/my_model.json", System.getProperty("user.dir")+"/my_model.weights", false);
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

    public double[] getHistArr(Mat image) {

        List<Mat> images = new ArrayList<>();
        Mat yuv_img = new Mat();
        Imgproc.cvtColor(image,yuv_img,Imgproc.COLOR_BGR2YUV);
        List<Mat> channels = new ArrayList<>();
        Core.split(yuv_img,channels);
        Imgproc.equalizeHist(channels.get(0),channels.get(0));
        Core.merge(channels,yuv_img);
        Imgproc.cvtColor(yuv_img,image,Imgproc.COLOR_YUV2BGR);
        images.add(image);
        double[] arr = new double[768];
        for (int i = 1; i <= 3; i++) {
            Mat hist = new Mat();

            Imgproc.calcHist(images,new MatOfInt(i-1),new Mat(),hist,new MatOfInt(256),new MatOfFloat(0f,256f));
            for (int j = 0; j < hist.height(); j++) {
                arr[(i-1)*255+j] = hist.get(j,0)[0]/100.0;
            }
        }
        return arr;

    }

    public static void main(String[] args) throws Exception {
        Predictor p = Predictor.getPredictor();
        System.out.println(p.getPlateKind(imread("/Users/nlfox/train/5/1016_0.jpg")));
    }
}
