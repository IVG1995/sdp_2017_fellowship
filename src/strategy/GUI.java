package strategy;

import strategy.actions.Behave;
import strategy.actions.other.DefendGoal;
import strategy.actions.other.GoToSafeLocation;
import strategy.actions.other.Goto;
import strategy.actions.offense.OffensiveKick;
import strategy.actions.other.Waiting;
import strategy.drives.FourWheelHolonomicDrive;
import strategy.drives.pid.PIDControlBase;
import strategy.drives.pid.PIDDirectionControl;
import strategy.drives.pid.PIDRotationControl;
import strategy.points.basicPoints.*;
import strategy.controllers.essentials.MotionController;
import communication.ports.robotPorts.FredRobotPort;
import strategy.robots.Fred;
import strategy.robots.Frodo;
import strategy.robots.RobotBase;
import vision.RobotAlias;
import communication.ports.interfaces.PropellerEquipedRobotPort;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

/**
 * Created by Simon Rovder
 */
public class GUI extends JFrame implements KeyListener{

    public JTextField action;
    public JTextField searchType;
    public JTextField behaviour;
    private JTextField r;
    private JTextField directionalPID;
    private JTextField rotationalPID;

    public static final GUI gui = new GUI();

    private GUI(){
        super("Strategy");
        this.setSize(640,480);
        this.setLayout(null);
        Container c = this.getContentPane();


        JLabel label = new JLabel("Action:");
        label.setBounds(20,20,200,30);
        c.add(label);

        this.action = new JTextField();
        this.action.setBounds(220,20,300,30);
        this.action.setEditable(false);
        c.add(this.action);


        label = new JLabel("NavigationInterface:");
        label.setBounds(20,60,200,30);
        c.add(label);

        this.searchType = new JTextField();
        this.searchType.setBounds(220,60,300,30);
        this.searchType.setEditable(false);
        c.add(this.searchType);


        label = new JLabel("Behavior Mode:");
        label.setBounds(20,100,200,30);
        c.add(label);

        this.behaviour = new JTextField();
        this.behaviour.setBounds(220,100,300,30);
        this.behaviour.setEditable(false);
        c.add(this.behaviour);
        this.addKeyListener(this);

        this.setVisible(true);





        label = new JLabel("Rotational PID: ");
        label.setBounds(20,140,200,30);
        c.add(label);
        this.rotationalPID = new JTextField();
        this.rotationalPID.setBounds(220,140,300,30);
        this.rotationalPID.setText("30 2 1");
        c.add(this.rotationalPID);
        this.rotationalPID.addKeyListener(this);


        label = new JLabel("Directional PID: ");
        label.setBounds(20,180,200,30);
        c.add(label);
        this.directionalPID = new JTextField();
        this.directionalPID.setBounds(220,180,300,30);
        this.directionalPID.setText("10 0 0");
        c.add(this.directionalPID);
        this.directionalPID.addKeyListener(this);


        label = new JLabel("Command box:");
        label.setBounds(20,250,200,30);
        c.add(label);
        r = new JTextField();
        r.setBounds(220,250,300,30);
        c.add(r);
        r.addKeyListener(this);

    }


    public void doesNothingButIsNecessarySoDontDelete(){}

    @Override
    public void keyTyped(KeyEvent e) {
    }


    private RobotBase robot;

