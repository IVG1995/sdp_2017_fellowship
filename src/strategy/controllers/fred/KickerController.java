package strategy.controllers.fred;

import communication.ports.robotPorts.FrodoRobotPort;
import strategy.controllers.ControllerBase;
import strategy.robots.RobotBase;

/**
 * Contains the logic behind when to kick.
 */
public class KickerController extends ControllerBase {
    private boolean wantToKick = false;

    public KickerController(RobotBase robot) {
        super(robot);
    }

    public void setWantToKick(boolean wantToKick) {
        this.wantToKick = wantToKick;
    }

    public void perform() {
        FrodoRobotPort frodoRobotPort = ((FrodoRobotPort)this.robot.port);
        if (wantToKick) frodoRobotPort.kick();
    }
}
