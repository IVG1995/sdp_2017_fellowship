package strategy.actions.offense;

import strategy.actions.ActionException;
import strategy.actions.ActionBase;
import strategy.points.basicPoints.BallPoint;
import strategy.points.basicPoints.EnemyGoal;
import strategy.robots.Fred;
import strategy.points.basicPoints.KickablePoint;
import strategy.Strategy;
import strategy.robots.RobotBase;
import vision.RobotType;
import vision.tools.VectorGeometry;

/**
 * Created by Simon Rovder
 */
public class OffensiveKick extends ActionBase {

    // A KickablePoint always contains the location 7 (this number could change) cm in front of the ball.
    private KickablePoint kickable = new KickablePoint(RobotType.FRIEND_2);
    private BallPoint ball = new BallPoint();

    public OffensiveKick(RobotBase robot) {
        super(robot);
        this.rawDescription = "OffensiveKick";
    }
    @Override
    public void enterState(int newState) {
        if (newState == 0) {
            this.robot.MOTION_CONTROLLER.setHeading(new BallPoint());
            this.robot.MOTION_CONTROLLER.setDestination(new BallPoint());
        } else if (newState == 1) {
            ((Fred)this.robot).KICKER_CONTROLLER.kick();
        }


        // For first friendly we don't have a propeller, so this code is useless.
        //if(newState == 0){
        //    if(this.robot instanceof Fred){
        //        ((Fred)this.robot).PROPELLER_CONTROLLER.setActive(true);
        //    }
        //}
        this.state = newState;
    }

    @Override
    public void tok() throws ActionException {
        // Two states:
        // 0: ball is too far away; move towards ball
        // 1: ball is within kicking distance; kick ball


        // If the robot or ball can not be located, try again next timer cycle.
        if (Strategy.world.getRobot(RobotType.FRIEND_2) == null || Strategy.world.getBall() == null) {
            return;
        } else {

            kickable.recalculate();
            ball.recalculate();

            VectorGeometry kickable_vector = new VectorGeometry(kickable.getX(), kickable.getY());
            VectorGeometry ball_vector = new VectorGeometry(ball.getX(), ball.getY());

            // contains vector pointing at enemy goal from the ball; used to ensure robot is facing enemy goal when kicking (NOT BEING USED CURRENTLY)
            //VectorGeometry ball_to_goal = VectorGeometry.fromTo(ball_vector, new VectorGeometry(new EnemyGoal().getX(), new EnemyGoal().getY()));

            // If the ball is too far away from the kicker (more than one cm), keep moving towards the ball. To do that, call this.enterState(0).
            if (kickable_vector.distance(ball_vector) > 1) {
                this.enterState(0);
            } else {
                // If this section is reached, the ball is within kicking range. Send the kick command by calling this.enterState(1).
                this.enterState(1);
            }



        }



    }
}
