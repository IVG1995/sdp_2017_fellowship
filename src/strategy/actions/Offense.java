package strategy.actions;

import strategy.actions.defence.Annoy;
import strategy.actions.offense.GetBall;
import strategy.actions.other.DefendGoal;
import strategy.actions.other.GoToSafeLocation;
import strategy.robots.RobotBase;
import vision.Ball;
import strategy.Strategy;
import vision.Robot;
import vision.RobotType;
import vision.constants.Constants;
import vision.tools.VectorGeometry;

/**
 * ANNOY:
 * DEFEND:
 * GET_BALL: When the ball is free, run and get it.
 * SCORE:
 * GET_OPEN: When our ally has the ball, get open for a pass.
 * CLEAR:
 * SAFE:
 */
enum OffenseEnum {
    ANNOY, DEFEND, GET_BALL, SCORE, GET_OPEN, CLEAR, SAFE
}

/**
 * A root action focused on offense and built for 2v2 matches.
 */
public class Offense extends StatefulActionBase<OffenseEnum>{


    public Offense(RobotBase robot) {
        super(robot, null);
    }

    @Override
    public void enterState(int newState) { this.state = newState; }

    @Override
    protected OffenseEnum getState() {
        Ball ball = Strategy.world.getBall();
        Robot ally = Strategy.world.getRobot(RobotType.FRIEND_1);
        Robot us = Strategy.world.getRobot(RobotType.FRIEND_2);
        RobotType ball_holder = Strategy.world.getProbableBallHolder();

        if (us == null) {
            // ???
            return this.nextState;
        }

        // =======================================================================================
        // What to do when our ally is lost or off the pitch.
        // =======================================================================================
        if (ally == null) {

            if (this.lastState == OffenseEnum.DEFEND || ball_holder == RobotType.FOE_1 || ball_holder == RobotType.FOE_2 || ball == null) {
                this.nextState = OffenseEnum.DEFEND;
            } else {

                // If we have the ball, decide whether to shoot or clear.
                if (ball_holder == RobotType.FRIEND_2) {
                    if (this.shot_on_goal()) {
                        this.nextState = OffenseEnum.SCORE;
                    } else {
                        this.nextState = OffenseEnum.CLEAR;
                    }
                } else {
                    // If the ball is free, go get it or defend.
                    RobotType closest = robot_closest_to_ball();

                    if (closest == RobotType.FRIEND_2) {
                        this.nextState = OffenseEnum.GET_BALL;
                    } else {

                        VectorGeometry ourGoal = new VectorGeometry(-Constants.PITCH_WIDTH / 2, 0);
                        if (us.location.distance(ourGoal) > ball.location.distance(ourGoal)) {
                            this.nextState = OffenseEnum.SAFE;
                        } else {
                            this.nextState = OffenseEnum.DEFEND;
                        }
                    }
                }


            }
            // =============================================================================================
            // Ally is on the pitch
            // =============================================================================================
        } else {

            if (ball_holder == RobotType.FOE_1 || ball_holder == RobotType.FOE_2) {
                this.nextState = OffenseEnum.ANNOY;
            }




        }


        return this.nextState;
    }

    @Override
    public void tok() {
        this.robot.MOTION_CONTROLLER.clearObstacles();
        this.lastState = this.nextState;
        switch (this.nextState) {
            case ANNOY:
                this.enterAction(new Annoy(this.robot), 0, 0);
                break;
            case DEFEND:
                this.enterAction(new DefendGoal(this.robot), 0, 0);
                break;
            case GET_BALL:
                this.enterAction(new GetBall(this.robot), 0, 0);
                break;
            case SCORE:

                break;
            case GET_OPEN:

                break;
            case CLEAR:

                break;
            case SAFE:
                this.enterAction(new GoToSafeLocation(this.robot), 0, 0);
                break;

        }

    }

    /**
     * Determines whether Frodo has a clean shot at the enemy goal.
     * @return
     */
    public boolean shot_on_goal() {


        return false;
    }

    /**
     * Returns the RobotType of the closest robot to the ball, or null if ball and/or all robots are lost.
     * @return
     */
    public static RobotType robot_closest_to_ball() {

        if (Strategy.world.getBall() == null) return null;

        VectorGeometry ball_loc = Strategy.world.getBall().location;
        Robot[] robots = (Robot[]) Strategy.world.getRobots().toArray();
        double closest_so_far = 100000000.0;

        RobotType closest = null;

        for (int i = 0; i < robots.length; i++) {
            if (robots[i] == null) continue;
            double distance = VectorGeometry.distance(robots[i].location, ball_loc);
            if (closest == null || closest_so_far > distance) {
                closest = robots[i].type;
                closest_so_far = distance;
            }
        }

        return closest;
    }
}
