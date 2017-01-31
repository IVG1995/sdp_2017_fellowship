package strategy.points.basicPoints;

import strategy.Strategy;
import strategy.WorldTools;
import strategy.points.DynamicPointBase;
import vision.Ball;
import vision.Robot;
import vision.RobotType;
import vision.constants.Constants;
import vision.robotAnalysis.newRobotAnalysis.PatternMatcher;
import vision.tools.VectorGeometry;

/**
 * Created by Simon Rovder
 */
public class MidDangerPoint extends DynamicPointBase {

    private DangerousPoint danger = new DangerousPoint();

    private RobotType usType;

    public MidDangerPoint(RobotType robotType){
        this.usType = robotType;
    }

    @Override
    public void recalculate() {
        Ball b = Strategy.world.getBall();
        Robot us = Strategy.world.getRobot(this.usType);
        // if the ball or our robot is null, or if the ball is not moving (barely moving), do the following:
        if (b == null || VectorGeometry.squareLength(b.velocity.x, b.velocity.y) < 0.1 || us == null) {
            this.danger.recalculate();
            // contains the dangerous point (which is the ball, foe or in front of the goal in the case of 1v1)
            VectorGeometry dangerV = new VectorGeometry(danger.getX(), danger.getY());
            VectorGeometry base;

            if (WorldTools.isPointInFriendDefenceArea(dangerV)) {
                // This method gets the closest point to dangerV on the line formed by the first two arguments
                // BUG: The first two arguments are the same (???), so it will always return "new VectorGeometry(-Constants.PITCH_WIDTH / 2, 20)
                // Bug fixed by changing the second argument's y value to -20.
                base = VectorGeometry.vectorToClosestPointOnFiniteLine(new VectorGeometry(-Constants.PITCH_WIDTH / 2, 20), new VectorGeometry(-Constants.PITCH_WIDTH / 2, -20), dangerV);
            } else {
                base = new VectorGeometry(-Constants.PITCH_WIDTH / 2, 0);
            }

            // This looks weird but works? pretty sure QA:1
            VectorGeometry goal = base.minus(danger.getX(), danger.getY());
            goal.multiply(0.7);
            goal.add(danger.getX(), danger.getY());
            this.x = (int) (goal.x);
            this.y = (int) (goal.y);


        } else {
            // if we know where the ball and our robot is (and the ball is moving), point is shortest distance from our robot to the ball's path
            VectorGeometry v = VectorGeometry.closestPointToLine(b.location, b.velocity, us.location);
//            VectorGeometry v = VectorGeometry.intersectionWithFiniteLine(b.location, b.velocity, new VectorGeometry(-Constants.PITCH_WIDTH/2, 30), new VectorGeometry(-Constants.PITCH_WIDTH/2, -30));
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
