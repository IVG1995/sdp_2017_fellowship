package strategy.actions.offense;

import strategy.Strategy;
import strategy.actions.ActionBase;
import strategy.points.basicPoints.RobotPoint;
import strategy.robots.RobotBase;
import vision.Robot;
import vision.RobotType;
import vision.constants.Constants;
import vision.tools.VectorGeometry;

import java.util.Queue;

/**
 * When our ally has the ball, get to a position where we can be passed to.
 */
public class GetOpen extends ActionBase {

    private final int GET_OPEN = 0;
    private final int STAY = 1;

    private static VectorGeometry usToFriend;
    private static Robot us;
    private static Robot friend;

    public GetOpen(RobotBase robot) {
        super(robot);
    }

    @Override
    public void enterState(int newState) {
        this.robot.MOTION_CONTROLLER.setHeading(new RobotPoint(RobotType.FRIEND_1));

        if (newState == GET_OPEN) {


            //this.robot.MOTION_CONTROLLER.setDestination(this.point);
        } else if (newState == STAY) {
            this.robot.MOTION_CONTROLLER.setDestination(null);
        }

        this.state = newState;

    }

    @Override
    public void tok() {
        us = Strategy.world.getRobot(RobotType.FRIEND_2);
        friend = Strategy.world.getRobot(RobotType.FRIEND_1);

        if (us == null || friend == null) {
            return;
        }

        usToFriend = VectorGeometry.fromTo(us.location, friend.location);
        if (open(us.location)) {
            this.enterState(STAY);
        } else {
            this.enterState(GET_OPEN);
        }

    }

    /**
     * Returns true if Frodo is open/would be open for a pass if he were in this location.
     * @return
     */
    private static boolean open(VectorGeometry location) {

        Robot foeOne = Strategy.world.getRobot(RobotType.FOE_1);
        Robot foeTwo = Strategy.world.getRobot(RobotType.FOE_2);

        if (foeOne != null && VectorGeometry.closestPointToLine(location, usToFriend, foeOne.location).length() < 40) {
            return false;
        }

        if (foeTwo != null && VectorGeometry.closestPointToLine(location, usToFriend, foeTwo.location).length() < 40) {
            return false;
        }

        return true;

    }

    /**
     * Returns the closest point Frodo can move to to get open. Tries the closest 100 points to Frodo.
     * WIP
     * @return
     */
    private static VectorGeometry getClosestOpenPoint() {

        return new VectorGeometry();

    }

    /**
     * Checks whether a point is both within the pitch boundaries and if Frodo would be open if he were there.
     * @param point
     * @return
     */
    private static boolean evaluatePoint(VectorGeometry point) {

        if (!open(point)) {
            return false;
        }

        if (Math.abs(point.x) > (Constants.PITCH_WIDTH - 10) || Math.abs(point.y) > (Constants.INPUT_HEIGHT - 10)) {
            return false;
        }

        return true;
    }

}
