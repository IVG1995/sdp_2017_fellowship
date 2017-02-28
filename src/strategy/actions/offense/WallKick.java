package strategy.actions.offense;

import communication.ports.robotPorts.FrodoRobotPort;
import strategy.Strategy;
import strategy.actions.ActionBase;
import strategy.points.basicPoints.BallPoint;
import strategy.robots.Frodo;
import strategy.robots.RobotBase;
import vision.Ball;
import vision.Robot;
import vision.RobotType;
import vision.tools.VectorGeometry;


/**
 * Kicks the ball forward when the ball is close to a wall (like ShuntKick's old functionality).
 */
public class WallKick extends ActionBase {

    private final int DISTANCE_TO_KICKER = 7;

    private final int GOTO = 0;
    private final int KICK = 1;

    // 8 seconds worth of cycles
    private final int kickLimit = 8000 / Strategy.cycleTime;
    private int sanityTimer;

    public WallKick(RobotBase robot) {
        super(robot, new BallPoint());
        this.sanityTimer = 0;
    }

    @Override
    public void enterState(int newState) {
        // this.point = a BallPoint
        this.robot.MOTION_CONTROLLER.setDestination(this.point);
        this.robot.MOTION_CONTROLLER.setHeading(this.point);
        this.robot.MOTION_CONTROLLER.setTolerance(-1);

        if (newState == KICK) {

            // If robot has been kicking for (8) seconds straight, just stop and reset.
            if (sanityTimer >= kickLimit) {
                this.enterState(GOTO);
                return;
            }
            ((FrodoRobotPort) (this.robot.port)).startKick();
            this.sanityTimer++;

        } else {
            ((FrodoRobotPort) (this.robot.port)).stopKick();
            this.sanityTimer = 0;
        }

    }

    @Override
    public void tok() {
        this.point.recalculate();
        Robot us = Strategy.world.getRobot(RobotType.FRIEND_2);
        Ball ball = Strategy.world.getBall();
        if (us == null || ball == null) return;

        VectorGeometry kickable = us.location.clone();
        kickable.add(new VectorGeometry().fromAngular(us.location.direction, DISTANCE_TO_KICKER));

        if (kickable.distance(ball.location) < 5) {
            this.enterState(KICK);
        } else {
            this.enterState(GOTO);
        }

    }

}
