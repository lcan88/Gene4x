package org.geworkbench.util.function;

public interface IFunction {
    void setRange(double min, double max);

    void setNumDataPoints(int numDataPoints);

    double[][] getData();
}
