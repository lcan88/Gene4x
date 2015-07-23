package org.geworkbench.engine.cascript;

import java.io.PrintWriter;
import java.lang.reflect.Method;

/** the wrapper class for Methods within CasDataPlugs
*
* @author Behrooz Badii - badiib@gmail.com
*/
class CasDataMethod extends CasDataType {
    Method m;
    String fordataplug;
    String othername;
    CasDataPlug association;
    Class[] parameters;

    CasDataMethod(String casname, String casmethod, CasDataPlug a, Class[] p) {
        name = casname + " " + casmethod;
        othername = casmethod;
        fordataplug = casname;
        association = a;
        parameters = p;
        try {
            m = a.getVar().getClass().getMethod(othername, parameters);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new CasException ("The method " + othername + " that you are trying to call for datatype " + fordataplug + " does not exist");
        }
    }

    public CasDataType copy() {
        return new CasDataMethod(fordataplug, othername, association, parameters);
    }

    public Method getm() {
        return m;
    }

    public CasDataPlug geta() {
        return association;
    }

    public Class[] getp() {
        return parameters;
    }

    public void print(PrintWriter w) {
        //if (name != null) w.print(name + " = ");
        this.toString();
    }
}
