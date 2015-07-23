package org.geworkbench.engine.cascript;

import java.io.PrintWriter;

/**CasArgument Class, facilitates argument handling
* @author Behrooz Badii - badiib@gmail.com
* argument type facilitates argument checking for function calls
*/

class CasArgument {
    int brackets;
    CasDataType type;
    String id;

    CasArgument(CasDataType t, String i, int b) {
        type = t;
        id = i;
        brackets = b;
    }

    public CasDataType getType() {
        return type;
    }

    public int getBrackets() {
        return brackets;
    }

    public String getId() {
        return id;
    }

    public void print(PrintWriter w) {
        //if (name != null) w.print(name + " = ");
        this.toString();
    }

}
