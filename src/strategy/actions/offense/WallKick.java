package strategy.actions.offense;

import strategy.actions.ActionBase;
import strategy.points.basicPoints.BallPoint;
import strategy.robots.Frodo;
import strategy.robots.RobotBase;


/**
 * Kicks the ball forward when the ball is close to a wall (like ShuntKick's old functionality).
 */
public class WallKick extends ActionBase {

    private final int KICK = 0;

    public WallKick(RobotBase robot) {
        super(robot, new BallPoint());
    }

    @Override
    public void enterState(int newState) {
        this.robot.MOTION_CONTROLLER.setDestination(this.point);
        this.robot.MOTION_CONTROLLER.setHeading(this.point);
        this.robot.MOTION_CONTROLLER.setTolerance(-1);
        ((Frodo)this.robot).KICKER_CONTROLLER.setWantToKick(true);

    }

    @Override
    public void tok() {
        this.enterState(this.KICK);
    }

}
