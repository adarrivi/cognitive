package io.adarrivi.cognitive.vision;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamEvent;
import com.github.sarxos.webcam.WebcamListener;
import rx.Observable;
import rx.Subscriber;

import java.awt.image.BufferedImage;

public class WebCamObservable implements Observable.OnSubscribe<BufferedImage> {

    private final Webcam webcam;

    public WebCamObservable(Webcam webcam) {
        this.webcam = webcam;
    }

    @Override
    public void call(Subscriber<? super BufferedImage> subscriber) {
        WebcamListener webcamListener = new WebcamListener() {

            @Override
            public void webcamOpen(WebcamEvent we) {
                subscriber.onStart();
            }

            @Override
            public void webcamClosed(WebcamEvent we) {
                subscriber.onCompleted();
            }

            @Override
            public void webcamDisposed(WebcamEvent we) {
                subscriber.onCompleted();
            }

            @Override
            public void webcamImageObtained(WebcamEvent we) {
                subscriber.onNext(we.getImage());
            }
        };
        webcam.addWebcamListener(webcamListener);
    }

}
