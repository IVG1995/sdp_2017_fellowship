package strategy.actions.defence;

import strategy.actions.ActionBase;
import strategy.Strategy;
import strategy.robots.Frodo;
import strategy.robots.RobotBase;
import vision.Robot;
import vision.RobotType;
import vision.constants.Constants;
import vision.tools.VectorGeometry;

/**
 * Kicks the ball to a corner by the enemy goal.
 * Called with the assumption that Frodo has the ball in the grabber.
 */
public class Clear extends ActionBase {

    private final int NEED_TO_TURN = 0;
    private final int FACING_POINT = 1;

    // Points to the left and right of the enemy goal.
    private VectorGeometry leftOfGoal = new VectorGeometry(Constants.PITCH_WIDTH / 2, Constants.PITCH_HEIGHT / 2);
    private VectorGeometry rightOfGoal = new VectorGeometry(Constants.PITCH_WIDTH / 2, -Constants.PITCH_HEIGHT / 2);

    private VectorGeometry chosenClearPoint;
    private Robot us;

    public Clear(RobotBase robot) {
        super(robot);
    }

    @Override
    public void enterState(int newState) {
        if (newState == NEED_TO_TURN) {
            // Rotate to point
        } else if (newState == FACING_POINT){
            // Kick
            ((Frodo)this.robot).KICKER_CONTROLLER.setWantToKick(true);
        }

        this.state = newState;
    }

    @Override
    public void tok() {
        this.us = Strategy.world.getRobot(RobotType.FRIEND_2);

        if (nothingInWay(leftOfGoal)) {
            this.chosenClearPoint = leftOfGoal;
        } else if (nothingInWay(rightOfGoal)){
            this.chosenClearPoint = rightOfGoal;
        } else {
            // TODO: Both corners are blocked

        }

        if (isFacing(chosenClearPoint)) {
            this.enterState(FACING_POINT);
        } else {
            this.enterState(NEED_TO_TURN);
        }

    }

    private boolean nothingInWay(VectorGeometry point) {
        VectorGeometry toPoint = VectorGeometry.fromTo(this.us.location, point);

        boolean canKick = true;
        for (Robot r : Strategy.world.getRobots()) {
            if (r != null && r.type != RobotType.FRIEND_2 && r.type != RobotType.FRIEND_1) {
                canKick = canKick && VectorGeometry.closestPointToLine(this.us.location, toPoint, r.location).length() > 20;
            }
        }

        return canKick;
    }

    private boolean isFacing(VectorGeometry point) {

        VectorGeometry usDir = (new VectorGeometry()).fromAngular(10, us.location.direction);
        VectorGeometry toPoint = VectorGeometry.fromTo(us.location, point);

        return (VectorGeometry.angle(usDir, toPoint) < (Math.PI / 6.0));
    }



}
