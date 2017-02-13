package strategy.actions.other;

import strategy.actions.ActionException;
import strategy.actions.ActionBase;
import strategy.points.DynamicPoint;
import strategy.robots.RobotBase;
/**
 * Created by Simon Rovder
 */
public class HoldPosition extends ActionBase {
    public HoldPosition(RobotBase robot, DynamicPoint point) {
        super(robot, point);
        this.rawDescription = " Hold Position";
    }

    // There's no need to use states with this action as it only makes the robot stay at a point.
    @Override
    public void enterState(int newState) {

    }


    // HoldPosition doesn't need to throw an ActionException because it doesn't naturally terminate. Its parent action
    // must be a stateful action so that it can be terminated manually by the parent.
    @Override
    public void tok() throws ActionException {
        this.robot.MOTION_CONTROLLER.setDestination(this.point);
        // The tolerance is how close the robot must be to its destination point before it considers itself to be there.
        // If you set tolerance = 0, it will go exactly to that point. If you set it 50, it will move until it
        // is 50 cm (I believe its cm) before stopping. If you set the tolerance equal to -1, it will hold that position
        // until interrupted.
        this.robot.MOTION_CONTROLLER.setTolerance(-1);
    }
}
