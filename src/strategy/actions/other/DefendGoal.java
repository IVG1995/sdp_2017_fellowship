package strategy.actions.other;

import strategy.actions.ActionException;
import strategy.actions.ActionBase;
import strategy.points.basicPoints.DangerousPoint;
import strategy.points.basicPoints.MidDangerPoint;
import strategy.robots.Fred;
import strategy.robots.RobotBase;
import vision.RobotType;
import vision.tools.VectorGeometry;
import vision.Ball;
import strategy.Strategy;
import vision.Robot;
import vision.constants.Constants;

/**
 * Created by Simon Rovder
 */
public class DefendGoal extends ActionBase {


    public DefendGoal(RobotBase robot) {
        super(robot);
        this.rawDescription = " Defend Goal";
    }
    @Override
    public void enterState(int newState) {
        if(newState == 0){
            if(this.robot instanceof Fred){
                ((Fred)this.robot).PROPELLER_CONTROLLER.setActive(true);
            }
        }
        this.state = newState;
    }

    @Override
    public void tok() throws ActionException {
        if(this.state == 0){
            DangerousPoint danger = new DangerousPoint();
            danger.recalculate();
            // This tells FRED to face the DangerousPoint continually
            // DangerousPoint's location is:
            //     -the ball if it isn't null
            //     -the foe if the ball isn't found
            //     -in front of our goal if neither is found
            robot.MOTION_CONTROLLER.setHeading(danger);
            // This tells fred to run in between the ball/foe and the goal to block the shot.
            this.enterAction(new HoldPosition(this.robot, new MidDangerPoint(RobotType.FRIEND_2)), 0, 0);
            this.enterState(1);


        }
    }
}
