package strategy.actions;

import strategy.Strategy;
import strategy.actions.defence.Annoy;
import strategy.actions.defence.BlockPass;
import strategy.actions.offense.PreciseKick;
import strategy.actions.offense.WallKick;
import strategy.actions.other.DefendGoal;
import strategy.actions.other.GoToSafeLocation;
import strategy.actions.other.Goto;
import strategy.actions.other.Waiting;
import strategy.controllers.essentials.MotionController;
import strategy.drives.FourWheelHolonomicDrive;
import strategy.points.basicPoints.BallPoint;
import strategy.robots.Frodo;
import strategy.robots.RobotBase;
import vision.Ball;
import vision.Robot;
import vision.RobotType;
import vision.constants.Constants;
import vision.tools.VectorGeometry;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * A root action focused on offense and built for 2v2 matches.
 */

enum OffenseEnum {
    DEFEND, SAFE, SCORE, ANNOY, GET_OPEN, BLOCK_PASS, WAIT, WALL, GO_TO_BALL, START
}

public class Offense extends StatefulActionBase<OffenseEnum>{

    // Contains info about which robot is closest to ball, how far away
    // each robot is from the ball, etc.
    private Samwise samwise = new Samwise();

    // Keep track of how long Frodo has been executing one continuous action.
    // If long enough, reset PID settings so they don't go stale.
    private int in_a_row = 0;

    public Offense(RobotBase robot) {
        super(robot, null);
        this.lastState = OffenseEnum.START;
    }

    @Override
    public void enterState(int newState) { this.state = newState; }

