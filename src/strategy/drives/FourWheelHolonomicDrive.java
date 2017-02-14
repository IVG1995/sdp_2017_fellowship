package strategy.drives;

import communication.ports.interfaces.FourWheelHolonomicRobotPort;
import communication.ports.interfaces.RobotPort;
import vision.tools.DirectedPoint;
import vision.tools.VectorGeometry;

/**
 * Created by Simon Rovder
 */

public class FourWheelHolonomicDrive implements DriveInterface {

    public int MAX_ROTATION = 30;
    public int MAX_MOTION = 200;

    public void move(RobotPort port, DirectedPoint location, VectorGeometry force, double rotation, double factor) {
        assert (port instanceof FourWheelHolonomicRobotPort);

        VectorGeometry dir = new VectorGeometry();
        // "force" is a vector containing the location of where the robot needs to travel to (not face toward). This vector
        // is in an absolute coordinate system, with the x and y axes aligning with the pitch walls.
        // This method below takes "force" and converts it to a new coordinate system where the robot's current direction
        // aligns with the x-axis.
        force.copyInto(dir).coordinateRotation(force.angle() - location.direction);
        factor = Math.min(1, factor);

        // Used for normalization.
        double lim = this.MAX_MOTION - Math.abs(rotation * this.MAX_ROTATION * factor);

        /** When the robot is facing RIGHT:
         A positive value for:
         -front: causes the front wheel to spin DOWN
         -back: causes the back wheel to spin DOWN
         -left: causes the left wheel to spin RIGHT
         -right: causes the right wheel to spin RIGHT
         **/
        double front = -dir.y;
        double back = dir.y;
        double left = -dir.x;
        double right = dir.x;


        // Used to normalize max wheel power to this.MAX_MOTION (=200).
        double normalizer = Math.max(Math.max(Math.abs(left), Math.abs(right)), Math.max(Math.abs(front), Math.abs(back)));
        // "rotation" is a signed angle measured in radians.
        System.out.println("rotation: " + rotation);

        normalizer = (lim / normalizer) * factor;


        front = front * normalizer - rotation * this.MAX_ROTATION;
        back = back * normalizer + rotation * this.MAX_ROTATION;
        left = left * normalizer - rotation * this.MAX_ROTATION;
        right = right * normalizer + rotation * this.MAX_ROTATION;

        System.out.println("front: " + front);
        System.out.println("back: " + back);
        System.out.println("left: " + left);
        System.out.println("right: " + right);


        ((FourWheelHolonomicRobotPort) port).fourWheelHolonomicMotion(front, back, left, right);

    }

    public void aim(RobotPort port, double rotation) {
        double front = rotation  * this.MAX_ROTATION;
        double left  = rotation  * this.MAX_ROTATION;
        double back  = -rotation * this.MAX_ROTATION;
        double right = -rotation * this.MAX_ROTATION;

        ((FourWheelHolonomicRobotPort) port).fourWheelHolonomicMotion(front, back, left, right);
    }
}
