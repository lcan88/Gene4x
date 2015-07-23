package org.geworkbench.engine.cascript;

import java.util.HashMap;

/**
 * Symbol table class for semantics
 *
 * @author Behrooz Badii - badiib@gmail.com
 * @modified from Hanhua Feng - hf2048@columbia.edu
 */
class CasPreSymbolTable extends HashMap {
    CasPreSymbolTable parent;
    boolean read_only;
    int level;

    public CasPreSymbolTable(CasPreSymbolTable sparent, int slevel) {
        parent = sparent;
        level = slevel; //this should be -1 for global scope
        read_only = false;
    }

    public CasPreSymbolTable() {
        parent = null;
        level = -2;
        read_only = false;
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setReadOnly() {
        read_only = true;
    }

    public final int getLevel() {
        return level;
    }

    public final CasPreSymbolTable Parent() {
        return parent;
    }

    public final boolean containsVar(String name) {
        return containsKey(name);
    }

    public final CasPreData findVar(String name) {
        if (this.containsVar(name)) return (CasPreData) get(name);
        if (level == -1) throw new CasException("Variable " + name + " not found");
        else return Parent().findVar(name);
    }

    public final CasPreSymbolTable getScope(String name) {
        if (this.containsVar(name)) return this;
        if (level == -1) throw new CasException("Variable " + name + " not found");
        else return this.Parent().getScope(name);
    }

    public final boolean exists(String name) {
        if (this.containsVar(name)) return true;
        if (level == -1) return false;
        else return this.Parent().exists(name);
    }

    public final boolean existsinscope(String name) {
        if (this.containsVar(name)) return true;
        return false;
    }

    public final boolean notexists(String name) {
        if (this.containsVar(name)) return false;
        if (level == -1) return true;
        else return this.Parent().notexists(name);
    }

    public final void setVar(String name, CasPreData data) {
        
        data.getData().name = name;
        CasPreSymbolTable st = getScope(name);
        /*Testing purposes
        System.out.println("Changing value of " + name + " in setVar in CasSymbolTable");*/
        st.putVar(name, data);
    }
    
    public final void putVar(String name, CasPreData data) {
        put(name, data);
    }

    private void jbInit() throws Exception {
    }
}
