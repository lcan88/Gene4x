package org.geworkbench.engine.cascript;

import org.geworkbench.engine.management.BisonFactory;

import java.io.PrintWriter;

/**
 * CasDataPlug is a rudimentary way of holding datastructures
 *
 * @author Behrooz Badii - badiib@gmail.com
 */

class CasDataPlug extends CasDataType {
    public CasDataPlug() {
    }

    String type;
    //CasDataTypeImport CDTI;
    Object var;

    CasDataPlug(String t) {
        name = null;
        type = t;
        var = null;
    }

    //this is used for the copy function
    CasDataPlug(String n, String t, Object variable) {
        name = n;
        type = t;
        var = variable;
    }

    CasDataPlug(String n, String t, CasDataTypeImport C) {
        //CDTI = C;
        name = n;
        type = t;
        var = null;
        CasDataPlugConstructorHelper(C);
    }

    //this method makes sure that the type of the CasDataPlug is one of the supported types found in
    private void CasDataPlugConstructorHelper(CasDataTypeImport C) {
            Object t = C.get(type);
            if (t != null) {
                try {
                    //the new instance should be created here, through the class
                    var = BisonFactory.createInstance((String)t);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new CasException("Error occurred creating new instance of Class " + type + " for datatype " + name);
                }
            }
            else
                throw new CasException("Class " + type + " not found for datatype " + name);

    }

    public String typename() {
        return "datatype with type " + type;
    }

    public String getType() {
        return type;
    }

    public Class getRealClass() {
        return var.getClass();
    }

    public Object getVar() {
        return var;
    }

    public void setVar(Object a) {
        try {
            var = var.getClass().cast(a);
        }
        catch (ClassCastException cce){}
    }
    public CasDataType copy() {
        return new CasDataPlug(name, type, var);
    }

    public void print(PrintWriter w) {
        //if (name != null) w.print(name + " = ");
        this.toString();
    }

}
