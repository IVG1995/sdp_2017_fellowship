package strategy.drives;

import communication.ports.interfaces.FourWheelHolonomicRobotPort;
import communication.ports.interfaces.RobotPort;
import strategy.drives.pid.ControlResult;
import strategy.drives.pid.PIDDirectionControl;
import strategy.drives.pid.PIDRotationControl;
import strategy.drives.pid.error.DirectionControlError;
import strategy.drives.pid.error.RotationControlError;
import vision.tools.DirectedPoint;
import vision.tools.VectorGeometry;

/**`
 * Created by Simon Rovder
 */

public class FourWheelHolonomicDrive implements DriveInterface {
    private static PIDRotationControl  pidRotation  = new PIDRotationControl(10d, 2d, 1d);
    private static PIDDirectionControl pidDirection = new PIDDirectionControl(10d, 0d, 0d);
    private static PIDRotationControl  pidAim       = new PIDRotationControl(30d, 2d, 150d);
    public ControlResult move(RobotPort port, DirectedPoint location, VectorGeometry force, double rotation) {
        assert(port instanceof FourWheelHolonomicRobotPort);

        VectorGeometry robotForce = new VectorGeometry();

        force.copyInto(robotForce).coordinateRotation(location.direction);

        ControlResult rotationControl;
        ControlResult directionControl = pidDirection.getActuatorInput(new DirectionControlError(robotForce));
        if (Math.abs(rotation * 180d / Math.PI) < 5d) {
            rotationControl = new ControlResult();
            pidRotation.getHistory().setAccumulated(new RotationControlError());
        } else {
            rotationControl  = pidRotation.getActuatorInput(new RotationControlError(rotation));
        }
        ((FourWheelHolonomicRobotPort) port).fourWheelHolonomicMotion(
                directionControl.getFront() + rotationControl.getFront(),
                directionControl.getBack()  + rotationControl.getBack(),
                directionControl.getLeft()  + rotationControl.getLeft(),
                directionControl.getRight() + rotationControl.getRight());

        return directionControl;
    }

    public void aim(RobotPort port, double rotation) {

        System.out.println(pidAim.toString());
        System.out.println("rotation = " + rotation);

        ControlResult rotationControl;
        /*if (Math.abs(rotation * 180d / Math.PI) < 5d) {
            rotationControl = new ControlResult();
            pidAim.getHistory().setAccumulated(new RotationControlError());
        } else {*/
            rotationControl  = pidAim.getActuatorInput(new RotationControlError(rotation));
//        }

//        ((FourWheelHolonomicRobotPort) port).fourWheelHolonomicMotion(
//                rotationControl.getFront(),
//                rotationControl.getBack(),
//                rotationControl.getLeft(),
//                rotationControl.getRight());
        if (rotation > 0) {
            ((FourWheelHolonomicRobotPort) port).fourWheelHolonomicMotion(24, -24, -24, 24);
        } else {
            ((FourWheelHolonomicRobotPort) port).fourWheelHolonomicMotion(-24, 24, 24, -24);
        }
    }

    public PIDRotationControl getPidRotation() {
        return pidRotation;
    }

    public PIDDirectionControl getPidDirection() {
        return pidDirection;
    }

    public PIDRotationControl getPidAim() {
        return pidAim;
    }

    public void setPidRotation(PIDRotationControl pidRotation) {
        FourWheelHolonomicDrive.pidRotation = pidRotation;
    }

    public void setPidDirection(PIDDirectionControl pidDirection) {
        FourWheelHolonomicDrive.pidDirection = pidDirection;
    }

    public void setPidAim(PIDRotationControl pidAim) {
        FourWheelHolonomicDrive.pidAim = pidAim;
    }

    public void resetHistory() {
        pidRotation.getHistory().setAccumulated(new RotationControlError());
        pidAim.getHistory().setAccumulated(new RotationControlError());
        pidDirection.getHistory().setAccumulated(new DirectionControlError());
    }
}
