package strategy.actions.offense;

import strategy.Strategy;
import strategy.actions.ActionException;
import strategy.actions.ActionBase;
import strategy.points.basicPoints.BallPoint;
import strategy.points.basicPoints.BallDirection;
import strategy.points.basicPoints.EnemyGoal;
import strategy.points.basicPoints.ShootingPoint;
import strategy.robots.Frodo;
import strategy.robots.RobotBase;
import vision.RobotType;
import vision.tools.VectorGeometry;

/**
 * Created by Simon Rovder
 */
public class ShuntKick extends ActionBase {
    
    private int GET_INTO_POSITION = 0;
    private int SHOOT = 1;

    private static final int CLOSE_ENOUGH = 3;

    public ShuntKick(RobotBase robot) {
        super(robot);
        this.rawDescription = "Shunt Kick";
    }


    private VectorGeometry destination;

    @Override
    public void enterState(int newState) {
        if (newState == GET_INTO_POSITION) {
            this.robot.MOTION_CONTROLLER.setDestination(new ShootingPoint());
            this.robot.MOTION_CONTROLLER.setHeading(new EnemyGoal());
            this.robot.MOTION_CONTROLLER.setTolerance(-1);
        } else if (newState == SHOOT) {
            this.robot.MOTION_CONTROLLER.setDestination(new BallPoint());
            this.robot.MOTION_CONTROLLER.setHeading(new EnemyGoal());
            this.robot.MOTION_CONTROLLER.setTolerance(-1);
            ((Frodo)this.robot).KICKER_CONTROLLER.setWantToKick(true);
        }
        this.state = newState;
    }

    @Override
    public void tok() throws ActionException {
        VectorGeometry ballLoc = Strategy.world.getBall().location;
        VectorGeometry ourLoc = Strategy.world.getRobot(RobotType.FRIEND_2).location;
        // If the ball is in our half, just hit it forward without worrying too much about aiming.
       
        if (ourLoc.distance(new ShootingPoint().getX(), new ShootingPoint().getY()) < CLOSE_ENOUGH) {
            // If the ball is in their half, aim at their goal before kicking.
            this.enterState(SHOOT);
        } else {
            // Get into shooting position
            this.enterState(GET_INTO_POSITION);
        }


    }
}
