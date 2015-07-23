package org.geworkbench.engine.cascript;

import org.geworkbench.engine.management.ComponentRegistry;

import java.io.PrintWriter;
import java.lang.reflect.Method;

/* the wrapper class for ObjectValue
*
* @author Behrooz Badii - badiib@gmail.com
*/
class CasValue extends CasDataType {
    Method m;
    String formodule;
    String othername;
    CasModule association;

    CasValue(String casname, String casmethod, CasModule a) {
        name = casname + " " + casmethod;
        othername = casmethod;
        formodule = casname;
        association = a;
        m = ComponentRegistry.getRegistry().getScriptMethodByName(a.getPlugin(), casmethod);
    }

    public Method getm() {
        return m;
    }

    public Object getPlugin() {
        return association.pd.getPlugin();
    }

    public Object geta() {
        return association;
    }

    public CasDataType copy() {
        return new CasValue(formodule, othername, association);
    }

    public void print(PrintWriter w) {
        //if (name != null) w.print(name + " = ");
        this.toString();
    }


}
