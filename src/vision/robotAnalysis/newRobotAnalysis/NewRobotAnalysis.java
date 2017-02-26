package vision.robotAnalysis.newRobotAnalysis;

import vision.Ball;
import vision.ShapeObject;
import vision.DynamicWorld;
import vision.Robot;
import vision.colorAnalysis.SDPColor;
import vision.robotAnalysis.RobotAnalysisBase;
import vision.RobotType;
import vision.robotAnalysis.RobotColorSettings;
import vision.shapeObject.ShapeObject;
import vision.spotAnalysis.approximatedSpotAnalysis.Spot;
import vision.tools.DirectedPoint;
import vision.tools.VectorGeometry;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Simon Rovder
 */
 //
public class NewRobotAnalysis extends RobotAnalysisBase {


//    private SDPColor[] teamColor = {SDPColor.YELLOW, SDPColor.BLUE};
//    private SDPColor[] spotColor = {SDPColor.GREEN, SDPColor.PINK};

    //constructor
    public NewRobotAnalysis(){
        super();
    }

    //analyses the undistorted spots and is literally the only thing here
    //@Override
    public void nextUndistortedSpots(HashMap<SDPColor, ArrayList<Spot>> spots, long time) {
        ArrayList<Spot> spotList;
        ArrayList<RobotPlate> plates = new ArrayList<RobotPlate>();

        PatternMatcher.patternMatch(spots.get(SDPColor.GREEN), plates);
        PatternMatcher.patternMatch(spots.get(SDPColor.PINK), plates);

        PatternMatcher.singularValidate(spots.get(SDPColor.GREEN), plates);
        PatternMatcher.singularValidate(spots.get(SDPColor.PINK), plates);

        PatternMatcher.removeInvalid(plates);

        PatternMatcher.teamAnalysis(plates, spots.get(SDPColor.YELLOW));
        PatternMatcher.teamAnalysis(plates, spots.get(SDPColor.BLUE));

        DynamicWorld world = new DynamicWorld(time);
        Robot r;

        //sets team for plates
        for(RobotPlate plate : plates){
            if(!plate.hasTeam()){
                plate.setTeam(RobotColorSettings.ASSUME_YELLOW ? SDPColor.YELLOW : SDPColor.BLUE);
            }
            r = plate.toRobot();
            world.setRobot(r);
        }

        spotList = spots.get(SDPColor._BALL);

        for(int i = 0; i < spotList.size(); i++){
            Spot s = spotList.get(i);
            if(PatternMatcher.isBotPart(plates, s)){
                spotList.remove(i);
                i--;
            }
        }

        //gets the number of robots on the field
        world.robotCount = world.getRobots().size();

        //the ball details
        Ball ball;
        Ball oldBall = null;
        long timeDelta = 0;
        if(lastKnownWorld != null){
            oldBall = lastKnownWorld.getBall();
            timeDelta = (time - lastKnownWorld.getTime())/1000000;
        }

        if(timeDelta == 0) timeDelta = 1;

        if(spotList.size() > 0){
            ball = new Ball();
            ball.location = spotList.get(0);
            world.setBall(ball);
        }
        ball = world.getBall();

        //update from the last known world
        if(lastKnownWorld != null && world.robotChangeDelay != 0 && lastKnownWorld.getProbableBallHolder() != null){

            world.setBall(null);
            world.setProbableBallHolder(lastKnownWorld.getProbableBallHolder());
            world.setLastKnownBall(lastKnownWorld.getLastKnownBall());
            if(world.robotCount == lastKnownWorld.robotCount){
                world.robotChangeDelay = lastKnownWorld.robotChangeDelay - 1;
            } else {
                world.robotChangeDelay = 20;
            }
        } else {
            if(this.lastKnownWorld != null){
                Ball lastKnownBall = this.lastKnownWorld.getBall();
                if(lastKnownBall != null){
                    if(ball == null){
                        Robot closest = null;
                        for(Robot robot : world.getRobots()){
                            if(closest == null) closest = robot;
                            else {
                                if(lastKnownBall.location.distance(closest.location) > lastKnownBall.location.distance(robot.location)){
                                    closest = robot;
                                }
                            }
                        }

                        if(closest != null && closest.location.distance(lastKnownBall.location) < 30){
                            Ball newBall = new Ball();
                            newBall.location = closest.location.clone();
                            world.setBall(newBall);
                            world.setProbableBallHolder(closest.type);
                        } else {
                            world.setBall(lastKnownBall.clone());
                        }
                    }
                }
            }
        }

        //ball stuff
        if(lastKnownWorld != null && world.getBall() != null && lastKnownWorld.getBall() != null){
            VectorGeometry velocity = VectorGeometry.fromTo(lastKnownWorld.getBall().location, world.getBall().location);
            velocity.setLength(velocity.length()/timeDelta);
            world.getBall().velocity = velocity;
        } else {
            if(world.getBall() != null) world.getBall().velocity = new VectorGeometry(0,0);
        }

        if(lastKnownWorld != null){
            for(RobotType rt : RobotType.values()){
                Robot old = lastKnownWorld.getRobot(rt);
                Robot newR = world.getRobot(rt);
                if(newR != null){
                    if(old != null){
                        newR.velocity = newR.location.clone();
                        newR.velocity.minus(old.location);
                        newR.velocity.direction = (newR.location.direction - old.location.direction)/timeDelta;
                        newR.velocity.setLength(newR.velocity.length() / timeDelta);
                    } else {
                        newR.velocity = new DirectedPoint(0,0,0);
                    }
//                    System.out.println(newR.velocity);
                }
            }
        }


        for(Robot toUndistort : world.getRobots()){
            if(toUndistort != null){
                // This part moves the strategy.robots closer to the center of the pitch. It compensates for the height
                // of the robot (15 cm)
                toUndistort.location.setLength(toUndistort.location.length() * (1 - 20.0/250));
            }
        }

        if (world.getRobots().contains(null) && this.lastKnownWorld != null){
			
			DynamicWorld previous = this.lastKnownWorld;
            System.out.println("At least one robot not found");
            //avoids multiple calls to the return method
            HashMap<RobotType, Robot> pre_rob = previous.returnRobots();
            //contains the probability that every robot is every other robot
            ArrayList<HashMap<RobotType, Double>> probabilities = new  ArrayList<HashMap<RobotType, Double>>();
            int count = 0;

            //loop over every robot for every shape
            for (ShapeObject obj : world.getObjects()){
                HashMap<RobotType, Double> obj_prob = new HashMap<RobotType, Double>();
                for (RobotType rType : pre_rob.keySet()){
                    //probabilities based on distance
                    obj_prob.put(rType, 1 / (Math.sqrt((pre_rob.get(rType).velocity.x - obj.x) * (pre_rob.get(rType).velocity.x - obj.x)) + Math.sqrt((pre_rob.get(rType).velocity.y - obj.y) * (pre_rob.get(rType).velocity.y - obj.y))));
                }
                probabilities.add(obj_prob);
            }

            //IDEALLY THESE ARE REMOVED BEFORE THE PROBABILITY CALCULATIONS TO SAVE TIME BUT THIS IS EASIER
            //remove any already found robots from consideration
            for (Robot rType : world.getRobots()){
                if (!(rType.equals(null))){
                    for (HashMap<RobotType, Double> map : probabilities){
                        map.remove(rType.type);
                    }
                }
            }

            //do the actual position updating
            for (ShapeObject obj : world.getObjects()){
                //init to null
                RobotType max_type = null;
                double max_prob = 0;
                //the robot with the highest probability is set to be the obj
                for (RobotType rType : probabilities.get(count).keySet()){
                    if (probabilities.get(count).get(rType) > max_prob){
                        max_prob = probabilities.get(count).get(rType);
                        max_type = rType;
                    }
                }

                //update the position
                world.update_robot(max_type, pre_rob.get(max_type), obj.x, obj.y);

                //disallow any robot to be assigned twice
                for (HashMap<RobotType, Double> map : probabilities){
                    map.remove(max_type);
                }

                count++;
            }

        }
        this.informListeners(world);
    }

    @Override
    public void nextUndistortedSpots(ArrayList<ShapeObject> objects, long time) {

    }
}
