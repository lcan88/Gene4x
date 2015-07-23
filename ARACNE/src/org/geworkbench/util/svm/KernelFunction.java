package org.geworkbench.util.svm;

/**
 * @author John Watkinson
 */
public interface KernelFunction {

    public double eval(float[] a, float[] b);
    
}
