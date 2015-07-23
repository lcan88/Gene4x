package org.geworkbench.util.function.tp;

import org.geworkbench.util.function.MultivariateGaussianBimodal;
import org.geworkbench.util.function.functionParameters.bimodalGaussianParams.BimodalGaussianParamsBase;
import org.geworkbench.util.function.functionParameters.bimodalGaussianParams.BimodalGaussianParamsFactory;
import org.geworkbench.util.network.GeneNetworkEdgeImpl;

public class BimodalGaussianCalculatedMI {
    public BimodalGaussianCalculatedMI() {
    }

    public static void main(String[] args) {
        new BimodalGaussianCalculatedMI().calculateMI();
    }

    void calculateMI() {
        BimodalGaussianParamsBase params = BimodalGaussianParamsFactory.getParams("BimodalGaussianParams3");

        double gaussianCovar1 = 0.069282032;
        double gaussianCovar2 = 0.707;


        params.setCovariance1(gaussianCovar1);
        params.setCovariance2(gaussianCovar2);

        MultivariateGaussianBimodal bimodalGaussian = new MultivariateGaussianBimodal(params);

        int numSamples = 500;

        bimodalGaussian.setNumDataPoints(numSamples);

        int numIterations = 10;
        double[] results = new double[10];
        for (int i = 0; i < numIterations; i++) {
            double[][] gaussianData = bimodalGaussian.getData();

            org.geworkbench.util.pathwaydecoder.GeneGeneRelationship ggr = new org.geworkbench.util.pathwaydecoder.GeneGeneRelationship(null, null, numSamples, 0, true);

            GeneNetworkEdgeImpl edge = ggr.newMIScore(gaussianData[0], gaussianData[1], 0.0);
            //                    GeneNetworkEdgeImpl edge = ggr.miScore(gaussian1Data[0], gaussian1Data[1], 0.0);
            double gaussianMiScore = edge.getMI();
            gaussianMiScore = gaussianMiScore * 2 * Math.log(numSamples);
            results[i] = gaussianMiScore;
            System.out.println(gaussianMiScore);
        }


    }

}
