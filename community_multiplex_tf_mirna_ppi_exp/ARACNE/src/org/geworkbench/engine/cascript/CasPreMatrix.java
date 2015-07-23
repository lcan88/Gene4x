package org.geworkbench.engine.cascript;

import java.io.PrintWriter;

/** the wrapper class for Arrays
*
* @author Behrooz Badii - badiib@gmail.com
* CasMatrix is a wrapper class for a 2D arrays
*/

class CasPreMatrix extends CasPreData {
    CasPreData [][] var;
    CasDataType type;

    public CasPreMatrix(int length1, int length2, CasPreData typereturn) {
        var = new CasPreData[length1][length2];
        type = typereturn.getData();
    }

    public CasPreMatrix(CasPreData [][]v, CasPreData typereturn) {
        var = v;
        type = typereturn.getData();
    }

    public CasPreData[][] getvar() {
        return var;
    }

    public CasDataType getelementType() {
        return type;
    }

    public String typename() {
        return "CasMatrix";
    }

    public CasPreData copy() {
        return new CasPreMatrix(var, new CasPreData(type, false, false, false));
    }

    public CasPreData subArrayofMatrix(int i) {
        return new CasPreArray(var[i], new CasPreData(type, false, false, false));
    }

    public CasPreData accessMatrix(int i, int j) {
        return var[i][j];
    }

    //for CasModule support, you have to check that the modules both have the same type
    public void setsubArrayofMatrixValue(CasPreData a, int i) {
        if (a instanceof CasPreArray) {
            if (((CasArray) a.getData()).getelementType().getClass().equals(type.getClass())) 
                var[i] = ((CasPreArray)a).getvar();
            else throw new CasException("you have the wrong type for assigning an array to a subMatrix");
        } else throw new CasException("you are assigning something other than an array to part of the matrix");
    }

    public void setMatrixValue(CasPreData a, int i, int j) {
        if (var[i][j].getData().getClass().isAssignableFrom(a.getData().getClass())) {
            var[i][j] = a;
        }
        else throw new CasException("you are assigning a different type to a value in the matrix" + getName());
    }

    public void initializeMatrix() {
        for (int i = 0; i < var.length; i++) {
            for (int j = 0; j < var[i].length; j++) {
                var[i][j] = new CasPreData(type.copy(),true,false,false);
                var[i][j].getData().setPartOf(this.getName());
                var[i][j].getData().setPosition(i);
            }
        }
    }

    public void print(PrintWriter w) {
        //if (name != null) w.print(name + " = ");
        w.println(var.toString());
    }


}

