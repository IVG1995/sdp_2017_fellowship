package vision;

import org.opencv.core.Rect;
import vision.gui.SDPConsole;
import vision.preProcessing.matProcessor.BgSubtractor;
import vision.robotAnalysis.newRobotAnalysis.BgRobotAnalysis;
import vision.shapeObject.RectObject;
import vision.shapeObject.ShapeObject;
import vision.tools.DirectedPoint;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by Simon Rovder
 *
 * SDP2017NOTE
 * This is the object that gets passed out of the Vision System to all its registered listeners. Read this carefully,
 * it is fairly important.
 */
public class DynamicWorld {
    // This HashMap contains the detected robots.
    private HashMap<RobotType, Robot> robots;
    private HashMap<RobotAlias, Robot> aliases;
    private ArrayList<ArrayList<Double>> objects;

    // The location of the ball.
    private Ball ball;

    // If the ball was not found (ball is null), this will be the last known location of it.
    private Ball lastKnownBall;

    // If the ball is not found, this will be set to the robot that is most likely holding it.
    private RobotType probableBallHolder;


    //number of robots
    public int robotCount;
    public int robotChangeDelay;

    //time in world
    private final long time;

    //return time
    public long getTime(){
        return this.time;
    }

    //constructor for dynamic world (which has time and robots)
    public DynamicWorld(long time){
        this.time = time;
        this.robots = new HashMap<RobotType, Robot>();
        this.aliases = new HashMap<RobotAlias, Robot>();
    }

    public ArrayList<ShapeObject> getObjects(){
        return BgSubtractor.objects;

    }


    //This is just a bunch of getters and setters
    //Robots can be searched for by either alias or type

    //returns robot from hashmap with the specified type
    public Robot getRobot(RobotType type){
        return this.robots.get(type);
    }

    //returns robot from hashmap given an alias
    public Robot getRobot(RobotAlias alias){
        return this.aliases.get(alias);
    }


    //returns the ball
    public Ball getBall(){
        return this.ball;
    }

    //sets the ball
    public void setBall(Ball ball){
        this.ball = ball;
    }

    //returns last known position of ball
    public Ball getLastKnownBall(){
        return this.lastKnownBall;
    }

    //sets last known position of ball
    public void setLastKnownBall(Ball ball){
        this.lastKnownBall = ball;
    }

    //returns the probable holder of the ball
    public RobotType getProbableBallHolder(){
        return this.probableBallHolder;
    }

    //adds a robot to both hashmaps using the robots type and alias (if it has
    //one)
    public void setRobot(Robot r, ArrayList<RectObject> rectObjects){
        this.robots.put(r.type, r);
        //add to last known one
        BgRobotAnalysis.lastKnownRobots.put(r.type, r);
        rectObjects.remove(r.object);
        if(r.alias != RobotAlias.UNKNOWN){
            this.aliases.put(r.alias, r);
        }
    }


    public void setRobot(Robot r){
        this.robots.put(r.type, r);
        //add to last known one
        BgRobotAnalysis.lastKnownRobots.put(r.type, r);
        if(r.alias != RobotAlias.UNKNOWN){
            this.aliases.put(r.alias, r);
        }
    }
    //sets the probable holder of the ball
    public void setProbableBallHolder(RobotType type){
        this.probableBallHolder = type;
    }

    //does what it says, pretty much prints every bit of information about the
    //world. probably meant for debugging
    public void printData() {
        DirectedPoint p;
        if(this.ball != null){
            SDPConsole.writeln("BALL at " + this.ball.location.x + " : " + this.ball.location.y);
        }
        for(RobotType rt : this.robots.keySet()){
            p = this.robots.get(rt).location;
            SDPConsole.writeln("ROBOT: " + rt + " at " + p.x + " : " + p.y + " heading: " + p.direction);
        }
        if(this.probableBallHolder != null) SDPConsole.writeln("Probable ball holder: " + this.probableBallHolder.toString());
        if(this.lastKnownBall != null) SDPConsole.writeln("Last Known ball: " + this.lastKnownBall.toString());
    }


    //extra getter method that just returns all the robots
    public Collection<Robot> getRobots(){
        return this.robots.values();
    }

    public HashMap<RobotType, Robot> returnRobots() {
        return robots;
    }

    //update directed point using the shape detection stuff
    public void update_robot(RobotType r, Robot rob, double x, double y){

        if (!(robots.keySet().contains(r))){
            setRobot(rob);
        }
        robots.get(r).update_robot(rob);
    }
}
