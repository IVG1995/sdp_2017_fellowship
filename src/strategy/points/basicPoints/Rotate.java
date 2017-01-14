package strategy.points.basicPoints;

import strategy.Strategy;
import strategy.points.DynamicPointBase;
import vision.Robot;
import vision.RobotType;
import vision.tools.VectorGeometry;

/**
 * Created by Simon Rovder
 */
public class Rotate extends DynamicPointBase {

    private boolean clockwise;

    public Rotate(boolean clockwise){
        this.clockwise = clockwise;
    }

    @Override
    public void recalculate() {
        Robot us = Strategy.world.getRobot(RobotType.FRIEND_2);
        if(us != null){
            VectorGeometry heading = VectorGeometry.fromAngular(us.location.direction, 10, null);
            heading.rotate(this.clockwise ? -1 : 1);
            heading.plus(us.location);
            this.x = (int)heading.x;
            this.y = (int)heading.y;
        }
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
