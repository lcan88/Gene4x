package org.geworkbench.util.function.optimization.tests;

import org.geworkbench.util.function.optimization.fortran.Uncmin_f77;
import org.geworkbench.util.function.optimization.fortran.Uncmin_methods;

/**
 * This class tests the Uncmin_f77 class.
 *
 * @author Steve Verrill
 * @version .5 --- September 3, 1998
 */


public class UncminTest_f77 extends Object implements Uncmin_methods {

    int id_f_to_min;
    double c, d, e;
    final double one_d_twopi = 1.0 / (4.0 * Math.asin(1.0));

    UncminTest_f77(int idtemp, double ctemp, double dtemp, double etemp) {

        id_f_to_min = idtemp;
        c = ctemp;
        d = dtemp;
        e = etemp;

    }

    public static void main(String args[]) {

        int another;
        int idtemp;
        double ctemp, dtemp, etemp;
        double x0save[] = new double[5];
        int i;

        // Declarations needed for both optif0 and optif9
        //
        // Those declared of length 2 should always be declared of length 2.
        // Those declared of length 5 should be declared of length narg+1
        // where narg is the number of arguments over which you are
        // optimizing.

        int n;
        int info[] = new int[2];
        double x0[] = new double[5];
        double x[] = new double[5];
        double f[] = new double[2];

        double g[] = new double[5];
        double a[][] = new double[5][5];
        double udiag[] = new double[5];

        // Declarations only needed for optif9
        //
        // Those declared of length 2 should always be declared of length 2.
        // Those declared of length 5 should be declared of length narg+1
        // where narg is the number of arguments over which you are
        // optimizing.

        double typsiz[] = new double[5];
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


        another = 1;

        while (another == 1) {

            ctemp = dtemp = etemp = 0.0;
            x0[0] = x0[1] = x0[2] = x0[3] = x0[4] = 0.0;

            /*

               Console is a public domain class described in Cornell
               and Horstmann's Core Java (SunSoft Press, Prentice-Hall).

            */

            idtemp = Console.readInt("\nWhat function do you " + "want to minimize?\n\n" + "0 -- x^2\n" + "1 -- (x - c)^2 times (y - d)^2\n" + "2 -- (x - c)^2 times (y - d)^2 times (z - e)^2\n" + "3 -- extended Rosenbrock function\n" + "4 -- Powell singular function\n" + "5 -- trigonometric function\n" + "6 -- helical valley function\n" + "7 -- Wood function\n\n");

            if (idtemp == 0) {

                n = 1;
                x0[1] = 10.0;

            } else if (idtemp == 1) {

                n = 2;
                ctemp = Console.readDouble("\nWhat is the c value?  ");
                dtemp = Console.readDouble("\nWhat is the d value?  ");

                x0[1] = ctemp - 2;
                x0[2] = dtemp + 3;

            } else if (idtemp == 2) {

                n = 3;
                ctemp = Console.readDouble("\nWhat is the c value?  ");
                dtemp = Console.readDouble("\nWhat is the d value?  ");
                etemp = Console.readDouble("\nWhat is the e value?  ");

                x0[1] = ctemp - 2;
                x0[2] = dtemp + 3;
                x0[3] = etemp - 1.5;

            } else if (idtemp == 3) {

                n = 4;
                x0[1] = -1.2;
                x0[2] = 1.0;
                x0[3] = -1.2;
                x0[4] = 1.0;

            } else if (idtemp == 4) {

                n = 4;
                x0[1] = 3.0;
                x0[2] = -1.0;
                x0[3] = 0.0;
                x0[4] = 1.0;

            } else if (idtemp == 5) {

                n = 2;
                x0[1] = .5;
                x0[2] = .5;

            } else if (idtemp == 6) {

                n = 3;
                x0[1] = -1.0;
                x0[2] = 0.0;
                x0[3] = 0.0;

            } else {

                n = 4;
                x0[1] = -3.0;
                x0[2] = -1.0;
                x0[3] = -3.0;
                x0[4] = -1.0;

            }

            for (i = 1; i <= 4; i++) {

                x0save[i] = x0[i];

            }

            UncminTest_f77 uncmintest = new UncminTest_f77(idtemp, ctemp, dtemp, etemp);

            //  Test of optif0

            System.out.print("\n\n*********Test of optif0*********\n\n");

            System.out.print("\nThe x0 vector is \n\n");

            System.out.print(x0[1] + "  " + x0[2] + "  " + x0[3] + "  " + x0[4] + "\n");

            x[0] = x[1] = x[2] = x[3] = x[4] = 0.0;
            g[0] = g[1] = g[2] = g[3] = g[4] = 0.0;

            Uncmin_f77.optif0_f77(n, x0, uncmintest, x, f, g, info, a, udiag);

            System.out.print("\nThe f value is " + f[1] + "\n");

            System.out.print("\nThe x vector is \n\n");

            System.out.print(x[1] + "  " + x[2] + "  " + x[3] + "  " + x[4] + "\n");

            System.out.print("\nThe gradient vector is \n\n");

            System.out.print(g[1] + "  " + g[2] + "  " + g[3] + "  " + g[4] + "\n");


            //  Test of optif9

            System.out.print("\n\n*********Test of optif9*********\n\n");
            System.out.print("\n\n*********iagflg=1, iahflg=0*********\n\n");

            for (i = 1; i <= 4; i++) {

                x0[i] = x0save[i];

            }

            System.out.print("\nThe x0 vector is \n\n");

            System.out.print(x0[1] + "  " + x0[2] + "  " + x0[3] + "  " + x0[4] + "\n");

            x[0] = x[1] = x[2] = x[3] = x[4] = 0.0;
            g[0] = g[1] = g[2] = g[3] = g[4] = 0.0;

            Uncmin_f77.dfault_f77(n, x0, typsiz, fscale, method, iexp, msg, ndigit, itnlim, iagflg, iahflg, dlt, gradtl, stepmx, steptl);
            iagflg[1] = 1;
            iahflg[1] = 0;
            iexp[1] = 0;
            Uncmin_f77.optif9_f77(n, x0, uncmintest, typsiz, fscale, method, iexp, msg, ndigit, itnlim, iagflg, iahflg, dlt, gradtl, stepmx, steptl, x, f, g, info, a, udiag);

            System.out.print("\nThe f value is " + f[1] + "\n");

            System.out.print("\nThe x vector is \n\n");

            System.out.print(x[1] + "  " + x[2] + "  " + x[3] + "  " + x[4] + "\n");

            System.out.print("\nThe gradient vector is \n\n");

            System.out.print(g[1] + "  " + g[2] + "  " + g[3] + "  " + g[4] + "\n");

            System.out.print("\n\n*********Test of optif9*********\n\n");
            System.out.print("\n\n*********iagflg=1, iahflg=1*********\n\n");

            for (i = 1; i <= 4; i++) {

                x0[i] = x0save[i];

            }

            System.out.print("\nThe x0 vector is \n\n");

            System.out.print(x0[1] + "  " + x0[2] + "  " + x0[3] + "  " + x0[4] + "\n");

            x[0] = x[1] = x[2] = x[3] = x[4] = 0.0;
            g[0] = g[1] = g[2] = g[3] = g[4] = 0.0;

            Uncmin_f77.dfault_f77(n, x0, typsiz, fscale, method, iexp, msg, ndigit, itnlim, iagflg, iahflg, dlt, gradtl, stepmx, steptl);
            iagflg[1] = 1;
            iahflg[1] = 1;
            iexp[1] = 0;
            Uncmin_f77.optif9_f77(n, x0, uncmintest, typsiz, fscale, method, iexp, msg, ndigit, itnlim, iagflg, iahflg, dlt, gradtl, stepmx, steptl, x, f, g, info, a, udiag);

            System.out.print("\nThe f value is " + f[1] + "\n");

            System.out.print("\nThe x vector is \n\n");

            System.out.print(x[1] + "  " + x[2] + "  " + x[3] + "  " + x[4] + "\n");

            System.out.print("\nThe gradient vector is \n\n");

            System.out.print(g[1] + "  " + g[2] + "  " + g[3] + "  " + g[4] + "\n");


            another = Console.readInt("\nAnother test?" + "   0 - no   1 - yes\n\n");

        }

        System.out.print("\n");

    }


