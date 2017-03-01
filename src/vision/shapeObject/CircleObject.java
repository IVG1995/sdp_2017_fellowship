package vision.shapeObject;

import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import strategy.navigation.aStarNavigation.Circle;

/**
 * Created by nlfox on 2/25/17.
 */
public class CircleObject  extends ShapeObject{

    float radius;

    public CircleObject( Point center,float radius, Rect boundingRect) {
        super(center.x, center.y);
        shape = ShapeObject.SQUARE;
        this.radius = radius;
        this.boundingRect = boundingRect;

    }
}
