package strategy;

import strategy.actions.Behave;
import strategy.actions.MainOffense;
import strategy.actions.offense.BallGrab;
import strategy.actions.offense.PreciseKick;
import strategy.actions.offense.ShuntKick;
import strategy.actions.other.*;
import strategy.controllers.essentials.MotionController;
import strategy.points.basicPoints.*;
import strategy.robots.Frodo;
import vision.*;
import vision.Robot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Simon Rovder
 */
public class TestStrategy {

    private String action;


    /**
     * SDP2017NOTE
     * The following variable is a static variable always containing the very last known state of the world.
     * It is accessible from anywhere in the project at any time as TestStrategy.world
     */
    public static DynamicWorld world = null;
    public static DynamicWorld previous = null;
    private Frodo[] robotControls = new Frodo[4];

    /** In milliseconds */
    public static final int cycleTime = 100;

    private String readLine() {
        try {
            return new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public TestStrategy(String [] args) {

        /** Control all robots using this array */
        this.robotControls[0] = new Frodo(RobotType.FRIEND_2);
        this.robotControls[1] = new Frodo(RobotType.FRIEND_1);
        this.robotControls[2] = new Frodo(RobotType.FOE_1);
        this.robotControls[3] = new Frodo(RobotType.FOE_2);

        Frodo frodo = robotControls[0];
        this.action = "";


        // =============================================================================================================
        // === ENTER ROBOT/BALL INFO BELOW: ============================================================================
        // === ASSUME VELOCITY UNITS ARE CM/S ==========================================================================
        // =============================================================================================================

        Robot[] robots = new Robot[4];
        //                location                      velocity
        // new Robot(new DirectedPoint(x, y, d), new DirectedPoint(x, y, d), RobotType type)    (or null)
        robots[0] = new Robot();
        robots[1] = new Robot();
        robots[2] = new Robot();
        robots[3] = new Robot();

        //              location                      velocity
        // new Ball(new VectorGeometry(x, y), new VectorGeometry(x,y))
        Ball ball = new Ball();

        // RobotType.FRIEND_2, FRIEND_1, FOE_1 or FOE_2 (or null)
        RobotType ballHolder = null;

        world = new DynamicWorld(robots, ball, ballHolder);

        // =============================================================================================================
        // =============================================================================================================

        while(true){
            /*
             * SDP2017NOTE
             * This is a debug loop. Test Frodo's strategy here.
             */
            System.out.print(">> ");
            this.action = this.readLine();
            if(this.action.equals("exit")){
                break;
            }
            switch(this.action){
                case "a":
                    frodo.setControllersActive(true);
                    frodo.MOTION_CONTROLLER.setMode(MotionController.MotionMode.MOVE);
                    break;
                case "grab":
                    frodo.ACTION_CONTROLLER.setAction(new BallGrab(frodo));
                    break;
                case "stop":
                    frodo.ACTION_CONTROLLER.setAction(new Stop(frodo));
                    break;
                case "!":
                    System.out.print("Action: ");
                    System.out.print(frodo.ACTION_CONTROLLER.isActive());
                    System.out.print(" Motion: ");
                    System.out.print(frodo.MOTION_CONTROLLER.isActive());
                    System.out.print(" Kicker: ");
                    System.out.println(frodo.KICKER_CONTROLLER.isActive());
                    System.out.print(" Grabber: ");
                    System.out.println(frodo.GRABBER_CONTROLLER.isActive());
                    break;
                case "?":
                    frodo.ACTION_CONTROLLER.printDescription();
                    break;
                case "hold":
                    frodo.ACTION_CONTROLLER.setAction(new HoldPosition(frodo, new MidFoePoint()));
                    break;
                case "kick":
                    frodo.ACTION_CONTROLLER.setAction(new PreciseKick(frodo));
                    break;
                case "go_kick":
                    frodo.ACTION_CONTROLLER.setAction(new Goto(frodo, new KickablePoint()));
                    break;
                case "h":
                    frodo.ACTION_CONTROLLER.setAction(new Waiting(frodo));
                    frodo.MOTION_CONTROLLER.setDestination(null);
                    frodo.MOTION_CONTROLLER.setHeading(null);
                    frodo.KICKER_CONTROLLER.setWantToKick(false);
                    break;
                case "reset":
                    frodo.ACTION_CONTROLLER.setAction(new Goto(frodo, new ConstantPoint(0,0)));
                    break;
                case "behave":
                    Status.fixedBehaviour = null;
                    frodo.ACTION_CONTROLLER.setAction(new Behave(frodo));
                    break;
                case "safe":
                    frodo.ACTION_CONTROLLER.setAction(new GoToSafeLocation(frodo));
                    break;
                case "shunt":
                    frodo.ACTION_CONTROLLER.setAction(new ShuntKick(frodo));
                    break;
                case "def":
                    frodo.ACTION_CONTROLLER.setAction(new DefendGoal(frodo));
                    break;
                case "annoy":
                    frodo.ACTION_CONTROLLER.setAction(null);
                    frodo.MOTION_CONTROLLER.setDestination(new InFrontOfRobot(RobotAlias.FELIX));
                    frodo.MOTION_CONTROLLER.setHeading(new RobotPoint(RobotAlias.FELIX));
                    break;
                case "rot":
                    frodo.ACTION_CONTROLLER.setActive(false);
                    frodo.MOTION_CONTROLLER.setDestination(new Rotate());
                    frodo.MOTION_CONTROLLER.setHeading(new BallPoint());
                    break;
                case "test":
                    frodo.MOTION_CONTROLLER.setHeading(new EnemyGoal());
                    frodo.MOTION_CONTROLLER.setDestination(new EnemyGoal());
                    break;
                // Robot should go to ball then stop.
                case "follow_ball":
                    frodo.ACTION_CONTROLLER.setAction(new Goto(frodo, new BallPoint()));
                    break;
                case "go_to_friendly_goal":
                    frodo.ACTION_CONTROLLER.setAction(new Goto(frodo, new ConstantPoint(-150, 0)));
                    break;
                case "go_to_opponent_goal":
                    frodo.ACTION_CONTROLLER.setAction(new Goto(frodo, new ConstantPoint(150, 0)));
                    break;
                case "move_forward":
                    frodo.ACTION_CONTROLLER.setAction(new Goto(frodo, new InFrontOfRobot(RobotType.FRIEND_2)));
                    break;
                case "turn_to_enemy_goal":
                    frodo.MOTION_CONTROLLER.setHeading(new EnemyGoal());
                    break;
                case "go_to_kickable":
                    frodo.ACTION_CONTROLLER.setAction(new Goto(frodo, new GrabbablePoint(RobotType.FRIEND_2)));
                    break;
                case "no_grabber":
                    frodo.ACTION_CONTROLLER.setAction(new MainOffense(frodo));
                    break;
                case "update":
                    this.updateWorld();
                    break;
            }
        }

        System.exit(0);
    }

    public void updateWorld() {
        previous = world;

        // ===
        if (world != null) {
            for (Frodo f : robotControls) {
                if (f != null) f.perform();
            }
        }
        // ===

        // TODO: update robots', ball's positions based on their velocities
        // TODO: model basic physics like collisions (for now robots/ball will stop on collision)
        for (Robot r : world.getRobots()) {
            
        }



        // TODO: print out relevant info/create really basic GUI showing everything's position
    }




    /**
     * SDP2017NOTE
     * This main() method launches the testing.
     * @param args
     */
    public static void main(String[] args) {
        new TestStrategy(args);
    }

}
