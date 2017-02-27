package vision.shapeObject;

import org.opencv.core.Rect;
import vision.colorAnalysis.SDPColor;
import vision.spotAnalysis.approximatedSpotAnalysis.Spot;
import vision.tools.VectorGeometry;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by nlfox on 2/20/17.
 */
public class ShapeObject {
    public VectorGeometry pos;
    public Integer shape;
    public Rect boundingRect;
    public static Integer SQUARE = 0;
    public static Integer CIRCLE = 1;
    public HashMap<SDPColor, ArrayList<Spot>> spots;

    public ShapeObject(Double x, Double y) {

        this.pos = new VectorGeometry(x, y);
        spots = new HashMap<SDPColor, ArrayList<Spot>>();
        for (SDPColor c : SDPColor.values()) {
            spots.put(c, new ArrayList<Spot>());
        }
    }


}
