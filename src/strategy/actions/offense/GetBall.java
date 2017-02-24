package strategy.actions.offense;

import strategy.Strategy;
import strategy.actions.ActionBase;
import strategy.actions.ActionException;
import strategy.actions.other.Goto;
import strategy.points.basicPoints.BallPoint;
import strategy.points.basicPoints.GrabbablePoint;
import strategy.robots.RobotBase;
import vision.Ball;
import vision.Robot;
import vision.RobotType;
import vision.tools.VectorGeometry;

import javax.swing.*;

public class GetBall extends ActionBase {

    private final int GO_TO_BALL = 0;
    private final int ROTATE = 1;
    private final int GRAB = 2;
    private final int SUCCESS = 3;

    private final int GRABBING_DISTANCE = 7;
    private final int CLOSE_ENOUGH_TO_GRAB = 3;

    public GetBall(RobotBase robot) {
        super(robot);
    }

    @Override
    public void enterState(int newState) {
        switch (newState) {
            case GO_TO_BALL:
                this.enterAction(new Goto(this.robot, new BallPoint()), ROTATE, GO_TO_BALL);
                break;
            case ROTATE:

                break;
            case GRAB:
                this.enterAction(new BallGrab(this.robot), SUCCESS, GRAB);
                break;

        }
    }

    @Override
    public void tok() throws ActionException {
        if (this.state == SUCCESS) throw new ActionException(true, true);

        Ball ball = Strategy.world.getBall();
        Robot us = Strategy.world.getRobot(RobotType.FRIEND_2);
        if (ball == null || us == null) return;

        GrabbablePoint gp = new GrabbablePoint(RobotType.FRIEND_2);
        gp.recalculate();

        if (VectorGeometry.distance(ball.location, us.location) > GRABBING_DISTANCE) {
            this.enterState(GO_TO_BALL);
        } else if (VectorGeometry.distance(ball.location, new VectorGeometry(gp.getX(), gp.getY())) <= CLOSE_ENOUGH_TO_GRAB) {
            this.enterState(ROTATE);
        } else {
            this.enterState(GRAB);
        }



    }
}
