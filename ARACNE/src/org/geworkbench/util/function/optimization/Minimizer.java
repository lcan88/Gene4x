package org.geworkbench.util.function.optimization;

import org.geworkbench.util.function.optimization.fortran.Uncmin_f77;
import org.geworkbench.util.function.optimization.fortran.Uncmin_methods;

/**
 * An object-oriented wrapper for UNCMIN Fortran-oid multi-variable
 * function minimization routines.
 * All the 1980's-paradigm ugliness is encapsulated within this class.
 * If it ain't broken, don't fix it... :)
 *
 * @author Dejan Vucinic <dvucinic@users.sourceforge.net>
 */
public class Minimizer implements Uncmin_methods {

    private int dimension;
    private Parameter[] parameters;
    private Minimizable function;

    public Minimizer(Minimizable function) {
        this.function = function;
        parameters = function.getParameters();
        dimension = parameters.length;
    }

    public void minimize() throws OptimizationException {

        double[] x0 = new double[dimension + 1]; // initial values
        int info[] = new int[2]; // return code
        double x[] = new double[dimension + 1]; // current parameter values
        double f[] = new double[2]; // current function value
        double g[] = new double[dimension + 1]; // gradient
        double a[][] = new double[dimension + 1][dimension + 1]; // hessian
        double udiag[] = new double[dimension + 1]; // who knows

        double typsiz[] = new double[dimension + 1];
        double fscale[] = new double[2];
        int method[] = new int[2];
        int iexp[] = new int[2];
        int msg[] = new int[2];
        int ndigit[] = new int[2];
        int itnlim[] = new int[2];
        int iagflg[] = new int[2];
        int iahflg[] = new int[2];
        double dlt[] = new double[2];
        double gradtl[] = new double[2];
        double stepmx[] = new double[2];
        double steptl[] = new double[2];

        Uncmin_f77.dfault_f77(dimension, x0, typsiz, fscale, method, iexp, msg, ndigit, itnlim, iagflg, iahflg, dlt, gradtl, stepmx, steptl);

        // iagflg[1] = 0;
        // iahflg[1] = 0;
        // iexp[1] = 0;
        // itnlim[1] = 50;
        // fscale[1] = 1;

        try {
            // Copy the initial parameters.
            for (int i = 0; i < dimension; i++) x0[i + 1] = parameters[i].get();

            // Run the minimization.
            Uncmin_f77.optif9_f77(dimension, x0, this, typsiz, fscale, method, iexp, msg, ndigit, itnlim, iagflg, iahflg, dlt, gradtl, stepmx, steptl, x, f, g, info, a, udiag);
        } catch (IllegalAccessException iae) {
            throw new OptimizationException(iae);
        }

    }

    public double f_to_minimize(double[] x) {
        try {
            for (int i = 0; i < dimension; i++) parameters[i].set(x[i + 1]);
        } catch (IllegalAccessException iae) {
            iae.printStackTrace();
        }
        return function.calculate();
    }

    public void gradient(double x[], double g[]) {
    }

    public void hessian(double x[], double h[][]) {
    }

}
