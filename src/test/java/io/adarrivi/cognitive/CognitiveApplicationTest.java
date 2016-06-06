package io.adarrivi.cognitive;


import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.observables.ConnectableObservable;

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
        sleepMainThread();
    }

    int i;

    @Test
    public void duplicateObservable() {

        Action1<Integer> subscriberA = value -> logger.info("Subscribe A {} {}", value, i++);
        Action1<Integer> subscriberB = value -> logger.info("Subscribe B {} {}", value, i++);

        Observable<Integer> observable = Observable.just(1, 2, 3)
                .map(value -> value * 2);
        observable
                .map(value -> value * 2)
                .subscribe(subscriberA);
        observable.subscribe(subscriberB);

        i = 0;
        ConnectableObservable<Integer> observable1 = Observable.just(1, 2, 3)
                .map(value -> value * 2)
                .publish();
        observable1
                .map(value -> value * 2)
                .subscribe(subscriberA);
        observable1.subscribe(subscriberB);
        observable1.connect();
    }

    private void sleepMainThread() {
        logger.info("Sleeping main thread");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private int expensiveOps;
    private int cheapOps;
    private int slowOps;


    @Test
    public void est2() {
        final Action1<Integer> subscriber1 = integer -> System.out.println("Subscriber 1: " + integer);
        final Action1<Integer> subscriber2 = integer -> System.out.println("Subscriber 2: " + integer);

        final Func1<Integer, Integer> expensiveOperation = integer -> {
            expensiveOps++;
            System.out.println("expensive operation");
            return integer * integer;
        };
        final Func1<Integer, Integer> cheapOperation = integer -> {
            cheapOps++;
            System.out.println("cheapOperation");
            return ++integer;
        };
        final Func1<Integer, Integer> slowOperation= integer -> {
            slowOps++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("slowOperation");
            return integer;
        };
        final ConnectableObservable<Integer> observable = Observable.just(1, 2, 3)
                .map(expensiveOperation)
                .publish();
        observable
                .map(slowOperation)
                .subscribe(subscriber1);
        observable
                .map(cheapOperation)
                .subscribe(subscriber2);
        observable.connect();
        System.out.println("Expensive " + expensiveOps + " vs Cheap " + cheapOps + " vs Slow " + slowOps);
    }

}
