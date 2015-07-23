package org.geworkbench.engine.cascript;

import org.geworkbench.engine.management.ComponentRegistry;

import java.io.PrintWriter;
import java.lang.reflect.Method;

/** the wrapper class for Methods within CasModules
*
* @author Behrooz Badii - badiib@gmail.com
*/
class CasMethod extends CasDataType {
    Method m;
    String formodule;
    String othername;
    CasModule association;
    Class[] parameters;

    CasMethod(String casname, String casmethod, CasModule a, Class[] p) {
        name = casname + " " + casmethod;
        othername = casmethod;
        formodule = casname;
        association = a;
        parameters = p;
        m = ComponentRegistry.getRegistry().getScriptMethodByNameAndParameters(a.getPlugin(), casmethod, parameters);
    }

    public CasDataType copy() {
        return new CasMethod(formodule, othername, association, parameters);
    }

    public String getformodule() {
        return formodule;
    }

    public String getothername() {
        return othername;
    }

    public Method getm() {
        return m;
    }

    public Class[] getp() {
        return parameters;
    }


    public Object getPlugin() {
        return association.pd.getPlugin();
    }

    public Object geta() {
        return association;
    }

    public void print(PrintWriter w) {
        //if (name != null) w.print(name + " = ");
        this.toString();
    }


}
