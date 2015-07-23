package org.geworkbench.engine.cascript;

import java.io.PrintWriter;

/**
 * the wrapper class of int
 *
 * @author Behrooz Badii - badiib@gmail.com
 * @version $Id: CasInt.java,v 1.5 2005/11/09 22:20:40 bb2122 Exp $
 * @modified from Hanhua Feng - hf2048@columbia.edu
 */
class CasInt extends CasDataType {
    int var;

    public CasInt(int x) {
        var = x;
    }

    public int getvar() {
        return var;
    }

    public String typename() {
        return "int";
    }

    public CasDataType copy() {
        return new CasInt(var);
    }

    public static int intValue(CasDataType b) {
        if (b instanceof CasDouble) return (int) ((CasDouble) b).var;
        if (b instanceof CasInt) return ((CasInt) b).var;
        b.error("cast to int");
        return 0;
    }

    public void print(PrintWriter w) {
        //if (name != null) w.print(name + " = ");
        w.println(Integer.toString(var));
    }

    public CasDataType uminus() {
        return new CasInt(-var);
    }

    public CasDataType plus(CasDataType b) {
        if (b instanceof CasInt) return new CasInt(var + intValue(b));
        else if (b instanceof CasDouble) return new CasDouble(var + CasDouble.doubleValue(b));
        else if (b instanceof CasString) return new CasString(var + ((CasString)b).getvar());
        else return error(b, "+");
    }

    public CasDataType add(CasDataType b) {
        var += intValue(b);
        return this;
    }

    public CasDataType minus(CasDataType b) {
        if (b instanceof CasInt) return new CasInt(var - intValue(b));
        return new CasDouble(var - CasDouble.doubleValue(b));
    }

    public CasDataType sub(CasDataType b) {
        var -= intValue(b);
        return this;
    }

    public CasDataType times(CasDataType b) {
        if (b instanceof CasInt) return new CasInt(var * intValue(b));
        return new CasDouble(var * CasDouble.doubleValue(b));
    }

    public CasDataType mul(CasDataType b) {
        var *= intValue(b);
        return this;
    }

    public CasDataType lfracts(CasDataType b) {
        if (b instanceof CasInt) return new CasInt(var / intValue(b));
        return new CasDouble(var / CasDouble.doubleValue(b));
    }

    public CasDataType rfracts(CasDataType b) {
        return lfracts(b);
    }

    public CasDataType ldiv(CasDataType b) {
        var /= intValue(b);
        return this;
    }

    public CasDataType rdiv(CasDataType b) {
        return ldiv(b);
    }

    public CasDataType modulus(CasDataType b) {
        if (b instanceof CasInt) return new CasInt(var % intValue(b));
        return new CasDouble(var % CasDouble.doubleValue(b));
    }


    public CasDataType rem(CasDataType b) {
        var %= intValue(b);
        return this;
    }

    public CasDataType gt(CasDataType b) {
        if (b instanceof CasInt) return new CasBool(var > intValue(b));
        return b.lt(this);
    }

    public CasDataType ge(CasDataType b) {
        if (b instanceof CasInt) return new CasBool(var >= intValue(b));
        return b.le(this);
    }

    public CasDataType lt(CasDataType b) {
        if (b instanceof CasInt) return new CasBool(var < intValue(b));
        return b.gt(this);
    }

    public CasDataType le(CasDataType b) {
        if (b instanceof CasInt) return new CasBool(var <= intValue(b));
        return b.ge(this);
    }

    public CasDataType eq(CasDataType b) {
        if (b instanceof CasInt) return new CasBool(var == intValue(b));
        return b.eq(this);
    }

    public CasDataType ne(CasDataType b) {
        if (b instanceof CasInt) return new CasBool(var != intValue(b));
        return b.ne(this);
    }

    /*public void ia() {
        var++;
    }

    public void da() {
        var--;
    }*/
    public CasDataType ib() {
        ++var;
        return this;
    }

    public CasDataType db() {
        --var;
        return this;
    }
}
