package communication.ports.robotPorts;

import communication.ports.interfaces.FourWheelHolonomicRobotPort;
import communication.ports.interfaces.GrabberEquippedRobotPort;
import communication.ports.interfaces.KickerEquippedRobotPort;
import communication.ports.interfaces.RobotPort;

/**
 * Created by Simon Rovder
 */
public class FrodoRobotPort extends RobotPort implements GrabberEquippedRobotPort, FourWheelHolonomicRobotPort, KickerEquippedRobotPort {

    public FrodoRobotPort(){
        super("pang");
    }

    @Override
    public void fourWheelHolonomicMotion(double front, double back, double left, double right) {
        this.sdpPort.commandSender("r", (int) front, (int) back, (int) left, (int) right);
    }

    @Override
    public void grab(){
        this.sdpPort.commandSender("grab");
        this.sdpPort.commandSender("grab");
        this.sdpPort.commandSender("grab");
        this.sdpPort.commandSender("grab");
    }

    @Override
    public void release(){
        this.sdpPort.commandSender("ungrab");
        this.sdpPort.commandSender("ungrab");
        this.sdpPort.commandSender("ungrab");
        this.sdpPort.commandSender("ungrab");
    }

    @Override
    public void kick(){
        this.sdpPort.commandSender("kick");
        this.sdpPort.commandSender("kick");
        this.sdpPort.commandSender("kick");
        this.sdpPort.commandSender("kick");
    }




}
