package strategy.actions.other;

import strategy.actions.ActionException;
import strategy.actions.StatefulActionBase;
import strategy.points.DynamicPoint;
import strategy.robots.RobotBase;
import vision.RobotAlias;

/**
 * Created by s1351669 on 14/01/17.
 */
public class CleverDefend extends StatefulActionBase<RobotAlias> {

    public CleverDefend(RobotBase robot, DynamicPoint point) {
        super(robot, point);
    }

    @Override
    protected RobotAlias getState() {
        return null;
    }

    @Override
    public void enterState(int newState) {

    }

    @Override
    public void tok() throws ActionException {

    }
}
