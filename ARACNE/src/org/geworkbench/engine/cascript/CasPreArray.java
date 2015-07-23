package org.geworkbench.engine.cascript;

import java.io.PrintWriter;

/** the wrapper class for Arrays
*
* @author Behrooz Badii - badiib@gmail.com
* CasArray is a wrapper class for arrays
*/

class CasPreArray extends CasPreData {
    CasPreData [] var;
    CasDataType type;

    public CasPreArray(int length, CasPreData typereturn) {
        var = new CasPreData[length];
        type = typereturn.getData();
        this.setKnown(true);
        this.setInitialized(true);
        this.setDeclared(true);
    }

    public CasPreArray(CasPreData []v, CasPreData typereturn) {
        var = v;
        type = typereturn.getData();
    }

    public CasPreData[] getvar() {
        return var;
    }

    public CasDataType getelementType() {
        return type;
    }

    public String typename() {
        return "CasArray";
    }

    public CasPreData copy() {
        return new CasPreArray(var, new CasPreData(type, false, false, false));
    }

    public CasPreData accessArray(int i) {
        return var[i];
    }

    public void setArrayValue(CasPreData a, int i) {
        if (var[i].getData().getClass().isAssignableFrom(a.getData().getClass())) var[i] = a;
        else throw new CasException("you are assigning a different type to a value in the array" + getName());
    }

    public void initializeArray() {
        for (int i = 0; i < var.length; i++) {
            var[i] = new CasPreData(type.copy(),true,false,false);
            var[i].getData().setPartOf(this.getName());
            var[i].getData().setPosition(i);
        }
    }

    public void print(PrintWriter w) {
        //if (name != null) w.print(name + " = ");
        w.println(var.toString());
    }

}
