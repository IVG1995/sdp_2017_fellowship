package communication.ports.interfaces;

import communication.SDPPort;
import communication.PortListener;

/**
 * Created by Simon Rovder
 *
 * To tell the system how to reach your robot and what commands it responds to, extend this class  and add your
 * robot's commands. If you wish to be able to use special controllers, you can add interfaces for those too
 * (example is the FourWheelHolonomicPort, which supports sending commands to four wheels)
 */
public class RobotPort implements PortListener {

    public final SDPPort sdpPort = new SDPPort();
    private boolean isStopped;
    private boolean isHalted;

    public RobotPort(final String expectedPingResponse){
        (new Thread() {
            public void run() {
                sdpPort.connect(null, expectedPingResponse);
            }
        }).start();
        sdpPort.addCommunicationListener(this);
        this.isStopped = false;
        this.isHalted = false;
    }

    @Override
    public void receivedStringHandler(String string) {

    }

    public void ping() {
        sdpPort.commandSender("ping");
    }

    public void stop() {
        if (!isStopped) {
            sdpPort.commandSender("f");
            this.isStopped = true;
        }
    }

    public void halt() {
        if (!isHalted) {
            sdpPort.commandSender("h");
            this.isHalted = true;
        }
    }
}
