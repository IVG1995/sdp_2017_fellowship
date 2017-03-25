package vision;

import org.bytedeco.javacpp.FloatPointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.javacpp.indexer.FloatRawIndexer;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.deeplearning4j.nn.modelimport.keras.InvalidKerasConfigurationException;
import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.deeplearning4j.nn.modelimport.keras.UnsupportedKerasConfigurationException;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;

import static org.bytedeco.javacpp.opencv_imgcodecs.imread;
import static org.bytedeco.javacpp.opencv_imgproc.calcHist;

public class Predictor {
    public static MultiLayerNetwork network = null;

    Predictor() {
        try {
            network = KerasModelImport.importKerasSequentialModelAndWeights("/Users/nlfox/my_model.json", "/Users/nlfox/my_model.weights", false);
        } catch (IOException | InvalidKerasConfigurationException | UnsupportedKerasConfigurationException e) {
            e.printStackTrace();
        }
    }

    public int getPlateKind(Mat image){
        INDArray data = Nd4j.create(getHistArr(image));
        return network.predict(data)[0];
    }
    public double[] getHistArr(Mat image){
        int[] channels = new int[]{0};
        final int[] histSize = new int[]{256};
        final float[] histRange = new float[]{0f, 255f};

        IntPointer intPtrHistSize = new IntPointer(histSize);
        final PointerPointer<FloatPointer> ptrPtrHistRange = new PointerPointer<>(histRange);
        double[] arr = new double[768];
        for (int i = 1; i <= 3; i++) {
            Mat hist = new Mat();
            channels[0] = i-1;
            IntPointer intPtrChannels = new IntPointer(channels);
            calcHist(image, 1, intPtrChannels, new Mat(), hist, 1, intPtrHistSize, ptrPtrHistRange, true, false);
            FloatRawIndexer sI = hist.createIndexer();
            for (int y = 0; y < hist.rows(); y++) {
                arr[(i - 1) * 256 + y] = (double) (sI.get(y, 0));
            }

        }
        return arr;

    }
    public static void main(String[] args) throws Exception {
        Predictor  p = new Predictor();
        System.out.println(p.getPlateKind(imread("/Users/nlfox/train/4/119_0.jpg")));
    }
}
