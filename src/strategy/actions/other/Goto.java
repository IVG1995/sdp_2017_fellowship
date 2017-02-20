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
 * Created by Simon Rovder. Moves to point until it is <10 cm away from it.
 */
public class Goto extends ActionBase {

    private final int STATIONARY = 0;
    private final int MOVING = 1;
    private final int ARRIVED = 2;

    public Goto(RobotBase robot, DynamicPoint point) {
        super(robot, point);
        this.rawDescription = " GOTO";
    }

    @Override
    public void enterState(int newState) {
        if(newState == MOVING){
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
            this.enterState(STATIONARY);
            return;
        }
        if(VectorGeometry.distance(this.point.getX(), this.point.getY(), us.location.x, us.location.y) < 10){
            this.enterState(ARRIVED);
        } else {
            if(this.state == STATIONARY){
                this.enterState(MOVING);
            }
        }
        if(this.state == ARRIVED){
            throw new ActionException(true, false);
        }
    }
}




