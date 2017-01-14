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
public class Defend extends ManualActionBase<Status.BallState> {
    public Defend(RobotBase robot, DynamicPoint point) {
        super(robot, point);
        this.addEquivalence(Status.BallState.THEM, Status.BallState.FREE);
        this.addEquivalence(Status.BallState.THEM, Status.BallState.FRIEND);
        this.addEquivalence(Status.BallState.THEM, Status.BallState.LOST);
    }

    @Override
    public void enterState(int newState) {
        this.state = newState;
    }

    @Override
    public void tok() throws ActionException {
        if(!this.behaviourTik()){
            switch(Strategy.status.ballState){
                case THEM:
                    this.enterBehaviourAction(new DefendGoal(null));
                    break;
                case FREE:
                    this.enterBehaviourAction(new DefendGoal(null));
                    break;
                case ME:
                    this.enterBehaviourAction(new Goto(this.robot, new BallPoint()));
                    break;
                case FRIEND:
                    this.enterBehaviourAction(new DefendGoal(null));
                    break;
                case LOST:
                    this.enterBehaviourAction(new DefendGoal(null));
                    break;
            }
        }
    }
}
