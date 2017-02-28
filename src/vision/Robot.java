package vision;

import vision.shapeObject.RectObject;
import vision.tools.DirectedPoint;

/**
 * Created by Simon Rovder
 */

//class for robots which have a location, velocity, type and alias
//these are defined in their own classes
public class Robot {
    public DirectedPoint location;
    public DirectedPoint velocity;
    public RobotType type;
    public RobotAlias alias;
    public RectObject object;

    //Umm.....
    public Robot(){

    }

    // FOR STRATEGY TESTING PURPOSES:
    public Robot(DirectedPoint location, DirectedPoint velocity, RobotType type) {
        this.location = location;
        this.velocity = velocity;
        this.type = type;
    }

    //override for clone to return an exact copy of the robot
    @Override
    public Robot clone(){
        Robot r = new Robot();
        r.location = this.location.clone();
        r.velocity = this.velocity.clone();
        r.type = this.type;
        return r;
    }

    public void update_point(double x, double y){
        location.x = x;
        location.y = y;
    }
    public void  update_robot(Robot r){
        location = r.location.clone();
        velocity = r.velocity.clone();
        type = r.type;

    }
}
