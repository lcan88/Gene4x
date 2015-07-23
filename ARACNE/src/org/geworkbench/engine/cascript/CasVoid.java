package org.geworkbench.engine.cascript;

import java.io.PrintWriter;

/**
 * The wrapper class for void returntype
 *
 * @author Behrooz Badii - badiib@gmail.com
 * @version $Id: CasVoid.java,v 1.3 2005/10/14 18:38:12 bb2122 Exp $
 */
class CasVoid extends CasDataType {
    Object var;

    public CasVoid() {
        var = null;
    }

    public Object getvar() {
        return var;
    }

    public String typename() {
        return "void";
    }

    public CasDataType copy() {
        return new CasVoid();
    }

    public void print(PrintWriter w) {
        //if (name != null) w.print(name + " = ");
        w.println("is void parameter");
    }
}
