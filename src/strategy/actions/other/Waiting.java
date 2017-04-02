package strategy.actions.other;

import communication.ports.robotPorts.FrodoRobotPort;
import strategy.actions.ActionException;
import strategy.actions.ActionBase;
import strategy.controllers.essentials.MotionController;
import strategy.robots.RobotBase;
import strategy.Strategy;

import javax.swing.*;

/**
 * Created by Simon Rovder
 */
public class Waiting extends ActionBase {
    public Waiting(RobotBase robot) {
        super(robot);
        this.rawDescription = " Waiting.";
    }

    @Override
    public void enterState(int newState) {
        if(newState == 0){
            this.robot.MOTION_CONTROLLER.clearObstacles();
            if (this.robot.MOTION_CONTROLLER.getMode() != MotionController.MotionMode.OFF) {
                this.robot.port.stop();
            }
            this.robot.MOTION_CONTROLLER.setDestination(null);
            this.robot.MOTION_CONTROLLER.setMode(MotionController.MotionMode.OFF);
            ((FrodoRobotPort)(this.robot.port)).stopKick();
        }
        this.state = newState;
    }

    @Override
    public void tok() throws ActionException {
        enterState(0);
        throw new ActionException(true, false);
    }
}
