package strategy.controllers.fred;

import communication.ports.robotPorts.FrodoRobotPort;
import strategy.controllers.ControllerBase;
import strategy.robots.RobotBase;

/**
 * Contains the logic behind when to kick.
 */
public class KickerController extends ControllerBase {
    private static boolean wantToKick = false;

    public KickerController(RobotBase robot) {
        super(robot);
    }

    public void setWantToKick() {
        wantToKick = true;
    }

    public void perform() {
        FrodoRobotPort frodoRobotPort = ((FrodoRobotPort)this.robot.port);
        if (wantToKick) {
            frodoRobotPort.kick();
            wantToKick = false;
        }
    }
}
