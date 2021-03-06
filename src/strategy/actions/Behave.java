package strategy.actions;

import strategy.Strategy;
import strategy.actions.offense.PreciseKick;
import strategy.actions.offense.WallKick;
import strategy.actions.other.DefendGoal;
import strategy.actions.other.GoToSafeLocation;
import strategy.actions.other.Waiting;
import strategy.actions.offense.ShuntKick;
import strategy.robots.RobotBase;
import vision.Ball;
import vision.Robot;
import vision.RobotType;
import vision.constants.Constants;
import vision.tools.VectorGeometry;

/**
 * Created by Simon Rovder
 */
enum BehaviourEnum{
    DEFEND, SHUNT, KICK, SAFE, EMPTY, WALL_KICK
}

/**
 * The main Action class. It basically plays the game.
 * Defensive class.
 */
public class Behave extends StatefulActionBase<BehaviourEnum> {

    private int optNumber = 1;
    private static final int NUMBER_OF_OPTS = 5;
    public static boolean RESET = true;

    //private Robot last_known_robot;
    // maybe keep a list of all last_known_robots (including teammate and foe(s))


    public Behave(RobotBase robot){
        super(robot, null);
    }

    @Override
    public void enterState(int newState) {
        if(newState == 0){
            this.robot.setControllersActive(true);
        }
        this.state = newState;
    }


    @Override
    public void tok() throws ActionException {

        this.robot.MOTION_CONTROLLER.clearObstacles();
        this.lastState = this.nextState;
        switch (this.nextState){
            case DEFEND:
                this.enterAction(new DefendGoal(this.robot), 0, 0);
                break;
            case KICK:
                this.enterAction(new PreciseKick(this.robot), 0, 0);
                break;
            case SHUNT:
                this.enterAction(new ShuntKick(this.robot), 0, 0);
                break;
            case WALL_KICK:
                this.enterAction(new WallKick(this.robot), 0, 0);
                break;
            case SAFE:
                this.enterAction(new GoToSafeLocation(this.robot), 0, 0);
                break;
            case EMPTY:
                this.enterAction(new Waiting(this.robot), 0, 0);
                break;
        }
    }

    @Override
    protected BehaviourEnum getState() {
        Ball ball = Strategy.world.getBall();
        // If our robot doesn't know where the ball is, go into defending mode (execute sub-action DefendGoal).
        // DefendGoal runs back in front of the goal and/or in the path of the ball/foe coming at the goal.
        if(ball == null){
            this.nextState = BehaviourEnum.DEFEND;
        } else {
            Robot us = Strategy.world.getRobot(this.robot.robotType);
            if(us == null){
                // Angry yelling
                this.nextState = BehaviourEnum.EMPTY;
            } else {
                // If our robot is further away from our goal then the ball is, go into SAFE mode (execute sub-action GoToSafeLocation).
                // This action rushes our robot back to right in front of our goal while also making sure not to accidentally
                // knock the ball into our own goal in the process.
                VectorGeometry ourGoal = new VectorGeometry(-Constants.PITCH_WIDTH/2, 0);
                if(us.location.distance(ourGoal) > ball.location.distance(ourGoal)){
                    this.nextState = BehaviourEnum.SAFE;
                } else {
                    // If the ball is within 20 cm of any wall, go into WALL_KICK mode (execute sub-action WallKick).
                    // For now, this sub-action just moves towards the ball and continually kicks.
                    if(Math.abs(ball.location.x) > Constants.PITCH_WIDTH/2 - 20 && Math.abs(ball.location.y) > Constants.PITCH_HEIGHT/2 - 20){
                        this.nextState = BehaviourEnum.WALL_KICK;
                    } else {
                        boolean canKick = true;
                        // If all other robots aren't null, not moving (quickly at least), and at least 50 cm away from the ball,
                        // then canKick is true (more conditions to follow)
                        for(Robot r : Strategy.world.getRobots()){
                            if(r != null && r.type != RobotType.FRIEND_2 && r.type != RobotType.FRIEND_1) {
                                canKick = canKick && r.location.distance(ball.location) > 50;
                            }
                        }
                        // If all the above conditions are met, plus at least one of these two conditions:
                        // 1: we weren't defending last cycle, or
                        // 2: the angle between the ball's current direction and the direction towards our goal is greater than 2 radians,
                        // then go into KICK mode.
                        if(canKick && (this.lastState != BehaviourEnum.DEFEND || VectorGeometry.angle(ball.velocity, VectorGeometry.fromTo(ball.location, new VectorGeometry(-Constants.PITCH_WIDTH/2, 0))) > 2)){
                            this.nextState = BehaviourEnum.KICK;
                        } else {
                            this.nextState = BehaviourEnum.DEFEND;
                        }
                    }
                }
            }
        }
        return this.nextState;
    }
}
