package strategy.actions.defence;

import strategy.Strategy;
import strategy.actions.ActionBase;
import strategy.robots.RobotBase;
import vision.Ball;
import vision.Robot;
import vision.RobotType;
import vision.tools.VectorGeometry;

/**
 * Has Frodo clear the ball without use of a grabber.
 */
public class ClearNoGrab extends ActionBase {

    public ClearNoGrab(RobotBase robot) {
        super(robot);
    }

    @Override
    public void enterState(int newState) {

    }

    @Override
    public void tok() {
        Ball ball = Strategy.world.getBall();
        Robot us = Strategy.world.getRobot(RobotType.FRIEND_2);
        Robot foeOne = Strategy.world.getRobot(RobotType.FOE_1);
        Robot foeTwo = Strategy.world.getRobot(RobotType.FOE_2);

        if (ball == null || us == null) {
            return;
        }


    }
}
