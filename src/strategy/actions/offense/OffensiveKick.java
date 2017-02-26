package strategy.actions.offense;

import communication.ports.robotPorts.FrodoRobotPort;
import strategy.actions.ActionException;
import strategy.actions.ActionBase;
import strategy.controllers.essentials.MotionController;
import strategy.points.basicPoints.BallPoint;
import strategy.points.basicPoints.EnemyGoal;
import strategy.Strategy;
import strategy.points.basicPoints.KickablePoint;
import strategy.robots.Frodo;
import strategy.robots.RobotBase;
import vision.Robot;
import vision.tools.VectorGeometry;

/**
 * Created by Simon Rovder
 */
public class OffensiveKick extends ActionBase {

    private final int GOING_TO_KICKABLE  = 0;
    private final int KICKING = 1;
    // A GrabbablePoint always contains the location 7 (this number could change) cm in front of the ball.

    public OffensiveKick(RobotBase robot) {
        super(robot);
        this.rawDescription = "OffensiveKick";
    }
    @Override
    public void enterState(int newState) {
        if (newState == GOING_TO_KICKABLE) {
            //stay in place and rotate
            this.robot.MOTION_CONTROLLER.setHeading(new KickablePoint());
            this.robot.MOTION_CONTROLLER.setDestination(new KickablePoint());
            this.robot.MOTION_CONTROLLER.setTolerance(-1);
        } else if (newState == KICKING) {
            this.robot.MOTION_CONTROLLER.setDestination(new BallPoint());
            this.robot.MOTION_CONTROLLER.setHeading(new BallPoint());
            ((FrodoRobotPort)(this.robot.port)).kick();
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
            if (VectorGeometry.distance(robotLocation, new KickablePoint().toVectorGeometry()) < 10){
                enterState(GOING_TO_KICKABLE);
            } else {
                enterState(KICKING);
            }
        }
    }
}
