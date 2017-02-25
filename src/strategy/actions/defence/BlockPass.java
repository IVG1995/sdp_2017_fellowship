package strategy.actions.defence;


import strategy.actions.ActionBase;
import strategy.points.basicPoints.MidFoePoint;
import strategy.points.basicPoints.RobotPoint;
import strategy.robots.RobotBase;
import strategy.Strategy;

/**
 * An action that puts Frodo in between the (enemy) ball holder and its teammate.
 */
public class BlockPass extends ActionBase {

    private final int BLOCK_PASS = 0;

    private MidFoePoint midPoint;
    private RobotPoint holderLoc;

    public BlockPass(RobotBase robot) {
        super(robot);
        this.midPoint = new MidFoePoint();
        this.holderLoc = new RobotPoint(Strategy.world.getProbableBallHolder());
    }

    @Override
    public void enterState(int newState) {

        this.robot.MOTION_CONTROLLER.setHeading(holderLoc);
        this.robot.MOTION_CONTROLLER.setDestination(midPoint);
        this.robot.MOTION_CONTROLLER.setTolerance(-1);

        this.state = newState;
    }

    @Override
    public void tok() {
        this.midPoint.recalculate();
        this.holderLoc.recalculate();
        this.enterState(BLOCK_PASS);
    }
}
