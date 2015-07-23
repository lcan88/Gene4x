package org.geworkbench.util.function;

public class FunctionIntegrator {
    public FunctionIntegrator() {
    }

    public double integrate(IProbabilityFunctionBivariate function, double min_x, double max_x, double min_y, double max_y) {
        double step = 0.001;
        double sum = 0.0;
        double x = 0.0;
        double y = 0.0;

        for (x = min_x; x < max_x; x += step) {
            for (y = min_y; y < max_y; y += step) {
                double prob = function.getProbability(x, y);
                sum += prob * step * step;
            }
        }
        return sum;
    }

    public double integrate(IProbabilityFunctionUnivariate function, double minX, double maxX) {
        double step = 0.01;
        double sum = 0.0;
        for (double x = minX; x <= maxX; x += step) {
            double prob = function.getProbability(x);
            sum += prob * step;
        }
        return sum;
    }

    public double getEntropy(IProbabilityFunctionUnivariate function, double min_x, double max_x, double min_y, double max_y) {
        double e_x = 0.0;
        double step = .001;
        for (double x = min_x; x < max_x; x += step) {
            double p_x = function.getProbability(x);
            e_x -= p_x * Math.log(p_x) * step;
        }
        return e_x;
    }


    public double getMI(IProbabilityFunctionBivariate function, double min_x, double max_x, double min_y, double max_y, double step) {
        double x = 0.0;
        double y = 0.0;
        double e_x = 0;
        double e_y = 0;
        double e_xy = 0;
        //        double step = .1;

        //        min_x = -3.0;
        //        min_y = -3.0;
        //        max_x = 3.0;
        //        max_y = 3.0;

        //        for (x = min_x; x < max_x; x += step) {
        //            double p_x = 0.0;
        //            for (y = min_y; y < max_y; y += step) {
        //                double p_xy = function.getProbability(x, y);
        //                p_x += p_xy * step;
        //            }
        //            if (p_x > 0) {
        //                e_x -= p_x * Math.log(p_x) * step;
        //            }
        //        }

        //        for(y = min_y; y < max_y; y += step){
        //            double p_y = 0.0;
        //            for(x = min_x; x < max_x; x += step){
        //                double p_xy = function.getProbability(y, x);
        //                p_y += p_xy * step;
        //            }
        //            if (p_y > 0) {
        //                e_y -= p_y * Math.log(p_y) * step;
        //            }
        //        }

        for (x = min_x; x < max_x; x += step) {
            double p_x = 0;
            double p_y = 0;
            for (y = min_y; y < max_y; y += step) {
                double p_xy = function.getProbability(x, y);
                double p_yx = function.getProbability(y, x);
                p_x += p_xy * step;
                p_y += p_yx * step;
                if (p_xy > 0) {
                    e_xy -= p_xy * Math.log(p_xy) * step * step;
                }

            }
            if (p_x > 0) {
                e_x -= p_x * Math.log(p_x) * step;
            }
            if (p_y > 0) {
                e_y -= p_y * Math.log(p_y) * step;
            }
        }

        //        double mi = (e_x + e_y - e_xy) / (e_x + e_y);
        double mi = e_x + e_y - e_xy;
        //        System.out.println("E_x " + e_x + " E_y " + e_y + " E_xy " + e_xy);
        return mi;
    }
}
