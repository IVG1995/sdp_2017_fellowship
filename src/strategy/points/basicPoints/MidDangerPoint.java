package strategy.points.basicPoints;

import strategy.WorldTools;
import strategy.points.DynamicPointBase;
import vision.constants.Constants;
import vision.tools.VectorGeometry;

/**
 * Created by Simon Rovder
 */
public class MidDangerPoint extends DynamicPointBase {

    private DangerousPoint danger = new DangerousPoint();

    @Override
    public void recalculate() {
        this.danger.recalculate();
        VectorGeometry dangerV = new VectorGeometry(danger.getX(), danger.getY());
        VectorGeometry base;

        if(WorldTools.isPointInFriendDefenceArea(dangerV)){
            base = VectorGeometry.vectorToClosestPointOnFiniteLine(new VectorGeometry(-Constants.PITCH_WIDTH/2, 20), new VectorGeometry(-Constants.PITCH_WIDTH/2, 20), dangerV);
        } else {
            base = new VectorGeometry(-Constants.PITCH_WIDTH/2, 0);
        }
        VectorGeometry goal = base.minus(danger.getX(), danger.getY());
        goal.multiply(0.5);
        goal.add(danger.getX(), danger.getY());
        this.x = (int)(goal.x);
        this.y = (int)(goal.y);

    }

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public int getY() {
        return this.y;
    }
}
