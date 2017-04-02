package vision.colorAnalysis;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import vision.*;
import vision.robotAnalysis.RobotPreview;

/**
 * Created by Simon Rovder
 */
public class ColorCalibration extends JPanel implements ActionListener{

	private List list;
	
	
	public static final ColorCalibration colorCalibration = new ColorCalibration();
	
	private ColorCalibration() {
		super();
		this.setLayout(null);
		list = new List();
		list.setBounds(10, 10, 273, 350);
		this.add(list);
		
		JButton btnCalibrate = new JButton("Calibrate");
		btnCalibrate.setBounds(289, 10, 222, 33);
		btnCalibrate.addActionListener(this);
		this.add(btnCalibrate);
		Dimension d = RobotPreview.preview.getSize();
		JLabel robotPreviewLabel = new JLabel("Robot preview");
		robotPreviewLabel.setBounds(289, 60, 200, 30);
		this.add(robotPreviewLabel);
		RobotPreview.preview.setBounds(289, 90, (int) (d.getWidth() + 0.5), (int) (d.getHeight() + 0.5));
		this.add(RobotPreview.preview);
		for(SDPColorInstance c : SDPColors.colors.values()){
			this.list.add(c.name);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		String selected = this.list.getSelectedItem();
		if(selected != null){
			SDPColors.colors.get(SDPColor.valueOf(selected)).setVisible(true);
		}
	}	
}
