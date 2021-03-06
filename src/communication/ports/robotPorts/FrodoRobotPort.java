package communication.ports.robotPorts;

import communication.PortListener;
import communication.ports.interfaces.FourWheelHolonomicRobotPort;
import communication.ports.interfaces.GrabberEquippedRobotPort;
import communication.ports.interfaces.KickerEquippedRobotPort;
import communication.ports.interfaces.RobotPort;

/**
 * Created by Simon Rovder
 */
public class FrodoRobotPort extends RobotPort implements GrabberEquippedRobotPort, FourWheelHolonomicRobotPort, KickerEquippedRobotPort, PortListener{

    private boolean isKicking = false;
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
    }

    @Override
    public void startKick(){
        if(!isKicking) {
            this.sdpPort.commandSender("start_kicking");
            this.sdpPort.commandSender("start_kicking");
            this.sdpPort.commandSender("start_kicking");
            this.sdpPort.commandSender("start_kicking");
            this.isKicking = true;
        }
    }

    @Override
    public void stopKick(){
        if(isKicking) {
            this.sdpPort.commandSender("stop_kicking");
            this.sdpPort.commandSender("stop_kicking");
            this.sdpPort.commandSender("stop_kicking");
            this.sdpPort.commandSender("stop_kicking");
            this.isKicking = false;
        }
    }

    @Override
    public boolean isKicking(){
        return this.isKicking;
    }

    @Override
    public void receivedStringHandler(String string) {

    }




}
