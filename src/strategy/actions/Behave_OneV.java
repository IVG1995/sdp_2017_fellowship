package strategy.actions;

import strategy.robots.RobotBase;
import strategy.Strategy;
import vision.Ball;

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
        if (newState == 0) {
            this.robot.setControllersActive(true);
        }
        this.state = newState;
    }

    @Override
    public Behaviour_OneV getState() {
        Ball ball = Strategy.world.getBall();
        // If ball can not be found, go to defense mode
        if (ball == null) {
            return Behaviour_OneV.DEFEND;
        }



        // TODO
        return null;
    }

    @Override
    public void tok() {

    }
}
