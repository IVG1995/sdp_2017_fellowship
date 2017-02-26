package vision.spotAnalysis;

import vision.colorAnalysis.SDPColor;
import vision.rawInput.RawInputListener;
import vision.shapeObject.ShapeObject;
import vision.spotAnalysis.approximatedSpotAnalysis.Spot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;

/**
 * Created by Simon Rovder
 */
 //base class for spot analysis (kinda like the robot one)
public abstract class SpotAnalysisBase implements RawInputListener{

    //spot listeners
    private LinkedList<NextSpotsListener> listeners;

    //constructor
    public SpotAnalysisBase(){
        this.listeners = new LinkedList<NextSpotsListener>();
    }

    //add the listeners
    public void addSpotListener(NextSpotsListener listener){
        this.listeners.add(listener);
    }

    //update listeners
    protected void informListeners(ArrayList<ShapeObject> objs, long time){
        for(NextSpotsListener listener : this.listeners){
            listener.nextSpots(objs, time);
        }
    }
}
