package org.geworkbench.util;

/**
 * Implemented by objects that want to be notified of progress in a classifier's training, usually for display on the GUI
 * to let the user know how well things are going.
 * User: mhall
 * Date: Jan 12, 2006
 * Time: 3:00:05 PM
 */
public interface TrainingProgressListener {

    public void stepUpdate(float value);
    public void stepUpdate(String message, float value);
}
