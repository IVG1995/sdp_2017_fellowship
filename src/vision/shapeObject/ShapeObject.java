package vision.shapeObject;

import org.opencv.core.Rect;
import vision.colorAnalysis.SDPColor;
import vision.spotAnalysis.approximatedSpotAnalysis.Spot;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by nlfox on 2/20/17.
 */
public class ShapeObject {
    Double x;
    Double y;
    Integer shape;
    public Rect boundingRect;
    public static Integer SQUARE = 0;
    public static Integer CIRCLE = 1;
    public HashMap<SDPColor, ArrayList<Spot>> spots;
    public ShapeObject(Double x, Double y) {
        this.x = x;
        this.y = y;
        spots = new HashMap<SDPColor, ArrayList<Spot>>();
        for (SDPColor c : SDPColor.values()) {
            spots.put(c, new ArrayList<Spot>());
        }
    }




}
