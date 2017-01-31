package strategy.points.basicPoints;

import strategy.points.*;
import strategy.Strategy;
import vision.*;
import vision.tools.VectorGeometry;


/**
 * Created by cole on 1/31/17.
 */
public class KickablePoint extends DynamicPointBase{


    private RobotType type = null;
    private RobotAlias alias = null;

    public KickablePoint(RobotType type){
        this.type = type;
    }

    public KickablePoint(RobotAlias alias){
        this.alias = alias;
    }

    @Override
    public void recalculate() {
        Robot r;

        if(this.alias == null) r = Strategy.world.getRobot(this.type);
        else r = Strategy.world.getRobot(this.alias);

        if(r != null){
            VectorGeometry v = r.location.clone();
            v.add((new VectorGeometry()).fromAngular(r.location.direction, 7));
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
