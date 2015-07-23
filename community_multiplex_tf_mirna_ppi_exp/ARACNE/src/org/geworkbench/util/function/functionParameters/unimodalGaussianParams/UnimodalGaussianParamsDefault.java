package org.geworkbench.util.function.functionParameters.unimodalGaussianParams;

import be.ac.ulg.montefiore.run.distributions.MultiGaussianDistribution;

public class UnimodalGaussianParamsDefault extends UnimodalGaussianParamsBase {
    public UnimodalGaussianParamsDefault() {
    }

    public UnimodalGaussianParamsDefault(String[] paramValues) {
        initialize(paramValues);
        mgd = new MultiGaussianDistribution(mu, covarianceMatrix);
        initializeHashMap();
    }

    void initialize(String[] paramVals) {
        mu[0] = Double.parseDouble(paramVals[0]);
        mu[1] = Double.parseDouble(paramVals[1]);

        covarianceMatrix[0][0] = Double.parseDouble(paramVals[2]);
        covarianceMatrix[1][1] = Double.parseDouble(paramVals[3]);
        covarianceMatrix[0][1] = Double.parseDouble(paramVals[4]);
        covarianceMatrix[1][0] = Double.parseDouble(paramVals[4]);
    }

    /**
     * initialize
     */
    public void initialize() {
    }


}
