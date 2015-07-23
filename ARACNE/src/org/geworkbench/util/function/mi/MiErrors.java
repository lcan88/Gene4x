package org.geworkbench.util.function.mi;

import org.geworkbench.util.function.optimization.fortran.Fmin_methods;

public class MiErrors implements Fmin_methods {
    double expectedMi1;
    double expectedMi2;

    double[][] data1;
    double[][] data2;


    double expectedMi;

    public double[][] getData1() {
        return data1;
    }

    public double getExpectedMi2() {
        return expectedMi2;
    }

    public double[][] getData2() {
        return data2;
    }

    public void setExpectedMi1(double expectedMi1) {
        this.expectedMi1 = expectedMi1;
    }

    public void setData1(double[][] data1) {
        this.data1 = data1;
    }

    public void setExpectedMi2(double expectedMi2) {
        this.expectedMi2 = expectedMi2;
    }

    public void setData2(double[][] data2) {
        this.data2 = data2;
    }


    public void setExpectedMi(double expectedMi) {
        this.expectedMi = expectedMi;
    }

    public double getExpectedMi1() {
        return expectedMi1;
    }


    public double getExpectedMi() {
        return expectedMi;
    }

    public MiErrors() {
    }

    public double getErrorOjective(double sigma) {
        double mi1 = FunctionMi.getMi(data1, sigma);
        double mi2 = FunctionMi.getMi(data2, sigma);

        double miError1 = Math.abs((expectedMi1 - mi1) / expectedMi1);
        double miError2 = Math.abs((expectedMi2 - mi2) / expectedMi2);

        //        double miDifference = mi2 - mi1;
        double differenceError = Math.abs((expectedMi2 - expectedMi1) - (mi2 - mi1));
        double pctDifferenceError = differenceError / ((expectedMi1 + expectedMi2) / 2);

        double objectiveFunctionError = pctDifferenceError + Math.pow(miError1 + miError2, 2);
        return objectiveFunctionError;
    }

    public double[] getPctErrors(double sigma) {

        //        double mi1 = FunctionMi.getMi(data1, sigma);
        //        double mi2 = FunctionMi.getMi(data2, sigma);
        //
        //        double miError1 = Math.abs((expectedMi1 - mi1) / expectedMi1);
        //        double miError2 = Math.abs((expectedMi2 - mi2) / expectedMi2);
        //
        //        double miDifference = mi2 - mi1;
        //        double differenceError = Math.abs((expectedMi2 - expectedMi1) - (mi2 - mi1));
        //        double pctDifferenceError = differenceError / ((expectedMi1 + expectedMi2) / 2);


        //        double objectiveFunctionError = pctDifferenceError + Math.pow(miError1 + miError2, 2);
        //
        //        double[] pctErrors = {pctDifferenceError, miError1, miError2, objectiveFunctionError};
        //        return pctErrors;

        double mi1 = FunctionMi.getMi(data1, sigma);
        double mi2 = FunctionMi.getMi(data2, sigma);

        //        double error1 = Math.abs((expectedMi1 - mi1) / mi1);
        //        double error2 = Math.abs((expectedMi2 - mi2) / mi2);;
        double error1 = (mi1 - expectedMi1) / expectedMi1;
        double error2 = (mi2 - expectedMi2) / expectedMi2;

        double miDifference = mi2 - mi1;
        //        double error = Math.abs(expectedMiDifference - miDifference);
        double expectedMiDifference = expectedMi2 - expectedMi1;
        double differenceError = miDifference - expectedMiDifference;
        double avgExpectedMi = (expectedMi1 + expectedMi2) / 2;
        double pctDifferenceError = differenceError / avgExpectedMi;

        double[] pctErrors = {pctDifferenceError, error1, error2, mi1, mi2};
        return pctErrors;

    }

    /**
     * f_to_minimize
     *
     * @param x double
     * @return double
     */
    public double f_to_minimize(double x) {
        return getErrorOjective(x);
    }


}
