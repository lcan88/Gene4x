package org.geworkbench.components.pathwaydecoder.modeling;

import java.util.ArrayList;

public class GeneInteraction {
    public class Parameter {
        double theta;
        double kr;
        double m;
        boolean pos;

        // Simulates a Hill law with e^m/(theta^m + e^m)
        public Parameter(double _t, double _k, double _m, boolean _p) {
            kr = _k;
            theta = _t;
            m = _m;
            pos = _p;
        }
    }

    ArrayList genes = new ArrayList();
    ArrayList parms = new ArrayList();
    GeneModel gene = null;
    double realK = 1.0;
    double k = 1.0;
    double pump = 0.0;

    public GeneInteraction(GeneModel g) {
        gene = g;
    }

    public void add(GeneModel gene, double theta, double kr, double m, boolean pos) {
        genes.add(gene);
        parms.add(new Parameter(theta, kr, m, pos));
    }

    public void update(int step, double noise) {
        double hill = 1.0;
        for (int i = 0; i < genes.size(); i++) {
            GeneModel input = (GeneModel) genes.get(i);
            Parameter parm = (Parameter) parms.get(i);
            //hill *= getHill(parm, input.getPerturbed(step, noise));
            hill *= getHill(parm, input.expression);
        }
        if (genes.size() > 0) {
            if (gene.expression < 0.00001) {
                int x = 1;
            }
            gene.expression += k * hill;// * 0.001;
            gene.set(gene.getPerturbed(step, noise));
        } else {
            gene.expression += pump;// * 0.001;
        }
    }

    public static double getHill(Parameter p, double expression) {
        double hill = 0;
        if (p.m == 0) {
            //   Linear relationship
            if (p.pos) {
                hill = p.theta * (expression - 50);
            } else {
                hill = -p.theta * (expression - 50);
            }
        } else {
            double upper = Math.pow(expression, p.m);
            double lower = Math.pow(p.theta, p.m) + Math.pow(expression, p.m);
            hill = upper / lower;
            if (!p.pos) {
                hill = 1.0 - hill;
            }
        }
        return p.kr * hill;
    }

    public void perturb(double noise) {
        //    k = (1.0 - noise) + (2 * noise) * Math.random();
    }

    public void setPump(double p) {
        pump = p;
    }
}
