package org.geworkbench.util.function;

import java.util.Random;

public class UniformFunction extends FunctionBase {
    public UniformFunction() {
    }

    /**
     * getData
     *
     * @return double[][]
     */
    public double[][] getData() {
        Random r = new Random();
        double[] xSeries = new double[numDataPoints];
        double[] ySeries = new double[numDataPoints];

        for (int i = 0; i < numDataPoints; i++) {
            double xVal = (Math.random() * (max - min)) + min;
            double yVal = (Math.random() * (max - min)) + min;
            xSeries[i] = xVal;
            ySeries[i] = yVal;
        }
        double[][] data = {xSeries, ySeries};
        return data;
    }
}
