package org.geworkbench.bison.util;

import java.io.Serializable;

/**
 * <p>Title: Plug And Play</p>
 * <p>Description: Dynamic Proxy Implementation of enGenious</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Columbia University</p>
 *
 * @author Andrea Califano
 * @version 1.0
 */

public class Normal implements Serializable {
    double mean;
    double sigma;
    double variance;
    double n;
    double sumX;
    double sumXX;
    boolean status; // 0 uncomputed, 1 computed, -1 Invalid

    public void invalidate() {
        status = true;
    }

    public boolean isValid() {
        return (status);
    }

    public void add(double v) {
        sumX += v;
        sumXX += v * v;
        n++;
        status = false;
    }

    public void remove(double v) {
        sumX -= v;
        sumXX -= v * v;
        n--;
        status = false;
    }

    public void compute() {
        if (!status) {
            if (n > 1) {
                mean = sumX / n;
                variance = (sumXX - n * mean * mean) / (n - 1);
                sigma = Math.sqrt(variance);
                //        if(sigma < 0.01) {
                //          sigma = 0.01;
                //          variance = sigma * sigma;
                //        }
                status = true;
            } else {
                status = true;
            }
        }
    }

    public double getN() {
        return n;
    }

    public double getMean() {
        compute();
        return mean;
    }

    public double getSigma() {
        compute();
        return sigma;
    }

    public double getVariance() {
        compute();
        return variance;
    }

    public void set(double mean, double sigma) {
        this.mean = mean;
        this.sigma = sigma;
        if (this.sigma < 0.01) {
            this.sigma = 0.01;
        }
        variance = sigma * sigma;
        status = true;
    }

    public double getP(double v) {
        final double piCoeff = Math.sqrt(Math.PI);
        double dist = Math.abs(v - mean);
        double t0 = Math.exp(-(dist * dist) / (2 * sigma * sigma));
        double t1 = t0 / (sigma * piCoeff);
        return t1;
    }

    public boolean inInterval(double x, double coeff) {
        return (Math.abs(x - mean) < sigma * coeff);
    }

    public Normal() {
        reset();
    }

    public void reset() {
        mean = 0;
        n = 0;
        variance = 0;
        sigma = 0;
        sumX = 0;
        sumXX = 0;
        status = false;
    }

    public Normal(double mean, double sigma) {
        reset();
        set(mean, sigma);
    }

    void getInterval(double min, double max, double coeff) {
        compute();
        min = mean - sigma * coeff;
        max = mean + sigma * coeff;
    }

    static public double tTest(Normal a, Normal b) {
        double numerator = Math.abs(a.getMean() - b.getMean());
        double denominator = Math.sqrt(a.getVariance() / a.getN() + b.getVariance() / b.getN());
        if (denominator == 0)
            denominator = 0.000000001;
        return numerator / denominator;
    }
}
