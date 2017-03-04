package strategy.actions.other;

import strategy.actions.ActionException;
import strategy.actions.ActionBase;
import strategy.points.DynamicPoint;
import strategy.Strategy;
import strategy.robots.RobotBase;
import vision.Robot;
import vision.RobotType;
import vision.tools.VectorGeometry;

/**
 * Created by Simon Rovder
 */
public class Goto extends ActionBase {

    private final int STATIONARY = 1;
    private final int GOTO_POINT = 0;
    private final int SUCCESS = 2;

    public Goto(RobotBase robot, DynamicPoint point) {
        super(robot, point);
        this.rawDescription = " GOTO";
    }

    @Override
    public void enterState(int newState) {
        if(newState == GOTO_POINT){
            this.robot.MOTION_CONTROLLER.setDestination(this.point);
            this.robot.MOTION_CONTROLLER.setHeading(this.point);
        } else {
            this.robot.MOTION_CONTROLLER.setDestination(null);
            this.robot.MOTION_CONTROLLER.setHeading(null);
        }
        this.state = newState;
    }

    @Override
    public void tok() throws ActionException {
        Robot us = Strategy.world.getRobot(RobotType.FRIEND_2);
        if(us == null){
            this.enterState(this.STATIONARY);
            return;
        }
        if(VectorGeometry.distance(this.point.getX(), this.point.getY(), us.location.x, us.location.y) < 5){
            this.enterState(this.SUCCESS);
        } else {
            if(this.state == this.STATIONARY){
                this.enterState(this.GOTO_POINT);
            }
        }
        if(this.state == this.SUCCESS){
            throw new ActionException(true, false);
        }
    }
}




