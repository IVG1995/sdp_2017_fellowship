package strategy;

import communication.ports.robotPorts.FrodoRobotPort;
import strategy.actions.Behave;
import strategy.actions.offense.BallGrab;
import strategy.actions.other.*;
import strategy.actions.offense.OffensiveKick;
import strategy.actions.offense.ShuntKick;
import communication.ports.robotPorts.FredRobotPort;
import strategy.points.basicPoints.*;
import strategy.robots.Fred;
import communication.PortListener;
import strategy.robots.Frodo;
import strategy.robots.RobotBase;
import vision.*;
import vision.Robot;
import vision.settings.SettingsManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Simon Rovder
 */
public class Strategy implements VisionListener, PortListener, ActionListener {



    private Timer timer;
    private String action;
    private Vision vision;
    //counter mechanism for cycling vision setting files


    /**
     * SDP2017NOTE
     * The following variable is a static variable always containing the very last known state of the world.
     * It is accessible from anywhere in the project at any time as Strategy.world
     */
    public static DynamicWorld world = null;
    public static DynamicWorld previous = null;

    public static Status status;

    private String readLine() {
        try {
            return new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private RobotBase [] robots;

    public Strategy(String [] args) {

        /*
         * SDP2017NOTE
         * Create your robots in the following line. All these robots will be instantly connected to the
         * navigation system and all its controllers will be launched every cycle.
         */
        // Our robot has RobotType "FRIEND_2"
        this.robots = new RobotBase [] {new Frodo(RobotType.FRIEND_2)};

//        Fred fred = (Fred) this.robots[0];
//        FredRobotPort port = (FredRobotPort) fred.port;

        Frodo frodo = (Frodo) this.robots[0];
        FrodoRobotPort port = (FrodoRobotPort) frodo.port;

        final Strategy semiStrategy = this;
        semiStrategy.vision = new Vision(args);
        semiStrategy.vision.addVisionListener(semiStrategy);


//        fred.PROPELLER_CONTROLLER.setActive(false);

        this.action = "";
        GUI.gui.doesNothingButIsNecessarySoDontDelete();
        GUI.gui.setRobot(frodo);
        this.timer = new Timer(100, this);
        this.timer.start();


        while(true){
            /*
             * SDP2017NOTE
             * This is a debug loop. You can add manual control over the robots here so as to make testing easier.
             * It simply loops forever. Vision System and Strategy run concurrently.
             *
             */
            System.out.print(">> ");
            this.action = this.readLine();
            if(this.action.equals("exit")){
//                frodo.PROPELLER_CONTROLLER.setActive(false);
//                port.propeller(0);
//                port.propeller(0);
//                port.propeller(0);
                break;
            }
            switch(this.action){
                case "a":
                    frodo.setControllersActive(true);
                    break;
                case "grab":
                    frodo.ACTION_CONTROLLER.setAction(new BallGrab(frodo));
                    break;
                case "stop":
                    frodo.ACTION_CONTROLLER.setAction(new Stop(frodo));
                    break;
//                case "!":
//                    System.out.print("Action: ");
//                    System.out.print(frodo.ACTION_CONTROLLER.isActive());
//                    System.out.print(" Motion: ");
//                    System.out.print(frodo.MOTION_CONTROLLER.isActive());
//                    System.out.print(" Propeller: ");
//                    System.out.println(frodo.PROPELLER_CONTROLLER.isActive());
//                    break;
                case "?":
                    frodo.ACTION_CONTROLLER.printDescription();
                    break;
                case "hold":
                    frodo.ACTION_CONTROLLER.setAction(new HoldPosition(frodo, new MidFoePoint()));
                    break;
                case "kick":
                    frodo.ACTION_CONTROLLER.setAction(new OffensiveKick(frodo));
                    break;
                case "h":
                    frodo.ACTION_CONTROLLER.setAction(new Waiting(frodo));
                    frodo.MOTION_CONTROLLER.setDestination(null);
                    frodo.MOTION_CONTROLLER.setHeading(null);
                    port.halt();
                    port.halt();
                    port.halt();
//                    frodo.PROPELLER_CONTROLLER.setActive(false);
//                    port.propeller(0);
//                    port.propeller(MOTION_CONTROLLER0);
//                    port.propeller(0);
                    break;
                case "reset":
                    frodo.ACTION_CONTROLLER.setAction(new Goto(frodo, new ConstantPoint(0,0)));
                    break;
                case "remote":
                    System.out.println(frodo.ACTION_CONTROLLER.isActive());
                    frodo.ACTION_CONTROLLER.setAction(new RemoteControl(frodo));
                    break;
                case "behave":
                    Status.fixedBehaviour = null;
                    frodo.ACTION_CONTROLLER.setAction(new Behave(frodo));
                    break;
                case "AUTO":
                    Status.fixedBehaviour = null;
                    break;
                case "safe":
                    frodo.ACTION_CONTROLLER.setAction(new GoToSafeLocation(frodo));
                    break;
                case "shunt":
                    frodo.ACTION_CONTROLLER.setAction(new ShuntKick(frodo));
                    break;
                case "demo":
                    frodo.ACTION_CONTROLLER.setAction(new Demo(frodo));
                    break;
                case "def":
                    frodo.ACTION_CONTROLLER.setAction(new DefendGoal(frodo));
                    break;
                case "annoy":
                    frodo.ACTION_CONTROLLER.setAction(null);
                    frodo.MOTION_CONTROLLER.setDestination(new InFrontOfRobot(RobotAlias.FELIX));
                    frodo.MOTION_CONTROLLER.setHeading(new RobotPoint(RobotAlias.FELIX));
                    break;
//                case "rot":
//                    frodo.PROPELLER_CONTROLLER.setActive(false);
//                    ((FredRobotPort) frodo.port).propeller(0);
//                    ((FredRobotPort) frodo.port).propeller(0);
//                    ((FredRobotPort) frodo.port).propeller(0);
//                    frodo.ACTION_CONTROLLER.setActive(false);
//                    frodo.MOTION_CONTROLLER.setDestination(new Rotate());
//                    frodo.MOTION_CONTROLLER.setHeading(new BallPoint());
//                    break;
//                case "p":
//                    boolean act = frodo.PROPELLER_CONTROLLER.isActive();
//                    frodo.PROPELLER_CONTROLLER.setActive(!act);
//                    if(!act){
//                        ((FredRobotPort) frodo.port).propeller(0);
//                        ((FredRobotPort) frodo.port).propeller(0);
//                        ((FredRobotPort) frodo.port).propeller(0);
//                    }
//                    System.out.println(frodo.PROPELLER_CONTROLLER.isActive());
//                    break;
                case "test":
                    frodo.MOTION_CONTROLLER.setHeading(new EnemyGoal());
                    frodo.MOTION_CONTROLLER.setDestination(new EnemyGoal());
                    break;
                // Robot should go to ball then stop.
                case "follow_ball":
                    frodo.ACTION_CONTROLLER.setAction(new Goto(frodo, new BallPoint()));
                    break;
                // Only if our goal is on the left-hand side of the pitch
                case "go_to_friendly_goal":
                    frodo.ACTION_CONTROLLER.setAction(new Goto(frodo, new ConstantPoint(-150, 0)));
                    break;
                // Only if our goal is on the left-hand side of the pitch
                case "go_to_opponent_goal":
                    frodo.ACTION_CONTROLLER.setAction(new Goto(frodo, new ConstantPoint(150, 0)));
                    break;
                case "move_forward":
                    frodo.ACTION_CONTROLLER.setAction(new Goto(frodo, new InFrontOfRobot(RobotType.FRIEND_2)));
                    break;
                case "turn_to_enemy_goal":
                    frodo.MOTION_CONTROLLER.setHeading(new EnemyGoal());
                    break;
            }
        }

        this.vision.terminateVision();
        System.exit(0);
    }




    @Override
    public void nextWorld(DynamicWorld dynamicWorld) {
        previous = world;
        world = dynamicWorld;
        status = new Status(world);

        if ((world.getRobots().contains(null)) || (world.getBall().equals(null))){
            /*TODO:
            For every object we want to work out (based on their previous positions)
            which object it is most likely to be.
            Do this by working out the probability that any object is any robot and then
            assign the highest probability robot to be that object*/
            //THE BALL WILL HAVE A DIFFERENT SHAPE SO WE CAN JUST UPDATE THAT FROM THE SHAPE
            for (ShapeObject obj : world.getObjects()){

            }
            //perform obj rec
            //work out which objects are null
            //calculate probabilities
            //assign point and direction to robot
        }
    }


    /**
     * SDP2017NOTE
     * This is the main() you want to run. It launches everything.
     * @param args
     */
    public static void main(String[] args) {
        new Strategy(args);
    }


    /**
     * SDP2017NOTE
     * This is the main loop of the entire strategy module. It is launched every couple of milliseconds.
     * Insert all your clever things here. You can access Strategy.world from here and control robots.
     *
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if(world != null){
            for(RobotBase robot : this.robots){
                if(world.getRobot(robot.robotType) == null){
                    // Angry

                    Toolkit.getDefaultToolkit().beep();
                }
                try{
                    // Tells all the Controllers of each robot to do what they need to do.
                    robot.perform();
                } catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    public void receivedStringHandler(String string) {

    }
}
