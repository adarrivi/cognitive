package io.adarrivi.cognitive;


import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import org.openimaj.feature.local.list.LocalFeatureList;
import org.openimaj.feature.local.matcher.FastBasicKeypointMatcher;
import org.openimaj.feature.local.matcher.LocalFeatureMatcher;
import org.openimaj.feature.local.matcher.consistent.ConsistentLocalFeatureMatcher2d;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.feature.local.engine.DoGSIFTEngine;
import org.openimaj.image.feature.local.keypoints.Keypoint;
import org.openimaj.image.processing.edges.CannyEdgeDetector;
import org.openimaj.image.typography.hershey.HersheyFont;
import org.openimaj.math.geometry.transforms.estimation.RobustAffineTransformEstimator;
import org.openimaj.math.model.fit.RANSAC;
import org.openimaj.util.pair.Pair;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;


public class MyWebcamExample {

    public static void main(String[] args) throws InterruptedException {
        new MyWebcamExample();
    }

    private WebcamObservable webcamObservable;

    private MyWebcamExample() {

        Webcam webcam = Webcam.getDefault();
        webcam.setViewSize(WebcamResolution.VGA.getSize());

        WebcamPanel panel = new WebcamPanel(webcam);
        panel.setFPSDisplayed(true);
        panel.setDisplayDebugInfo(true);
        panel.setImageSizeDisplayed(true);
        panel.setMirrored(true);
        JFrame window = new JFrame("Test webcam panel");
        window.add(panel);

        ImagePanel imagePanel = new ImagePanel(webcam.getImage());
        ImagePanel imagePanel2 = new ImagePanel(webcam.getImage());
        window.add(imagePanel);
        window.add(imagePanel2);
        webcamObservable = new WebcamObservable(webcam);

        LocalFeatureMatcher<Keypoint> matcher;
        DoGSIFTEngine engine;
        RobustAffineTransformEstimator modelFitter = new RobustAffineTransformEstimator(5.0, 1500,
                new RANSAC.PercentageInliersStoppingCondition(0.5));
        matcher = new ConsistentLocalFeatureMatcher2d<>(
                new FastBasicKeypointMatcher<>(8), modelFitter);

        try {
            FImage query = ImageUtilities.readF(Paths.get(ClassLoader.getSystemResource("lips.png").toURI()).toFile());
//            FImage query = ImageUtilities.readF(Paths.get(ClassLoader.getSystemResource("left-eye.png").toURI()).toFile());
            engine = new DoGSIFTEngine();
            LocalFeatureList<Keypoint> queryKeypoints = engine.findFeatures(query);
            matcher.setModelFeatures(queryKeypoints);
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }

        Func1<FImage, FImage> pointDetection = fImage -> {
            if (matcher.findMatches(engine.findFeatures(fImage))) {

                for (Pair<Keypoint> match : matcher.getMatches()) {
                    Keypoint keypoint = match.firstObject();
                    fImage.drawText("X", (int) keypoint.getLocation().getX(), (int) keypoint.getLocation().getY(), HersheyFont.ASTROLOGY, 20, 1f);

                }
            }
            return fImage;
        };
        newEdgeObservable(1000)
                .map(pointDetection)
                .map(ImageUtilities::createBufferedImage)
                .subscribeOn(Schedulers.computation())
                .subscribe(imagePanel::setImage);


        newEdgeObservable(100)
                .map(ImageUtilities::createBufferedImage)
                // why?
//                .subscribeOn(Schedulers.computation())
                .subscribe(imagePanel2::setImage);


        window.setLayout(new GridLayout(1, 3));
        window.setResizable(true);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.pack();
        window.setVisible(true);
    }

    private Observable<FImage> newEdgeObservable(long millis) {
        return Observable.create(webcamObservable)
                .sample(millis, TimeUnit.MILLISECONDS)
                .map(this::toFImage)
                .map(FImage::flipX)
                .map(fImage -> fImage.processInplace(new CannyEdgeDetector()));
    }

    private FImage toFImage(BufferedImage bufferedImage) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos);
            return ImageUtilities.readF(new ByteArrayInputStream(baos.toByteArray()));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
