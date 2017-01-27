package vision.distortion;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JLabel;

import vision.constants.Constants;
/**
 * Created by Simon Rovder
 */
 //distortion gui preview things
public class DistortionPreview extends JFrame {

	//its a label
	public final JLabel previewLabel;

	public static final DistortionPreview preview = new DistortionPreview();

	//list for listeners
	private LinkedList<DistortionPreviewClickListener> listeners;

	//handles mpouse clicks
	public void clickHandler(int x, int y){
		for(DistortionPreviewClickListener l : this.listeners){
			l.distortionPreviewClickHandler(x, y);
		}
	}

	//add a listener for distortion previews (most of these methods just do what they say)
	public static void addDistortionPreviewClickListener(DistortionPreviewClickListener listener){
		DistortionPreview.preview.listeners.add(listener);
	}

	//constructor
	private DistortionPreview(){
		//mostly just gui setup
		super("Distortion Preview");
		this.setSize(Constants.INPUT_WIDTH, Constants.INPUT_HEIGHT + 20);
		this.setResizable(false);
		this.listeners = new LinkedList<DistortionPreviewClickListener>();
		this.previewLabel = new JLabel();
		this.getContentPane().add(this.previewLabel);
		this.setVisible(false);
		//mouse listener methods
		this.previewLabel.addMouseListener(new MouseListener() {
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
