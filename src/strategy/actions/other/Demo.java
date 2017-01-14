package strategy.actions.other;

import strategy.actions.ActionException;
import strategy.actions.AutomaticActionBase;
import strategy.robots.RobotBase;

/**
 * Created by Simon Rovder
 */
public class Demo extends AutomaticActionBase {
    public Demo(RobotBase robot) {
        super(robot);
        this.rawDescription = " Demo Action";
    }

    @Override
    public void enterState(int newState) {

    }

    @Override
    public void tok() throws ActionException {

    }
}
