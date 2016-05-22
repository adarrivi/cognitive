package io.adarrivi.cognitive;


import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.processing.edges.CannyEdgeDetector;
import rx.Observable;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.TimeUnit;


public class MyWebcamExample {
    private ImagePanel imagePanel;
    private JFrame otherJframe;

    public static void main(String[] args) throws InterruptedException {
        new MyWebcamExample();
    }

    private WebcamObservable webcamObservable;

    public MyWebcamExample() {

        Webcam webcam = Webcam.getDefault();
        webcam.setViewSize(WebcamResolution.VGA.getSize());

        WebcamPanel panel = new WebcamPanel(webcam);
        panel.setFPSDisplayed(true);
        panel.setDisplayDebugInfo(true);
        panel.setImageSizeDisplayed(true);
        panel.setMirrored(true);
        JFrame window = new JFrame("Test webcam panel");
        window.add(panel);

        otherJframe = new JFrame("Other Jframe");

        imagePanel = new ImagePanel(webcam.getImage());
        window.add(imagePanel);
        webcamObservable = new WebcamObservable(webcam);

        Observable.create(webcamObservable)
                .sample(100, TimeUnit.MILLISECONDS)
                .subscribe(imageBuffer -> {
                    try {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(imageBuffer, "png", baos);
                        //black and white image
                        FImage fImage = ImageUtilities.readF(new ByteArrayInputStream(baos.toByteArray()));
                        fImage.processInplace(new CannyEdgeDetector(0.6f));
                        fImage.flipX();
                        BufferedImage bufferedImage = ImageUtilities.createBufferedImage(fImage);
                        imagePanel.setImage(bufferedImage);
                    } catch (Exception e) {

                    }
                });

        window.setLayout(new GridLayout(1, 2));
        window.setResizable(true);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.pack();
        window.setVisible(true);

        otherJframe.setResizable(true);

        otherJframe.setResizable(true);
        otherJframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        otherJframe.pack();
        otherJframe.setVisible(true);
    }
}