    @Override
    protected OffenseEnum getState() {
        Ball ball = Strategy.world.getBall();
        Robot ally = Strategy.world.getRobot(RobotType.FRIEND_1);
        Robot us = Strategy.world.getRobot(RobotType.FRIEND_2);
        RobotType ballHolderType = Strategy.world.getProbableBallHolder();
        this.samwise.calculate_closest();


        if (us == null) {
            // ???
            this.nextState = OffenseEnum.WAIT;
            return this.nextState;
        } else if (ball != null && ((Strategy.world.getRobot(RobotType.FOE_1) == null && Strategy.world.getRobot(RobotType.FOE_2) == null) ||
                this.samwise.isImmobile(RobotType.FOE_2) && this.samwise.isImmobile(RobotType.FOE_1))) {
            this.nextState = OffenseEnum.SCORE;
            return this.nextState;
        }

        // =======================================================================================
        // What to do when our ally is lost, off the pitch, or immobile.
        // =======================================================================================
        if (ally == null || this.samwise.isImmobile(ally.type)) {

            // Enemies have the ball OR ball is lost ==> DEFEND
            if (ballHolderType == RobotType.FOE_1 || ballHolderType == RobotType.FOE_2 || ball == null) {
                this.nextState = OffenseEnum.DEFEND;
                return this.nextState;
            }

            // We're further away from our goal than the ball is ==> SAFE
            VectorGeometry ourGoal = new VectorGeometry(-Constants.PITCH_WIDTH / 2, 0);
            if (us.location.distance(ourGoal) > ball.location.distance(ourGoal)) {
                this.nextState = OffenseEnum.SAFE;
                return this.nextState;
            }

            // The ball is close to a wall ==> DEFEND
            if (Math.abs(ball.location.y) > Constants.PITCH_HEIGHT - 20 ||
                    Math.abs(ball.location.x) > Constants.PITCH_WIDTH - 20) {
                this.nextState = OffenseEnum.DEFEND;
                return this.nextState;
            }

            // We have the ball AND a shot on goal ==> SCORE
            if (ballHolderType == RobotType.FRIEND_2 && shot_on_goal(us, us.location)) {
                this.nextState = OffenseEnum.SCORE;
                return this.nextState;
            }

            // There are no enemies/they're both immobile ==> SCORE
            if (this.samwise.getClosestEnemyDist() == null ||
                    (this.samwise.isImmobile(RobotType.FOE_1) && this.samwise.isImmobile(RobotType.FOE_2))) {
                this.nextState = OffenseEnum.SCORE;
                return this.nextState;
            }
            
            // We are closest to the ball by far AND could have a shot ==> SCORE
            if (this.samwise.getClosest() == RobotType.FRIEND_2 && shot_on_goal(us, ball.location) &&
                    (this.samwise.getClosestEnemyDist() / this.samwise.getDist(RobotType.FRIEND_2)) >= 2 &&
                    this.samwise.getClosestEnemyDist() > 50) {
                this.nextState = OffenseEnum.SCORE;
                return this.nextState;
            }

            // None of the above conditions hold ==> DEFEND
            this.nextState = OffenseEnum.DEFEND;
            return this.nextState;

        }
        // =============================================================================================
        // Ally is on the pitch: go full offense mode.
        // =============================================================================================

        Robot holderRobot = Strategy.world.getRobot(ballHolderType);

        // We need to defend (see method for details) ==> DEFEND
        // (basically if the defender is out of position)
        if (samwise.doWeNeedToDefend()) {
            this.nextState = OffenseEnum.DEFEND;
            return this.nextState;
        }

        // Enemies have ball AND ball is not near our goal ==> ANNOY
        //      (stay directly in front of enemy ball holder)
        // Enemies have ball AND ball is near our goal ==> BLOCK_PASS
        //      (stay in between the 2 enemies)
        //      (to keep out of friendly defender's way)
        if (ballHolderType == RobotType.FOE_1 || ballHolderType == RobotType.FOE_2)  {
            if (holderRobot.location.x > -(Constants.PITCH_WIDTH / 2) + 50) {
                this.nextState = OffenseEnum.ANNOY;
            } else {
                this.nextState = OffenseEnum.BLOCK_PASS;
            }
            return this.nextState;
        }

        // Friend has the ball and is moving up offensively ==> SAFE
        if ((ballHolderType == RobotType.FRIEND_1 || this.samwise.friendPossession()) &&
                ally.location.x > -50 && ally.velocity.x > 3) {
            this.nextState = OffenseEnum.SAFE;
            return this.nextState;
        }

        // Ball is lost ==> WAIT
        if (ball == null) {
            this.nextState = OffenseEnum.WAIT;
            return this.nextState;
        }

        VectorGeometry ourGoal = new VectorGeometry(-Constants.PITCH_WIDTH / 2, 0);
        // We're further/sameish distance from our goal than the ball is ==> SCORE
        //  (SCORE makes Frodo reposition himself behind the ball)
        if (us.location.distance(ourGoal) > (ball.location.distance(ourGoal) - 20)) {
            this.nextState = OffenseEnum.SCORE;
            return this.nextState;
        }

        // Ball is near a wall ==> WALL
        if (Math.abs(ball.location.x) > (Constants.PITCH_WIDTH / 2) - 20 ||
                Math.abs(ball.location.y) > (Constants.PITCH_HEIGHT / 2) - 20) {
            this.nextState = OffenseEnum.WALL;
        }

        // Ball is close to our goal ==> SAFE
        if (ball.location.x < -60) {
            this.nextState = OffenseEnum.SAFE;
            return this.nextState;
        }

        // Frodo has the ball ==> SCORE
        // Frodo is closest and could have a shot on goal ==> SCORE
        if (ballHolderType == RobotType.FRIEND_2 ||
                (this.samwise.getClosest() == RobotType.FRIEND_2 && shot_on_goal(us, ball.location))) {
            this.nextState = OffenseEnum.SCORE;
            return this.nextState;
        }

        // Ball is free, no other conditions apply ==> GO_TO_BALL
        this.nextState = OffenseEnum.GO_TO_BALL;
        return this.nextState;

    }

