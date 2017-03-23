package vision.spotAnalysis.recursiveSpotAnalysis;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import vision.colorAnalysis.SDPColor;
import vision.colorAnalysis.SDPColorInstance;
import vision.colorAnalysis.SDPColors;
import vision.constants.Constants;
import vision.gui.Preview;
import vision.preProcessing.matProcessor.BgSubtractor;
import vision.shapeObject.CircleObject;
import vision.shapeObject.RectObject;
import vision.shapeObject.ShapeObject;
import vision.spotAnalysis.SpotAnalysisBase;
import vision.spotAnalysis.approximatedSpotAnalysis.Spot;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static org.opencv.highgui.Highgui.imwrite;
import static vision.tools.ImageTools.rgbToHsv;

/**
 * Created by Simon Rovder
 */
public class PartialSpotAnalysis extends SpotAnalysisBase {

    private int[] rgb;
    private float[] hsv;
    private SDPColor[] found;
    private int width;
    private int height;
    private int x;
    private int y;
    private int marker_count;

    public PartialSpotAnalysis() {
        super();
        // Have arrays of 4 times the size for the inputs\
        // (for red, green, blue, alpha OR hue, saturation, value, alpha)
        this.rgb = new int[4 * Constants.INPUT_WIDTH * Constants.INPUT_HEIGHT];
        this.hsv = new float[4 * Constants.INPUT_WIDTH * Constants.INPUT_HEIGHT];


        // array to keep track of visited spots
        this.found = new SDPColor[Constants.INPUT_WIDTH * Constants.INPUT_HEIGHT];
    }

    private int getIndex(int x, int y) {
        return y * Constants.INPUT_WIDTH * 3 + x * 3;
    }

    private void processPixel(int x, int y, SDPColorInstance sdpColorInstance, XYCumulativeAverage average, int maxDepth) {
        if (maxDepth <= 0 || x < this.x || x >= this.x + this.width || y < this.y || y >= this.y + this.height) return;
        int i = getIndex(x, y);
        if (this.found[i / 3] == sdpColorInstance.sdpColor) return;
        if (sdpColorInstance.isColor(this.hsv[i], this.hsv[i + 1], this.hsv[i + 2], x, y)) {

            average.addPoint(x, y);
            this.found[i / 3] = sdpColorInstance.sdpColor;
            this.processPixel(x, y + 1, sdpColorInstance, average, maxDepth - 1);
            this.processPixel(x, y - 1, sdpColorInstance, average, maxDepth - 1);
            this.processPixel(x - 1, y, sdpColorInstance, average, maxDepth - 1);
            this.processPixel(x + 1, y, sdpColorInstance, average, maxDepth - 1);
            Graphics g = Preview.getImageGraphics();
            if (g != null && sdpColorInstance.isVisible()) {
                g.setColor(Color.WHITE);
                g.drawRect(x, y, 1, 1);
            }
        }
    }


    @Override
    public void nextFrame(BufferedImage image, long time) {


        Raster raster = image.getData();

        /*
         * SDP2017NOTE
         * This line right here, right below is the reason our vision system is real time. We fetch the
         * rgb values of the Raster into a preallocated array this.rgb, without allocating more memory.
         * We recycle the memory, so garbage collection is never called.
         */
        raster.getPixels(0, 0, Constants.INPUT_WIDTH, Constants.INPUT_HEIGHT, this.rgb);
        rgbToHsv(this.rgb, this.hsv);

        XYCumulativeAverage average = new XYCumulativeAverage();
        SDPColorInstance colorInstance;
        ArrayList<ShapeObject> objs = new ArrayList<>();
        for (ShapeObject i : BgSubtractor.objects) {

            this.width = i.boundingRect.width;
            this.height = i.boundingRect.height;
            this.x = i.boundingRect.x;
            this.y = i.boundingRect.y;


            for (int j = this.y; j < this.y + this.height; j++) {
                for (int k = this.x; k < this.width + this.x; k++) {
                    this.found[getIndex(k, j) / 3] = null;
                }
            }
            Integer color_count = 0;
            Integer spot_count = 0;
            for (SDPColor color : SDPColor.values()) {
                colorInstance = SDPColors.colors.get(color);
                Boolean flag_color = false;
                for (int y = i.boundingRect.y; y < i.boundingRect.y + i.boundingRect.height; y++) {
                    for (int x = i.boundingRect.x; x < i.boundingRect.x + i.boundingRect.width; x++) {
                        this.processPixel(x, y, colorInstance, average, 200);
                        if (average.getCount() > 5) {
                            spot_count += 1;
                            i.spots.get(color).add(new Spot(average.getXAverage(), average.getYAverage(), average.getCount(), color));
                            flag_color = true;
                        }
                        average.reset();
                    }
                }
                Collections.sort(i.spots.get(color));
                if (flag_color) {
                    color_count += 1;
                }


            }

            if (color_count > 2 || spot_count > 2) {
                objs.add(i);

            } else if (
                    ((i.spots.get(SDPColor._BALL).size() >= 1) || (i.spots.get(SDPColor.PINK).size() >= 1))
                            && (i instanceof CircleObject)
                    ) {
                objs.add(i);
            }


        }
        BgSubtractor.objects = objs;
        for (int i = 0; i < objs.size(); i++) {
            if (objs.get(i) instanceof RectObject) {
                Mat m = new Mat(BgSubtractor.cur_mat, objs.get(i).boundingRect);
                imwrite(String.format("/tmp/train/%s_%s.jpg", Integer.toString((int) BgSubtractor.cnt), Integer.toString(i)), m);
            }
        }
        this.informListeners(objs, time);
        Preview.flushToLabel();

    }
}
