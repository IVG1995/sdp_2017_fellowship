package strategy.actions.offense;

import strategy.actions.ActionException;
import strategy.actions.ActionBase;
import strategy.controllers.essentials.MotionController;
import strategy.points.basicPoints.EnemyGoal;
import strategy.points.basicPoints.RobotPoint;
import strategy.Strategy;
import strategy.robots.Frodo;
import strategy.robots.RobotBase;
import vision.Robot;
import vision.tools.VectorGeometry;

/**
 * Created by Simon Rovder
 */
public class OffensiveKick extends ActionBase {

    private final int NOT_FACING_GOAL  = 0;
    private final int READY_TO_RELEASE = 1;
    private final int READY_TO_SHOOT   = 2;
    // A GrabbablePoint always contains the location 7 (this number could change) cm in front of the ball.

    public OffensiveKick(RobotBase robot) {
        super(robot);
        this.rawDescription = "OffensiveKick";
    }
    @Override
    public void enterState(int newState) {
        if (newState == NOT_FACING_GOAL) {
            //stay in place and rotate
            System.out.println("not facing goal");
            this.robot.MOTION_CONTROLLER.setHeading(new EnemyGoal());
            this.robot.MOTION_CONTROLLER.setDestination(new RobotPoint(this.robot.robotType));
            this.robot.MOTION_CONTROLLER.setMode(MotionController.MotionMode.AIM);
            this.robot.MOTION_CONTROLLER.setTolerance(-1);
            this.robot.MOTION_CONTROLLER.setRotationTolerance(5);
        } else if (newState == READY_TO_RELEASE) {
            this.robot.MOTION_CONTROLLER.setDestination(null);
            this.robot.MOTION_CONTROLLER.setHeading(null);
            System.out.println("ready to release");
            ((Frodo) this.robot).GRABBER_CONTROLLER.wantToRelease();
        } else {
            System.out.println("ready to kick");
            ((Frodo)this.robot).KICKER_CONTROLLER.setWantToKick();
        }

        this.state = newState;
    }

    @Override
    public void tok() throws ActionException {
        Robot us = Strategy.world.getRobot(this.robot.robotType);
        if (us == null || Strategy.world.getBall() == null) {
            return;
        } else {
            VectorGeometry robotLocation = new VectorGeometry();
            us.location.copyInto(robotLocation);
            EnemyGoal target = new EnemyGoal();
            VectorGeometry robotHeading = VectorGeometry.fromAngular(us.location.direction, 10, null);
            if (VectorGeometry.isInGeneralDirection(robotLocation, robotHeading, target.toVectorGeometry())){
                if(this.state == NOT_FACING_GOAL){
                    enterState(READY_TO_RELEASE);
                } else {
                    enterState(READY_TO_SHOOT);
                    this.robot.MOTION_CONTROLLER.setMode(MotionController.MotionMode.ON);
                    throw new ActionException(true, true);
                }
            } else {
                enterState(NOT_FACING_GOAL);
            }
        }
    }
}
