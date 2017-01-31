package strategy.drives;

import communication.ports.interfaces.FourWheelHolonomicRobotPort;
import communication.ports.interfaces.RobotPort;
import vision.tools.DirectedPoint;
import vision.tools.VectorGeometry;

/**
 * Created by Simon Rovder
 */

public class FourWheelHolonomicDrive implements DriveInterface{

    public int MAX_ROTATION = 30;
    public int MAX_MOTION = 200;

    public void move(RobotPort port, DirectedPoint location, VectorGeometry force, double rotation, double factor){
        assert(port instanceof FourWheelHolonomicRobotPort);

        VectorGeometry dir = new VectorGeometry();
        force.copyInto(dir).coordinateRotation(force.angle() - location.direction);
        factor = Math.min(1, factor);

        double lim = this.MAX_MOTION - Math.abs(rotation * this.MAX_ROTATION * factor);
                    // 200 - 90
        System.out.println("location: " + location.toString());
        System.out.println("force: " + force.toString());
        System.out.println("force.angle: " + force.angle());
        System.out.println("dir: " + dir.toString());

        double front = -dir.y;
        double left =  dir.x;
        double back =  -dir.y;
        double right = dir.x;
        double normalizer = Math.max(Math.max(Math.abs(left), Math.abs(right)), Math.max(Math.abs(front), Math.abs(back)));

        double normalizer2 = (lim/normalizer)*factor;
        front = front*normalizer2 + rotation * this.MAX_ROTATION;
        back  = back*normalizer2 + rotation * this.MAX_ROTATION;
        left  = left*normalizer2 + rotation * this.MAX_ROTATION;
        right = right*normalizer2 + rotation * this.MAX_ROTATION;
        System.out.println("front: " + front);
        System.out.println("back: " + back);
        System.out.println("left: " + left);
        System.out.println("right: " + right);
        System.out.println("=======================");

        ((FourWheelHolonomicRobotPort) port).fourWheelHolonomicMotion(front, back, left, right);

    }
}
