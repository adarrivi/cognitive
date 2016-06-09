package io.adarrivi.cognitive;


import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class CognitiveApplicationTest {

    private static final Logger logger = LoggerFactory.getLogger(CognitiveApplicationTest.class);

    private void sleepThread() {
        logger.info("Sleeping thread...");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private int expensiveOps;
    private int cheapOps;
    private int firstOps;

    @Test
    public void multithreadedOneOperation() {
        final Func1<Integer, Integer> firstOperation = integer -> {
            firstOps++;
            int result = integer;
            logger.info("first operation {}", result);
            return result;
        };

        final Func1<Integer, Integer> expensiveOperation = integer -> {
            expensiveOps++;
            int result = integer * integer;
            sleepThread();
            logger.info("expensive operation {}", result);
            return result;
        };
        final Func1<Integer, Integer> cheapOperation = integer -> {
            cheapOps++;
            int result = integer++;
            logger.info("cheap operation {}", result);
            return result;
        };

        Observable.range(1, 3)
                .map(firstOperation)
                .flatMap(val -> Observable.just(val)
                        .subscribeOn(Schedulers.computation())
                        .map(expensiveOperation))
                .map(cheapOperation)
                .subscribe(getSubscriberAction(1));
        sleepThread();
        sleepThread();
        logger.info("Expensive " + expensiveOps + " vs Cheap " + cheapOps + " vs First " + firstOps);
    }

    private Action1<Integer> getSubscriberAction(int number) {
        return integer -> logger.info("Subscriber {}: {}", number, integer);
    }

    @Test
    public void duplicateObserver() {
        Observable<Integer> integerObservable = Observable.range(1, 3)
                .map(integer -> {
                    logger.info("First operation with {}", integer);
                    firstOps++;
                    return integer;
                })
                .flatMap(Observable::just);

        integerObservable
                .observeOn(Schedulers.computation())
                .map(integer -> {
                    sleepThread();
                    logger.info("Second in independent {}", integer);
                    return integer;
                })
                .subscribe(getSubscriberAction(1));
        integerObservable
                .map(integer -> {
                    logger.info("Second {}", integer);
                    return integer;
                })
                .subscribeOn(Schedulers.computation())
                .subscribe(getSubscriberAction(2));
        sleepThread();
        sleepThread();
        sleepThread();
        sleepThread();
        logger.info("ops: {}", firstOps);
    }

}
