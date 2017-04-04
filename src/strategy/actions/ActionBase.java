package strategy.actions;

import strategy.GUI;
import strategy.Strategy;
import strategy.drives.FourWheelHolonomicDrive;
import strategy.points.DynamicPoint;
import strategy.robots.RobotBase;
import vision.gui.SDPConsole;

import java.util.FormatFlagsConversionMismatchException;

/**
 * Created by Simon Rovder
 */
public abstract class ActionBase implements ActionInterface {
    protected DynamicPoint point; // Because most actions need one
    protected ActionBase action = null; // The sub action of the action

    /**
     * When a subaction finishes and throws the ActionException, these variables decide which state to continue
     * executing the current action in. (enterState() will be called automatically by tik() )
     */
    private int successExit, failureExit;

    /**
     * The current state
     */
    protected int state;

    /**
     * The description returned by description(). If null, returns the class name
     */
    protected String rawDescription = null;

    /**
     * This is the time at which the action should start executing again. (Set automatically by delay() )
     */
    private long delayedUntil = 0;

    /**
     * The robot that is performing this action.
     */
    protected final RobotBase robot;

    public ActionBase(RobotBase robot, DynamicPoint point){
        if (robot.drive instanceof FourWheelHolonomicDrive) {
            ((FourWheelHolonomicDrive) robot.drive).resetHistory();
        }
        this.robot = robot;
        this.point = point;
        this.enterState(0);

        // Set Strategy GUI text-field to show this action
        GUI.gui.action.setText(this.description());
    }

    public ActionBase(RobotBase robot){
        this(robot, null);
    }

    protected void enterAction(ActionBase action, int successExit, int failureExit){
        SDPConsole.writeln("Creating action: " + action.getClass().getName());
        this.action = action;
        this.successExit = successExit;
        this.failureExit = failureExit;
    }


    @Override
    public void tik() throws ActionException{
        // Check the delay
        if(this.delayedUntil > System.currentTimeMillis()) return;

        // If there is no active subaction, perform this action.
        if(this.action == null){
            this.tok();

        // If there is an active subaction, execute that and
        } else {
            // Recalculate the relevant point, if applicable
            if (this.point != null) this.point.recalculate();
            if (this.action.point != null) this.action.point.recalculate();

            try{
                // Activate the current subaction's tik() method (nothing else to do)
                this.action.tik();
            } catch (ActionException e){

                // If this section is reached, it means the subaction has terminated, so we erase the subaction
                this.action = null;

                // If the subaction is requesting a stop, stop the robot.
                if(!e.getContinueOnExit()) this.robot.port.stop();

                // Enter the appropriate state
                if(e.getSuccess()){
                    this.enterState(this.successExit);
                } else {
                    this.enterState(this.failureExit);
                }
            }
        }
    }

    @Override
    public String description() {
        String description = this.rawDescription;
        if(description == null){
            description = this.getClass().getName();
        }
        if(this.action != null) description = description + this.action.description();
        return description;
    }


    /**
     * Delays the action.
     * @param millis Amount of milliseconds to delay by
     */
    @Override
    public void delay(long millis){
        this.delayedUntil = System.currentTimeMillis() + millis;
    }
}
