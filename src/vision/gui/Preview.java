package vision.gui;

import vision.constants.Constants;
import vision.rawInput.RawInputListener;
import vision.tools.ColoredPoint;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Simon Rovder
 */
public class Preview extends JPanel implements RawInputListener {

    private ArrayList<PreviewSelectionListener> listeners;
    private BufferedImage drawnImage;
    private BufferedImage originalImage;

    public boolean visible = false;

    public Preview() {
        super();

        this.listeners = new ArrayList<PreviewSelectionListener>();

        this.setSize(Constants.INPUT_WIDTH, Constants.INPUT_HEIGHT);
        this.setBorder(BorderFactory.createLineBorder(Color.black));

        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selection(e.getX(), e.getY());
            }

            @Override
            public void mouseEntered(MouseEvent arg0) {
            }

            @Override
            public void mouseExited(MouseEvent arg0) {
            }

            @Override
            public void mousePressed(MouseEvent arg0) {
            }

            @Override
            public void mouseReleased(MouseEvent arg0) {
            }
        });
    }

    public void addSelectionListener(PreviewSelectionListener listener) {
        listeners.add(listener);
    }

//    public static Mat bufferedImageToMat(BufferedImage bi) {
//        Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CV_8UC3);
//        byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
//        mat.put(0, 0, data);
//        return mat;
//    }

    private BufferedImage createImageFromBytes(byte[] imageData) {
        ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
        try {
            return ImageIO.read(bais);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void nextFrame(BufferedImage bi, long time) {
        originalImage = bi;
//        Preview.preview.drawnImage = deepCopy(bi);

        //opencv_video.BackgroundSubtractorMOG2 b = opencv_video.createBackgroundSubtractorMOG2(0,30,false);
//		byte[] pixels = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
//		Mat img = new Mat(Constants.INPUT_WIDTH, Constants.INPUT_HEIGHT, CV_8UC1, new BytePointer(pixels));
//		//opencv_video.BackgroundSubtractorMOG2 sub = createBackgroundSubtractorMOG2(0, 30, false);
//		b.apply(img,img);
        drawnImage = bi;
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(drawnImage, 0, 0, null);
    }

    public Graphics getImageGraphics() {
        if (drawnImage == null) {
            return null;
        }
        return drawnImage.getGraphics();
    }

    private void selection(int x, int y) {

        if (originalImage == null) return;

        ColoredPoint cp = new ColoredPoint(x, y, new Color(originalImage.getRGB(x, y)));
        for (PreviewSelectionListener psl : listeners) {
            psl.previewClickHandler(cp);
        }
    }

    static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }
}
