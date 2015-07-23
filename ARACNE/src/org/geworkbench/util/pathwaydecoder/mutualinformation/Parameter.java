package org.geworkbench.util.pathwaydecoder.mutualinformation;

import java.io.Serializable;

public class Parameter implements Serializable {
    public Parameter() {
    }

    static final public int HIGH = 1;
    static final public int LOW = 2;
    static final public int BOTH = 3;

    public String dataMatrixName = "";
    public String adjMatrixName = "";
    public double miThreshold = 0.01; // The minimum MI
    public int mArrayNo = 0; // The number of array (0 == all)
    public int[] mArrayId = null; // The list of array ids
    public boolean reduce = false; // If true the reduced matrix is computed
    public int controlId = -1; // Gene constraint
    public double percent = 0.33; // percent for high and low
    public double miErrorPercent = 0.1; // Error tolerance
    public int type = BOTH;
    public double mean = 50; // Mminimum Variance
    public double variance = 0.3; // Minimum Standard DeviationString
    public String method = "gaussian"; // gaussian ? fast
    public void setMatrixName(String _theMatrix) {
        dataMatrixName = _theMatrix;
    }

    public void setOutputName(String _output) {
        adjMatrixName = _output;
    }

    public void setMIThreshold(double _miThreshold) {
        miThreshold = _miThreshold;
    }

    public void setMArray(int _mArrayNo, int[] _mArrayId) {
        mArrayNo = _mArrayNo;
        mArrayId = _mArrayId;
    }

    public void setReduce(boolean _reduce) {
        reduce = _reduce;
    }

    public void setType(int _type) {
        type = _type;
    }

    public void setControlId(int _controlId) {
        controlId = _controlId;
    }

    public void setMIErrorPercent(double error) {
        miErrorPercent = error;
    }

    public void setParams(double _mean, double _variance) {
        mean = _mean;
        variance = _variance;
    }

}
