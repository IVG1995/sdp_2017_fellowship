package strategy.points.basicPoints;


import strategy.Strategy;
import strategy.points.DynamicPointBase;
import vision.Robot;
import vision.RobotType;
import vision.constants.Constants;
import vision.tools.VectorGeometry;

/**
 * If the enemy ball holder (EBH) is:
 *  -facing away from our goal: this point is right next to EBH, in between it and the friendly goal.
 *  -facing towards our goal: this point is directly in front of EBH, (hopefully) blocking its passes and shots.
 */
public class AnnoyBallHolderPoint extends DynamicPointBase {

    @Override
    public void recalculate() {
        Robot us = Strategy.world.getRobot(RobotType.FRIEND_2);
        Robot ballHolder = Strategy.world.getRobot(Strategy.world.getProbableBallHolder());

        if (us == null || ballHolder == null) { return; }

        VectorGeometry enemyGoal = new VectorGeometry(Constants.PITCH_WIDTH / 2, 0);
        VectorGeometry friendlyGoal = new VectorGeometry(-Constants.PITCH_WIDTH / 2, 0);
        VectorGeometry enemyDir = new VectorGeometry().fromAngular(ballHolder.location.direction, 40);

        // If EBH is facing away from our goal:
        if (VectorGeometry.isInGeneralDirection(ballHolder.location, enemyDir, enemyGoal)) {
            VectorGeometry holderToGoal = VectorGeometry.fromTo(ballHolder.location, friendlyGoal);
            double angle = VectorGeometry.angle(holderToGoal, enemyDir);
            VectorGeometry annoyingPoint = ballHolder.location.fromAngular(angle, 40);

            this.x = (int) annoyingPoint.x;
            this.y = (int) annoyingPoint.y;
        } else {

            this.x = (int) enemyDir.x;
            this.y = (int) enemyDir.y;
        }

    }

    @Override
    public int getX() { return this.x; }

    @Override
    public int getY() { return this.y; }

}
