package org.geworkbench.util.function.tp;

import org.geworkbench.util.function.MultivariateGaussianBimodal;
import org.geworkbench.util.function.functionParameters.bimodalGaussianParams.BimodalGaussianParamsBase;
import org.geworkbench.util.function.functionParameters.bimodalGaussianParams.BimodalGaussianParamsFactory;
import org.geworkbench.util.function.mi.MiEstimationError;
import org.geworkbench.util.function.optimization.fortran.FminReturnResults;

import java.io.File;
import java.io.FileWriter;
import java.util.Vector;

public class OptimizeMiEstimation {

    public OptimizeMiEstimation() {
    }

    public static void main(String[] args) {
        new OptimizeMiEstimation().optimizeEstimation(args);
    }

    void optimizeEstimation(String[] args) {
        try {
            int baseType = Integer.parseInt(args[0]);
            int startIndex = Integer.parseInt(args[1]);
            int endIndex = Integer.parseInt(args[2]);

            String dirBase;
            if (baseType == 2) {
                dirBase = "/users/aam2110";
            } else {
                dirBase = "Z:";
            }
            File readFile = new File(dirBase + "/MI/BimodalGaussian/BimodalGaussianMIs/Params2.txt");
            File writeFile = new File(dirBase + "/MI/BimodalGaussian/BimodalGaussianMIs/MpiTest/Accurate/OptimizeSigma/MiEstimationError/Params2/" + startIndex + ".txt");
            writeFile.getParentFile().mkdirs();
            FileWriter writer = null;

            writer = new FileWriter(writeFile);

            Vector fileData = org.geworkbench.bison.util.FileUtil.readFile(readFile);

            String[] headers = (String[]) fileData.get(0);
            for (int i = 0; i < headers.length; i++) {
                System.out.print(headers[i] + "\t");
                //                writer.write(headers[i] + "\t");
            }

            int[] arrNumDataPoints = {125, 250, 500, 1000, 2000, 4000, 8000, 16000, 32000};
            //                125, 250, 500};
            for (int i = 0; i < arrNumDataPoints.length; i++) {
                System.out.print(arrNumDataPoints[i] + "\t");
            }
            System.out.println();

            for (int lineIndex = startIndex; lineIndex < endIndex; lineIndex++) {

                String[] lineData = (String[]) fileData.get(lineIndex);
                String paramsName = lineData[0];
                double gaussianCovar1 = Double.parseDouble(lineData[1]);
                double gaussianCovar2 = Double.parseDouble(lineData[2]);
                double expectedMi = Double.parseDouble(lineData[3]);

                BimodalGaussianParamsBase params = BimodalGaussianParamsFactory.getParams(paramsName);
                params.setCovariance1(gaussianCovar1);
                params.setCovariance2(gaussianCovar2);
                MultivariateGaussianBimodal gaussian = new MultivariateGaussianBimodal(params);

                double min = .0000001;
                double max = .8;
                double tol = .0001;
                FminReturnResults fMin = new FminReturnResults();

                for (int i = 0; i < lineData.length; i++) {
                    System.out.print(lineData[i] + "\t");
                    writer.write(lineData[i] + "\t");
                }

                gaussian.setNumDataPoints(arrNumDataPoints[arrNumDataPoints.length - 1]);
                double[][] allData = gaussian.getData();
                for (int dpCtr = 0; dpCtr < arrNumDataPoints.length; dpCtr++) {
                    //            for (int dpCtr = 0; dpCtr < 1; dpCtr++) {
                    int numDataPoints = arrNumDataPoints[dpCtr];
                    double[][] data = new double[2][numDataPoints];
                    for (int i = 0; i < numDataPoints; i++) {
                        data[0][i] = allData[0][i];
                        data[1][i] = allData[1][i];
                    }

                    MiEstimationError estError = new MiEstimationError();
                    estError.setData(data);
                    estError.setExpectedMi(expectedMi);

                    Object[] results = fMin.fmin(min, max, estError, tol);
                    double minVal = ((Double) results[1]).doubleValue();
                    System.out.print(minVal + "\t");
                    writer.write(minVal + "\t");
                    //                Vector allResults = (Vector) results[0];
                    //                Iterator it = allResults.iterator();
                    //                while (it.hasNext()) {
                    //                    double[] result = (double[]) it.next();
                    //                    System.out.println(result[0] + "\t" + result[1]);
                    //                }
                    //                System.out.println("Min Val  " + minVal);
                    //                System.out.println();
                    //                System.out.println();

                }
                System.out.println();
                writer.write("\n");
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
