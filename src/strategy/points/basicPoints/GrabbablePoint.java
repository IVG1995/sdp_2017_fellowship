package strategy.points.basicPoints;

import strategy.points.*;
import strategy.Strategy;
import vision.*;
import vision.tools.VectorGeometry;


/**
 *
 */
public class GrabbablePoint extends DynamicPointBase{


    private RobotType type = null;
    private RobotAlias alias = null;
    private static final int DISTANCE_TO_GRABBER = 7;
    public GrabbablePoint(RobotType type){
        this.type = type;
    }

    public GrabbablePoint(RobotAlias alias){
        this.alias = alias;
    }

    @Override
    public void recalculate() {
        Robot r;

        if(this.alias == null) r = Strategy.world.getRobot(this.type);
        else r = Strategy.world.getRobot(this.alias);

        if(r != null){
            VectorGeometry v = r.location.clone();
            v.add((new VectorGeometry()).fromAngular(r.location.direction, DISTANCE_TO_GRABBER));
            this.x = (int) v.x;
            this.y = (int) v.y;
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
