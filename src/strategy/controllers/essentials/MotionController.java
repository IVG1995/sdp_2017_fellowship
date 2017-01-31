package strategy.controllers.essentials;

import strategy.Strategy;
import strategy.controllers.ControllerBase;
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
        ON, OFF
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

    public void perform(){
        if(this.mode == MotionMode.OFF) return;

        Robot us = Strategy.world.getRobot(RobotType.FRIEND_2);
        if(us == null) return;

        NavigationInterface navigation;

        VectorGeometry heading = null;
        VectorGeometry destination = null;



        if(this.destination != null){
            this.destination.recalculate();

            destination = new VectorGeometry(this.destination.getX(), this.destination.getY());

            boolean intersects = false;

            // Search through obstacles (not sure what that would be apart from other robots) and check if any will be in our way
            for(Obstacle o : this.obstacles){
                intersects = intersects || o.intersects(us.location, destination);
            }

            // Search through detected robots and check if they'll get in our way, too
            for(Robot r : Strategy.world.getRobots()){
                if(r != null && r.type != RobotType.FRIEND_2){
                    intersects = intersects || VectorGeometry.vectorToClosestPointOnFiniteLine(us.location, destination, r.location).minus(r.location).length() < 30;
                }
            }

            // If anything is in our way or if robot isn't pretty much already at destination, set up A* navigation.
            if(intersects || us.location.distance(destination) > 30){
                navigation = new AStarNavigation();
                GUI.gui.searchType.setText("A*");
            } else {
                // Otherwise set up "potential field navigation".
                navigation = new PotentialFieldNavigation();
                GUI.gui.searchType.setText("Potential Fields");
            }

            navigation.setDestination(new VectorGeometry(destination.x, destination.y));

        // If no destination was specified, return (do nothing).
        } else {
            return;
        }

        if(this.heading != null){
            // If a direction to head in was specified, use that.
            this.heading.recalculate();
            heading = new VectorGeometry(this.heading.getX(), this.heading.getY());
        } else {
            // If no direction was specified, go forward.
            heading = VectorGeometry.fromAngular(us.location.direction, 10, null);
        }



        if(this.obstacles != null){
            navigation.setObstacles(this.obstacles);
        }



        VectorGeometry force = navigation.getForce();
        if(force == null){
            this.robot.port.stop();
            return;
        }

        // Contains a vector of length 10 pointing in the direction the robot is currently heading in.
        VectorGeometry robotHeading = VectorGeometry.fromAngular(us.location.direction, 10, null);
        VectorGeometry robotToPoint = VectorGeometry.fromTo(us.location, heading);
        double factor = 1;
        double rotation = VectorGeometry.signedAngle(robotToPoint, robotHeading);
        // Can throw null without check because null check takes SourceGroup into consideration.
        if(destination.distance(us.location) < 30){
            factor = 0.7;
        }

        // If we are already close enough to the destination, don't move anymore (tolerance denotes close-enoughness)
        // The lower the tolerance, the closer we have to be to the point.
        if(this.destination != null && us.location.distance(destination) < tolerance){
            this.robot.port.stop();
            return;
        }


//        strategy.navigationInterface.draw();

        this.robot.drive.move(this.robot.port, us.location, force, rotation, factor);

    }
}
