package org.geworkbench.util.function.functionParameters.bimodalGaussianParams;

import org.geworkbench.util.function.MultivariateGaussian;
import org.geworkbench.util.function.functionParameters.FunctionParameters;

public abstract class BimodalGaussianParamsBase extends FunctionParameters {
    double[] mu1 = new double[2];
    double[] mu2 = new double[2];

    double[][] covar1 = new double[2][2];
    double[][] covar2 = new double[2][2];

    double tau1;
    double tau2;

    MultivariateGaussian gaussian1;
    MultivariateGaussian gaussian2;

    public double getTau2() {
        return tau2;
    }

    public double[] getMu1() {
        return mu1;
    }

    public double[][] getCovar2() {
        return covar2;
    }

    public MultivariateGaussian getGaussian1() {
        return gaussian1;
    }

    public double getTau1() {
        return tau1;
    }

    public MultivariateGaussian getGaussian2() {
        return gaussian2;
    }

    public double[] getMu2() {
        return mu2;
    }

    public void setCovar1(double[][] covar1) {
        this.covar1 = covar1;
    }

    public void setMu1(double[] mu1) {
        this.mu1 = mu1;
    }

    public void setCovar2(double[][] covar2) {
        this.covar2 = covar2;
    }

    public void setGaussian1(MultivariateGaussian gaussian1) {
        this.gaussian1 = gaussian1;
        initParamsByGaussians();
    }

    public void setTau1(double tau1) {
        this.tau1 = tau1;
        this.tau2 = 1.0 - tau1;
    }

    public void setGaussian2(MultivariateGaussian gaussian2) {
        this.gaussian2 = gaussian2;
        initParamsByGaussians();
    }

    public void setMu2(double[] mu2) {
        this.mu2 = mu2;
    }

    public double[][] getCovar1() {
        return covar1;
    }

    public void setCovariance1(double covar) {
        this.covar1[0][1] = covar;
        this.covar1[1][0] = covar;

        this.gaussian1 = new MultivariateGaussian(this.mu1, this.covar1);
        put("gaussian1", gaussian1);
    }

    public void setCovariance2(double covar) {
        this.covar2[0][1] = covar;
        this.covar2[1][0] = covar;

        this.gaussian2 = new MultivariateGaussian(this.mu2, this.covar2);
        put("gaussian2", gaussian2);
    }

    void initParamsByGaussians() {
        if (gaussian1 != null) {
            this.mu1 = gaussian1.getMu();
            this.covar1 = gaussian1.getCovarianceMatrix();
        }

        if (gaussian2 != null) {
            this.mu2 = gaussian2.getMu();
            this.covar2 = gaussian2.getCovarianceMatrix();
        }

        put("mu1", mu1);
        put("mu2", mu2);

        put("covar1", covar1);
        put("covar2", covar2);

        put("tau1", new Double(tau1));
        put("tau2", new Double(tau2));

        put("gaussian1", gaussian1);
        put("gaussian2", gaussian2);
    }

    public void initializeHashMap() {
        put("mu1", mu1);
        put("mu2", mu2);

        put("covar1", covar1);
        put("covar2", covar2);

        put("tau1", new Double(tau1));
        put("tau2", new Double(tau2));

        put("gaussian1", gaussian1);
        put("gaussian2", gaussian2);

    }

    public BimodalGaussianParamsBase() {
        initialize();
        gaussian1 = new MultivariateGaussian(mu1, covar1);
        gaussian2 = new MultivariateGaussian(mu2, covar2);

        initializeHashMap();
    }

    void initialize(String[] paramVals) {
        mu1[0] = Double.parseDouble(paramVals[0]);
        mu1[1] = Double.parseDouble(paramVals[1]);

        covar1[0][0] = Double.parseDouble(paramVals[2]);
        covar1[1][1] = Double.parseDouble(paramVals[3]);
        covar1[0][1] = Double.parseDouble(paramVals[4]);
        covar1[1][0] = Double.parseDouble(paramVals[4]);

        mu2[0] = Double.parseDouble(paramVals[5]);
        mu2[1] = Double.parseDouble(paramVals[6]);

        covar2[0][0] = Double.parseDouble(paramVals[7]);
        covar2[1][1] = Double.parseDouble(paramVals[8]);
        covar2[0][1] = Double.parseDouble(paramVals[9]);
        covar2[1][0] = Double.parseDouble(paramVals[9]);

        tau1 = Double.parseDouble(paramVals[10]);
        tau2 = Double.parseDouble(paramVals[11]);

    }

    public String toString() {
        String retVal = gaussian1.toString() + "\t" + gaussian2.toString() + "\t" + tau1 + "\t" + tau2;
        return retVal;
    }

    public static String getHeader() {
        return "MuX1\tMuY1\tVarX1\tVarY1\tCovar1\tMuX2\tMuY2\tVarX2\tVarY2\tCovar2\tTau1\tTau2";
    }


    public abstract void initialize();
}
