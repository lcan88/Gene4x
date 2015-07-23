package org.geworkbench.engine.cascript;

import java.io.PrintWriter;

/**
 * This holds CasDataType and the status of them being declared, initialized, 
 * and known for the PreSymbolTable
 * statement that is return the CasDataType is from a return statement,
 * so the CasDataType is wrapped inside CasReturn
 *
 * @author Behrooz Badii
 */
public class CasPreData {

    CasDataType data;
    String name;
    boolean declared, initialized, known;

    public CasPreData() {
        name = null;
        declared = false;
        initialized = false;
        known = false;
        data = null;
    }
    public CasPreData(CasDataType dataType) {
        data = dataType;
        declared = false;
        initialized = false;
        known = false;
    }

    /** for copy() purposes
     */
    public CasPreData(CasDataType dataType, boolean d, boolean i, boolean k) {
        data = dataType;
        declared = d;
        initialized = i;
        known = k;
    }
    
    public CasDataType getData() {
        return data;
    }

    public CasDataType setData(CasDataType dataType) {
        return data = dataType;
    }
    
    public boolean getDeclared() {
        return declared;
    }

    public void setDeclared(boolean d) {
        declared = d;
    }
    
    public boolean getInitialized() {
        return initialized;
    }

    public void setInitialized(boolean i) {
        initialized = i;
    }
    
    public boolean getKnown() {
        return known;
    }

    public void setKnown(boolean k) {
        known = k;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    public CasPreData copy() {
        return new CasPreData(data, initialized, declared, known);
    }
    
    public String typename() {
        return "CasPreData";
    }

    public void print(PrintWriter w) {
        w.println(typename());
    }
    
    public CasPreData error(String msg) {
        throw new CasException("illegal operation: " + msg + "( <" + typename() + "> " + (name != null ? name : "<?>") + " )");
    }

    public CasPreData error(CasDataType b, String msg) {
        if (null == b) return error(msg);
        throw new CasException("illegal operation: " + msg + "( <" + typename() + "> " + (name != null ? name : "<?>") + " and " + "<" + typename() + "> " + (name != null ? name : "<?>") + " )");
    }
}
