package strategy.actions;

import strategy.GUI;
import strategy.Strategy;
import strategy.WorldTools;
import strategy.actions.other.DefendGoal;
import strategy.actions.other.GoToSafeLocation;
import strategy.actions.offense.OffensiveKick;
import strategy.actions.offense.ShuntKick;
import strategy.robots.Fred;
import strategy.robots.RobotBase;
import vision.Ball;
import vision.Robot;
import vision.RobotType;
import vision.constants.Constants;
import vision.tools.VectorGeometry;

/**
 * Created by Simon Rovder
 */
enum BehaviourEnum{
    DEFEND, SHUNT, KICK, SAFE, EMPTY
}

public class Behave extends ManualActionBase {

    private BehaviourEnum lastState = null;
    private BehaviourEnum nextBehaviour = null;

    public static boolean RESET = true;

    private final RobotBase robot;

    public Behave(RobotBase robot){
        super(robot, null);
        this.robot = robot;
    }

    @Override
    public void enterState(int newState) {
        if(newState == 0){
            if(this.robot instanceof Fred){
                ((Fred)this.robot).PROPELLER_CONTROLLER.setActive(true);
            }
        }
        this.state = newState;
    }


    @Override
    public void tok() throws ActionException {

        if((this.lastState != this.nextBehaviour || RESET) && this.nextBehaviour != null){
            RESET = false;
            this.robot.MOTION_CONTROLLER.clearObstacles();
            if(this.robot instanceof Fred) ((Fred)this.robot).PROPELLER_CONTROLLER.setActive(true);
            this.lastState = this.nextBehaviour;
            switch (this.nextBehaviour){
                case DEFEND:
                    this.enterBehaviourAction(new DefendGoal(this.robot));
                    break;
                case KICK:
                    this.enterBehaviourAction(new OffensiveKick(this.robot));
                    break;
                case SHUNT:
                    this.enterBehaviourAction(new ShuntKick(this.robot));
                    break;
                case SAFE:
                    this.enterBehaviourAction(new GoToSafeLocation(this.robot));
                    break;
            }
        }
        if(this.behaviourAction != null) this.behaviourAction.tik();
    }

    @Override
    public void tik() throws ActionException {
        Ball ball = Strategy.world.getBall();
        if(ball == null){
            this.nextBehaviour = BehaviourEnum.DEFEND;
        } else {
            Robot us = Strategy.world.getRobot(this.robot.robotType);
            if(us == null){
                // TODO: Angry yelling
            } else {
                VectorGeometry ourGoal = new VectorGeometry(-Constants.PITCH_WIDTH/2, 0);
                if(us.location.distance(ourGoal) > ball.location.distance(ourGoal)){
                    this.nextBehaviour = BehaviourEnum.SAFE;
                } else {
                    if(Math.abs(ball.location.x) > Constants.PITCH_WIDTH/2 - 20 && Math.abs(ball.location.y) > Constants.PITCH_HEIGHT/2 - 20){
                        this.nextBehaviour = BehaviourEnum.SHUNT;
                    } else {
                        boolean canKick = true;
                        for(Robot r : Strategy.world.getRobots()){
                            if(r != null && r.type != RobotType.FRIEND_2 && r.velocity.length() < 1) canKick = canKick && r.location.distance(ball.location) > 50;
                        }
                        canKick = canKick && !WorldTools.isPointInEnemyDefenceArea(ball.location);
                        if(canKick && (this.lastState != BehaviourEnum.DEFEND || VectorGeometry.angle(ball.velocity, VectorGeometry.fromTo(ball.location, new VectorGeometry(-Constants.PITCH_WIDTH/2, 0))) > 2)){
                            this.nextBehaviour = BehaviourEnum.KICK;
                        } else {
                            this.nextBehaviour = BehaviourEnum.DEFEND;
                        }
                    }
                }
            }
        }
        GUI.gui.behaviour.setText(this.lastState != null ? this.lastState.toString() : "");
        try{
            this.tok();
        } catch (ActionException ex){
            this.behaviourAction = null;
        }
    }

    @Override
    public void delay(long millis) {

    }
}
