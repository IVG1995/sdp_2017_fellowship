package strategy.points;

import strategy.points.basicPoints.ConstantPoint;
import vision.constants.Constants;
import vision.tools.VectorGeometry;

/**
 * Created by Simon Rovder
 * SDP2017NOTE
 * Extend this class to create more points.
 */
public abstract class DynamicPointBase implements DynamicPoint {
    protected int x;
    protected int y;

    public VectorGeometry toVectorGeometry(){
        return new VectorGeometry(x, y);
    }
    public static DynamicPoint getEnemyGoalPoint(){
        return new ConstantPoint(Constants.PITCH_WIDTH/2, 0);
    }
}
