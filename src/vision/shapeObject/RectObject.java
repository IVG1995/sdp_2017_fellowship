package vision.shapeObject;

import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by nlfox on 2/24/17.
 */
public class RectObject extends ShapeObject {


    public RectObject(Double x, Double y, Point[] rect_points) {
        super(x, y);
        shape = ShapeObject.SQUARE;
    }

    public RectObject(RotatedRect rotatedRect,Rect boundingRect) {
        super(rotatedRect.center.x, rotatedRect.center.y);
        shape = ShapeObject.SQUARE;
        this.boundingRect = boundingRect;
    }

}
