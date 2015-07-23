package org.geworkbench.engine.management;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Simple asynchronous event synch model.
 *
 * @author John Watkinson
 */
public class Asynchronous implements SynchModel {
    private ExecutorService executor;

    public void initialize() {
        executor = Executors.newCachedThreadPool();
    }

    public void shutdown() {
        executor.shutdownNow();
    }

    public void addTask(Runnable task, Object subscriber, Object object, Object publisher) {
        executor.submit(task);
    }
}
