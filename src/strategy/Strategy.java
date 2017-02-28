package strategy;

import communication.PortListener;
import communication.ports.robotPorts.FrodoRobotPort;
import strategy.actions.Behave;
import strategy.actions.NoGrabber;
import strategy.actions.offense.BallGrab;
import strategy.actions.offense.OffensiveKick;
import strategy.actions.offense.ShuntKick;
import strategy.actions.other.*;
import strategy.controllers.essentials.MotionController;
import strategy.points.basicPoints.*;
import strategy.robots.Frodo;
import strategy.robots.RobotBase;
import vision.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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

    /** In milliseconds */
    public static final int cycleTime = 50;

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


        Frodo frodo = (Frodo) this.robots[0];
        FrodoRobotPort port = (FrodoRobotPort) frodo.port;

        final Strategy semiStrategy = this;
        semiStrategy.vision = new Vision(args);
        semiStrategy.vision.addVisionListener(semiStrategy);

        this.action = "";
        GUI.gui.doesNothingButIsNecessarySoDontDelete();
        GUI.gui.setRobot(frodo);
        this.timer = new Timer(cycleTime, this);
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
                    frodo.ACTION_CONTROLLER.setAction(new OffensiveKick(frodo));
                    break;
                case "go_kick":
                    frodo.ACTION_CONTROLLER.setAction(new Goto(frodo, new KickablePoint()));
                    break;
                case "h":
                    frodo.ACTION_CONTROLLER.setAction(new Waiting(frodo));
                    frodo.MOTION_CONTROLLER.setDestination(null);
                    frodo.MOTION_CONTROLLER.setHeading(null);
                    frodo.KICKER_CONTROLLER.setWantToKick(false);
                    port.halt();
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
                case "go_to_kickable":
                    frodo.ACTION_CONTROLLER.setAction(new Goto(frodo, new GrabbablePoint(RobotType.FRIEND_2)));
                    break;
                case "send_kick_command":
                    frodo.port.sdpPort.commandSender("kick");
                    break;
                case "grab_and_release":
                    frodo.port.sdpPort.commandSender("grab");
                    frodo.port.sdpPort.commandSender("ungrab");
                    break;
                case "rotate_left":
                    frodo.port.sdpPort.commandSender("r", -200, 200, -200, 200);
                    break;
                case "rotate_right":
                    frodo.port.sdpPort.commandSender("r", 200, -200, 200, -200);
                    break;
                case "spin_front_wheel_up":
                    frodo.port.sdpPort.commandSender("r", -40, 0, 0, 0);
                    break;
                case "spin_back_wheel_down":
                    frodo.port.sdpPort.commandSender("r", 0, 40, 0, 0);
                    break;
                case "spin_left_wheel_down":
                    frodo.port.sdpPort.commandSender("r", 0, 0, -40, 0);
                    break;
                case "spin_right_wheel_up":
                    frodo.port.sdpPort.commandSender("r", 0, 0, 0, 40);
                    break;
                case "forward_command":
                    frodo.port.sdpPort.commandSender("r", 0, 0, 200, 200);
                    break;
                case "no_grabber":
                    frodo.ACTION_CONTROLLER.setAction(new NoGrabber(frodo));
                    break;
            }
        }

        this.vision.terminateVision();
        System.exit(0);
    }




    @Override
    public void nextWorld(DynamicWorld dynamicWorld) {
		world = dynamicWorld;
		status = new Status(world);
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
