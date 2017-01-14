package strategy;

import strategy.actions.Behave;
import strategy.actions.other.*;
import strategy.actions.offense.OffensiveKick;
import strategy.actions.offense.ShuntKick;
import communication.ports.robotPorts.FredRobotPort;
import strategy.points.basicPoints.ConstantPoint;
import strategy.points.basicPoints.EnemyGoal;
import strategy.points.basicPoints.MidFoePoint;
import strategy.robots.Fred;
import communication.PortListener;
import strategy.robots.RobotBase;
import vision.DynamicWorld;
import vision.RobotType;
import vision.Vision;
import vision.VisionListener;

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


    /**
     * SDP2017NOTE
     * The following variable is a static variable always containing the very last known state of the world.
     * It is accessible from anywhere in the project at any time as Strategy.world
     */
    public static DynamicWorld world = null;

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
        this.robots = new RobotBase [] {new Fred(RobotType.FRIEND_2)};

        Fred fred = (Fred) this.robots[0];
        FredRobotPort port = (FredRobotPort) fred.port;

        final Strategy semiStrategy = this;
        semiStrategy.vision = new Vision(args);
        semiStrategy.vision.addVisionListener(semiStrategy);


//        fred.PROPELLER_CONTROLLER.setActive(false);

        this.action = "";
        GUI.gui.doesNothingButIsNecessarySoDontDelete();
        GUI.gui.setRobot(fred);
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
                fred.PROPELLER_CONTROLLER.setActive(false);
                port.propeller(0);
                port.propeller(0);
                port.propeller(0);
                break;
            }
            switch(this.action){
                case "stop":
                    fred.ACTION_CONTROLLER.setAction(new Stop(fred));
                    break;
                case "?":
                    fred.ACTION_CONTROLLER.printDescription();
                    break;
                case "hold":
                    fred.ACTION_CONTROLLER.setAction(new HoldPosition(fred, new MidFoePoint()));
                    break;
                case "kick":
                    fred.ACTION_CONTROLLER.setAction(new OffensiveKick(fred));
                    break;
                case "h":
                    fred.ACTION_CONTROLLER.setAction(new Waiting(fred));
                    fred.MOTION_CONTROLLER.setDestination(null);
                    fred.MOTION_CONTROLLER.setHeading(null);
                    port.halt();
                    port.halt();
                    port.halt();
                    fred.PROPELLER_CONTROLLER.setActive(false);
                    port.propeller(0);
                    port.propeller(0);
                    port.propeller(0);
                    break;
                case "reset":
                    fred.ACTION_CONTROLLER.setAction(new Goto(fred, new ConstantPoint(0,0)));
                    break;
                case "remote":
                    fred.ACTION_CONTROLLER.setAction(new RemoteControl(fred));
                    break;
                case "behave":
                    Status.fixedBehaviour = null;
                    fred.ACTION_CONTROLLER.setAction(new Behave(fred));
                    break;
                case "AUTO":
                    Status.fixedBehaviour = null;
                    break;
                case "safe":
                    fred.ACTION_CONTROLLER.setAction(new GoToSafeLocation(fred));
                    break;
                case "shunt":
                    fred.ACTION_CONTROLLER.setAction(new ShuntKick(fred));
                    break;
                case "def":
                    fred.ACTION_CONTROLLER.setAction(new DefendGoal(fred));
                    break;
                case "p":
                    System.out.println(fred.MOTION_CONTROLLER.mode);
                    System.out.println(fred.PROPELLER_CONTROLLER.isActive());
                    break;
                case "test":
                    fred.MOTION_CONTROLLER.setHeading(new EnemyGoal());
                    fred.MOTION_CONTROLLER.setDestination(new EnemyGoal());
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
                    // Angry yelling.
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
