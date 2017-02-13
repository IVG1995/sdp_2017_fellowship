package strategy.actions;

import strategy.actions.defence.Annoy;
import strategy.actions.defence.BlockPass;
import strategy.actions.defence.Clear;
import strategy.actions.offense.OffensiveKick;
import strategy.actions.offense.WallKick;
import strategy.actions.other.DefendGoal;
import strategy.actions.other.GoToSafeLocation;
import strategy.actions.other.Goto;
import strategy.actions.other.Waiting;
import strategy.points.basicPoints.BallPoint;
import strategy.points.basicPoints.KickablePoint;
import strategy.robots.RobotBase;
import vision.Ball;
import strategy.Strategy;
import vision.Robot;
import vision.RobotType;
import vision.constants.Constants;
import vision.tools.VectorGeometry;

import java.util.HashMap;

/**
 * A root action focused on offense and built for 2v2 matches.
 */

enum NoGrabberEnum {
    DEFEND, SAFE, SCORE, CLEAR, ANNOY, GET_OPEN, BLOCK_PASS, WAIT, WALL, GO_TO_BALL
}

public class NoGrabber extends StatefulActionBase<NoGrabberEnum>{

    // Contains info about which robot is closest to ball, how far away
    // each robot is from the ball, etc.
    private ClosestRobotInfo closestRobotInfo = new ClosestRobotInfo();

    public NoGrabber(RobotBase robot) {
        super(robot, null);
    }

    @Override
    public void enterState(int newState) { this.state = newState; }

