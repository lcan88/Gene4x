package org.geworkbench.engine.cascript;

import java.io.PrintWriter;

/**
 * CasBreak Class
 *
 * @author Behrooz Badii - badiib@gmail.com
 */

class CasBreak extends CasDataType {
    CasBreak() {
    }

    public String typename() {
        return "CasBreak";
    }

    public void print(PrintWriter w) {
        w.println(typename());
    }
}
