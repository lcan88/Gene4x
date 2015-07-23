package org.geworkbench.components.pathwaydecoder.modeling;

import java.util.ArrayList;

/**
 * <p>Title: Bioworks</p>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype Analysis</p>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p>Company: Columbia University</p>
 *
 * @author not attributable
 * @version 1.0
 */

public class GeneModel {
    double expression;
    double degradation;
    double baseline;
    ArrayList edges = new ArrayList();

    public GeneModel(double e, double d, double b) {
        expression = e;
        degradation = d;
        baseline = b;
    }

    public GeneModel() {
        expression = Math.random();
        degradation = Math.random() * 0.1;
    }

    public String toString() {
        return expression + "\t" + 0.0 + "\t";
    }

    public void set(double e) {
        expression = e;
    }

    public double get() {
        return expression;
    }

    public void update() {
        expression -= (expression * degradation - baseline);// * 0.001;
        expression = Math.min(100, Math.max(expression, 0));
    }

    public double getPerturbed(int it, double noise) {
        double eps = noise - (2 * noise) * Math.random();
        double newExpression = expression + eps;
        if (newExpression > 100) {
            return 100;
        } else if (newExpression > 0) {
            return newExpression;
        } else {
            return 0;
        }
    }

    public void add(GeneInteraction gi) {
        edges.add(gi);
    }

    public ArrayList getEdges() {
        return edges;
    }
}
