package vision.distortion;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;

import javax.swing.*;

import vision.constants.Constants;
/**
 * Created by Simon Rovder
 */
public class DistortionPreview extends JPanel {

	public static final DistortionPreview preview = new DistortionPreview();

	public boolean visible = false;
	
	private LinkedList<DistortionPreviewClickListener> listeners;
	
	public void clickHandler(int x, int y){
		for(DistortionPreviewClickListener l : this.listeners){
			l.distortionPreviewClickHandler(x, y);
		}
	}
	
	public static void addDistortionPreviewClickListener(DistortionPreviewClickListener listener){
		DistortionPreview.preview.listeners.add(listener);
	}
	
	private DistortionPreview(){
		super();
		this.setSize(Constants.INPUT_WIDTH, Constants.INPUT_HEIGHT);
		this.listeners = new LinkedList<DistortionPreviewClickListener>();
		this.setBorder(BorderFactory.createLineBorder(Color.black));
		this.addMouseListener(new MouseListener() {
	        @Override
	        public void mouseClicked(MouseEvent e) {
	            DistortionPreview.preview.clickHandler(e.getX(), e.getY());
	        }

			@Override
			public void mouseEntered(MouseEvent arg0) {}

			@Override
			public void mouseExited(MouseEvent arg0) {}

			@Override
			public void mousePressed(MouseEvent arg0) {}

			@Override
			public void mouseReleased(MouseEvent arg0) {}
	    });
	}
}
