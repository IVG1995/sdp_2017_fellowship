package strategy.actions.offense;

import strategy.actions.ActionBase;
import strategy.actions.other.Goto;
import strategy.points.basicPoints.BallPoint;
import strategy.robots.RobotBase;

public class GetBall extends ActionBase {

    private final int GO_TO_BALL = 0;
    private final int ROTATE = 1;
    private final int GRAB = 2;

    public GetBall(RobotBase robot) {
        super(robot);
    }

    @Override
    public void enterState(int newState) {
        switch (newState) {
            case GO_TO_BALL:
                this.enterAction(new Goto(this.robot, new BallPoint()), 1, 0);
                break;
            case ROTATE:

                break;
            case GRAB:

                break;


        }
    }

    @Override
    public void tok() {

    }
}
