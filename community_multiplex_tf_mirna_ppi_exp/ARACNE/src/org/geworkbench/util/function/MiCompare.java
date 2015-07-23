package org.geworkbench.util.function;

import cern.colt.list.DoubleArrayList;
import cern.jet.stat.Descriptive;
import org.geworkbench.util.network.GeneNetworkEdgeImpl;
import org.geworkbench.util.pathwaydecoder.GeneGeneRelationship;

public class MiCompare {
    public MiCompare() {
    }

    void compareMi() {
        double min = 0.0;
        double max = 1.0;
        int[] numSamplesArr = {100, 200, 300, 400, 500, 600, 800, 1000, 1200, 1400, 1600, 1800, 2000};
        //int[] numSamplesArr = {100,200,300,400, 500, 600, 1800, 2000};
        //int[] numSamplesArr = {600, 1800, 2000};

        int microarrayNo = 100;
        int numIterations = 5;

        UniformFunction uniF = new UniformFunction();
        uniF.setRange(min, max);
        uniF.setNumDataPoints(microarrayNo);

        MultivariateGaussian mvg = new MultivariateGaussian();
        mvg.setRange(min, max);
        mvg.setNumDataPoints(microarrayNo);

        DoubleArrayList dal = new DoubleArrayList();
        DoubleArrayList dalUniMi = new DoubleArrayList();
        DoubleArrayList dalMvgMi = new DoubleArrayList();
        for (int maCtr = 0; maCtr < numSamplesArr.length; maCtr++) {
            microarrayNo = numSamplesArr[maCtr];
            mvg.setNumDataPoints(microarrayNo);
            uniF.setNumDataPoints(microarrayNo);
            for (int i = 0; i < numIterations; i++) {

                double[][] uniData = uniF.getData();
                double[][] mvgData = mvg.getData();

                GeneGeneRelationship ggr = new GeneGeneRelationship(null, null, microarrayNo, 0, true);
                //        GeneNetworkEdgeImpl edge = ggr.newMIScore(uniData[0], uniData[1], 0.0);
                GeneNetworkEdgeImpl edge = ggr.miScore(uniData[0], uniData[1], 0.0);
                double uniMiScore = edge.getMI();

                //        edge = ggr.newMIScore(mvgData[0], mvgData[1], 0.0);
                edge = ggr.miScore(mvgData[0], mvgData[1], 0.0);
                double mvgMiScore = edge.getMI();

                double diff = mvgMiScore - uniMiScore;
                dal.add(diff);
                dalUniMi.add(uniMiScore);
                dalMvgMi.add(mvgMiScore);
                //      System.out.println("Iteration" + i + "\tUni MI\t" + uniMiScore + "\tMVG MI " + mvgMiScore +
                //                         "\tDiff " + (uniMiScore - mvgMiScore));
                System.out.print(i + " ");

            }
            System.out.println();
            System.out.println();

            double mean = Descriptive.mean(dal);
            double var = Descriptive.sampleVariance(dal, mean);
            double sd = Descriptive.standardDeviation(var);

            double uniMean = Descriptive.mean(dalUniMi);
            double uniVar = Descriptive.sampleVariance(dalUniMi, uniMean);
            double uniSd = Descriptive.standardDeviation(uniVar);

            double mvgMean = Descriptive.mean(dalMvgMi);
            double mvgVar = Descriptive.sampleVariance(dalMvgMi, mvgMean);
            double mvgSd = Descriptive.standardDeviation(mvgVar);

            System.out.print("Data Points\t" + microarrayNo + "\tMean\t" + mean + "\tVar\t" + var + "\tsd\t" + sd);
            System.out.print("\tData Points\t" + microarrayNo + "\tUni Mean\t" + uniMean + "\tUni Var\t" + uniVar + "\tUni Sd\t" + uniSd);
            System.out.println("\tData Points\t" + microarrayNo + "\tMvg Mean\t" + mvgMean + "\tMvg Var\t" + mvgVar + "\tMvg Sd\t" + mvgSd);
        }

    }

    public static void main(String[] args) {
        new MiCompare().compareMi();
    }
}
