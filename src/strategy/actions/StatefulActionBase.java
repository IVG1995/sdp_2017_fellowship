package strategy.actions;

import strategy.points.DynamicPoint;
import strategy.robots.RobotBase;

import java.util.HashSet;
import java.util.LinkedList;

/**
 * Created by Simon Rovder
 */
public abstract class StatefulActionBase<A> extends ActionBase {
    protected A lastState;
    protected A nextState;

    public StatefulActionBase(RobotBase robot, DynamicPoint point) {
        super(robot, point);
        this.equivalenceSets = new LinkedList<>();
    }

    private LinkedList<HashSet<A>> equivalenceSets;

    protected void addEquivalence(A a, A b){
        for(HashSet<A> set : this.equivalenceSets){
            if(set.contains(a) || set.contains(b)){
                set.add(a);
                set.add(b);
                return;
            }
        }
        HashSet<A> newSet = new HashSet<A>();
        newSet.add(a);
        newSet.add(b);
        this.equivalenceSets.add(newSet);
    }



    protected boolean checkEquivalent(A a, A b){
        if(a == b) return true;
        for(HashSet<A> set : this.equivalenceSets){
            if(set.contains(a) && set.contains(b)) return true;
        }
        return false;
    }

    protected abstract A getState();

    @Override
    public void tik() throws ActionException {
        A current = this.getState();
        if(checkEquivalent(current, this.lastState)){
            if(this.action != null) super.tik();
            return;
        }
        this.tok();
        this.lastState = this.nextState;
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
}