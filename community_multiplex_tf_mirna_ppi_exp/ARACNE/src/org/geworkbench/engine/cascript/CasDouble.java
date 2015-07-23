package org.geworkbench.engine.cascript;

import java.io.PrintWriter;

/**
 * The wrapper class for double
 *
 * @author Behrooz Badii - badiib@gmail.com
 * @version $Id: CasDouble.java,v 1.5 2005/11/09 22:20:40 bb2122 Exp $
 * @modified Hanhua Feng - hf2048@columbia.edu
 */
class CasDouble extends CasDataType {
    double var;

    public CasDouble(double x) {
        var = x;
    }

    public double getvar() {
        return var;
    }

    public String typename() {
        return "double";
    }

    public CasDataType copy() {
        return new CasDouble(var);
    }

    public static double doubleValue(CasDataType b) {
        if (b instanceof CasDouble) return ((CasDouble) b).var;
        if (b instanceof CasInt) return (double) ((CasInt) b).var;
        b.error("cast to double");
        return 0;
    }

    public void print(PrintWriter w) {
        //if (name != null) w.print(name + " = ");
        w.println(Double.toString(var));
    }

    public CasDataType uminus() {
        return new CasDouble(-var);
    }

    public CasDataType plus(CasDataType b) {
        if (b instanceof CasString) return new CasString(var + ((CasString)b).getvar());
        else return new CasDouble(var + doubleValue(b));
    }

    public CasDataType add(CasDataType b) {
        var += doubleValue(b);
        return this;
    }

    public CasDataType minus(CasDataType b) {
        return new CasDouble(var - doubleValue(b));
    }

    public CasDataType sub(CasDataType b) {
        var -= doubleValue(b);
        return this;
    }

    public CasDataType times(CasDataType b) {
        return new CasDouble(var * doubleValue(b));
    }

    public CasDataType mul(CasDataType b) {
        var *= doubleValue(b);
        return this;
    }

    public CasDataType lfracts(CasDataType b) {
        return new CasDouble(var / doubleValue(b));
    }

    public CasDataType rfracts(CasDataType b) {
        return lfracts(b);
    }

    public CasDataType ldiv(CasDataType b) {
        var /= doubleValue(b);
        return this;
    }

    public CasDataType rdiv(CasDataType b) {
        return ldiv(b);
    }

    public CasDataType modulus(CasDataType b) {
        return new CasDouble(var % doubleValue(b));
    }


    public CasDataType rem(CasDataType b) {
        var %= doubleValue(b);
        return this;
    }

    public CasDataType gt(CasDataType b) {
        return new CasBool(var > doubleValue(b));
    }

    public CasDataType ge(CasDataType b) {
        return new CasBool(var >= doubleValue(b));
    }

    public CasDataType lt(CasDataType b) {
        return new CasBool(var < doubleValue(b));
    }

    public CasDataType le(CasDataType b) {
        return new CasBool(var <= doubleValue(b));
    }

    public CasDataType eq(CasDataType b) {
        return new CasBool(var == doubleValue(b));
    }

    public CasDataType ne(CasDataType b) {
        return new CasBool(var != doubleValue(b));
    }

    /*public void ia() {
        var++;
    }

    public void da() {
        var--;
    }
     */

    public CasDataType ib() {
        ++var;
        return this;
    }

    public CasDataType db() {
        --var;
        return this;
    }
}
