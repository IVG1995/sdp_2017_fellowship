package strategy.actions;

import strategy.Strategy;
import strategy.Status;
import strategy.actions.other.DefendGoal;
import strategy.actions.other.Goto;
import strategy.points.DynamicPoint;
import strategy.points.basicPoints.BallPoint;
import strategy.robots.RobotBase;

/**
 * Created by Simon Rovder
 */
public class Defend extends StatefulActionBase<Status.BallState> {
    public Defend(RobotBase robot, DynamicPoint point) {
        super(robot, point);
        this.addEquivalence(Status.BallState.THEM, Status.BallState.FREE);
        this.addEquivalence(Status.BallState.THEM, Status.BallState.FRIEND);
        this.addEquivalence(Status.BallState.THEM, Status.BallState.LOST);
    }

    @Override
    protected Status.BallState getState() {
        return Strategy.status.ballState;
    }

    @Override
    public void enterState(int newState) {
        this.state = newState;
    }

    @Override
    public void tok() throws ActionException {
        switch(this.nextState){
            case THEM:
                this.enterAction(new DefendGoal(null), 0, 0);
                break;
            case FREE:
                this.enterAction(new DefendGoal(null), 0, 0);
                break;
            case ME:
                this.enterAction(new Goto(this.robot, new BallPoint()), 0, 0);
                break;
            case FRIEND:
                this.enterAction(new DefendGoal(null), 0, 0);
                break;
            case LOST:
                this.enterAction(new DefendGoal(null), 0, 0);
                break;
        }
    }
}
