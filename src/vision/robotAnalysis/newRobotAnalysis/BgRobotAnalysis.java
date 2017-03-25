package vision.robotAnalysis.newRobotAnalysis;

import org.opencv.core.Rect;
import strategy.navigation.aStarNavigation.Circle;
import vision.Ball;
import vision.DynamicWorld;
import vision.Robot;
import vision.colorAnalysis.ColorGroup;
import vision.colorAnalysis.SDPColor;
import vision.robotAnalysis.RobotAnalysisBase;
import vision.RobotType;
import vision.robotAnalysis.RobotColorSettings;
import vision.shapeObject.CircleObject;
import vision.shapeObject.RectObject;
import vision.shapeObject.ShapeObject;
import vision.spotAnalysis.approximatedSpotAnalysis.Spot;
import vision.tools.DirectedPoint;
import vision.tools.VectorGeometry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Simon Rovder
 */
public class BgRobotAnalysis extends RobotAnalysisBase {


//    private SDPColor[] teamColor = {SDPColor.YELLOW, SDPColor.BLUE};
//    private SDPColor[] spotColor = {SDPColor.GREEN, SDPColor.PINK};


    public BgRobotAnalysis() {
        super();
    }
    public static HashMap<RobotType, Robot> lastKnownRobots = new HashMap<>();