    public double f_to_minimize(double x[]) {

        double f, sq1, sq2, sq3, f1, f2, f3, f4, fac, c1, c2, s1, s2;
        double theta, x1, x2, x3, x4;

        if (id_f_to_min == 0) {

            f = x[1] * x[1];

        } else if (id_f_to_min == 1) {

            sq1 = (x[1] - c) * (x[1] - c);
            sq2 = (x[2] - d) * (x[2] - d);

            f = sq1 + sq2;

        } else if (id_f_to_min == 2) {

            sq1 = (x[1] - c) * (x[1] - c);
            sq2 = (x[2] - d) * (x[2] - d);
            sq3 = (x[3] - e) * (x[3] - e);

            f = sq1 + sq2 + sq3;

        } else if (id_f_to_min == 3) {

            f1 = 10.0 * (x[2] - x[1] * x[1]);
            f2 = 1.0 - x[1];
            f3 = 10.0 * (x[4] - x[3] * x[3]);
            f4 = 1.0 - x[3];

            f = f1 * f1 + f2 * f2 + f3 * f3 + f4 * f4;

        } else if (id_f_to_min == 4) {

            f1 = x[1] + 10.0 * x[2];
            f2 = Math.sqrt(5.0) * (x[3] - x[4]);
            fac = x[2] - 2.0 * x[3];
            f3 = fac * fac;
            fac = x[1] - x[4];
            f4 = Math.sqrt(10.0) * fac * fac;

            f = f1 * f1 + f2 * f2 + f3 * f3 + f4 * f4;

        } else if (id_f_to_min == 5) {

            c1 = Math.cos(x[1]);
            c2 = Math.cos(x[2]);

            s1 = Math.sin(x[1]);
            s2 = Math.sin(x[2]);

            f1 = 1.0 - (c1 + (1.0 - c1) - s1);
            f2 = 2.0 - ((c1 + 2.0 * (1 - c2) - s2) + (c2 + 2.0 * (1 - c2) - s2));

            f = f1 * f1 + f2 * f2;

        } else if (id_f_to_min == 6) {


            if (x[1] > 0.0) {

                theta = one_d_twopi * Math.atan(x[2] / x[1]);

            } else {

                theta = one_d_twopi * Math.atan(x[2] / x[1]) + .5;

            }

            f1 = 10.0 * (x[3] - 10.0 * theta);
            f2 = 10.0 * (Math.sqrt(x[1] * x[1] + x[2] * x[2]) - 1.0);
            f3 = x[3];

            f = f1 * f1 + f2 * f2 + f3 * f3;

        } else {

            x1 = x[1];
            x2 = x[2];
            x3 = x[3];
            x4 = x[4];

            f = 100.0 * (x1 * x1 - x2) * (x1 * x1 - x2) + (1.0 - x1) * (1.0 - x1) + 90.0 * (x3 * x3 - x4) * (x3 * x3 - x4) + (1.0 - x3) * (1.0 - x3) + 10.1 * ((1.0 - x2) * (1.0 - x2) + (1.0 - x4) * (1.0 - x4)) + 19.8 * (1.0 - x2) * (1.0 - x4);

        }

        return f;

    }