    @Override
    protected NoGrabberEnum getState() {
        Ball ball = Strategy.world.getBall();
        Robot ally = Strategy.world.getRobot(RobotType.FRIEND_1);
        Robot us = Strategy.world.getRobot(RobotType.FRIEND_2);
        RobotType ballHolderType = Strategy.world.getProbableBallHolder();
        this.closestRobotInfo.calculate_closest();


        if (us == null) {
            // ???
            this.nextState = NoGrabberEnum.WAIT;
            return this.nextState;
        }

        // =======================================================================================
        // What to do when our ally is lost or off the pitch.
        // =======================================================================================
        if (ally == null) {

            // Enemies have the ball OR ball is lost ==> DEFEND
            if (ballHolderType == RobotType.FOE_1 || ballHolderType == RobotType.FOE_2 || ball == null) {
                this.nextState = NoGrabberEnum.DEFEND;
                return this.nextState;
            }

            // We're further away from our goal than the ball is ==> SAFE
            VectorGeometry ourGoal = new VectorGeometry(-Constants.PITCH_WIDTH / 2, 0);
            if (us.location.distance(ourGoal) > ball.location.distance(ourGoal)) {
                this.nextState = NoGrabberEnum.SAFE;
                return this.nextState;
            }

            // The ball is close to a wall ==> DEFEND
            if (Math.abs(ball.location.y) > Constants.PITCH_HEIGHT - 20 ||
                    Math.abs(ball.location.x) > Constants.PITCH_WIDTH - 20) {
                this.nextState = NoGrabberEnum.DEFEND;
                return this.nextState;
            }

            // We have the ball AND a shot on goal ==> SCORE
            if (ballHolderType == RobotType.FRIEND_2 && shot_on_goal(us, us.location)) {
                this.nextState = NoGrabberEnum.SCORE;
                return this.nextState;
            }

            // We are closest to the ball by far AND could have a shot ==> SCORE
            if (this.closestRobotInfo.getClosest() == RobotType.FRIEND_2 && shot_on_goal(us, ball.location) &&
                    (this.closestRobotInfo.getClosestEnemyDist() / this.closestRobotInfo.getDist(RobotType.FRIEND_2)) >= 2 &&
                    this.closestRobotInfo.getClosestEnemyDist() > 50) {
                this.nextState = NoGrabberEnum.SCORE;
                return this.nextState;
            }

            // We are closest to the ball by far AND have no shot ==> CLEAR
            // TODO

            // None of the above conditions hold ==> DEFEND
            this.nextState = NoGrabberEnum.DEFEND;
            return this.nextState;

        }
        // =============================================================================================
        // Ally is on the pitch: go full offense mode.
        // =============================================================================================

        Robot holderRobot = Strategy.world.getRobot(ballHolderType);

        // We need to defend (see method for details) ==> DEFEND
        // (basically if the defender is out of position)
        if (doWeNeedToDefend()) {
            this.nextState = NoGrabberEnum.DEFEND;
            return this.nextState;
        }

        // Enemies have ball AND ball is not near our goal ==> ANNOY
        //      (stay directly in front of enemy ball holder)
        // Enemies have ball AND ball is near our goal ==> BLOCK_PASS
        //      (stay in between the 2 enemies)
        //      (to keep out of friendly defender's way)
        if (ballHolderType == RobotType.FOE_1 || ballHolderType == RobotType.FOE_2)  {
            if (holderRobot.location.x > -(Constants.PITCH_WIDTH / 2) + 50) {
                this.nextState = NoGrabberEnum.ANNOY;
            } else {
                this.nextState = NoGrabberEnum.BLOCK_PASS;
            }
            return this.nextState;
        }

        // Friend has the ball ==> GET_OPEN
        // TODO: Figure this out
        if (ballHolderType == RobotType.FRIEND_1 || this.closestRobotInfo.friendPossession()) {
            this.nextState = NoGrabberEnum.WAIT;
            return this.nextState;
        }

        // Ball is lost ==> WAIT
        if (ball == null) {
            this.nextState = NoGrabberEnum.WAIT;
            return this.nextState;
        }

        VectorGeometry ourGoal = new VectorGeometry(-Constants.PITCH_WIDTH / 2, 0);
        // We're further/sameish distance from our goal than the ball is ==> SCORE
        //  (SCORE makes Frodo reposition himself behind the ball)
        if (us.location.distance(ourGoal) > (ball.location.distance(ourGoal) - 20)) {
            this.nextState = NoGrabberEnum.SCORE;
            return this.nextState;
        }

        // Ball is near a wall ==> WALL
        // TODO: ==> WAIT instead? Going near a wall never goes well
        if (Math.abs(ball.location.x) > (Constants.PITCH_WIDTH / 2) - 20 ||
                Math.abs(ball.location.y) > (Constants.PITCH_HEIGHT / 2) - 20) {
            this.nextState = NoGrabberEnum.WALL;
        }

        // Ball is close to our goal ==> GET_OPEN
        // TODO: figure this out
        if (ball.location.x < -60) {
            this.nextState = NoGrabberEnum.WAIT;
            return this.nextState;
        }

        // Frodo has the ball ==> SCORE
        // Frodo is closest and could have a shot on goal ==> SCORE
        if (ballHolderType == RobotType.FRIEND_2 ||
                (this.closestRobotInfo.getClosest() == RobotType.FRIEND_2 && shot_on_goal(us, ball.location))) {
            this.nextState = NoGrabberEnum.SCORE;
            return this.nextState;
        }

        // TODO: ???
        // Ball is free, no other conditions apply ==> GO_TO_BALL
        this.nextState = NoGrabberEnum.GO_TO_BALL;
        return this.nextState;

    }

    @Override
    public void tok() {
        this.robot.MOTION_CONTROLLER.clearObstacles();
        this.lastState = this.nextState;
        switch (this.nextState) {
            case SCORE:
                this.enterAction(new OffensiveKick(this.robot), 0, 0);
                break;
            case SAFE:
                this.enterAction(new GoToSafeLocation(this.robot), 0, 0);
                break;
            case DEFEND:
                this.enterAction(new DefendGoal(this.robot), 0, 0);
                break;
            case WAIT:
                this.enterAction(new Waiting(this.robot), 0, 0);
                break;
            case WALL:
                this.enterAction(new WallKick(this.robot), 0, 0);
                break;
            case ANNOY:
                this.enterAction(new Annoy(this.robot), 0, 0);
                break;
            case BLOCK_PASS:
                this.enterAction(new BlockPass(this.robot), 0, 0);
                break;
            case CLEAR:
                this.enterAction(new Clear(this.robot), 0, 0);
                break;
            case GO_TO_BALL:
                this.enterAction(new Goto(this.robot, new BallPoint()), 0, 0);
                break;
        }

    }

