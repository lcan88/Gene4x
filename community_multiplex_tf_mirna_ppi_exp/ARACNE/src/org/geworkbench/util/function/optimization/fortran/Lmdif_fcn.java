package org.geworkbench.util.function.optimization.fortran;

public interface Lmdif_fcn {

    void fcn(int m, int n, double x[], double fvec[], int iflag[]);

}
