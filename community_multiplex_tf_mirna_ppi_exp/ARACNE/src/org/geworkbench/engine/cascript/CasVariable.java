package org.geworkbench.engine.cascript;

import java.io.PrintWriter;

/**
 * The wrapper class for unsigned variables
 *
 * @author Behrooz Badii - badiib@gmail.com
 * @version $Id: CasVariable.java,v 1.2 2005/08/18 20:44:02 bb2122 Exp $
 * @modified from Hanhua Feng - hf2048@columbia.edu
 */
class CasVariable extends CasDataType {
    public CasVariable(String name) {
        super(name);
    }

    public String typename() {
        return "undefined-variable";
    }

    public CasDataType copy() {
        throw new CasException("Variable " + name + " has not been defined");
    }

    public void print(PrintWriter w) {
        w.println(name + " = <undefined>");
    }
}
