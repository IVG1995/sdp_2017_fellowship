package vision.colorAnalysis;

import java.awt.Color;
import java.util.HashMap;
/**
 * Created by Simon Rovder
 */
public class SDPColors {

	//call
	public static final SDPColors sdpColors = new SDPColors();

	//hashmap of colours and instances
	public static HashMap<SDPColor, SDPColorInstance> colors;

	//constructor
	private SDPColors(){
		colors = new HashMap<SDPColor, SDPColorInstance>();
		//for every colour enum we want to give it its own instance
		for(SDPColor c : SDPColor.values()){
			colors.put(c,  new SDPColorInstance(c.toString(), new Color(255,0,0), c));
		}
	}
}