    public void gradient(double x[], double g[]) {

        double f, sq1, sq2, sq3, f1, f2, f3, f4, fac, c1, c2, s1, s2;
        double theta, x1, x2, x3, x4;
        double f1d1, f1d2, f1d3;
        double f2d1, f2d2, f2d3;
        double f3d1, f3d2, f3d3;
        double thd1, thd2;

        if (id_f_to_min == 0) {

            g[1] = 2.0 * x[1];

        } else if (id_f_to_min == 1) {

            g[1] = 2.0 * (x[1] - c);
            g[2] = 2.0 * (x[2] - d);

        } else if (id_f_to_min == 2) {

            g[1] = 2.0 * (x[1] - c);
            g[2] = 2.0 * (x[2] - d);
            g[3] = 2.0 * (x[3] - e);

        } else if (id_f_to_min == 3) {

            g[2] = 200.0 * (x[2] - x[1] * x[1]);
            g[4] = 200.0 * (x[4] - x[3] * x[3]);

            g[1] = -g[2] * 2.0 * x[1] - 2.0 * (1.0 - x[1]);
            g[3] = -g[4] * 2.0 * x[3] - 2.0 * (1.0 - x[3]);


        } else if (id_f_to_min == 4) {

            f1 = x[1] + 10.0 * x[2];
            f2 = x[3] - x[4];
            f3 = x[2] - 2.0 * x[3];
            f4 = x[1] - x[4];

            g[1] = 2.0 * f1 + 40.0 * f4 * f4 * f4;
            g[2] = 20.0 * f1 + 4.0 * f3 * f3 * f3;
            g[3] = 10.0 * f2 - 8.0 * f3 * f3 * f3;
            g[4] = -(10.0 * f2 + 40.0 * f4 * f4 * f4);

        } else if (id_f_to_min == 5) {

            c1 = Math.cos(x[1]);
            c2 = Math.cos(x[2]);

            s1 = Math.sin(x[1]);
            s2 = Math.sin(x[2]);

            f1 = 1.0 - (c1 + (1.0 - c1) - s1);
            f2 = 2.0 - ((c1 + 2.0 * (1 - c2) - s2) + (c2 + 2.0 * (1 - c2) - s2));

            f1d1 = c1;
            f1d2 = 0.0;

            f2d1 = s1;
            f2d2 = -3.0 * s2 + 2.0 * c2;

            g[1] = 2.0 * f1 * f1d1 + 2.0 * f2 * f2d1;
            g[2] = 2.0 * f2 * f2d2;


        } else if (id_f_to_min == 6) {


            if (x[1] > 0.0) {

                theta = one_d_twopi * Math.atan(x[2] / x[1]);

            } else {

                theta = one_d_twopi * Math.atan(x[2] / x[1]) + .5;

            }

            f1 = 10.0 * (x[3] - 10.0 * theta);
            f2 = 10.0 * (Math.sqrt(x[1] * x[1] + x[2] * x[2]) - 1.0);
            f3 = x[3];

            fac = 1.0 / (1.0 + x[2] * x[2] / (x[1] * x[1]));

            thd2 = one_d_twopi * fac / x[1];
            thd1 = -thd2 * x[2] / x[1];

            f1d1 = -100.0 * thd1;
            f1d2 = -100.0 * thd2;
            f1d3 = 10.0;

            fac = 1.0 / Math.sqrt(x[1] * x[1] + x[2] * x[2]);

            f2d1 = 10.0 * x[1] * fac;
            f2d2 = 10.0 * x[2] * fac;
            f2d3 = 0.0;

            f3d1 = 0.0;
            f3d2 = 0.0;
            f3d3 = 1.0;

            g[1] = 2.0 * (f1 * f1d1 + f2 * f2d1 + f3 * f3d1);
            g[2] = 2.0 * (f1 * f1d2 + f2 * f2d2 + f3 * f3d2);
            g[3] = 2.0 * (f1 * f1d3 + f2 * f2d3 + f3 * f3d3);

        } else {

            x1 = x[1];
            x2 = x[2];
            x3 = x[3];
            x4 = x[4];

            g[1] = 400.0 * x1 * (x1 * x1 - x2) - 2.0 * (1.0 - x1);
            g[2] = -200.0 * (x1 * x1 - x2) - 20.2 * (1.0 - x2) - 19.8 * (1.0 - x4);
            g[3] = 360.0 * x3 * (x3 * x3 - x4) - 2.0 * (1.0 - x3);
            g[4] = -180.0 * (x3 * x3 - x4) - 20.2 * (1.0 - x4) - 19.8 * (1.0 - x2);

        }


    }

