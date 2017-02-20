package strategy.actions.offense;

import strategy.Strategy;
import strategy.actions.ActionException;
import strategy.actions.ActionBase;
import strategy.points.DynamicPoint;
import strategy.points.basicPoints.BallPoint;
import strategy.points.basicPoints.BallDirection;
import strategy.points.basicPoints.EnemyGoal;
import strategy.points.basicPoints.ShootingPoint;
import strategy.robots.Frodo;
import strategy.robots.RobotBase;
import vision.Robot;
import vision.RobotType;
import vision.tools.VectorGeometry;

/**
 * Created by Simon Rovder
 */
public class ShuntKick extends ActionBase {
    
    private int GET_INTO_POSITION = 0;
    private int SHOOT = 1;

    private static final int CLOSE_ENOUGH = 15;

    public ShuntKick(RobotBase robot) {
        super(robot);
//        this.rawDescription = "Shunt Kick";
        this.point = new ShootingPoint();
    }

    @Override
    public void enterState(int newState) {
        if (newState == GET_INTO_POSITION) {
            System.out.println("GET_INTO_POSITION");
            this.robot.MOTION_CONTROLLER.setDestination(this.point);
            this.robot.MOTION_CONTROLLER.setHeading(new EnemyGoal());
            this.robot.MOTION_CONTROLLER.setTolerance(-1);
            ((Frodo)this.robot).KICKER_CONTROLLER.setWantToKick(false);

        } else if (newState == SHOOT) {
            System.out.println("SHOOT");
            BallPoint ballPoint = new BallPoint();
            ballPoint.recalculate();
            this.robot.MOTION_CONTROLLER.setDestination(ballPoint);
            this.robot.MOTION_CONTROLLER.setHeading(new EnemyGoal());
            this.robot.MOTION_CONTROLLER.setTolerance(-1);
            ((Frodo)this.robot).KICKER_CONTROLLER.setWantToKick(true);
        }
        this.state = newState;
    }

    @Override
    public void tok() throws ActionException {
        Robot us = Strategy.world.getRobot(RobotType.FRIEND_2);

        if(us != null){
//            System.out.println("ShootingPoint: " + this.point.getX() + " " + this.point.getY());
//            System.out.println("us: " + us.location.toString());
            // Are we already in a good position to shoot toward the enemy goal?
            if (us.location.distance(this.point.getX(), this.point.getY()) < CLOSE_ENOUGH) {
                this.enterState(SHOOT);
            } else {
                // If not, get into shooting position
                this.enterState(GET_INTO_POSITION);
            }
        }



    }
}
