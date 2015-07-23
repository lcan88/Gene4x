package org.geworkbench.engine.cascript;

import java.io.PrintWriter;

/**
 * CasContinue Class
 *
 * @author Behrooz Badii - badiib@gmail.com
 */

class CasContinue extends CasDataType {
    CasContinue() {
    }

    public String typename() {
        return "CasContinue";
    }

    public void print(PrintWriter w) {
        w.println(typename());
    }
}
