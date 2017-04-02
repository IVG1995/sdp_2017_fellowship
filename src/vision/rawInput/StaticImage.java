package vision.rawInput;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Objects;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.Timer;

import vision.constants.Constants;
import vision.gui.Preview;
import vision.gui.SDPConsole;

/**
 * Created by Simon Rovder
 */
public class StaticImage extends AbstractRawInput implements ActionListener {
    private Timer timer;
    private BufferedImage img;

    private JButton btnStartInput;
    private JButton btnStopInput;
    private JButton btnPauseInput;
    private JButton btnBrowse;
    private Integer breakPoint = 0;
    private boolean enableBreak = false;
    private String filePath;

    JLabel lblImageSource;
    JTextField textField;

    public static final StaticImage staticImage = new StaticImage();
    private JButton btnBreak;
    private JTextField breakFrame;

    private StaticImage() {
        super();
        this.setLayout(null);
        this.tabName = "Static Image";
        this.timer = new Timer(100, this);
        this.initGUI();
    }

    private void initGUI() {
        // TODO: Refactor This
        textField = new JTextField();
        textField.setBounds(150, 25, 311, 20);
        textField.setEnabled(false);
        this.add(textField);
        textField.setColumns(10);

        lblImageSource = new JLabel("Image source:");
        lblImageSource.setBounds(10, 28, 129, 14);
        this.add(lblImageSource);

        btnBrowse = new JButton("Browse");
        btnBrowse.setBounds(471, 24, 89, 23);
        this.add(btnBrowse);

        btnStartInput = new JButton("Start Input");
        btnStartInput.setBounds(20, 56, 150, 23);
        this.add(btnStartInput);

        btnStopInput = new JButton("Stop Input");
        btnStopInput.setBounds(180, 56, 150, 23);
        this.add(btnStopInput);

        btnPauseInput = new JButton("Pause Input");
        btnPauseInput.setBounds(20, 86, 150, 23);
        this.add(btnPauseInput);

        btnBreak = new JButton("Set breakpoint");
        btnBreak.setBounds(20, 116, 150, 23);
        this.add(btnBreak);

        breakFrame = new JTextField();
        breakFrame.setBounds(180, 116, 150, 23);
        this.add(breakFrame);

        this.btnStartInput.addActionListener(this);
        this.btnStopInput.addActionListener(this);
        this.btnBrowse.addActionListener(this);
        this.btnPauseInput.addActionListener(this);
        this.btnBreak.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.timer) {
            if (cnt > 250) {
                cnt = 1;
                System.out.println(cnt);
            } else {
                if (!pause && !(breakPoint.equals(cnt) && enableBreak)) {
                    cnt += 1;
                }
            }
            try {
                this.img = ImageIO.read(new File(filePath + String.format("/00000%1$03d.jpg", cnt)));
            } catch (Exception e1) {
                SDPConsole.message("Could not open the image. Something went wrong. Try JPG and JPEG images of size 640 by 480.", this);
            }
            this.listener.nextFrame(this.img, System.currentTimeMillis());
        } else if (e.getSource() == this.btnStartInput) {
            this.start();
        } else if (e.getSource() == this.btnStopInput) {
            this.stop();
        } else if (e.getSource() == this.btnBrowse) {
            String newFilePath = SDPConsole.chooseFolder();
            if (newFilePath != null) {
                this.filePath = newFilePath;
                this.textField.setText(this.filePath);
            }
        } else if (e.getSource() == this.btnPauseInput) {
            pause = !pause;
            if (!pause) {
                btnPauseInput.setText("Play");
            }
            else {
                btnPauseInput.setText("Pause");
            }
        }else if (e.getSource() == this.btnBreak){
            breakPoint = Integer.parseInt(this.breakFrame.getText());
            enableBreak = !enableBreak;
            if (!enableBreak) {
                btnBreak.setText("start break");
            }
            else {
                btnBreak.setText("stop break");
            }
        }
    }

    @Override
    public void stop() {
        this.timer.stop();
        this.btnBrowse.setEnabled(true);
        cnt = 1;
    }

    public static Integer cnt = 1;
    public static boolean pause = false;

    @Override
    public void start() {
        try {
            this.img = ImageIO.read(new File(filePath + String.format("/00000%1$03d.jpg", cnt)));
            if (this.img.getWidth() != Constants.INPUT_WIDTH || this.img.getHeight() != Constants.INPUT_HEIGHT) {
                SDPConsole.message("The image you tried to open is not the correct dimensions. The dimensions are supposed to be " + Constants.INPUT_WIDTH + " by " + Constants.INPUT_HEIGHT + "!", this);
                return;
            }

            this.timer.start();
            this.btnBrowse.setEnabled(false);
        } catch (Exception e) {
            SDPConsole.message("Could not open the image. Something went wrong. Try JPG and JPEG images of size 640 by 480.", this);
        }
    }


}
