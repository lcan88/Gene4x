package org.geworkbench.util.function.mi;

import org.geworkbench.util.function.optimization.fortran.Fmin_methods;

public class MiEstimationError implements Fmin_methods {
    double expectedMi;
    double[][] data;

    public double getExpectedMi() {
        return expectedMi;
    }

    public void setData(double[][] data) {
        this.data = data;
    }

    public void setExpectedMi(double expectedMi) {
        this.expectedMi = expectedMi;
    }

    public double[][] getData() {
        return data;
    }

    public MiEstimationError() {
    }

    public double getEstimationError(double sigma) {
        double mi = FunctionMi.getMi(data, sigma);
        double error = mi - expectedMi;
        return error;
    }

    public double getSquaredError(double sigma) {
        double error = getEstimationError(sigma);
        return error * error;
    }

    public double f_to_minimize(double x) {
        return getSquaredError(x);
    }

}
