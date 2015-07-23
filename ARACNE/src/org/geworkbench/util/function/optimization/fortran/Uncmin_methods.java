package org.geworkbench.util.function.optimization.fortran;

public interface Uncmin_methods {

    double f_to_minimize(double x[]);

    void gradient(double x[], double g[]);

    void hessian(double x[], double h[][]);

}
