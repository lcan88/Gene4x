package org.geworkbench.util.function.functionParameters.bimodalGaussianParams;

import org.geworkbench.util.function.MultivariateGaussian;

public class BimodalGaussianParamsDefault extends BimodalGaussianParamsBase {
    public BimodalGaussianParamsDefault() {
    }

    public BimodalGaussianParamsDefault(BimodalGaussianParamsBase params) {
        this.mu1 = params.getMu1();
        this.mu2 = params.getMu2();

        this.covar1 = params.getCovar1();
        this.covar2 = params.getCovar2();

        this.gaussian1 = new MultivariateGaussian(mu1, covar1);
        this.gaussian2 = new MultivariateGaussian(mu2, covar2);

        this.tau1 = params.getTau1();
        this.tau2 = params.getTau2();
    }

    public BimodalGaussianParamsDefault(String[] paramVals) {
        initialize(paramVals);
        gaussian1 = new MultivariateGaussian(mu1, covar1);
        gaussian2 = new MultivariateGaussian(mu2, covar2);

        initializeHashMap();
    }

    /**
     * initialize
     */
    public void initialize() {
    }
}
