package strategy.actions.offense;

import communication.ports.robotPorts.FrodoRobotPort;
import strategy.Strategy;
import strategy.actions.ActionBase;
import strategy.points.basicPoints.BallPoint;
import strategy.robots.Frodo;
import strategy.robots.RobotBase;
import vision.RobotType;
import vision.tools.VectorGeometry;


/**
 * Kicks the ball forward when the ball is close to a wall (like ShuntKick's old functionality).
 */
public class WallKick extends ActionBase {

    private final int KICK = 0;

    public WallKick(RobotBase robot) {
        super(robot, new BallPoint());
    }

    @Override
    public void enterState(int newState) {
        this.robot.MOTION_CONTROLLER.setDestination(this.point);
        this.robot.MOTION_CONTROLLER.setHeading(this.point);
        this.robot.MOTION_CONTROLLER.setTolerance(-1);
        ((FrodoRobotPort)(this.robot.port)).kick();

    }

    @Override
    public void tok() {
        this.point.recalculate();



        VectorGeometry usLoc = Strategy.world.getRobot(RobotType.FRIEND_2).location;
        VectorGeometry ballLoc = new VectorGeometry(this.point.getX(), this.point.getY());

        this.enterState(this.KICK);
    }

}
