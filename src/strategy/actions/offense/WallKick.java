package strategy.actions.offense;

import communication.ports.robotPorts.FrodoRobotPort;
import strategy.Strategy;
import strategy.actions.ActionBase;
import strategy.points.basicPoints.BallPoint;
import strategy.points.basicPoints.ConstantPoint;
import strategy.robots.Frodo;
import strategy.robots.RobotBase;
import vision.Ball;
import vision.Robot;
import vision.RobotType;
import vision.constants.Constants;
import vision.tools.VectorGeometry;


/**
 * Kicks the ball forward when the ball is close to a wall (like ShuntKick's old functionality).
 */
public class WallKick extends ActionBase {

    private final int DISTANCE_TO_KICKER = 7;

    private final int GOTO = 0;
    private final int APPROACH = 1;
    private final int KICK = 2;

    private ConstantPoint approachPoint;

    public WallKick(RobotBase robot) {
        super(robot, new BallPoint());
    }

    @Override
    public void enterState(int newState) {
        // this.point = a BallPoint
        this.robot.MOTION_CONTROLLER.setTolerance(-1);
        this.robot.MOTION_CONTROLLER.setHeading(this.point);

        if (newState == GOTO) {
            ((FrodoRobotPort) (this.robot.port)).stopKick();
            this.robot.MOTION_CONTROLLER.setDestination(approachPoint);

        } else if (newState == APPROACH) {
            ((FrodoRobotPort) (this.robot.port)).stopKick();
            this.robot.MOTION_CONTROLLER.setDestination(this.point);


        } else if (newState == KICK){
            ((FrodoRobotPort) (this.robot.port)).startKick();
            this.robot.MOTION_CONTROLLER.setDestination(this.point);
        }

    }

    @Override
    public void tok() {
        this.point.recalculate();
        Robot us = Strategy.world.getRobot(RobotType.FRIEND_2);
        Ball ball = Strategy.world.getBall();
        if (us == null || ball == null) return;

        // === GO TO APPROACH POINT ============================================================================

        // We want Frodo to approach the ball from behind, not just slam into it and then into the wall
        VectorGeometry behindBall = VectorGeometry.fromTo(ball.location, new VectorGeometry(-Constants.PITCH_WIDTH / 2, ball.location.y));
        VectorGeometry approachPoint = VectorGeometry.closestPointToLine(ball.location, behindBall, us.location);

        // Make sure Frodo isn't too close to the wall
        int limit = (Constants.PITCH_HEIGHT / 2) - 10;
        int approachYSign = (approachPoint.y >= 0) ? 1 : -1;
        if (Math.abs(approachPoint.y) > limit) approachPoint.y = approachYSign * limit;

        if (us.location.distance(approachPoint) >= 7) {
            this.approachPoint = new ConstantPoint((int) approachPoint.x, (int) approachPoint.y);
            this.enterState(GOTO);
            return;
        }

        // === APPROACH BALL THEN KICK =========================================================================

        VectorGeometry kickable = us.location.clone();
        kickable.add(new VectorGeometry().fromAngular(us.location.direction, DISTANCE_TO_KICKER));

        if (kickable.distance(ball.location) < 5) {
            this.enterState(KICK);
        } else {
            this.enterState(APPROACH);
        }

    }

}
