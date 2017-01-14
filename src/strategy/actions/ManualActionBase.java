package strategy.actions;

import strategy.points.DynamicPoint;
import strategy.robots.RobotBase;
import vision.gui.SDPConsole;

import java.util.HashSet;
import java.util.LinkedList;

/**
 * Created by Simon Rovder
 */
abstract class ManualActionBase<A> extends AutomaticActionBase{
    protected AutomaticActionBase behaviourAction;
    protected A lastState;

    public ManualActionBase(RobotBase robot, DynamicPoint point) {
        super(robot, point);
        this.ballStateEquivalenceSets = new LinkedList<HashSet<A>>();
    }

    protected void enterBehaviourAction(AutomaticActionBase action){
        SDPConsole.writeln("Entering top level action: " + action.getClass().getName());
        this.behaviourAction = action;
    }

    private LinkedList<HashSet<A>> ballStateEquivalenceSets;

    protected void addEquivalence(A a, A b){
        for(HashSet<A> set : this.ballStateEquivalenceSets){
            if(set.contains(a) || set.contains(b)){
                set.add(a);
                set.add(b);
                return;
            }
        }
        HashSet<A> newSet = new HashSet<A>();
        newSet.add(a);
        newSet.add(b);
        this.ballStateEquivalenceSets.add(newSet);
    }



    protected boolean equivalentBallStates(A a, A b){
        if(a == b) return true;
        for(HashSet<A> set : this.ballStateEquivalenceSets){
            if(set.contains(a) && set.contains(b)) return true;
        }
        return false;
    }



    protected boolean behaviourTik() throws ActionException {
//        if(equivalentBallStates(Strategy.status.ballState, this.lastBallState) && this.behaviourAction != null){
//            this.behaviourAction.tik();
//            this.lastBallState = Strategy.status.ballState;
//            return true;
//        }
        return false;
    }

    @Override
    public String description() {
        String description = this.rawDescription;
        if(description == null){
            description = this.getClass().getName();
        }
        if(this.behaviourAction != null) description = description + this.behaviourAction.description();
        return description;
    }
}