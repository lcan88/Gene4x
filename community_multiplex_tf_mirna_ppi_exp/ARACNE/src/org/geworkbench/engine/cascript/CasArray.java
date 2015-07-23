package org.geworkbench.engine.cascript;

import java.io.PrintWriter;

/** the wrapper class for Arrays
*
* @author Behrooz Badii - badiib@gmail.com
* CasArray is a wrapper class for arrays
*/

class CasArray extends CasDataType {
    CasDataType [] var;
    CasDataType type;

    public CasArray(int length, CasDataType typereturn) {
        var = new CasDataType[length];
        type = typereturn;
    }

    public CasArray(CasDataType []v) {
        var = v;
    }

    public CasDataType[] getvar() {
        return var;
    }

    public CasDataType getelementType() {
        return type;
    }

    public String typename() {
        return "CasArray";
    }

    public CasDataType copy() {
        return new CasArray(var);
    }

    public CasDataType accessArray(int i) {
        return var[i];
    }

    public void setArrayValue(CasDataType a, int i) {
        if (var[i].getClass().isAssignableFrom(a.getClass())) var[i] = a;
        else throw new CasException("you are assigning a different type to a value in the array" + getName());
    }

    public void initializeArray() {
        for (int i = 0; i < var.length; i++) {
            var[i] = type.copy();
            var[i].setPartOf(this.getName());
            var[i].setPosition(i);
        }
    }

    public void print(PrintWriter w) {
        //if (name != null) w.print(name + " = ");
        w.println(var.toString());
    }

}
