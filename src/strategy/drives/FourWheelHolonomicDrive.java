package strategy.drives;

import communication.ports.interfaces.FourWheelHolonomicRobotPort;
import communication.ports.interfaces.RobotPort;
import vision.tools.DirectedPoint;
import vision.tools.VectorGeometry;

/**
 * Created by Simon Rovder
 */

public class FourWheelHolonomicDrive implements DriveInterface {
    private final double ROTATION_WEIGHT = 20d;
    private final double MIN_ROTATION = 10d;
    private final double FORCE_WEIGHT    = 70d;
    private final double RECTANGULAR_DRIVE_FACTOR = 0.8;

    public int MAX_ROTATION = 100; //deprecated
    public int MAX_MOTION = 0; //deprecated

    public void move(RobotPort port, DirectedPoint location, VectorGeometry force, double rotation, double factor) {
        assert(port instanceof FourWheelHolonomicRobotPort);

        VectorGeometry dir = new VectorGeometry();

        force.copyInto(dir).coordinateRotation(location.direction).reduceLinearlyTo(1);

        double front = dir.y * FORCE_WEIGHT + rotationHeuristic(rotation) * RECTANGULAR_DRIVE_FACTOR;
        double left  = dir.x * FORCE_WEIGHT - rotationHeuristic(rotation);
        double back  = dir.y * FORCE_WEIGHT - rotationHeuristic(rotation) * RECTANGULAR_DRIVE_FACTOR;
        double right = dir.x * FORCE_WEIGHT + rotationHeuristic(rotation);

        ((FourWheelHolonomicRobotPort) port).fourWheelHolonomicMotion(front, back, left, right);

    }

    private double rotationHeuristic(double rotation){
        if (Math.abs(rotation) < Math.PI / 9) return 0d;
        if (rotation > 0) {
            return rotation * ROTATION_WEIGHT + MIN_ROTATION;
        } else {
            return rotation * ROTATION_WEIGHT - MIN_ROTATION;

        }
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
