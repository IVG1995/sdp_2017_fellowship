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
public abstract class SpotAnalysisBase implements RawInputListener{

    private LinkedList<NextSpotsListener> listeners;

    public SpotAnalysisBase(){
        this.listeners = new LinkedList<NextSpotsListener>();
    }

    public void addSpotListener(NextSpotsListener listener){
        this.listeners.add(listener);
    }

    protected void informListeners(ArrayList<ShapeObject> objs, long time){
        for(NextSpotsListener listener : this.listeners){
            listener.nextSpots(objs, time);
        }
    }
}
