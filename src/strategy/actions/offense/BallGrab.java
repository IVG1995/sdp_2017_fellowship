package strategy.actions.offense;

import strategy.actions.ActionException;
import strategy.actions.ActionBase;
import strategy.points.basicPoints.BallPoint;
import strategy.points.basicPoints.EnemyGoal;
import strategy.robots.Fred;
import strategy.points.basicPoints.GrabbablePoint;
import strategy.Strategy;
import strategy.robots.Frodo;
import strategy.robots.RobotBase;
import vision.RobotType;
import vision.tools.VectorGeometry;

/**
 * Action that grabs the ball.
 * Assumes that ball is close enough.
 * Does not check if the ball is possessed by another robot.
 */
public class BallGrab extends ActionBase {
    private final int BALL_RELEASED = 0;
    private final int BALL_GRABBED  = 1;

    public BallGrab(RobotBase robot) {
        super(robot);
        this.rawDescription = "BallGrab";
    }
    @Override
    public void enterState(int newState) {
        if (newState == BALL_RELEASED) {
            ((Frodo)this.robot).GRABBER_CONTROLLER.wantToGrab();
        }
        this.state = newState;
    }

    @Override
    public void tok() throws ActionException {
        // grab the ball
        this.enterState(BALL_RELEASED);
        //todo determine if grab is success or failure (with sensor).
        throw new ActionException(true, true);
    }
}