    @Override
    public void tok() {
        this.robot.MOTION_CONTROLLER.clearObstacles();

        // Reset PID settings if they may have gone stale
        if (this.lastState == this.nextState) {
            this.in_a_row++;
        } else this.in_a_row = 0;
        if (this.in_a_row > 40) {
            ((FourWheelHolonomicDrive)((Frodo)this.robot).drive).resetHistory();
        }
        // =======


        this.lastState = this.nextState;
        if (this.nextState != OffenseEnum.WAIT) {
            this.robot.MOTION_CONTROLLER.setMode(MotionController.MotionMode.MOVE);
        }
        switch (this.nextState) {
            case SCORE:
                this.enterAction(new PreciseKick(this.robot), 0, 0);
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
     * Used for calculating all robots' distances to the ball, deciding whether Frodo needs to run back
     * and defend our goal, determining whether a robot has been immobile for a while.
     */
    private class Samwise {
        private RobotType closest;
        private HashMap<RobotType, Integer> distances;

        private HashMap<RobotType, CircularFIFO> trackers;
        private final int trackingGranularity = 5;
        private int t;

        Samwise() {
            this.distances = new HashMap<>();
            this.trackers = new HashMap<>();
            this.trackers.put(RobotType.FRIEND_2, new CircularFIFO(5));
            this.trackers.put(RobotType.FRIEND_1, new CircularFIFO(5));
            this.trackers.put(RobotType.FOE_1, new CircularFIFO(5));
            this.trackers.put(RobotType.FOE_2, new CircularFIFO(5));
            this.t = 0;
        }

        private final Integer POSSESSION_RANGE = 8;

        void calculate_closest() {
            distances.clear();
            this.closest = null;
            if (Strategy.world.getBall() == null) return;

            VectorGeometry ball_loc = Strategy.world.getBall().location;
            double closest_so_far = 100000000.0;

            for (Robot r : Strategy.world.getRobots()) {
                if (r == null) continue;

                // Update immobile-tracker every 5 calls to this method
                if (this.t++ % trackingGranularity == 0) {
                    this.trackers.get(r.type).add(r.location);
                }


                // Get distance
                double dist = VectorGeometry.distance(r.location, ball_loc);
                distances.put(r.type, (int) dist);

                if (closest == null || closest_so_far > dist) {
                    this.closest = r.type;
                    closest_so_far = dist;
                }
            }
        }

        RobotType getClosest() { return this.closest; }

        /**
         * Returns the robot's distance from the ball, or null if the robot is lost.
         * @param robot
         * @return
         */
        Integer getDist(RobotType robot) { return distances.get(robot); }

        Integer getClosestEnemyDist() {
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
         * Same as enemyPossession but for friendly robot.
         * @return
         */
        boolean friendPossession() {
            Integer closestDist = this.getDist(this.closest);
            if (closestDist == null) { return false; }

            return (closestDist <= POSSESSION_RANGE && closest == RobotType.FRIEND_1);
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
        boolean doWeNeedToDefend() {
            Robot friend = Strategy.world.getRobot(RobotType.FRIEND_1);
            Robot us = Strategy.world.getRobot(RobotType.FRIEND_2);
            if (friend == null || friend.location.x > 20) {
                return true;
            }

            Robot ballHolder = Strategy.world.getRobot(Strategy.world.getProbableBallHolder());

            if (ballHolder == null) {
                if (this.closest == RobotType.FOE_2 || this.closest == RobotType.FOE_1) {
                    ballHolder = Strategy.world.getRobot(this.closest);
                } else return false;
            }

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

        boolean isImmobile(RobotType r) {
            return (this.trackers.get(r) != null && this.trackers.get(r).isImmobile());
        }
    }

    /**
     * Track last n locations of each robot. Helps determine if a robot is immobile.
     *
     */
    private class CircularFIFO {

        private LinkedList<VectorGeometry> list;
        private int cap;
        private final int tolerance = 5;

        CircularFIFO(int cap) {
            assert (cap > 0);
            this.cap = cap;
            this.list = new LinkedList<VectorGeometry>();
        }

        /**
         * Add element to front of list, remove last-added element if list is over capacity.
         */
        void add(VectorGeometry location) {
            this.list.add(location);
            if (this.list.size() > this.cap) {
                this.list.remove();
            }
        }

        /**
         * Check if a robot has moved much recently.
         * @return
         */
        boolean isImmobile() {
            VectorGeometry v = this.list.getFirst();
            for (VectorGeometry loc : this.list) {
                if (v.distance(loc) > this.tolerance) return false;
            }
            return true;
        }

        int size() {
            return this.list.size();
        }
    }


}
