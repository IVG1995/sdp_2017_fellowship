package strategy.actions.other;

import strategy.Strategy;
import strategy.actions.ActionException;
import strategy.actions.ActionBase;
import strategy.points.basicPoints.BallPoint;
import strategy.navigation.Obstacle;
import strategy.points.basicPoints.ConstantPoint;
import strategy.robots.RobotBase;
import vision.Ball;
import vision.Robot;
import vision.RobotType;
import vision.constants.Constants;
import vision.tools.VectorGeometry;

/**
 * Created by Simon Rovder
 */
public class GoToSafeLocation extends ActionBase {
    public GoToSafeLocation(RobotBase robot) {
        super(robot);
        this.rawDescription = " Go To safe location";
    }

    @Override
    public void enterState(int newState) {
        if(newState == 0){
            //if(this.robot instanceof Fred) {
            //    ((Fred)this.robot).PROPELLER_CONTROLLER.setActive(false);
            //    ((FredRobotPort)this.robot.port).propeller(0);
            //    ((FredRobotPort)this.robot.port).propeller(0);
            //    ((FredRobotPort)this.robot.port).propeller(0);
            //}


            Robot us = Strategy.world.getRobot(RobotType.FRIEND_2);
            Ball ball = Strategy.world.getBall();
            if(us == null || ball == null) return;

            // Run in front of our goal and hold that position until further notice.
            this.robot.MOTION_CONTROLLER.addObstacle(new Obstacle((int)ball.location.x, (int)ball.location.y, 30));
            this.robot.MOTION_CONTROLLER.setDestination(new ConstantPoint(-Constants.PITCH_WIDTH/2, 0));
            this.robot.MOTION_CONTROLLER.setHeading(new BallPoint());
            this.robot.MOTION_CONTROLLER.setTolerance(-1);
        }
    }

    @Override
    public void tok() throws ActionException {
        if(safe()){
            this.robot.MOTION_CONTROLLER.clearObstacles();
            throw new ActionException(true, false);
        } else {
            // In this case, call this.enterState(0) to make the robot go stand in front of our goal. enterState also
            // automatically avoids the ball so we don't accidentally end up shoving the ball into our own goal while
            // running back to defend it. Before calling it, clear the motion controller's list of obstacles so our
            // robot doesn't erroneously try to avoid where the ball was during past timer cycles.
            this.robot.MOTION_CONTROLLER.clearObstacles();

            this.enterState(0);


        }

    }

    public static boolean safe(){
        Robot us  = Strategy.world.getRobot(RobotType.FRIEND_2);
        Ball ball = Strategy.world.getLastKnownBall();
        // If we can't find our own robot or can't find the ball, it is not safe
        if(us == null || ball == null) return false;
        VectorGeometry ourGoal = new VectorGeometry(-Constants.PITCH_WIDTH/2, 0);
        // Otherwise, it is safe if we are closer to our goal than the ball is.
        return us.location.distance(ourGoal) < ball.location.distance(ourGoal);
    }
}
