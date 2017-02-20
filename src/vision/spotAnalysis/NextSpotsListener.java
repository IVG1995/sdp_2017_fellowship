package vision.spotAnalysis;

import java.util.ArrayList;
import java.util.HashMap;

import vision.colorAnalysis.SDPColor;
import vision.shapeObject.ShapeObject;
import vision.spotAnalysis.approximatedSpotAnalysis.Spot;
/**
 * Created by Simon Rovder
 */
public interface NextSpotsListener {
	void nextSpots(ArrayList<ShapeObject> objects, long time);
}
