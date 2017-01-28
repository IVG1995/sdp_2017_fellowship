package vision.robotAnalysis;

import javax.swing.JComboBox;

import vision.colorAnalysis.SDPColor;
import vision.colorAnalysis.SDPColorInstance;
/**
 * Created by Simon Rovder
 */
 //just a colour selector box for the robots on the GUI
public class SDPColorSelector extends JComboBox<String>{

	//constructor
	public SDPColorSelector(){
		super();
		for(SDPColor c : SDPColor.values()){
			this.addItem(c.toString());
		}
	}

	//get the value selected for the colour
	public SDPColor getSelectedSDPColorInstance(){
		return SDPColor.valueOf((String)this.getSelectedItem());
	}

	//set the colour chosen
	public void setSelectedSDPColorInstance(SDPColorInstance instance){
		this.setSelectedItem(instance.name);
	}
}
