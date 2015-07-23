package org.geworkbench.engine.cascript;

import java.io.PrintWriter;

/**
 * This holds the return value for a function call to geworkbench
 * retValue has to be tested to make sure it's acceptable as an object for CAS
 * aka, we want it to be a datatype, an accepted module, or a primitive
 * so the object is wrapped inside CasCallReturn
 *
 * @author Behrooz Badii
 */
public class CasCallReturn extends CasDataType {

    Object retValue;

    public CasCallReturn(Object dataType) {
        retValue = dataType;
    }

    public Object getRetValue() {
        return retValue;
    }

    public void print(PrintWriter w) {
            //if (name != null) w.print(name + " = ");
            this.toString();
    }
}
