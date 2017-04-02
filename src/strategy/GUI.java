package strategy;

import strategy.actions.Behave;
import strategy.actions.MainOffense;
import strategy.actions.defence.Annoy;
import strategy.actions.defence.BlockPass;
import strategy.actions.offense.OffensiveKick;
import strategy.actions.offense.WallKick;
import strategy.actions.other.DefendGoal;
import strategy.actions.other.GoToSafeLocation;
import strategy.actions.other.Goto;
import strategy.actions.other.Waiting;
import strategy.controllers.essentials.MotionController;
import strategy.drives.FourWheelHolonomicDrive;
import strategy.drives.pid.PIDDirectionControl;
import strategy.drives.pid.PIDRotationControl;
import strategy.points.basicPoints.BallPoint;
import strategy.points.basicPoints.ConstantPoint;
import strategy.robots.Frodo;
import strategy.robots.RobotBase;
import vision.constants.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

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
    private JTextField aimPID;
    private JTextField distanceToKicker;
    private JTextField kickingAngle;

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
        this.rotationalPID.setText(new FourWheelHolonomicDrive().getPidRotation().toString());
        c.add(this.rotationalPID);
        this.rotationalPID.addKeyListener(this);


        label = new JLabel("Directional PID: ");
        label.setBounds(20,180,200,30);
        c.add(label);
        this.directionalPID = new JTextField();
        this.directionalPID.setBounds(220,180,300,30);
        this.directionalPID.setText(new FourWheelHolonomicDrive().getPidDirection().toString());
        c.add(this.directionalPID);
        this.directionalPID.addKeyListener(this);

        label = new JLabel("Aim PID: ");
        label.setBounds(20,220,200,30);
        c.add(label);
        this.aimPID = new JTextField();
        this.aimPID.setBounds(220,220,300,30);
        this.aimPID.setText(new FourWheelHolonomicDrive().getPidAim().toString());
        c.add(this.aimPID);
        this.aimPID.addKeyListener(this);

        label = new JLabel("Kicking Distance: ");
        label.setBounds(20,260,200,30);
        c.add(label);
        this.distanceToKicker = new JTextField();
        this.distanceToKicker.setBounds(220,260,300,30);
        this.distanceToKicker.setText(Integer.toString(Constants.distanceToKicker));
        c.add(this.distanceToKicker);
        this.distanceToKicker.addKeyListener(this);

        label = new JLabel("Kicking Angle Tolerance: ");
        label.setBounds(20,300,200,30);
        c.add(label);
        this.kickingAngle = new JTextField();
        this.kickingAngle.setBounds(220,300,300,30);
        this.kickingAngle.setText(Double.toString(Constants.kickingAngleTolerance * 180 / Math.PI));
        c.add(this.kickingAngle);
        this.kickingAngle.addKeyListener(this);


        label = new JLabel("Command box:");
        label.setBounds(20,340,200,30);
        c.add(label);
        r = new JTextField();
        r.setBounds(220,340,300,30);
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
            this.robot.port.sdpPort.commandSender("f");
            this.robot.port.sdpPort.commandSender("f");
            this.robot.port.sdpPort.commandSender("f");
            switch(e.getKeyChar()){
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
                    this.robot.ACTION_CONTROLLER.setAction(new Waiting(this.robot));
                    this.robot.port.halt();
                    this.robot.port.stop();
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
                case 'o':
                    this.robot.setControllersActive(true);
                    this.robot.ACTION_CONTROLLER.setAction(new MainOffense(this.robot));
                    break;
                case 'g':
                    this.robot.setControllersActive(true);
                    this.robot.ACTION_CONTROLLER.setAction(new Goto(this.robot, new BallPoint()));
                    break;
                case 'a':
                    this.robot.setControllersActive(true);
                    this.robot.ACTION_CONTROLLER.setAction(new Annoy(this.robot));
                    break;
                case 'p':
                    this.robot.setControllersActive(true);
                    this.robot.ACTION_CONTROLLER.setAction(new BlockPass(this.robot));
                    break;
                case 'w':
                    this.robot.setControllersActive(true);
                    this.robot.ACTION_CONTROLLER.setAction(new WallKick(this.robot));
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
                    } catch(Exception ex){
                        ex.printStackTrace();
                    }
                    System.out.println("Directional PID change : " + drive.getPidDirection().toString());
                } else if(e.getSource() == this.aimPID){
                    System.out.println("Aim PID change");
                    try{
                        drive.setPidAim(parsePIDRotationControl((this.aimPID.getText())));
                    } catch(Exception ex){}
                    System.out.println("Aim PID change : " + drive.getPidAim().toString());
                } else if (e.getSource() == this.distanceToKicker){
                    System.out.println("Kicking Distance change");
                    Constants.distanceToKicker = Integer.parseInt(this.distanceToKicker.getText());
                } else if (e.getSource() == this.kickingAngle){
                    System.out.println("Kicking Angle change");
                    Constants.kickingAngleTolerance = Double.parseDouble(this.kickingAngle.getText()) * Math.PI / 180;
                }
            }

        }
        r.setText("");
    }

    private PIDRotationControl parsePIDRotationControl(String input) {
        String[] params = input.split("\\s+");
        return new PIDRotationControl(
            Double.parseDouble(params[0]),
            Double.parseDouble(params[1]),
            Double.parseDouble(params[2])
        );
    }

    private PIDDirectionControl parsePIDDirectionControl(String input) {
        String[] params = input.split("\\s+");
        return new PIDDirectionControl(
                Double.parseDouble(params[0]),
                Double.parseDouble(params[1]),
                Double.parseDouble(params[2])
        );
    }
}