    @Override
    public void nextUndistortedSpots(ArrayList<ShapeObject> objects, long time) {
        ArrayList<Spot> spotList = new ArrayList<>();
        ArrayList<RobotPlate> plates = new ArrayList<RobotPlate>();
        ArrayList<RectObject> rectObjects = new ArrayList<>();
        ArrayList<CircleObject> circleObjects = new ArrayList<>();
        for (ShapeObject obj : objects) {
            if (obj instanceof RectObject) {
                rectObjects.add((RectObject) obj);
            } else {
                circleObjects.add((CircleObject) obj);
            }
        }


        for (RectObject i : rectObjects) {
            HashMap<SDPColor, ArrayList<Spot>> spots = i.spots;
            ArrayList<Spot> pinkSpots = new ArrayList<>();
            ArrayList<Spot> greenSpots = new ArrayList<>();
            for (SDPColor c : ColorGroup.pink) {
                pinkSpots.addAll(spots.get(c));
            }

            for (SDPColor c : ColorGroup.green) {
                greenSpots.addAll(spots.get(c));
            }

            if (spots.get(SDPColor.PINK).size() > 1) {

                PatternMatcher.patternMatch(pinkSpots, plates, i);
            } else {
                PatternMatcher.patternMatch(greenSpots, plates, i);
            }
            Spot marker = null;
            if (spots.get(SDPColor.MARKER).size()>0) {
                marker = spots.get(SDPColor.MARKER).get(0);
            }
            PatternMatcher.singularValidate(greenSpots, plates, marker);
            PatternMatcher.singularValidate(pinkSpots, plates, marker);

            PatternMatcher.removeInvalid(plates);

            PatternMatcher.teamAnalysis(plates, spots.get(SDPColor.YELLOW));
            PatternMatcher.teamAnalysis(plates, spots.get(SDPColor.BLUE));
        }

        // maybe pink?
        for (CircleObject c : circleObjects) {
            HashMap<SDPColor, ArrayList<Spot>> spots = c.spots;
            if (!spots.get(SDPColor._BALL).isEmpty()) {
                spotList.addAll(spots.get(SDPColor._BALL));
            }
        }


        DynamicWorld world = new DynamicWorld(time);
        Robot bot;

        for (RobotPlate plate : plates) {
            if (!plate.hasTeam()) {
                plate.setTeam(RobotColorSettings.ASSUME_YELLOW ? SDPColor.YELLOW : SDPColor.BLUE);
            }
            bot = plate.toRobot();
            world.setRobot(bot, rectObjects);
        }


        for (int i = 0; i < spotList.size(); i++) {
            Spot s = spotList.get(i);
            if (PatternMatcher.isBotPart(plates, s)) {
                spotList.remove(i);
                i--;
            }
        }


        world.robotCount = world.getRobots().size();


        Ball ball;
        Ball oldBall = null;
        long timeDelta = 0;
        if (lastKnownWorld != null) {
            oldBall = lastKnownWorld.getBall();
            timeDelta = (time - lastKnownWorld.getTime()) / 1000000;
        }

        if (timeDelta == 0) timeDelta = 1;

        if (spotList.size() > 0) {
            ball = new Ball();
            ball.location = spotList.get(0);
            world.setBall(ball);
        }
        ball = world.getBall();

        if (lastKnownWorld != null && world.robotChangeDelay != 0 && lastKnownWorld.getProbableBallHolder() != null) {

            world.setBall(null);
            world.setProbableBallHolder(lastKnownWorld.getProbableBallHolder());
            world.setLastKnownBall(lastKnownWorld.getLastKnownBall());
            if (world.robotCount == lastKnownWorld.robotCount) {
                world.robotChangeDelay = lastKnownWorld.robotChangeDelay - 1;
            } else {
                world.robotChangeDelay = 20;
            }
        } else {
            if (this.lastKnownWorld != null) {
                Ball lastKnownBall = this.lastKnownWorld.getBall();
                if (lastKnownBall != null) {
                    if (ball == null) {
                        Robot closest = null;
                        for (Robot robot : world.getRobots()) {
                            if (closest == null) closest = robot;
                            else {
                                if (lastKnownBall.location.distance(closest.location) > lastKnownBall.location.distance(robot.location)) {
                                    closest = robot;
                                }
                            }
                        }

                        if (closest != null && closest.location.distance(lastKnownBall.location) < 30) {
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

        if (lastKnownWorld != null && world.getBall() != null && lastKnownWorld.getBall() != null) {
            VectorGeometry velocity = VectorGeometry.fromTo(lastKnownWorld.getBall().location, world.getBall().location);
            velocity.setLength(velocity.length() / timeDelta);
            world.getBall().velocity = velocity;
        } else {
            if (world.getBall() != null) world.getBall().velocity = new VectorGeometry(0, 0);
        }

        if (lastKnownWorld != null) {
            for (RobotType rt : RobotType.values()) {
                Robot old = lastKnownWorld.getRobot(rt);
                Robot newR = world.getRobot(rt);
                if (newR != null) {
                    if (old != null) {
                        newR.velocity = newR.location.clone();
                        newR.velocity.minus(old.location);
                        newR.velocity.direction = (newR.location.direction - old.location.direction) / timeDelta;
                        newR.velocity.setLength(newR.velocity.length() / timeDelta);
                    } else {
                        newR.velocity = new DirectedPoint(0, 0, 0);
                    }
//                    System.out.println(newR.velocity);
                }
            }
        }


        for (Robot toUndistort : world.getRobots()) {
            if (toUndistort != null) {
                // This part moves the strategy.robots closer to the center of the pitch. It compensates for the height
                // of the robot (15 cm)
                toUndistort.location.setLength(toUndistort.location.length() * (1 - 20.0 / 250));
            }
        }

        if ( /*world.getRobots().contains(null) &&*/ rectObjects.size() > 0 && this.lastKnownWorld != null) {
            RobotType[] r_types = {RobotType.FOE_1, RobotType.FOE_2, RobotType.FRIEND_2, RobotType.FRIEND_1};
            DynamicWorld previous = this.lastKnownWorld;
            //System.out.println("At least one robot not found");
            //avoids multiple calls to the return method
            HashMap<RobotType, Robot> pre_rob = previous.returnRobots();
            HashMap<RobotType, Boolean> robot_mask = new HashMap<>();
            for (Robot i : world.getRobots()) {
                robot_mask.put(i.type, true);
            }
            //contains the probability that every robot is every shape
            ArrayList<HashMap<RobotType, Double>> probabilities = new ArrayList<HashMap<RobotType, Double>>();
            int count = 0;
            //use only rectangles (to avoid considering the ball)


            //loop over every robot for every shape
            for (ShapeObject obj : rectObjects) {
                HashMap<RobotType, Double> obj_prob = new HashMap<RobotType, Double>();
                for (RobotType rType : lastKnownRobots.keySet()) {
                    if (!robot_mask.containsKey(rType)) {
                        //probabilities based on distance
                        obj_prob.put(rType, 1 / (Math.sqrt(((lastKnownRobots.get(rType).velocity.x - obj.pos.x) * (lastKnownRobots.get(rType).velocity.x - obj.pos.x)) + ((lastKnownRobots.get(rType).velocity.y - obj.pos.y) * (lastKnownRobots.get(rType).velocity.y - obj.pos.y)))));
                        //obj_prob.put(rType, 1 / (Math.sqrt((lastKnownRobots.get(rType).velocity.x - obj.pos.x) * (lastKnownRobots.get(rType).velocity.x - obj.pos.x)) + Math.sqrt((lastKnownRobots.get(rType).velocity.y - obj.pos.y) * (lastKnownRobots.get(rType).velocity.y - obj.pos.y))));
                    }
                }
                probabilities.add(obj_prob);
            }


            //do the actual position updating
            for (ShapeObject obj : rectObjects) {
                //init to null
                RobotType max_type = null;
                double max_prob = 0;
                //the robot with the highest probability is set to be the obj
                for (RobotType rType : probabilities.get(count).keySet()) {
                    if (probabilities.get(count).get(rType) > max_prob) {
                        max_prob = probabilities.get(count).get(rType);
                        max_type = rType;
                    }
                }
                //update the position
                if (max_type != null) {
                    Robot rob = lastKnownRobots.get(max_type);
                    rob.update_point(obj.pos.x,obj.pos.y);
                    world.update_robot(max_type, lastKnownRobots.get(max_type), obj.pos.x, obj.pos.y);
                }
                //disallow any robot to be assigned twice
                for (HashMap<RobotType, Double> map : probabilities) {
                    map.remove(max_type);
                }

                count++;
            }

        }
        this.informListeners(world);
    }
}
