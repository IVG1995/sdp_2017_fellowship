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
 *      -or its direction is unknown: ^
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
            int x_sign = (enemyDir.x >= 0) ? 1 : -1;
            int y_sign = (enemyDir.y >= 0) ? 1 : -1;

            // Make sure the point isn't outside the pitch/too close to walls
            if (Math.abs(enemyDir.x) > (Constants.PITCH_WIDTH / 2) - 15) enemyDir.x = x_sign * ((Constants.PITCH_WIDTH / 2) - 15);
            if (Math.abs(enemyDir.y) > (Constants.PITCH_HEIGHT / 2) - 15) enemyDir.y = y_sign * ((Constants.PITCH_HEIGHT / 2) - 15);

            this.x = (int) enemyDir.x;
            this.y = (int) enemyDir.y;
        }

    }

    @Override
    public int getX() { return this.x; }

    @Override
    public int getY() { return this.y; }

}
