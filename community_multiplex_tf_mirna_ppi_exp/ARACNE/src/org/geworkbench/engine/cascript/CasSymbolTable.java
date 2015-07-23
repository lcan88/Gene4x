package org.geworkbench.engine.cascript;

import java.util.HashMap;

/**
 * Symbol table class
 *
 * @author Behrooz Badii - badiib@gmail.com
 * @version $Id: CasSymbolTable.java,v 1.5 2005/12/07 15:47:10 manju Exp $
 * @modified from Hanhua Feng - hf2048@columbia.edu
 */
class CasSymbolTable extends HashMap {
    CasSymbolTable parent;
    boolean read_only;
    int level;

    public CasSymbolTable(CasSymbolTable sparent, int slevel) {
        parent = sparent;
        level = slevel; //this should be -1 for global scope
        read_only = false;
    }

    public CasSymbolTable() {
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

    public final CasSymbolTable Parent() {
        return parent;
    }

    public final boolean containsVar(String name) {
        return containsKey(name);
    }

    public final CasDataType findVar(String name) {
        if (this.containsVar(name)) return (CasDataType) get(name);
        if (level == -1) throw new CasException("Variable " + name + " not found");
        else return Parent().findVar(name);
    }

    public final CasSymbolTable getScope(String name) {
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

    public final void setVar(String name, CasDataType data) {

        data.name = name;
        CasSymbolTable st = getScope(name);
        /*Testing purposes
        System.out.println("Changing value of " + name + " in setVar in CasSymbolTable");*/
        st.put(name, data);
    }

    private void jbInit() throws Exception {
    }
}
