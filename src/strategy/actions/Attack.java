package strategy.actions;

import strategy.Strategy;
import strategy.Status;
import strategy.actions.other.Goto;
import strategy.actions.other.HoldPosition;
import strategy.actions.other.Stop;
import strategy.points.DynamicPoint;
import strategy.points.basicPoints.BallPoint;
import strategy.points.basicPoints.MidFoePoint;
import strategy.robots.RobotBase;

/**
 * Created by Simon Rovder
 */
public class Attack extends ManualActionBase<Status.BallState> {
    public Attack(RobotBase robot, DynamicPoint point) {
        super(robot, point);
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
                    this.enterBehaviourAction(new HoldPosition(this.robot, new MidFoePoint()));
                    break;
                case FREE:
                    this.enterBehaviourAction(new Goto(this.robot, new BallPoint()));
                    break;
                case ME:
//                    if(Fred.FRED.hasBall()){
                    this.enterBehaviourAction(new Goto(this.robot, new BallPoint()));
                    break;
                case FRIEND:
                    this.enterBehaviourAction(new Stop(null));
                    this.delay(1500);
                    break;
                case LOST:
                    this.enterBehaviourAction(new Stop(null));
                    this.delay(1500);
                    break;
            }
        }
    }
}
