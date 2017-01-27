package vision;

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

    //Umm.....
    public Robot(){

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
}