    public void setRobot(RobotBase robot){
        this.robot = robot;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getSource() == this.r){
            this.robot.MOTION_CONTROLLER.setMode(MotionController.MotionMode.MOVE);
            this.robot.MOTION_CONTROLLER.setHeading(null);
            this.robot.MOTION_CONTROLLER.setDestination(null);
            this.robot.MOTION_CONTROLLER.clearObstacles();
            if(this.robot instanceof Fred){
                ((Fred)this.robot).PROPELLER_CONTROLLER.setActive(false);
                ((FredRobotPort)this.robot.port).propeller(0);
                ((FredRobotPort)this.robot.port).propeller(0);
                ((FredRobotPort)this.robot.port).propeller(0);
            }
            this.robot.port.sdpPort.commandSender("f");
            this.robot.port.sdpPort.commandSender("f");
            this.robot.port.sdpPort.commandSender("f");
            switch(e.getKeyChar()){
                case 'a':
                    this.robot.setControllersActive(true);
                    this.robot.ACTION_CONTROLLER.setAction(null);
                    this.robot.MOTION_CONTROLLER.setDestination(new InFrontOfRobot(RobotAlias.FELIX));
                    this.robot.MOTION_CONTROLLER.setHeading(new RobotPoint(RobotAlias.FELIX));
                    break;
                case 'q':
                    this.robot.setControllersActive(true);
                    this.robot.ACTION_CONTROLLER.setAction(null);
                    this.robot.MOTION_CONTROLLER.setDestination(new InFrontOfRobot(RobotAlias.JEFFREY));
                    this.robot.MOTION_CONTROLLER.setHeading(new RobotPoint(RobotAlias.JEFFREY));
                    break;
                case 'o':
                    this.robot.setControllersActive(true);
                    this.robot.ACTION_CONTROLLER.setAction(null);
                    this.robot.MOTION_CONTROLLER.setDestination(new MidPoint(new RobotPoint(RobotAlias.FELIX), new BallPoint()));
                    this.robot.MOTION_CONTROLLER.setHeading(new RobotPoint(RobotAlias.FELIX));
                    break;
                case 'p':
                    this.robot.setControllersActive(true);
                    this.robot.ACTION_CONTROLLER.setAction(null);
                    this.robot.MOTION_CONTROLLER.setDestination(new MidPoint(new RobotPoint(RobotAlias.JEFFREY), new BallPoint()));
                    this.robot.MOTION_CONTROLLER.setHeading(new RobotPoint(RobotAlias.JEFFREY));
                    break;
                case 'd':
                    this.robot.setControllersActive(true);
                    this.robot.ACTION_CONTROLLER.setAction(new DefendGoal(this.robot));
                    break;
                case 'k':
                    this.robot.setControllersActive(true);
                    this.robot.ACTION_CONTROLLER.setAction(new OffensiveKick(this.robot));
                    break;
                case 's':
                    this.robot.setControllersActive(true);
                    this.robot.ACTION_CONTROLLER.setAction(new GoToSafeLocation(this.robot));
                    break;
                case 'b':
                    this.robot.setControllersActive(true);
                    this.robot.ACTION_CONTROLLER.setAction(new Behave(this.robot));
                    break;
                case '1':
                    this.robot.ACTION_CONTROLLER.setAction(new Goto(this.robot, new ConstantPoint(-50,-50)));
                    break;
                case '2':
                    this.robot.ACTION_CONTROLLER.setAction(new Goto(this.robot, new ConstantPoint(0,-50)));
                    break;
                case '3':
                    this.robot.ACTION_CONTROLLER.setAction(new Goto(this.robot, new ConstantPoint(50,-50)));
                    break;
                case '4':
                    this.robot.ACTION_CONTROLLER.setAction(new Goto(this.robot, new ConstantPoint(-50,0)));
                    break;
                case '5':
                    this.robot.ACTION_CONTROLLER.setAction(new Goto(this.robot, new ConstantPoint(0,0)));
                    break;
                case '6':
                    this.robot.ACTION_CONTROLLER.setAction(new Goto(this.robot, new ConstantPoint(50,0)));
                    break;
                case '7':
                    this.robot.ACTION_CONTROLLER.setAction(new Goto(this.robot, new ConstantPoint(-50,50)));
                    break;
                case '8':
                    this.robot.ACTION_CONTROLLER.setAction(new Goto(this.robot, new ConstantPoint(0,50)));
                    break;
                case '9':
                    this.robot.ACTION_CONTROLLER.setAction(new Goto(this.robot, new ConstantPoint(50,50)));
                    break;
                case 'h':
                case ' ':
                    this.robot.MOTION_CONTROLLER.setMode(MotionController.MotionMode.OFF);
                    if(this.robot instanceof Fred){
                        ((Fred)this.robot).PROPELLER_CONTROLLER.setActive(false);
                        ((PropellerEquipedRobotPort) this.robot.port).propeller(0);
                        ((PropellerEquipedRobotPort) this.robot.port).propeller(0);
                        ((PropellerEquipedRobotPort) this.robot.port).propeller(0);
                    }
                    this.robot.ACTION_CONTROLLER.setAction(new Waiting(this.robot));
                    break;
            }
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(this.robot instanceof Frodo){
            FourWheelHolonomicDrive drive = (FourWheelHolonomicDrive)this.robot.drive;
            if (e.getExtendedKeyCode() == KeyEvent.VK_ENTER) {
                if(e.getSource() == this.rotationalPID){
                    System.out.println("Rotational PID change");
                    try{
                        drive.setPidRotation(parsePIDRotationControl(this.rotationalPID.getText()));
                    } catch(Exception ex){}
                    System.out.println("Rotational PID change : " + drive.getPidRotation().toString());
                } else if(e.getSource() == this.directionalPID){
                    System.out.println("Directional PID change");
                    try{
                        drive.setPidDirection(parsePIDDirectionControl(this.directionalPID.getText()));
                    } catch(Exception ex){}
                    System.out.println("Directional PID change : " + drive.getPidDirection().toString());
                }
            }

        }
        r.setText("");
    }

    private PIDRotationControl parsePIDRotationControl(String input) {
        String[] params = input.split("\\s+");
        System.out.println("Parser params[0] : " + Double.parseDouble(params[0]));
        System.out.println("Parser params[1] : " + Double.parseDouble(params[1]));
        System.out.println("Parser params[2] : " + Double.parseDouble(params[2]));
        return new PIDRotationControl(
            Double.parseDouble(params[0]),
            Double.parseDouble(params[1]),
            Double.parseDouble(params[2])
        );
    }

    private PIDDirectionControl parsePIDDirectionControl(String input) {
        String[] params = input.split("\\s+");
        System.out.println("Parser params[0] : " + Double.parseDouble(params[0]));
        System.out.println("Parser params[1] : " + Double.parseDouble(params[1]));
        System.out.println("Parser params[2] : " + Double.parseDouble(params[2]));
        return new PIDDirectionControl(
                Double.parseDouble(params[0]),
                Double.parseDouble(params[1]),
                Double.parseDouble(params[2])
        );
    }
}
