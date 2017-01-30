package strategy.actions;

import strategy.robots.RobotBase;

/**
 * Created by cole on 1/30/17.
 * A simple main action (alternative to Behave.java) built specifically for 1v1 friendlies.
 *
 */

enum Behaviour_OneV {
    DEFEND, CHASE_BALL, SHOOT
}

public class Behave_OneV extends StatefulActionBase<Behaviour_OneV> {


    public Behave_OneV(RobotBase robot) {
        super(robot, null);
    }

    @Override
    public void enterState(int newState)
    {
        this.state = newState;
    }

    @Override
    public Behaviour_OneV getState() {
        



        // TODO
        return null;
    }

    @Override
    public void tok() {

    }
}
