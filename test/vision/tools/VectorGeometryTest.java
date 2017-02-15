package vision.tools;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for mathematical methods using VectorGeometry
 */
public class VectorGeometryTest {
    private final double EPSILON = 10E-6;
    @Test
    public void testCoordinateRotation() throws Exception {
        // positive rotations
        VectorGeometry vTest = new VectorGeometry(0, 1);
        VectorGeometry vExpected = new VectorGeometry(1, 0);
        vTest.coordinateRotation(Math.PI / 2);
        assertTrue("VectorGeometry(0, 1) is equal to VectorGeometry(1, 0) after 90 degree rotation", epsilonEquality(vExpected, vTest));
        vTest = new VectorGeometry(0, 1);
        vExpected = new VectorGeometry(0, -1);
        vTest.coordinateRotation(Math.PI);
        assertTrue("VectorGeometry(0, 1) is equal to VectorGeometry(0, -1) after 180 degree rotation", epsilonEquality(vExpected, vTest));
        vTest = new VectorGeometry(0, 1);
        vExpected = new VectorGeometry(-1, 0);
        vTest.coordinateRotation(Math.PI * 3 / 2);
        assertTrue("VectorGeometry(0, 1) is equal to VectorGeometry(-1, 0) after 270 degree rotation", epsilonEquality(vExpected, vTest));
        vTest = new VectorGeometry(0, 1);
        vExpected = new VectorGeometry(0, 1);
        vTest.coordinateRotation(Math.PI * 2);
        assertTrue("VectorGeometry(0, 1) is equal to VectorGeometry(0, 1) after 360 degree rotation", epsilonEquality(vExpected, vTest));

        // negative rotations
        vTest = new VectorGeometry(0, 1);
        vExpected = new VectorGeometry(-1, 0);
        vTest.coordinateRotation(-Math.PI / 2);
        assertTrue("VectorGeometry(0, 1) is equal to VectorGeometry(-1, 0) after -90 degree rotation", epsilonEquality(vExpected, vTest));
        vTest = new VectorGeometry(0, 1);
        vExpected = new VectorGeometry(0, -1);
        vTest.coordinateRotation(-Math.PI);
        assertTrue("VectorGeometry(0, -1) is equal to VectorGeometry(0, -1) after -180 degree rotation", epsilonEquality(vExpected, vTest));
        vTest = new VectorGeometry(0, 1);
        vExpected = new VectorGeometry(1, 0);
        vTest.coordinateRotation(-Math.PI * 3 / 2);
        assertTrue("VectorGeometry(0, 1) is equal to VectorGeometry(1, 0) after -270 degree rotation", epsilonEquality(vExpected, vTest));
        vTest = new VectorGeometry(0, 1);
        vExpected = new VectorGeometry(0, 1);
        vTest.coordinateRotation(-Math.PI * 2);
        assertTrue("VectorGeometry(0, 1) is equal to VectorGeometry(0, 1) after -360 degree rotation", epsilonEquality(vExpected, vTest));
    }

    private boolean epsilonEquality(VectorGeometry a, VectorGeometry b) {
        VectorGeometry difference = a.minus(b);
        return Math.abs(difference.x) < EPSILON && Math.abs(difference.y) < EPSILON;
    }
}