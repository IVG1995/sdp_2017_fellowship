package vision.colorAnalysis;

import vision.constants.Constants;
import vision.gui.Preview;

import java.awt.*;
import java.util.HashMap;

/**
 * Created by Simon Rovder
 */
public class SDPColors {

    public static final SDPColors sdpColors = new SDPColors();

    public static HashMap<SDPColor, SDPColorInstance> colors;

    private SDPColors() {
        colors = new HashMap<SDPColor, SDPColorInstance>();
        colors.put(SDPColor._BALL, new SDPColorInstance(SDPColor._BALL.toString(), new Color(255, 0, 0), SDPColor._BALL, x -> true));
        colors.put(SDPColor.PINK, new SDPColorInstance(SDPColor.PINK.toString(), new Color(255, 0, 0), SDPColor.PINK, x -> true));
        colors.put(SDPColor.YELLOW, new SDPColorInstance(SDPColor.YELLOW.toString(), new Color(255, 0, 0), SDPColor.YELLOW, x -> true));
        colors.put(SDPColor.MARKER, new SDPColorInstance(SDPColor.MARKER.toString(), new Color(255, 0, 0), SDPColor.MARKER, x -> true));
        colors.put(SDPColor.BLUE, new SDPColorInstance(SDPColor.BLUE.toString(), new Color(255, 0, 0), SDPColor.BLUE, x -> true));
        colors.put(SDPColor.GREEN, new SDPColorInstance(SDPColor.GREEN.toString(), new Color(255, 0, 0), SDPColor.GREEN, x -> true));
        colors.put(SDPColor.GREEN_0, new SDPColorInstance(SDPColor.GREEN_0.toString(), new Color(255, 0, 0), SDPColor.GREEN_0, vectorGeometry -> (vectorGeometry.x >= Constants.INPUT_WIDTH / 2 && vectorGeometry.y >= Constants.INPUT_HEIGHT / 2)));
        colors.put(SDPColor.GREEN_1, new SDPColorInstance(SDPColor.GREEN_1.toString(), new Color(255, 0, 0), SDPColor.GREEN_1, vectorGeometry -> (vectorGeometry.x >= Constants.INPUT_WIDTH / 2 && vectorGeometry.y <= Constants.INPUT_HEIGHT / 2)));
        colors.put(SDPColor.GREEN_3, new SDPColorInstance(SDPColor.GREEN_3.toString(), new Color(255, 0, 0), SDPColor.GREEN_3, vectorGeometry -> (vectorGeometry.x <= Constants.INPUT_WIDTH / 2 && vectorGeometry.y <= Constants.INPUT_HEIGHT / 2)));
        colors.put(SDPColor.GREEN_2, new SDPColorInstance(SDPColor.GREEN_2.toString(), new Color(255, 0, 0), SDPColor.GREEN_2, vectorGeometry -> (vectorGeometry.x <= Constants.INPUT_WIDTH / 2 && vectorGeometry.y >= Constants.INPUT_HEIGHT / 2)));
    }

    public static Preview getActivePreview() {
        for(SDPColorInstance instance : colors.values()) {
            if(instance.isVisible()) {
                return instance.preview;
            }
        }
        return null;
    }
}
