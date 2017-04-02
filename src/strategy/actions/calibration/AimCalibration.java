package strategy.actions.calibration;

import strategy.Strategy;
import strategy.actions.ActionBase;
import strategy.actions.ActionException;
import strategy.controllers.essentials.MotionController;
import strategy.points.basicPoints.BallPoint;
import strategy.points.basicPoints.EnemyGoal;
import strategy.points.basicPoints.KickablePoint;
import strategy.robots.RobotBase;
import vision.Robot;
import vision.constants.Constants;
import vision.tools.VectorGeometry;


public class AimCalibration extends ActionBase {
    private final int AIMING = 0;
    private final int STOP = 3;
    // A GrabbablePoint always contains the location 7 (this number could change) cm in front of the ball.
    private BallPoint ballPoint = new BallPoint();
    private EnemyGoal goal = new EnemyGoal();
    public AimCalibration(RobotBase robot) {
        super(robot);
        this.rawDescription = "PreciseKick";
        this.point = new KickablePoint();
    }


    @Override
    public void enterState(int newState) {
        if (newState == AIMING) {
            System.out.println("AIMING");
            this.robot.MOTION_CONTROLLER.setDestination(null);
            this.robot.MOTION_CONTROLLER.setHeading(goal);
            this.robot.MOTION_CONTROLLER.setMode(MotionController.MotionMode.AIM);
        } else if (newState == STOP) {
            System.out.println("STOP");
            this.robot.MOTION_CONTROLLER.setHeading(null);
            this.robot.MOTION_CONTROLLER.setMode(MotionController.MotionMode.OFF);
            ((this.robot.port)).stop();
        }
        this.state = newState;
    }

    @Override
    public void tok() throws ActionException {
        goal.recalculate();
        this.point.recalculate();
        Robot us = Strategy.world.getRobot(this.robot.robotType);
        if (us == null) {
            enterState(STOP);
        } else {
            VectorGeometry robotLocation = new VectorGeometry();
            VectorGeometry robotHeading = new VectorGeometry().fromAngular(us.location.direction, 10d);
            us.location.copyInto(robotLocation);
            VectorGeometry robotToGoal  = VectorGeometry.fromTo(robotLocation, goal.toVectorGeometry());
            System.out.println("angle to target: " + Math.abs(VectorGeometry.signedAngle(robotHeading, robotToGoal)) * 180 / Math.PI);
            boolean isFacingGoal = Math.abs(VectorGeometry.signedAngle(robotHeading, robotToGoal)) <= Constants.kickingAngleTolerance;
            if (isFacingGoal) {
                enterState(STOP);
                System.out.println("angle to target: " + Math.abs(VectorGeometry.signedAngle(robotHeading, robotToGoal)) * 180 / Math.PI);
                throw new ActionException(true, true);
            } else {
                enterState(AIMING);
            }
        }
    }
}
