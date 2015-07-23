package org.geworkbench.engine.cascript;

import java.io.PrintWriter;

/**
 * the wrapper class for string
 *
 * @author Behrooz Badii - badiib@gmail.com
 * @version $Id: CasString.java,v 1.7 2005/12/07 15:47:09 manju Exp $
 * @modified from Hanhua Feng - hf2048@columbia.edu
 */
class CasString extends CasDataType {
    String var;

    public CasString(String str) {
        this.var = str;
    }

    public String typename() {
        return "string";
    }


    public String getvar() {
        return var;
    }

    public CasDataType copy() {
        return new CasString(var);
    }

    public void print(PrintWriter w) {
        //if (name != null) w.print(name + " = ");
        w.print(var);
        w.println();
    }

    public CasDataType plus(CasDataType b) {
        if (b instanceof CasBool) return new CasString(Boolean.toString(((CasBool)b).getvar()));
        else if (b instanceof CasInt) return new CasString(Integer.toString(((CasInt)b).getvar()));
        else if (b instanceof CasDouble) return new CasString(Double.toString(((CasDouble)b).getvar()));
        else if (b instanceof CasString) return new CasString(var + ((CasString) b).getvar());
        else return error(b, "+");
    }

    public CasDataType add(CasDataType b) {
        if (b instanceof CasString) {
            var = var + ((CasString) b).var;
            return this;
        }

        return error(b, "+=");
    }
}
