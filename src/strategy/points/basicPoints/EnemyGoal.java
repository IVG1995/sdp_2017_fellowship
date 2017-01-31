package strategy.points.basicPoints;

import strategy.points.DynamicPointBase;
import vision.constants.Constants;
import vision.tools.VectorGeometry;

/**
 * Created by Simon Rovder
 */
public class EnemyGoal extends DynamicPointBase {
    @Override
    public void recalculate() {

    }

    @Override
    public int getX() {
        return Constants.PITCH_WIDTH/2;
    }

    @Override
    public int getY() {
        return 0;
    }

    public VectorGeometry toVectorGeometry() {
        return new VectorGeometry(getX(), getY());
    }
}
