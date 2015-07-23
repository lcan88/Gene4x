package org.geworkbench.engine.management;


/**
 * SynchModel types define an event synchronization model.
 * Implementing classes can specify which SynchModel they want to use.
 *
 * @author John Watkinson
 */
public interface SynchModel {
    public void initialize();

    /**
     * Called by the {@link ComponentRegistry} to add a task to this synch model.
     *
     * @param task the task to run.
     * @param object the object on which this task will be run.
     */
    public void addTask(Runnable task, Object subscriber, Object object, Object publisher);

    public void shutdown();
}
