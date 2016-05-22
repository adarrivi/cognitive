package io.adarrivi.cognitive;


import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CognitiveApplicationTest {

    private static final Logger logger = LoggerFactory.getLogger(CognitiveApplicationTest.class);

    @Test
    public void other() throws InterruptedException {
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        Observable.create(subscriber -> {
            executorService.submit(() -> {
                int i = 0;
                while (true) {
                    subscriber.onNext(++i);
                }
            });
        })
                .sample(1, TimeUnit.SECONDS)
                .subscribe(i -> logger.info("{}", i));
        logger.info("Sleeping main thread");
        Thread.sleep(10000);
    }

}
