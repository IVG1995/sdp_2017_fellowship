package strategy.robots;

import communication.ports.robotPorts.FrodoRobotPort;
import strategy.controllers.fred.GrabberController;
import strategy.controllers.fred.PropellerController;
import strategy.controllers.fred.KickerController;
import strategy.drives.DriveInterface;
import strategy.drives.FourWheelHolonomicDrive;
import communication.ports.robotPorts.FredRobotPort;
import vision.RobotType;

/**
 * O
 */
public class Frodo extends RobotBase {

    public final KickerController KICKER_CONTROLLER = new KickerController(this);
    public final GrabberController GRABBER_CONTROLLER = new GrabberController(this);

    public Frodo(RobotType robotType){
        super(robotType, new FrodoRobotPort(), new FourWheelHolonomicDrive());
        this.controllers.add(this.KICKER_CONTROLLER);
        this.controllers.add(this.GRABBER_CONTROLLER);
    }


    @Override
    public void performManual() {

    }
}
