package strategy.actions.defence;


import strategy.actions.ActionBase;
import strategy.points.DynamicPoint;
import strategy.points.basicPoints.MidDangerPoint;
import strategy.points.basicPoints.MidFoePoint;
import strategy.points.basicPoints.RobotPoint;
import strategy.robots.RobotBase;
import strategy.Strategy;
import vision.Robot;
import vision.RobotType;

/**
 * An action that puts Frodo in between the (enemy) ball holder and its teammate.
 */
public class BlockPass extends ActionBase {

    private final int BLOCK_PASS = 0;

    // When one or both enemies are invisible, or none are holding the ball
    // (this action should NOT be called in this situation).
    private final int WAIT = 1;

    private MidFoePoint midPoint;
    private RobotPoint holderLoc;

    public BlockPass(RobotBase robot) {
        super(robot);
    }

    @Override
    public void enterState(int newState) {

        if (newState == BLOCK_PASS) {
            this.robot.MOTION_CONTROLLER.setHeading(holderLoc);
            this.robot.MOTION_CONTROLLER.setDestination(midPoint);
            this.robot.MOTION_CONTROLLER.setTolerance(-1);
        } else {
            System.out.println("BlockPass should NOT have been entered in this situation.");
            System.out.println("Enemies are not holding ball, or one/both are invisible/lost.");
            this.robot.MOTION_CONTROLLER.setHeading(null);
            this.robot.MOTION_CONTROLLER.setDestination(null);
        }

        this.state = newState;
    }

    @Override
    public void tok() {
        Robot foeOne = Strategy.world.getRobot(RobotType.FOE_1);
        Robot foeTwo = Strategy.world.getRobot(RobotType.FOE_2);
        RobotType ballHolder = Strategy.world.getProbableBallHolder();

        if (foeOne != null && foeTwo != null && ballHolder != null) {
            this.midPoint = new MidFoePoint();
            this.holderLoc = new RobotPoint(ballHolder);

            this.midPoint.recalculate();
            this.holderLoc.recalculate();

            this.enterState(BLOCK_PASS);
        } else {
            this.enterState(WAIT);
        }

    }
}
