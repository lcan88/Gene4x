package org.geworkbench.algorithms;

import org.geworkbench.bison.algorithm.AlgorithmEvent;
import org.geworkbench.bison.algorithm.AlgorithmEventListener;
import org.geworkbench.util.SwingWorker;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * <p>Title: Bioworks</p>
 * <p>Description: Mechanism for development and distribution of
 * complex modular software applications for biomedical research</p>
 * <p/>
 * Implementation of the <code>AbstractAlgorithm</code> interface. This class
 * provides functionality in excess of that specified by the interface, namely:
 * It handles the saving of all named parameters sets
 * (from within the saveParametersUnderName method).
 * <p/>
 * Implementation in part ported from NCICB-FGT caWorkbench efforts
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Columbia University</p>
 *
 * @author Califano Lab
 * @version 1.0
 */

public abstract class BWAbstractAlgorithm implements BWAlgorithm {
    boolean useTimer = true;

    public BWAbstractAlgorithm() {
        useTimer = true;
    }

    public BWAbstractAlgorithm(boolean useTimer) {
        this.useTimer = useTimer;
    }

    /**
     * Specifies if a stop has been requested
     */
    protected boolean stopRequested = false;

    /**
     * Runs the analysis on a separate thread
     */
    private AnalysisThread worker = null;

    /**
     * Notifies <code>AlgorithmListener</code> objects of Algorithm progress
     */
    protected Timer timer = null;

    /**
     * Completion state
     */
    protected double state = 0d;

    /**
     * Starts the Algorithm execution
     */
    public void start() {
        if ((worker == null) || (!worker.isRunning())) {
            worker = new AnalysisThread(this);
            worker.start();
        }
    }

    /**
     * This method returns an indeterminate value between 0 and 1 while the
     * algorithm is in progress
     *
     * @return completion state
     */
    public double getCompletion() {
        return Math.sin(state++ / 100.0) / 2 + 0.5;
    }

    /**
     * Stops the algorithm execution
     */
    public void stop() {
        stopRequested = true;
        worker.interrupt();

    }

    /**
     * To be implemented by subclass to describe the actual execution steps
     */
    protected abstract void execute();

    /**
     * This method should be used to check if a stop request was made on the
     * analysis.
     *
     * @return if a stop has been requested
     */
    protected boolean isStopRequested() {
        return stopRequested;
    }

    /**
     * For registering the <code>AlgorithmEventlistener</code> objects, to be
     * notified when the current analysis changes state or goes through any
     * significant process
     */
    private EventListenerList listenerList = new EventListenerList();

    /**
     * Adds a new listener.
     *
     * @param ael the container implementing the
     *            <code>AlgorithmEventListener</code> interface
     */
    public void addAlgorithmEventListener(AlgorithmEventListener ael) {
        listenerList.add(AlgorithmEventListener.class, ael);
    }

    protected void fireAlgorithmEvent(AlgorithmEvent event) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2)
            ((AlgorithmEventListener) listeners[i + 1]).receiveAlgorithmEvent(event);
    }

    public class TimeTickNotification implements ActionListener {

        private BWAbstractAlgorithm analysis = null;

        public TimeTickNotification(BWAbstractAlgorithm algo) {
            analysis = algo;
        }

        /**
         * actionPerformed
         *
         * @param e ActionEvent
         */
        public void actionPerformed(ActionEvent e) {
            timeTick();
        }
    }

    class AnalysisThread extends SwingWorker {
        private BWAbstractAlgorithm analysis = null;

        public AnalysisThread(BWAbstractAlgorithm algo) {
            super();
            analysis = algo;
        }

        public Object construct() {
            synchronized (analysis) {
                try {
                    if (useTimer && timer == null) {
                        timer = new Timer(600, new TimeTickNotification(analysis));
                        timer.start();
                    }
                    analysis.execute();
                    stopRequested = false;
                    state = 50 * Math.PI;
                    fireAlgorithmEvent(new AlgorithmEvent(analysis, AlgorithmEvent.algorithmCompleted, analysis, true));
                } catch (Exception e) {
                    return e;
                } finally {
                    if (useTimer) {
                        timer.stop();
                    }
                }
            }
            return analysis;
        }
    }

    public void timeTick() {
        fireAlgorithmEvent(new AlgorithmEvent(this, org.geworkbench.bison.algorithm.AlgorithmEvent.algorithmTimeTick, this, true));
    }
}
