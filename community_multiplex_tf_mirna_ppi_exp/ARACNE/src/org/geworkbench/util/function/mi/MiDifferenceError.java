package org.geworkbench.util.function.mi;

import org.geworkbench.util.function.IProbabilityFunctionBivariate;
import org.geworkbench.util.function.optimization.fortran.Fmin_methods;

public class MiDifferenceError implements Fmin_methods {
    IProbabilityFunctionBivariate function1;
    IProbabilityFunctionBivariate function2;

    double expectedMiDifference;
    double expectedMi;

    double expectedMi1;
    double expectedMi2;

    public IProbabilityFunctionBivariate getFunction1() {
        return function1;
    }

    public double getExpectedMiDifference() {
        return expectedMiDifference;
    }

    public double getExpectedMi() {
        return expectedMi;
    }

    public void setFunction2(IProbabilityFunctionBivariate function2) {
        this.function2 = function2;
    }

    public void setFunction1(IProbabilityFunctionBivariate function1) {
        this.function1 = function1;
    }

    public void setExpectedMiDifference(double expectedMiDifference) {
        this.expectedMiDifference = expectedMiDifference;
    }

    public void setExpectedMi(double expectedMi) {
        this.expectedMi = expectedMi;
    }

    public void setExpectedMi2(double expectedMi2) {
        this.expectedMi2 = expectedMi2;
    }

    public void setExpectedMi1(double expectedMi1) {
        this.expectedMi1 = expectedMi1;
    }

    public IProbabilityFunctionBivariate getFunction2() {
        return function2;
    }

    public double getExpectedMi2() {
        return expectedMi2;
    }

    public double getExpectedMi1() {
        return expectedMi1;
    }

    public MiDifferenceError() {
    }

    public double getPctError(double sigma) {
        int numDataPoints = 125;
        int numIterations = 2;

        double mi1 = FunctionMi.getMi(function1, sigma, numDataPoints, numIterations)[0];
        double mi2 = FunctionMi.getMi(function2, sigma, numDataPoints, numIterations)[0];

        double miDifference = mi2 - mi1;
        double error = Math.abs(expectedMiDifference - miDifference);
        double pctError = error / expectedMi;

        return pctError;
    }

    public double[] getPctError(double[][] gaussian1Data, double[][] gaussian2Data, double sigma) {

        double mi1 = FunctionMi.getMi(gaussian1Data, sigma);
        double mi2 = FunctionMi.getMi(gaussian2Data, sigma);

        //        double error1 = Math.abs((expectedMi1 - mi1) / mi1);
        //        double error2 = Math.abs((expectedMi2 - mi2) / mi2);
        double error1 = (expectedMi1 - mi1) / mi1;
        double error2 = (expectedMi2 - mi2) / mi2;

        double miDifference = mi2 - mi1;
        //        double error = Math.abs(expectedMiDifference - miDifference);
        double error = expectedMiDifference - miDifference;
        double pctError = error / expectedMi;

        double[] pctErrors = {pctError, error1, error2};
        return pctErrors;
    }


    /**
     * f_to_minimize
     *
     * @param x double
     * @return double
     */
    public double f_to_minimize(double x) {
        return getPctError(x);
    }
}
