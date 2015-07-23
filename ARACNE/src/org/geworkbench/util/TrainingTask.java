package org.geworkbench.util;

/**
 * @author John Watkinson
 */
public interface TrainingTask {
    TrainingProgressListener getTrainingProgressListener();

    void setTrainingProgressListener(TrainingProgressListener trainingProgressListener);

    boolean isCancelled();

    void setCancelled(boolean cancelled);
}