    public void hessian(double x[], double h[][]) {

        double f, sq1, sq2, sq3, f1, f2, f3, f4, fac, c1, c2, s1, s2;
        double theta, x1, x2, x3, x4;
        double f1d1, f1d2, f1d3, f1d11, f1d12, f1d13, f1d22, f1d23, f1d33;
        double f2d1, f2d2, f2d3, f2d11, f2d12, f2d13, f2d22, f2d23, f2d33;
        double f3d1, f3d2, f3d3, f3d11, f3d12, f3d13, f3d22, f3d23, f3d33;
        double thd1, thd2, thd11, thd22, thd12;

        // IMPORTANT: If you want Uncmin to check your analytic
        // Hessian, you must fill only the lower triangle of h.

        if (id_f_to_min == 0) {

            h[1][1] = 2.0;

        } else if (id_f_to_min == 1) {

            h[1][1] = 2.0;
            //         h[1][2] = 0.0;

            h[2][1] = 0.0;
            h[2][2] = 2.0;

        } else if (id_f_to_min == 2) {

            h[1][1] = 2.0;
            //         h[1][2] = 0.0;
            //         h[1][3] = 0.0;

            h[2][1] = 0.0;
            h[2][2] = 2.0;
            //         h[2][3] = 0.0;

            h[3][1] = 0.0;
            h[3][2] = 0.0;
            h[3][3] = 2.0;


        } else if (id_f_to_min == 3) {

            h[1][1] = 800.0 * x[1] * x[1] - 400.0 * (x[2] - x[1] * x[1]) + 2.0;
            //         h[1][2] = -400.0*x[1];
            //         h[1][3] = 0.0;
            //         h[1][4] = 0.0;

            h[2][1] = -400.0 * x[1];
            h[2][2] = 200.0;
            //         h[2][3] = 0.0;
            //         h[2][4] = 0.0;

            h[3][1] = 0.0;
            h[3][2] = 0.0;
            h[3][3] = 800.0 * x[3] * x[3] - 400.0 * (x[4] - x[3] * x[3]) + 2.0;
            //         h[3][4] = -400.0*x[3];

            h[4][1] = 0.0;
            h[4][2] = 0.0;
            h[4][3] = -400.0 * x[3];
            h[4][4] = 200.0;

        } else if (id_f_to_min == 4) {

            f3 = x[2] - 2.0 * x[3];
            f4 = x[1] - x[4];

            h[1][1] = 2.0 + 120.0 * f4 * f4;
            //         h[1][2] = 20.0;
            //         h[1][3] = 0.0;
            //         h[1][4] = -120.0*f4*f4;

            h[2][1] = 20.0;
            h[2][2] = 200.0 + 12.0 * f3 * f3;
            //         h[2][3] = -24.0*f3*f3;
            //         h[2][4] = 0.0;

            h[3][1] = 0.0;
            h[3][2] = -24.0 * f3 * f3;
            h[3][3] = 10.0 + 48.0 * f3 * f3;
            //         h[3][4] = -10.0;

            h[4][1] = -120.0 * f4 * f4;
            h[4][2] = 0.0;
            h[4][3] = -10.0;
            h[4][4] = 10.0 + 120.0 * f4 * f4;

        } else if (id_f_to_min == 5) {

            c1 = Math.cos(x[1]);
            c2 = Math.cos(x[2]);

            s1 = Math.sin(x[1]);
            s2 = Math.sin(x[2]);

            f1 = 1.0 - (c1 + (1.0 - c1) - s1);
            f2 = 2.0 - ((c1 + 2.0 * (1 - c2) - s2) + (c2 + 2.0 * (1 - c2) - s2));

            f1d1 = c1;
            f1d2 = 0.0;

            f2d1 = s1;
            f2d2 = -3.0 * s2 + 2.0 * c2;

            f1d11 = -s1;
            f1d22 = 0.0;
            f1d12 = 0.0;

            f2d11 = c1;
            f2d22 = -3.0 * c2 - 2.0 * s2;
            f2d12 = 0.0;

            h[1][1] = 2.0 * f1d1 * f1d1 + 2.0 * f1 * f1d11 + 2.0 * f2d1 * f2d1 + 2.0 * f2 * f2d11;

            //         h[1][2] = 2.0*f1d1*f1d2 + 2.0*f1*f1d12 +
            //                   2.0*f2d1*f2d2 + 2.0*f2*f2d12;

            h[2][1] = 2.0 * f1d1 * f1d2 + 2.0 * f1 * f1d12 + 2.0 * f2d1 * f2d2 + 2.0 * f2 * f2d12;

            h[2][2] = 2.0 * f1d2 * f1d2 + 2.0 * f1 * f1d22 + 2.0 * f2d2 * f2d2 + 2.0 * f2 * f2d22;

        } else if (id_f_to_min == 6) {

            x1 = x[1];
            x2 = x[2];

            if (x[1] > 0.0) {

                theta = one_d_twopi * Math.atan(x[2] / x[1]);

            } else {

                theta = one_d_twopi * Math.atan(x[2] / x[1]) + .5;

            }

            f1 = 10.0 * (x[3] - 10.0 * theta);
            f2 = 10.0 * (Math.sqrt(x[1] * x[1] + x[2] * x[2]) - 1.0);
            f3 = x[3];

            fac = 1.0 / (1.0 + x[2] * x[2] / (x[1] * x[1]));

            thd2 = one_d_twopi * fac / x[1];
            thd1 = -thd2 * x[2] / x[1];

            thd11 = -one_d_twopi * fac * fac * 2.0 * x2 * x2 * x2 / (x1 * x1 * x1 * x1 * x1) + one_d_twopi * fac * 2.0 * x2 / (x1 * x1 * x1);
            thd12 = one_d_twopi * fac * fac * 2.0 * x2 * x2 / (x1 * x1 * x1 * x1) - one_d_twopi * fac / (x1 * x1);
            thd22 = -one_d_twopi * fac * fac * 2.0 * x2 / (x1 * x1 * x1);

            f1d1 = -100.0 * thd1;
            f1d2 = -100.0 * thd2;
            f1d3 = 10.0;

            fac = 1.0 / Math.sqrt(x[1] * x[1] + x[2] * x[2]);

            f2d1 = 10.0 * x[1] * fac;
            f2d2 = 10.0 * x[2] * fac;
            f2d3 = 0.0;

            f3d1 = 0.0;
            f3d2 = 0.0;
            f3d3 = 1.0;

            f1d11 = -100.0 * thd11;
            f1d12 = -100.0 * thd12;
            f1d13 = 0.0;

            f1d22 = -100.0 * thd22;
            f1d23 = 0.0;

            f1d33 = 0.0;

            f2d11 = -10.0 * fac * fac * fac * x1 * x1 + 10.0 * fac;
            f2d12 = -10.0 * x1 * x2 * fac * fac * fac;
            f2d13 = 0.0;

            f2d22 = -10.0 * fac * fac * fac * x2 * x2 + 10.0 * fac;
            f2d23 = 0.0;

            f2d33 = 0.0;

            f3d11 = 0.0;
            f3d12 = 0.0;
            f3d13 = 0.0;

            f3d22 = 0.0;
            f3d23 = 0.0;

            f3d33 = 0.0;

            h[1][1] = 2.0 * f1d1 * f1d1 + 2.0 * f1 * f1d11 + 2.0 * f2d1 * f2d1 + 2.0 * f2 * f2d11 + 2.0 * f3d1 * f3d1 + 2.0 * f3 * f3d11;

            //         h[1][2] = 2.0*f1d1*f1d2 + 2.0*f1*f1d12 +
            //                   2.0*f2d1*f2d2 + 2.0*f2*f2d12 +
            //                   2.0*f3d1*f3d2 + 2.0*f3*f3d12;

            //         h[1][3] = 2.0*f1d1*f1d3 + 2.0*f1*f1d13 +
            //                   2.0*f2d1*f2d3 + 2.0*f2*f2d13 +
            //                   2.0*f3d1*f3d3 + 2.0*f3*f3d13;

            h[2][1] = 2.0 * f1d1 * f1d2 + 2.0 * f1 * f1d12 + 2.0 * f2d1 * f2d2 + 2.0 * f2 * f2d12 + 2.0 * f3d1 * f3d2 + 2.0 * f3 * f3d12;

            h[2][2] = 2.0 * f1d2 * f1d2 + 2.0 * f1 * f1d22 + 2.0 * f2d2 * f2d2 + 2.0 * f2 * f2d22 + 2.0 * f3d2 * f3d2 + 2.0 * f3 * f3d22;

            //         h[2][3] = 2.0*f1d2*f1d3 + 2.0*f1*f1d23 +
            //                   2.0*f2d2*f2d3 + 2.0*f2*f2d23 +
            //                   2.0*f3d2*f3d3 + 2.0*f3*f3d23;

            h[3][1] = 2.0 * f1d1 * f1d3 + 2.0 * f1 * f1d13 + 2.0 * f2d1 * f2d3 + 2.0 * f2 * f2d13 + 2.0 * f3d1 * f3d3 + 2.0 * f3 * f3d13;

            h[3][2] = 2.0 * f1d2 * f1d3 + 2.0 * f1 * f1d23 + 2.0 * f2d2 * f2d3 + 2.0 * f2 * f2d23 + 2.0 * f3d2 * f3d3 + 2.0 * f3 * f3d23;

            h[3][3] = 2.0 * f1d3 * f1d3 + 2.0 * f1 * f1d33 + 2.0 * f2d3 * f2d3 + 2.0 * f2 * f2d33 + 2.0 * f3d3 * f3d3 + 2.0 * f3 * f3d33;

        } else {

            x1 = x[1];
            x2 = x[2];
            x3 = x[3];
            x4 = x[4];

            h[1][1] = 400.0 * (3.0 * x1 * x1 - x2) + 2.0;
            //         h[1][2] = -400.0*x1;
            //         h[1][3] = 0.0;
            //         h[1][4] = 0.0;

            h[2][1] = -400.0 * x1;
            h[2][2] = 220.2;
            //         h[2][3] = 0.0;
            //         h[2][4] = 19.8;

            h[3][1] = 0.0;
            h[3][2] = 0.0;
            h[3][3] = 360.0 * (3.0 * x3 * x3 - x4) + 2.0;
            //         h[3][4] = -360.0*x3;

            h[4][1] = 0.0;
            h[4][2] = 19.8;
            h[4][3] = -360.0 * x3;
            h[4][4] = 200.2;

        }


    }


}
