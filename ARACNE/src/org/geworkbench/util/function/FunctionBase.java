package org.geworkbench.util.function;

public abstract class FunctionBase implements IFunction {
    double min;
    double max;
    int numDataPoints;

    public FunctionBase() {
    }

    /**
     * setRange
     *
     * @param min double
     * @param max double
     */
    public void setRange(double min, double max) {
        this.min = min;
        this.max = max;
    }

    /**
     * setNumDataPoints
     *
     * @param numDataPoints int
     */
    public void setNumDataPoints(int numDataPoints) {
        this.numDataPoints = numDataPoints;
    }

}
