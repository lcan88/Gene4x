package org.geworkbench.util.function.algorithm;

import org.geworkbench.util.function.optimization.fortran.Fmin_methods;

public class FunctionBracketer {
    final double GOLD = 1.618034;

    public FunctionBracketer() {
    }

    public double[][] bracketFunction(double ax, double bx, Fmin_methods function) {
        double fa = function.f_to_minimize(ax);
        double fb = function.f_to_minimize(bx);

        //Go downhill from a to b
        if (fb > fa) {
            double tmp = fb;
            fb = fa;
            fa = tmp;

            tmp = bx;
            bx = ax;
            ax = tmp;
        }

        //first guess of c
        double cx = bx + GOLD * (bx - ax);
        double fc = function.f_to_minimize(cx);
        while (fb > fc) {
            ax = bx;
            bx = cx;
            cx = bx + GOLD * (bx - ax);

            fa = fb;
            fb = fc;
            fc = function.f_to_minimize(cx);
        }
        double[][] retVal = new double[3][2];
        retVal[0][0] = ax;
        retVal[1][0] = bx;
        retVal[2][0] = cx;

        retVal[0][1] = fa;
        retVal[1][1] = fb;
        retVal[2][1] = fc;

        return retVal;
    }
}
