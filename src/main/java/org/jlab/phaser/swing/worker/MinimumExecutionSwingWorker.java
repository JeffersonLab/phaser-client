package org.jlab.phaser.swing.worker;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.SwingWorker;

/**
 * This is a SwingWorker which guarantees that it will take at least a minimum
 * amount of time to do its job.
 * 
 * This is useful for ensuring that a "Please
 * Wait" dialog doesn't flicker because it is shown too little time.
 *
 * @author ryans
 * @param <T> the result type returned by this SwingWorker's doInBackground and
 * get methods
 * @param <V> the type used for carrying out intermediate results by this
 * SwingWorker's publish and process methods
 */
public abstract class MinimumExecutionSwingWorker<T, V> extends SwingWorker<T, V> {

    public static final long DEFAULT_MIN_MILLISECONDS = 750L;
    private final long minimumMilliseconds;
    private final Object waitLock = new Object();
    private boolean minimumElapsed = false;

    /**
     * Creates a new MinimumExecutionSwingWorker with a default minimum
     * execution time of 750 milliseconds (seems to work okay with Exceed).
     */
    public MinimumExecutionSwingWorker() {
        this(DEFAULT_MIN_MILLISECONDS);
    }

    /**
     * Creates a new MinimumExecutionSwingWorker with the specified minimum execution time.
     * 
     * @param minimumMilliseconds The minimum execution time in milliseconds
     */
    public MinimumExecutionSwingWorker(long minimumMilliseconds) {
        this.minimumMilliseconds = minimumMilliseconds;
    }

    @Override
    protected T doInBackground() throws Exception {
        T result = null;
        ScheduledExecutorService service
                = Executors.newSingleThreadScheduledExecutor();

        try {
            service.schedule(new Runnable() {

                @Override
                public void run() {
                    synchronized (waitLock) {
                        minimumElapsed = true;
                        waitLock.notify();
                    }
                }
            }, minimumMilliseconds, TimeUnit.MILLISECONDS);

            result = doWithMinimumExecution();

            synchronized (waitLock) {
                while (!minimumElapsed) {
                    waitLock.wait();
                }
            }
        } finally {
            service.shutdown();
        }

        return result;
    }

    protected abstract T doWithMinimumExecution() throws Exception;
}
