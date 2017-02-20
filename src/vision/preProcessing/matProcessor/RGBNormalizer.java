package vision.preProcessing.matProcessor;

import org.opencv.core.Mat;

/**
 * Created by daniel on 2/21/17.
 */
public class RGBNormalizer implements MatProcessor {
    public RGBNormalizer(){

    }
    @Override
    public Mat process(Mat mat) {
        for(int i = 0; i<mat.rows(); i++){
            for(int j = 0; j < mat.cols(); j++){
                double [] rgb = mat.get(i, j);
                double sum = rgb[0]+rgb[1]+rgb[2];
                mat.put(i, j, rgb[0]/sum, rgb[1]/sum, rgb[2]/sum);
            }
        }
        return mat;
    }
}