    /**
     * Determines whether a robot would have a clean shot at their attacking goal from point 'loc'.
     * @return
     */
    private static boolean shot_on_goal(Robot attackingRobot, VectorGeometry loc) {
        VectorGeometry toGoal;
        if (attackingRobot.type == RobotType.FOE_1 || attackingRobot.type == RobotType.FOE_2) {
            toGoal = VectorGeometry.fromTo(loc, new VectorGeometry(-Constants.PITCH_WIDTH / 2, 0));
        } else {
            toGoal = VectorGeometry.fromTo(loc, new VectorGeometry(Constants.PITCH_WIDTH / 2, 0));
        }

        boolean canKick = true;
        for (Robot r : Strategy.world.getRobots()) {
            if (r != null && r.type != attackingRobot.type) {
                canKick = canKick && VectorGeometry.closestPointToLine(loc, toGoal, r.location).length() > 20;
            }
        }

        return canKick;
    }

    /**
     * Used for calculating all robots' distances to the ball.
     */
    private class ClosestRobotInfo {
        private RobotType closest;
        private HashMap<RobotType, Integer> distances = new HashMap<>();

        private final Integer POSSESSION_RANGE = 8;

        public void calculate_closest() {
            distances.clear();
            this.closest = null;
            if (Strategy.world.getBall() == null) return;

            VectorGeometry ball_loc = Strategy.world.getBall().location;
            double closest_so_far = 100000000.0;

            for (Robot r : Strategy.world.getRobots()) {
                if (r == null) continue;
                double dist = VectorGeometry.distance(r.location, ball_loc);
                distances.put(r.type, (int) dist);

                if (closest == null || closest_so_far > dist) {
                    this.closest = r.type;
                    closest_so_far = dist;
                }
            }
        }

        public RobotType getClosest() { return this.closest; }

        public Integer getDist(RobotType robot) { return distances.get(robot); }

        public Integer getClosestEnemyDist() {
            Integer one = this.distances.get(RobotType.FOE_1);
            Integer two = this.distances.get(RobotType.FOE_2);
            if (one != null) {
                if (two != null) {
                    return (one <= two) ?  one : two;
                } else { return one; }
            } else if (two != null) { return two; }

            return null;
        }

        /**
         * Checks if the closest robot is actually close enough to be considered to have
         * possession of the ball, and that that robot is an enemy.
         * @return
         */
        public boolean enemyPossession() {
            Integer closestDist = this.getDist(this.closest);
            if (closestDist == null) { return false; }

            return (closestDist <= POSSESSION_RANGE && (closest == RobotType.FOE_1 || closest == RobotType.FOE_2));
        }

        /**
         * Same as enemyPossession but for friendly robot.
         * @return
         */
        public boolean friendPossession() {
            Integer closestDist = this.getDist(this.closest);
            if (closestDist == null) { return false; }

            return (closestDist <= POSSESSION_RANGE && closest == RobotType.FRIEND_1);
        }
    }

    /**
     * A method for determining if Frodo needs to run back and defend our goal.
     * An enemy ball must be holding/closest to the ball plus one of the following:
     * 1. Both Frodo and enemy ball holder are closer to our goal than friend ==> TRUE
     * 2. Frodo is closer to our goal, is in a better position to block a shot
     *      and enemy ball holder is very close to our goal ==> TRUE
     * Otherwise ==> FALSE
     * @return
     */
    private boolean doWeNeedToDefend() {
        Robot friend = Strategy.world.getRobot(RobotType.FRIEND_1);
        Robot us = Strategy.world.getRobot(RobotType.FRIEND_2);
        if (friend == null) {
            return true;
        }

        ClosestRobotInfo c = new ClosestRobotInfo();
        c.calculate_closest();
        Robot ballHolder = Strategy.world.getRobot(Strategy.world.getProbableBallHolder());

        if (ballHolder == null) { return false; }

        VectorGeometry friendlyGoal = new VectorGeometry((-Constants.PITCH_WIDTH / 2), 0);
        VectorGeometry ballHolderToGoal = VectorGeometry.fromTo(ballHolder.location, friendlyGoal);

        if (us.location.distance(friendlyGoal) < friend.location.distance(friendlyGoal)) {

            if (ballHolder.location.distance(friendlyGoal) < friend.location.distance(friendlyGoal)) {
                return true;
            }

            if (VectorGeometry.closestPointToLine(ballHolder.location, ballHolderToGoal, friend.location).length() >
                    VectorGeometry.closestPointToLine(ballHolder.location, ballHolderToGoal, us.location).length() &&
                    ballHolder.location.x < -60) {
                return true;
            }
        }

        return false;
    }
}
