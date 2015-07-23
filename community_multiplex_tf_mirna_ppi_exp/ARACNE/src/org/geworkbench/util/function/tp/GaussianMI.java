package org.geworkbench.util.function.tp;

import org.geworkbench.util.function.MultivariateGaussian;
import org.geworkbench.util.network.GeneNetworkEdgeImpl;
import org.geworkbench.util.pathwaydecoder.GeneGeneRelationship;

public class GaussianMI {
    public GaussianMI() {
    }

    public static void main(String[] args) {
        new GaussianMI().calculateMI();
    }

    void calculateMI() {

        int microarrayNo = 300;
        MultivariateGaussian mvg = new MultivariateGaussian();
        mvg.setNumDataPoints(microarrayNo);
        double[][] mvgData = mvg.getData();
        GeneGeneRelationship ggr = new org.geworkbench.util.pathwaydecoder.GeneGeneRelationship(null, null, microarrayNo, GeneGeneRelationship.RANK_MI, false);

        double[] xData = mvgData[0];
        double[] yData = mvgData[1];

        GeneNetworkEdgeImpl edge = ggr.miScore(xData, yData, 0.0);
        double mvgMiScore = edge.getMI();

        System.out.println("MI " + mvgMiScore);

        /*
        double[] xData = mvgData[0];
        double[] yData = mvgData[1];

        DoubleArrayList xDal = new DoubleArrayList(xData);
        DoubleArrayList yDal = new DoubleArrayList(yData);

        double xMean = Descriptive.mean(xDal);
        double xVar = Descriptive.sampleVariance(xDal, xMean);

        double yMean = Descriptive.mean(yDal);
        double yVar = Descriptive.sampleVariance(yDal, yMean);

        double covariance = Descriptive.covariance(xDal, yDal);

        System.out.println("xMean " + xMean + " xVar " + xVar + " yMean " +
                           yMean + " yVar " + yVar + " covariance " +
                           covariance);
    */
    }
}
