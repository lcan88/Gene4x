package org.geworkbench.util.function.optimization;

public class OptimizationException extends Exception {
    public OptimizationException(Throwable causing) {
        causing.printStackTrace();
    }
}
