package strategy.actions.defence;

import strategy.Strategy;
import strategy.actions.ActionBase;
import strategy.actions.other.HoldPosition;
import strategy.points.basicPoints.AnnoyBallHolderPoint;
import strategy.points.basicPoints.BallPoint;
import strategy.points.basicPoints.MidFoePoint;
import strategy.robots.RobotBase;
import vision.Ball;


public class Annoy extends ActionBase {

    private final int ANNOY = 0;
    // When ball is null or no one is holding the ball.
    // This action should NOT be called in this situation.
    private final int WAIT = 0;

    public Annoy(RobotBase robot) {
        super(robot);
        this.point = new BallPoint();
    }

    @Override
    public void enterState(int newState) {
        if (newState == ANNOY) {
            this.robot.MOTION_CONTROLLER.setHeading(this.point);
            this.enterAction(new HoldPosition(this.robot, new AnnoyBallHolderPoint()), 0, 0);

        } else {
            System.out.println("Action Annoy should NOT have been started in this situation.");
            System.out.println("Ball and probableBallHolder are both null.");
        }

        this.state = newState;
    }

    @Override
    public void tok() {
        if (Strategy.world.getBall() == null && Strategy.world.getProbableBallHolder() == null) this.enterState(WAIT);

        this.point.recalculate();
        this.enterState(ANNOY);
    }
}
