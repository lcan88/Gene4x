package org.geworkbench.util.function.mi;

import cern.colt.list.DoubleArrayList;
import cern.jet.stat.Descriptive;
import org.geworkbench.util.function.IProbabilityFunctionBivariate;
import org.geworkbench.util.network.GeneNetworkEdgeImpl;

public class FunctionMi {
    public FunctionMi() {
    }

    public static double getMi(double[][] data, double sigma) {
        int numDataPoints = data[0].length;
        org.geworkbench.util.pathwaydecoder.GeneGeneRelationship ggr = new org.geworkbench.util.pathwaydecoder.GeneGeneRelationship(numDataPoints, 0, false, sigma);
        ggr.setCopulaTransform(true);

        GeneNetworkEdgeImpl edge = ggr.newMIScore(data[0], data[1], 0.0);
        //                    GeneNetworkEdgeImpl edge = ggr.miScore(gaussian1Data[0], gaussian1Data[1], 0.0);
        double miScore = edge.getMI();
        //                miScore = miScore * Math.log(numDataPoints) * 2;
        return miScore;
    }

    public static double[] getMi(IProbabilityFunctionBivariate function, double sigma, int numDataPoints, int numIterations) {
        function.setNumDataPoints(numDataPoints);
        DoubleArrayList dalMis = new DoubleArrayList();
        for (int i = 0; i < numIterations; i++) {
            try {
                double[][] functionData = function.getData();

                org.geworkbench.util.pathwaydecoder.GeneGeneRelationship ggr = new org.geworkbench.util.pathwaydecoder.GeneGeneRelationship(numDataPoints, 0, false, sigma);

                GeneNetworkEdgeImpl edge = ggr.newMIScore(functionData[0], functionData[1], 0.0);
                //                    GeneNetworkEdgeImpl edge = ggr.miScore(gaussian1Data[0], gaussian1Data[1], 0.0);
                double miScore = edge.getMI();
                //correction
                //                miScore = miScore * Math.log(numDataPoints) * 2;
                dalMis.add(miScore);
                //                System.out.print(i + " ");
            } catch (Exception e) {
                System.out.println("Exception " + e.getMessage());
            }
        }
        double miMean = Descriptive.mean(dalMis);
        double miVariance = Descriptive.sampleVariance(dalMis, miMean);
        double miSd = Descriptive.standardDeviation(miVariance);

        double[] retVal = {miMean, miSd};
        return retVal;
    }

}
