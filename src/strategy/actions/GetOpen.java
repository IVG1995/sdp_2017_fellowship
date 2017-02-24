package strategy.actions;

import strategy.Strategy;
import strategy.points.basicPoints.RobotPoint;
import strategy.robots.RobotBase;
import vision.Robot;
import vision.RobotType;
import vision.tools.VectorGeometry;

/**
 * When our ally has the ball, get to a position where we can be passed to.
 */
public class GetOpen extends ActionBase {

    private final int GET_OPEN = 0;
    private final int STAY = 1;

    private VectorGeometry usToFriend;
    private Robot us;
    private Robot friend;

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
        this.us = Strategy.world.getRobot(RobotType.FRIEND_2);
        this.friend = Strategy.world.getRobot(RobotType.FRIEND_1);

        if (this.us == null || this.friend == null) {
            return;
        }

        this.usToFriend = VectorGeometry.fromTo(this.us.location, this.friend.location);
        if (open()) {
            this.enterState(STAY);
        } else {
            this.enterState(GET_OPEN);
        }

    }

    /**
     * Returns true if Frodo is open for a pass, false otherwise.
     * @return
     */
    public boolean open() {

        Robot foeOne = Strategy.world.getRobot(RobotType.FOE_1);
        Robot foeTwo = Strategy.world.getRobot(RobotType.FOE_2);

        if (foeOne != null && VectorGeometry.closestPointToLine(us.location, usToFriend, foeOne.location).length() < 40) {
            return false;
        }

        if (foeTwo != null && VectorGeometry.closestPointToLine(us.location, usToFriend, foeTwo.location).length() < 40) {
            return false;
        }

        return true;

    }

}
