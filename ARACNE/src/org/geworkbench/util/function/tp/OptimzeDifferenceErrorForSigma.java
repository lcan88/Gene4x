package org.geworkbench.util.function.tp;

import cern.colt.list.DoubleArrayList;
import cern.jet.stat.Descriptive;
import org.geworkbench.util.function.MultivariateGaussianBimodal;
import org.geworkbench.util.function.functionParameters.bimodalGaussianParams.BimodalGaussianParamsBase;
import org.geworkbench.util.function.functionParameters.bimodalGaussianParams.BimodalGaussianParamsFactory;
import org.geworkbench.util.function.mi.MiDifferenceError;
import org.geworkbench.util.function.mi.MiErrors;
import org.geworkbench.util.function.optimization.fortran.FminReturnResults;

import java.io.File;
import java.util.Vector;

public class OptimzeDifferenceErrorForSigma {
    public OptimzeDifferenceErrorForSigma() {

    }

    public static void main(String[] args) {
        new OptimzeDifferenceErrorForSigma().testSigmas();
        //        new OptimzeDifferenceErrorForSigma().doIt();
    }

    void testSigmas() {
        File dataFile = new File("Z:\\MI\\BimodalGaussian\\BimodalGaussianMIs\\MpiTest\\Accurate\\Data125.txt");

        Vector fileData = org.geworkbench.bison.util.FileUtil.readFile(dataFile);
        //        for (int dataIndex = 4; dataIndex < fileData.size(); dataIndex++) {
        for (int dataIndex = 5; dataIndex < 6; dataIndex++) {
            String[] lineData = (String[]) fileData.get(dataIndex);

            int index = Integer.parseInt(lineData[0]);

            String params1Name = "BimodalGaussianParams" + lineData[1];
            double gaussian1Covar1 = Double.parseDouble(lineData[2]);
            double gaussian1Covar2 = Double.parseDouble(lineData[3]);
            double expectedMi1 = Double.parseDouble(lineData[4]);

            String params2Name = "BimodalGaussianParams" + lineData[5];
            double gaussian2Covar1 = Double.parseDouble(lineData[6]);
            double gaussian2Covar2 = Double.parseDouble(lineData[7]);
            double expectedMi2 = Double.parseDouble(lineData[8]);

            double expectedMiDifference = Double.parseDouble(lineData[9]);
            //            double expectedMiDifference = 0.0;

            BimodalGaussianParamsBase params1 = BimodalGaussianParamsFactory.getParams(params1Name);
            params1.setCovariance1(gaussian1Covar1);
            params1.setCovariance2(gaussian1Covar2);

            BimodalGaussianParamsBase params2 = BimodalGaussianParamsFactory.getParams(params2Name);

            params2.setCovariance1(gaussian2Covar1);
            params2.setCovariance2(gaussian2Covar2);

            MultivariateGaussianBimodal gaussian1 = new MultivariateGaussianBimodal(params1);

            MultivariateGaussianBimodal gaussian2 = new MultivariateGaussianBimodal(params2);

            double min = 0.000000001;
            double max = 0.5;
            double tol = 0.0000000000001;

            //            FunctionBracketer bracketer = new FunctionBracketer();
            //        double[][] bracket = bracketer.bracketFunction(min, max, differenceError);

            int numDataPoints = 10000;
            gaussian1.setNumDataPoints(numDataPoints);
            gaussian2.setNumDataPoints(numDataPoints);
            int iterations = 1;
            double[][] gaussian1Data = gaussian1.getData();
            double[][] gaussian2Data = gaussian2.getData();

            MiErrors miErrors = new MiErrors();
            miErrors.setExpectedMi1(expectedMi1);
            miErrors.setExpectedMi2(expectedMi2);
            miErrors.setData1(gaussian1Data);
            miErrors.setData2(gaussian2Data);

            //            System.out.println("Sigma\tDifference Error\tError 1\tError 2");
            System.out.println("Sigma\tExpected 1\tObserved 1\tExpected 2\tObserved 2\tDifference Error\tError 1\tError 2");
            for (double sigma = 0.001; sigma < 0.3; sigma += .001) {
                for (int iterationCtr = 0; iterationCtr < iterations; iterationCtr++) {

                    double[] errors = miErrors.getPctErrors(sigma);

                    double observedMi1 = errors[3];
                    double observedMi2 = errors[4];

                    System.out.println(sigma + "\t" + expectedMi1 + "\t" + observedMi1 + "\t" + expectedMi2 + "\t" + observedMi2 + "\t" + errors[0] + "\t" + errors[1] + "\t" + errors[2] + "\t");

                    //                    System.out.println(sigma + "\t" + errors[0] + "\t" +
                    //                                       errors[1] + "\t" + errors[2] + "\t");

                    //                    dalSigmas.add(sigma);
                    //                    dalPctErrors.add(errors[0]);
                    //                    dalMiError1.add(errors[1]);
                    //                    dalMiError2.add(errors[2]);
                    //                    dalObjective.add(errors[3]);
                }
            }

            //            double[] xData = dalSigmas.elements();
            //            double[][] yData = new double[4][];
            //            yData[0] = dalSigmas.elements();
            //            yData[1] = dalPctErrors.elements();
            //            yData[2] = dalMiError1.elements();
            //            yData[3] = dalMiError2.elements();
            //
            //            XYSeriesCollection plots = new XYSeriesCollection();
            //
            //            for (int i = 0; i < 4; i++) {
            //                XYSeries dataSeries = new XYSeries("Data");
            //                for (int j = 0; j < xData.length; j++) {
            //                    dataSeries.add(xData[j], yData[i][j]);
            //                }
            //                plots.addSeries(dataSeries);
            //            }
            //
            //            JFreeChart chart = ChartFactory.createScatterPlot(
            //                "Function Plot", // Title
            //                "X", //(, // X-Axis label
            //                "Y", // Y-Axis label
            //                plots, // Dataset
            //                true, // Show legend
            //                true,
            //                false);
            //
            //
            //
            //            ChartFrame frame = new ChartFrame("chart frame", chart);
            //            frame.setSize(500, 500);
            //            frame.show();
        }
    }


