package strategy.actions.offense;

import communication.ports.robotPorts.FrodoRobotPort;
import strategy.actions.ActionException;
import strategy.actions.ActionBase;
import strategy.controllers.essentials.MotionController;
import strategy.navigation.Obstacle;
import strategy.points.basicPoints.BallPoint;
import strategy.points.basicPoints.EnemyGoal;
import strategy.Strategy;
import strategy.points.basicPoints.KickablePoint;
import strategy.robots.RobotBase;
import vision.Robot;
import vision.constants.Constants;
import vision.tools.VectorGeometry;

/**
 * Created by Simon Rovder
 */
public class PreciseKick extends ActionBase {
    private final int GOING_TO_KICKABLE  = 0;
    private final int AIMING = 1;
    private final int KICK = 2;
    private final int STOP = 3;
    private BallPoint ballPoint = new BallPoint();
    private EnemyGoal goal = new EnemyGoal();
    public PreciseKick(RobotBase robot) {
        super(robot);
        this.rawDescription = "PreciseKick";
        this.point = new KickablePoint();
    }

    @Override
    public void enterState(int newState) {
        if (newState == GOING_TO_KICKABLE) {
            System.out.println("GOING_TO_KICKABLE");
            //stay in place and rotate
            this.robot.MOTION_CONTROLLER.setMode(MotionController.MotionMode.MOVE);
            this.robot.MOTION_CONTROLLER.setHeading(ballPoint);
            this.robot.MOTION_CONTROLLER.setDestination(this.point);
            this.robot.MOTION_CONTROLLER.clearObstacles();
            if(ballPoint != null){
                this.robot.MOTION_CONTROLLER.addObstacle(new Obstacle(ballPoint.getX(), ballPoint.getY(), 25));
            }
            this.robot.MOTION_CONTROLLER.setTolerance(-1);
        } else if (newState == AIMING) {
            System.out.println("AIMING");
            this.robot.MOTION_CONTROLLER.setDestination(null);
            this.robot.MOTION_CONTROLLER.setHeading(goal);
            this.robot.MOTION_CONTROLLER.setMode(MotionController.MotionMode.AIM);
        } else if (newState == KICK) {
            System.out.println("KICK");
            this.robot.port.stop();
            ((FrodoRobotPort)(this.robot.port)).kick();

            this.robot.MOTION_CONTROLLER.setDestination(null);
            this.robot.MOTION_CONTROLLER.setHeading(null);
        } else if (newState == STOP) {
            System.out.println("STOP");
            ((this.robot.port)).stop();
        }
        this.state = newState;
    }

    @Override
    public void tok() throws ActionException {
        ballPoint.recalculate();
        goal.recalculate();
        this.point.recalculate();
        Robot us = Strategy.world.getRobot(this.robot.robotType);
        if (us == null || Strategy.world.getBall() == null) {
            enterState(STOP);
        } else {
            VectorGeometry robotLocation = new VectorGeometry();
            VectorGeometry robotHeading = new VectorGeometry().fromAngular(us.location.direction, 10d);
            us.location.copyInto(robotLocation);
            double distanceToKickable = VectorGeometry.distance(robotLocation, new VectorGeometry(this.point.getX(), this.point.getY()));
            VectorGeometry robotToGoal  = VectorGeometry.fromTo(robotLocation, goal.toVectorGeometry());

            boolean shouldMove = distanceToKickable > 5 && Strategy.world.getProbableBallHolder() != us.type;
            boolean isFacingGoal = Math.abs(VectorGeometry.signedAngle(robotHeading, robotToGoal)) <= Constants.kickingAngleTolerance;

            if (this.state == KICK) {
                enterState(STOP);
                throw new ActionException(true, true);
            } else if (!shouldMove && !isFacingGoal) {
                enterState(AIMING);
            } else if (!shouldMove){
                enterState(KICK);
            } else {
                enterState(GOING_TO_KICKABLE);
            }
        }
    }
}
