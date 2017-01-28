package vision.robotAnalysis;

import vision.DynamicWorld;
import vision.distortion.DistortionListener;
import vision.spotAnalysis.SpotAnalysisBase;

import java.util.LinkedList;

/**
 * Created by Simon Rovder
 */
public abstract class RobotAnalysisBase implements DistortionListener {

    //listeners
    private LinkedList<DynamicWorldListener> listeners;

    //last known world is null be default (cause there is no world intitially)
    protected DynamicWorld lastKnownWorld = null;

    //constructor
    public RobotAnalysisBase(){
        this.listeners = new LinkedList<DynamicWorldListener>();
    }

    //add listener
    public void addDynamicWorldListener(DynamicWorldListener listener){
        this.listeners.add(listener);
    }

    //show the listeners the new world
    protected void informListeners(DynamicWorld world){

        this.lastKnownWorld = world;
        for(DynamicWorldListener listener : this.listeners){
            listener.nextDynamicWorld(world);
        }
    }
}
