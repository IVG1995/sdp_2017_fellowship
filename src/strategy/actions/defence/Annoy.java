package strategy.actions.defence;

import strategy.actions.ActionBase;
import strategy.actions.other.HoldPosition;
import strategy.points.basicPoints.BallPoint;
import strategy.points.basicPoints.MidFoePoint;
import strategy.robots.RobotBase;


public class Annoy extends ActionBase {



    public Annoy(RobotBase robot) {
        super(robot);
        this.point = new BallPoint();
    }

    @Override
    public void enterState(int newState) {
        this.state = newState;
    }

    @Override
    public void tok() {
        this.point.recalculate();
        this.robot.MOTION_CONTROLLER.setHeading(this.point);
        this.enterAction(new HoldPosition(this.robot, new MidFoePoint()), 0, 0);
    }
}
