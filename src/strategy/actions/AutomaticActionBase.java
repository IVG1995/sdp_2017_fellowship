package strategy.actions;

import strategy.points.DynamicPoint;
import strategy.robots.RobotBase;
import vision.gui.SDPConsole;

/**
 * Created by Simon Rovder
 */
public abstract class AutomaticActionBase implements ActionInterface {
    protected DynamicPoint point;
    protected AutomaticActionBase action = null;
    private int successExit;
    private int failureExit;
    protected boolean continueOnExit = false;
    protected int state;
    protected String rawDescription = null;

    protected long delayedUntil = 0;

    protected final RobotBase robot;

    public AutomaticActionBase(RobotBase robot, DynamicPoint point){
        this.robot = robot;
        this.point = point;
    }

    public AutomaticActionBase(RobotBase robot){
        this.robot = robot;
        this.enterState(0);
    }

    protected void enterAction(AutomaticActionBase action, int successExit, int failureExit){
        SDPConsole.writeln("Creating action: " + action.getClass().getName());
        this.action = action;
        this.successExit = successExit;
        this.failureExit = failureExit;
    }


    @Override
    public void tik() throws ActionException{
        if(this.delayedUntil > System.currentTimeMillis()) return;
        if(this.point != null) this.point.recalculate();
        if(this.action == null){
            this.tok();
        } else {
            try{
                this.action.tik();
            } catch (ActionException e){
                if(!e.getContinueOnExit()) this.robot.port.stop();
                this.action = null;
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

    @Override
    public void delay(long millis){
        this.delayedUntil = System.currentTimeMillis() + millis;
    }
}
