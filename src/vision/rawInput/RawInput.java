package vision.rawInput;

import vision.constants.Constants;
import vision.gui.Preview;
import vision.gui.SDPConsole;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
/**
 * Created by Simon Rovder
 */
 //displays the raw input
public class RawInput extends JPanel{

	//tab pane
	private JTabbedPane tabbedPane;

	//this will be the raw image
	public BufferedImage lastImage;

	//get the raw inputs
	private AbstractRawInput[] rawInputs = {
		LiveCameraInput.liveCameraInput,
		StaticImage.staticImage
	};

	//more listeners...
	private LinkedList<RawInputListener> imageListeners;

	//raw input
	public static final RawInput rawInputMultiplexer = new RawInput();

	//constructor
	private RawInput(){

		super();
		this.setLayout(new BorderLayout(0, 0));

		this.tabbedPane     = new JTabbedPane(JTabbedPane.TOP);
		this.imageListeners = new LinkedList<RawInputListener>();

		this.add(this.tabbedPane);

		//add a tab for every raw input?
		for(AbstractRawInput rawInput : this.rawInputs){
			rawInput.setInputListener(this);
			this.tabbedPane.addTab(rawInput.getTabName(), null, rawInput, null);
		}
	}

	//lets add some listeners cause we can never have enough of those
	public static void addRawInputListener(RawInputListener ril){
		RawInput.rawInputMultiplexer.imageListeners.add(ril);
	}

	//get the next frame
	public void nextFrame(BufferedImage image, long time){
		this.lastImage = image;
		for(RawInputListener ril : this.imageListeners){
			ril.nextFrame(image, time);
		}
	}

	//THERE WILL BE NO INPUTS!
	public void stopAllInputs(){
		for(RawInputInterface input : this.rawInputs){
			input.stop();
		}
	}

	//set video port
	public void setVideoChannel(int port){
		((LiveCameraInput)(this.rawInputs[0])).setVideoChannel(port);
	}

	//lets start a twitch stream
	public void streamVideo(){
		this.rawInputs[0].start();
	}


}
