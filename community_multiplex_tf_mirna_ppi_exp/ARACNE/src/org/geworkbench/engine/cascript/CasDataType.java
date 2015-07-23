package org.geworkbench.engine.cascript;

import java.io.PrintWriter;

/**
 * The base data type class (also a meta class)
 * <p/>
 * Error messages are generated here.
 *
 * @author Behrooz Badii to CasDataType.java
 * @version $Id: CasDataType.java,v 1.7 2005/12/07 15:47:09 manju Exp $
 * @modified from Hanhua Feng - hf2048@columbia.edu MxDataType.java
 */
public class CasDataType {
    String name;   // used in hash table
    int position = -1
    ,
    position2 = -1; //for arrays and matrices
    String partOf = null; //for arrays and matrices

    public CasDataType() {
        name = null;
    }

    public CasDataType(String name) {
        this.name = name;
    }

    public String typename() {
        return "unknown";
    }

    public String getType() {
        return "unknown";
    }

    public CasDataType copy() {
        return new CasDataType();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public CasDataType error(String msg) {
        throw new CasException("illegal operation: " + msg + "( <" + typename() + "> " + (name != null ? name : "<?>") + " )");
    }

    public CasDataType error(CasDataType b, String msg) {
        if (null == b) return error(msg);
        throw new CasException("illegal operation: " + msg + "( <" + typename() + "> " + (name != null ? name : "<?>") + " and " + "<" + typename() + "> " + (name != null ? name : "<?>") + " )");
    }

    public void print(PrintWriter w) {
        //if (name != null) w.print(name + " = ");
        w.println("<undefined>");
    }

    public void print() {
        print(new PrintWriter(System.out, true));
    }

    public void what(PrintWriter w) {
        w.print("<" + typename() + ">  ");
        print(w);
    }

    public void what() {
        what(new PrintWriter(System.out, true));
    }

    public CasDataType assign(CasDataType b) {
        return error(b, "=");
    }

    public CasDataType transpose() {
        return error("\'");
    }

    public CasDataType uminus() {
        return error("-");
    }

    public CasDataType plus(CasDataType b) {
        return error(b, "+");
    }

    public CasDataType add(CasDataType b) {
        return error(b, "+=");
    }

    public CasDataType minus(CasDataType b) {
        return error(b, "-");
    }

    public CasDataType sub(CasDataType b) {
        return error(b, "-=");
    }

    public CasDataType times(CasDataType b) {
        return error(b, "*");
    }

    public CasDataType mul(CasDataType b) {
        return error(b, "*=");
    }

    public CasDataType lfracts(CasDataType b) {
        return error(b, "/");
    }

    public CasDataType rfracts(CasDataType b) {
        return error(b, "/\'");
    }

    public CasDataType ldiv(CasDataType b) {
        return error(b, "/=");
    }

    public CasDataType rdiv(CasDataType b) {
        return error(b, "/\'=");
    }

    public CasDataType modulus(CasDataType b) {
        return error(b, "%");
    }

    public CasDataType rem(CasDataType b) {
        return error(b, "%=");
    }

    public CasDataType gt(CasDataType b) {
        return error(b, ">");
    }

    public CasDataType ge(CasDataType b) {
        return error(b, ">=");
    }

    public CasDataType lt(CasDataType b) {
        return error(b, "<");
    }

    public CasDataType le(CasDataType b) {
        return error(b, "<=");
    }

    public CasDataType eq(CasDataType b) {
        return error(b, "==");
    }

    public CasDataType ne(CasDataType b) {
        return error(b, "!=");
    }

    public CasDataType and(CasDataType b) {
        return error(b, "and");
    }

    public CasDataType or(CasDataType b) {
        return error(b, "or");
    }

    public CasDataType not() {
        return error("not");
    }

    public void ia() {
        error("incrementation after");
    }

    public void da() {
        error("decrementation after");
    }

    public CasDataType ib() {
        return error("incrementation before");
    }

    public CasDataType db() {
        return error("decrementation before");
    }

    public CasDataType accessArray(int i) {
        return error("array access");
    }

    public CasDataType accessMatrix(int i, int j) {
        return error("matrix access");
    }

    public CasDataType subArrayofMatrix(int i) {
        return error("sub-array in Matrix");
    }

    public void initializeArray() {
        error("intialize Array");
    }

    public void initializeMatrix() {
        error("initialize matrix");
    }

    public void setMatrixValue(CasDataType a, int i, int j) {
        error("set Matrix value");
    }

    public void setArrayValue(CasDataType a, int i) {
        error("set Array value");
    }

    public void setPartOf(String p) {
        partOf = p;
    }

    public String getPartOf() {
        return partOf;
    }

    public void setPosition(int p) {
        position = p;
    }

    public void setPositions(int p, int p2) {
        position = p;
        position2 = p2;
    }

    public int getPosition() {
        return position;
    }

    public int getPosition2() {
        return position2;
    }
}
