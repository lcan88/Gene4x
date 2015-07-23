package org.geworkbench.components.pathwaydecoder.tp;

import cern.jet.stat.Gamma;


public class Test {
    public Test() {
    }

    public static void main(String[] args) {
        new Test().doIt();
    }

    void doIt() {
        //        double val = log2(.0333) * .0333 * 15;
        double val = Gamma.gamma(.5) / (Gamma.gamma(.25) * Gamma.gamma(.25));
        System.out.println(val);


    }

    public double log2(double d) {
        return Math.log(d) / Math.log(2.0);
    }

}
