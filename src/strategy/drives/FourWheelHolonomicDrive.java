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
    private PIDRotationControl  pidRotation  = new PIDRotationControl(30d, 2d, 1d); //perfect
    private PIDDirectionControl pidDirection = new PIDDirectionControl(10d, 0d, 0d);

    public int MAX_ROTATION = 100; //deprecated
    public int MAX_MOTION = 0; //deprecated

    public ControlResult move(RobotPort port, DirectedPoint location, VectorGeometry force, double rotation) {
        assert(port instanceof FourWheelHolonomicRobotPort);

        VectorGeometry robotForce = new VectorGeometry();

        force.copyInto(robotForce).coordinateRotation(location.direction);

//        rotation = 0;
        robotForce = new VectorGeometry(0d, 0d);

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

    //not completed
    public void aim(RobotPort port, double rotation) {
        double front = -rotation;
        double back  = rotation;
        double left  = -rotation;
        double right = rotation;

        ((FourWheelHolonomicRobotPort) port).fourWheelHolonomicMotion(front, back, left, right);
    }

}
