package strategy.drives;

import communication.ports.robotPorts.FrodoRobotPort;
import org.junit.Test;
import strategy.drives.pid.ControlResult;
import vision.constants.Constants;
import vision.tools.DirectedPoint;
import vision.tools.VectorGeometry;

import java.util.Random;

import static org.junit.Assert.*;


public class FourWheelHolonomicDriveTest {
    private FourWheelHolonomicDrive drive = new FourWheelHolonomicDrive();
    private FrodoRobotPort port = new FrodoRobotPort();
    @Test
    public void moveDirectionTest() throws Exception {

        VectorGeometry force = new VectorGeometry(1, 0); //target is enemy goal
        ControlResult motorControl = drive.move(port, new DirectedPoint(0, 0, 0), force, 0d);
        assertTrue("front motor is not needed", motorControl.getFront() == 0d);
        assertTrue("back motor is not needed", motorControl.getBack() == 0d);
        assertTrue("left and right motors have the same values", motorControl.getLeft() == motorControl.getRight());
        assertTrue("left and right motors positive values", motorControl.getLeft() > 0d);

        force = new VectorGeometry(1, 1);
        motorControl = drive.move(port, new DirectedPoint(0, 0, 0), force, 0d);
        assertTrue("left and right motors have the same values", motorControl.getLeft() == motorControl.getRight());
        assertTrue("front and back motors have the same values", motorControl.getFront() == motorControl.getBack());
        assertTrue("left and right motors positive values", motorControl.getLeft() > 0d);
        assertTrue("front and back motors positive values", motorControl.getFront() > 0d);

        force = new VectorGeometry(0, 1);
        motorControl = drive.move(port, new DirectedPoint(0, 0, 0), force, 0d);
        assertTrue("left and right motors have the same values", motorControl.getLeft() == motorControl.getRight());
        assertTrue("front and back motors have the same values", motorControl.getFront() == motorControl.getBack());
        assertTrue("left and right motors positive values", motorControl.getLeft() > 0d);
        assertTrue("front and back motors positive values", motorControl.getFront() > 0d);
    }
}