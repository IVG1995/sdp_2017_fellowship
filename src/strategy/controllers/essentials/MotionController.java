package strategy.controllers.essentials;

import strategy.Strategy;
import strategy.controllers.ControllerBase;
import strategy.drives.pid.ControlHistory;
import strategy.navigation.NavigationInterface;
import strategy.navigation.Obstacle;
import strategy.points.DynamicPoint;
import strategy.navigation.aStarNavigation.AStarNavigation;
import strategy.navigation.potentialFieldNavigation.PotentialFieldNavigation;
import strategy.robots.RobotBase;
import strategy.GUI;
import vision.Robot;
import vision.RobotType;
import vision.tools.VectorGeometry;

import java.util.LinkedList;

/**
 * Created by Simon Rovder
 */
public class MotionController extends ControllerBase {

    public MotionMode mode;
    private DynamicPoint heading = null;
    private DynamicPoint destination = null;

    private int tolerance;

    private LinkedList<Obstacle> obstacles = new LinkedList<Obstacle>();

    public MotionController(RobotBase robot) {
        super(robot);
    }

    public enum MotionMode{
        MOVE, AIM, OFF
    }

    public void setMode(MotionMode mode){
        this.mode = mode;
    }

    public void setTolerance(int tolerance){
        this.tolerance = tolerance;
    }



    public void setDestination(DynamicPoint destination){
        this.destination = destination;
    }

    public void setHeading(DynamicPoint dir){
        this.heading = dir;
    }

    public void addObstacle(Obstacle obstacle){
        this.obstacles.add(obstacle);
    }

    public void clearObstacles(){
        this.obstacles.clear();
    }

    /** FOR STRATEGY TESTING PURPOSES: */
    public DynamicPoint getDestination() {
        return this.destination;
    }

    public DynamicPoint getHeading() {
        return this.heading;
    }
    /** ============================== */

    public void perform(){
        if(this.mode == MotionMode.OFF) return;

        Robot us = Strategy.world.getRobot(RobotType.FRIEND_2);
        if(us == null)
        {
           //TODO Angry yelling
            return;
        }

        NavigationInterface navigation;

        VectorGeometry heading = null;
        VectorGeometry destination = null;


        if(this.heading != null){
            // If a direction to face was specified, use that.
            this.heading.recalculate();
            heading = new VectorGeometry(this.heading.getX(), this.heading.getY());
        } else {
            // If no direction was specified, do not turn as you move.
            // (heading contains a vector of length ten pointing in the direction our robot is currently facing)
            heading = VectorGeometry.fromAngular(us.location.direction, 10, null);
        }

        // Contains a vector of length 10 pointing in the direction the robot is currently heading in.
        VectorGeometry robotHeading = VectorGeometry.fromAngular(us.location.direction, 10, null);
        // Contains a vector pointing in the direction the motion controller was told to face.
        VectorGeometry robotToPoint = VectorGeometry.fromTo(us.location, heading);
        // factor denotes basically the speed of movement; factor = 1 means the robot travels at full speed, 0 means doesn't move, etc.
        // rotation contains the angle the robot has to turn to be facing the right direction
        double rotation = VectorGeometry.signedAngle(robotToPoint, robotHeading);


        if(this.destination != null){
            this.destination.recalculate();

            destination = new VectorGeometry(this.destination.getX(), this.destination.getY());

            boolean intersects = false;

            // Search through obstacles and check if any will be in our way
            for(Obstacle o : this.obstacles){
                intersects = intersects || o.intersects(us.location, destination);
            }

            // Search through detected robots and check if they'll get in our way, too
            for(Robot r : Strategy.world.getRobots()){
                if(r != null && r.type != RobotType.FRIEND_2){
                    intersects = intersects || VectorGeometry.vectorToClosestPointOnFiniteLine(us.location, destination, r.location).minus(r.location).length() < 30;
                }
            }

//            // If anything is in our way or if robot isn't pretty much already at destination, set up A* navigation.
//            if(intersects || us.location.distance(destination) > 30){
//                navigation = new AStarNavigation();
//                GUI.gui.searchType.setText("A*");
//            } else {
//                // Otherwise set up "potential field navigation".
//                navigation = new PotentialFieldNavigation();
//                GUI.gui.searchType.setText("Potential Fields");
//            }
            navigation = new AStarNavigation();
            GUI.gui.searchType.setText("A*");
            navigation.setDestination(new VectorGeometry(destination.x, destination.y));

        // If no destination was specified, and we mean to aim, use aim navigation.
        } else if (this.mode == MotionMode.AIM) {
            this.robot.drive.aim(this.robot.port, rotation);
            return;
        } else {
            return;
        }


        if(this.obstacles != null){
            navigation.setObstacles(this.obstacles);
        }


        VectorGeometry force = navigation.getForce();
        if(force == null){
            this.robot.port.stop();
            System.out.println("MOTION CONTROLLER IS STOPPING MOVEMENT");
            return;
        }



        // If we are close enough to the destination, don't move anymore (tolerance denotes close-enoughness)
        // The lower the tolerance, the closer we have to be to the point before the robot stops moving.
        if(this.destination != null && us.location.distance(destination) < tolerance){
            System.out.println("MOTION CONTROLLER IS STOPPING MOVEMENT");
            this.robot.port.stop();
            return;
        }

        // Can throw null without check because null check takes SourceGroup into consideration.


//        navigation.draw();// uncomment to get a JFrame of navigation info
        switch (this.mode) {
            case MOVE: this.robot.drive.move(this.robot.port, us.location, force, rotation); break;
            case AIM: this.robot.drive.aim(this.robot.port, rotation); break;
        }


    }
}
