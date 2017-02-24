package strategy.drives;

import communication.ports.interfaces.FourWheelHolonomicRobotPort;
import communication.ports.interfaces.RobotPort;
import vision.tools.DirectedPoint;
import vision.tools.VectorGeometry;

/**
 * Created by Simon Rovder
 */

public class FourWheelHolonomicDrive implements DriveInterface {
    private final int FORCE_WEIGHT    = 120;
    private final int ROTATION_WEIGHT = 120;
    private final double RECTANGULAR_DRIVE_FACTOR = 0.8;

    public int MAX_ROTATION = 0; //deprecated
    public int MAX_MOTION = 0; //deprecated

    public void move(RobotPort port, DirectedPoint location, VectorGeometry force, double rotation, double factor) {
        assert(port instanceof FourWheelHolonomicRobotPort);

        VectorGeometry dir = new VectorGeometry();

        force.copyInto(dir).coordinateRotation(location.direction).normaliseToLength(1);

        rotation /= Math.PI;

        double front = dir.y * FORCE_WEIGHT - rotation * ROTATION_WEIGHT * RECTANGULAR_DRIVE_FACTOR;
        double left  = dir.x * FORCE_WEIGHT - rotation * ROTATION_WEIGHT;
        double back  = dir.y * FORCE_WEIGHT + rotation * ROTATION_WEIGHT * RECTANGULAR_DRIVE_FACTOR;
        double right = dir.x * FORCE_WEIGHT + rotation * ROTATION_WEIGHT;

        ((FourWheelHolonomicRobotPort) port).fourWheelHolonomicMotion(front, back, left, right);

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
