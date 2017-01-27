package vision.colorAnalysis;

import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
/**
 * Created by Simon Rovder
 */
 //GUI part of colorAnalysis
public class ColorCalibration extends JPanel implements ActionListener{

	//list will be used to display all the colours found
	private List list;

	//does things
	public static final ColorCalibration colorCalibration = new ColorCalibration();

	//constructor
	private ColorCalibration() {
		super();
		this.setLayout(null);
		list = new List();
		list.setBounds(10, 10, 273, 350);
		this.add(list);

		//button for callibration with listener what fun
		JButton btnCalibrate = new JButton("Calibrate");
		btnCalibrate.setBounds(289, 10, 222, 33);
		btnCalibrate.addActionListener(this);
		this.add(btnCalibrate);
		for(SDPColorInstance c : SDPColors.colors.values()){
			this.list.add(c.name);
		}
	}


	//if the button is pressed then calibrate
	@Override
	public void actionPerformed(ActionEvent arg0) {
		String selected = this.list.getSelectedItem();
		if(selected != null){
			SDPColors.colors.get(SDPColor.valueOf(selected)).setVisible(true);
			SDPColors.colors.get(SDPColor.valueOf(selected)).transferFocus();
		}
	}
}
