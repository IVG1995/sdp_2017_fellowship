package strategy.controllers.fred;

import communication.ports.interfaces.GrabberEquippedRobotPort;
import communication.ports.robotPorts.FrodoRobotPort;
import strategy.Strategy;
import strategy.controllers.ControllerBase;
import strategy.robots.RobotBase;
import vision.Robot;

/**
 * Contains the logic that tells the robot whether it should use its grabber.
 * Depends on us having a way to determine whether we have the ball.
 * Knowing whether to have the ball is TO BE IMPLEMENTED.
 */

public class GrabberController extends ControllerBase {


    private boolean wantToGrab    = false;
    private boolean wantToRelease = false;

    public GrabberController(RobotBase robot) {
        super(robot);
    }

    public void wantToGrab() {
        this.wantToGrab = true;
        this.wantToRelease = false;
    }

    public void wantToRelease() {
        this.wantToGrab = false;
        this.wantToRelease = true;
    }

    public void perform() {
        assert (this.robot.port instanceof GrabberEquippedRobotPort);
        FrodoRobotPort frodoRobotPort = (FrodoRobotPort)this.robot.port;

        if (wantToGrab) {
            frodoRobotPort.halt();
            frodoRobotPort.grab();
            wantToGrab = false;
        }
        if (wantToRelease) {
            frodoRobotPort.halt();
            frodoRobotPort.release();
            wantToRelease = false;
        }
    }
}
