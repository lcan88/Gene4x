package org.geworkbench.util.function.functionParameters.bimodalGaussianParams;


public class BimodalGaussianParams1 extends BimodalGaussianParamsBase {
    public BimodalGaussianParams1() {

    }

    /**
     * initialize
     */
    public void initialize() {
        mu1[0] = 0.0;
        mu1[1] = 0.0;

        mu2[0] = 1.0;
        mu2[1] = 1.0;

        double var_x1 = .25;
        double var_y1 = .2;
        //        double covar_xy1 = 0.15;
        double covar_xy1 = 0.0;
        double var_x2 = .2;
        double var_y2 = .25;
        //        double covar_xy2 = -.2;
        double covar_xy2 = 0.0;
        tau1 = 0.7;
        tau2 = 1.0 - tau1;

        covar1[0][0] = var_x1;
        covar1[0][1] = covar_xy1;
        covar1[1][0] = covar_xy1;
        covar1[1][1] = var_y1;

        covar2[0][0] = var_x2;
        covar2[0][1] = covar_xy2;
        covar2[1][0] = covar_xy2;
        covar2[1][1] = var_y2;
    }
}