    void doIt() {

        File dataFile = new File("Z:\\MI\\BimodalGaussian\\BimodalGaussianMIs\\MpiTest\\Accurate\\Data125.txt");

        Vector fileData = org.geworkbench.bison.util.FileUtil.readFile(dataFile);
        for (int dataIndex = 4; dataIndex < fileData.size(); dataIndex++) {
            //        for (int dataIndex = 4; dataIndex < 5; dataIndex++) {
            String[] lineData = (String[]) fileData.get(dataIndex);

            int index = Integer.parseInt(lineData[0]);

            String params1Name = "BimodalGaussianParams" + lineData[1];
            double gaussian1Covar1 = Double.parseDouble(lineData[2]);
            double gaussian1Covar2 = Double.parseDouble(lineData[3]);

            String params2Name = "BimodalGaussianParams" + lineData[5];
            double gaussian2Covar1 = Double.parseDouble(lineData[6]);
            double gaussian2Covar2 = Double.parseDouble(lineData[7]);

            double expectedMi = Double.parseDouble(lineData[4]);
            //            double expectedMiDifference = Double.parseDouble(lineData[9]);
            double expectedMiDifference = 0.0;

            BimodalGaussianParamsBase params1 = BimodalGaussianParamsFactory.getParams(params1Name);
            params1.setCovariance1(gaussian1Covar1);
            params1.setCovariance2(gaussian1Covar2);

            BimodalGaussianParamsBase params2 = BimodalGaussianParamsFactory.getParams(params2Name);

            params2.setCovariance1(gaussian2Covar1);
            params2.setCovariance2(gaussian2Covar2);

            MultivariateGaussianBimodal gaussian1 = new MultivariateGaussianBimodal(params1);

            MultivariateGaussianBimodal gaussian2 = new MultivariateGaussianBimodal(params2);

            int numDataPoints = 125;

            MiDifferenceError differenceError = new MiDifferenceError();

            differenceError.setFunction1(gaussian1);
            differenceError.setFunction2(gaussian2);
            differenceError.setExpectedMiDifference(expectedMiDifference);
            differenceError.setExpectedMi(expectedMi);

            double min = 0.000000001;
            double max = 0.5;
            double tol = 0.0000000000001;

            //            FunctionBracketer bracketer = new FunctionBracketer();
            //        double[][] bracket = bracketer.bracketFunction(min, max, differenceError);

            FminReturnResults fMin = new FminReturnResults();

            int iterations = 5;

            DoubleArrayList dalMinVals = new DoubleArrayList();
            for (int iterationCtr = 0; iterationCtr < iterations; iterationCtr++) {

                Object[] results = fMin.fmin(min, max, differenceError, tol);

                Vector allResults = (Vector) results[0];
                double minVal = ((Double) results[1]).doubleValue();
                dalMinVals.add(minVal);

                //                Iterator it = allResults.iterator();
                //                while (it.hasNext()) {
                //                    double[] result = (double[]) it.next();
                //                    System.out.println(result[0] + "\t" + result[1]);
                //                }
                //                System.out.println("Min Val  " + minVal);
                //                System.out.println();
                //                System.out.println();

            }

            double minValMean = Descriptive.mean(dalMinVals);
            double minValVar = Descriptive.sampleVariance(dalMinVals, minValMean);
            double minValSd = Descriptive.standardDeviation(minValVar);

            System.out.println(minValMean + "\t" + minValSd);

            //            Iterator it = allResults.iterator();
            //        while (it.hasNext()) {
            //            double[] result = (double[]) it.next();
            //            System.out.println(result[0] + "\t" + result[1]);
            //        }
            //        System.out.println("Min Val  " + minVal);

        }

        //        BimodalGaussianParamsBase params1 = BimodalGaussianParamsFactory.
        //            getParams("BimodalGaussianParams1");
        //        double gaussian1Covar1 = -0.089442719;
        //        double gaussian1Covar2 = -0.0000000000000000139;
        //
        //        params1.setCovariance1(gaussian1Covar1);
        //        params1.setCovariance2(gaussian1Covar2);
        //
        //        BimodalGaussianParamsBase params2 = BimodalGaussianParamsFactory.
        //            getParams("BimodalGaussianParams3");
        //        double gaussian2Covar1 = 0.10392305;
        //        double gaussian2Covar2 = 0.424;

        //        params2.setCovariance1(gaussian2Covar1);
        //        params2.setCovariance2(gaussian2Covar2);
        //
        //        MultivariateGaussianBimodal gaussian1 = new MultivariateGaussianBimodal(
        //            params1);
        //
        //        MultivariateGaussianBimodal gaussian2 = new MultivariateGaussianBimodal(
        //            params2);

        //        double data[][] = gaussian1.getData();
        //        DoubleArrayList dalData1 = new DoubleArrayList(data[0]);
        //        double var1 = Descriptive.sampleVariance(dalData1, Descriptive.mean(dalData1));
        //        System.out.println(Descriptive.standardDeviation(var1) + "");
        //        DoubleArrayList dalData2 = new DoubleArrayList(data[1]);
        //        double var2 = Descriptive.sampleVariance(dalData2, Descriptive.mean(dalData1));
        //        System.out.println(Descriptive.standardDeviation(var2) + "");
        //        if(true) return;

        //        double trueMi = 0.18071929;
        //        double expectedMiDifference = 0.009421894;
    }


}
