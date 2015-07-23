package org.geworkbench.engine.management;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Asynchronous event synch model that "overflows".
 * If more than one event is added while another is processing, then only the latest of those events is processed.
 *
 * @author John Watkinson
 */
public class Overflow implements SynchModel {

    /**
     * Class that implements a "Gate" to processing. Only one task can process at once, and only the most recent
     * task can wait at the gate to pass.
     */
    private static class Gate {

        private boolean running = false;
        private Runnable waitingTask = null;

        public boolean isRunning() {
            return running;
        }

        public void setRunning(boolean value) {
            running = value;
        }

        public void setWaitingTask(Runnable task) {
            waitingTask = task;
        }

        public Runnable popWaitingTask() {
            Runnable returnVal = waitingTask;
            waitingTask = null;
            return returnVal;
        }

    }

    private static class SubscriberAndObject {
        private Object subscriber;
        private Object object;

        public SubscriberAndObject(Object subscriber, Object object) {
            this.subscriber = subscriber;
            this.object = object;
        }

        @Override public int hashCode() {
            return new HashCodeBuilder().append(subscriber).append(object).toHashCode();

        }

        @Override public boolean equals(Object obj) {
            if (obj instanceof SubscriberAndObject) {
                SubscriberAndObject other = (SubscriberAndObject) obj;
                return new EqualsBuilder().append(subscriber, other.subscriber).append(object, other.object).isEquals();
            } else {
                return false;
            }
        }
    }

    private ExecutorService executor;
    private Map<SubscriberAndObject, Gate> gates;

    public void initialize() {
        executor = Executors.newCachedThreadPool();
        gates = new HashMap<SubscriberAndObject, Gate>();
    }

    public void shutdown() {
        executor.shutdownNow();
    }

    private void runTask(final Runnable task, final Gate gate, final SubscriberAndObject sao) {
        executor.submit(new Runnable() {
            public void run() {
                task.run();
                synchronized(Overflow.this) {
                    Runnable waiting = gate.popWaitingTask();
                    if (waiting != null) {
                        runTask(waiting, gate, sao);
                    } else {
                        // Nobody waiting? Destroy the gate
                        gates.remove(sao);
                    }
                }
            }
        });
    }

    public void addTask(Runnable task, Object subscriber, Object object, Object publisher) {
        SubscriberAndObject sao = new SubscriberAndObject(subscriber, object.getClass());
        synchronized(this) {
            Gate gate = gates.get(sao);
            if (gate == null) {
                gate = new Gate();
                gates.put(sao, gate);
            }
            if (gate.isRunning()) {
                // This task becomes the waiting task
//                System.out.println("Waiting on this task");
                gate.setWaitingTask(task);
            } else {
//                System.out.println("Running task immediately");
                gate.setRunning(true);
                runTask(task, gate, sao);
            }
        }
    }

    private static class TestRunnable implements Runnable {

        String name;

        public TestRunnable(String name) {
            this.name = name;
        }

        public void run() {
            System.out.println("Running '" + name + "'...");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                // Ignore
            }
            System.out.println("... run of '" + name + "' is complete.");
        }
    }

    /**
     * Testing.
     */
    public static void main(String[] args) {
        Overflow overflow = new Overflow();
        overflow.initialize();
        TestRunnable r1 = new TestRunnable("Task #1");
        TestRunnable r2 = new TestRunnable("Task #2");
        TestRunnable r3 = new TestRunnable("Task #3");
        TestRunnable r4 = new TestRunnable("Task #4");
        TestRunnable r5 = new TestRunnable("Task #5");
        Object subscriber = new Object();
        Object object1 = new Object();
        Object object2 = new Object();
        // Submit r1, r2 and r3 at the same time to object1
        overflow.addTask(r1, subscriber, object1, null);
        overflow.addTask(r2, subscriber, object1, null);
        overflow.addTask(r3, subscriber, object1, null);
        // Only r1 and r3 should run
        // Independently, submit r4, wait 3 seconds, then submit r5 (both to object2)
        overflow.addTask(r4, subscriber, object2, null);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            // Ignore
        }
        overflow.addTask(r5, subscriber, object2, null);
    }
}
