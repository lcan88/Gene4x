package org.geworkbench.engine.cascript;

import antlr.collections.AST;

import java.io.PrintWriter;
import java.util.Vector;

/**
 * The function data type
 *
 * @author Behrooz Badii - badiib@gmail.comHanhua Feng - hf2048@columbia.edu
 * @version $Id: CasPreFunction.java,v 1.3 2006/01/05 21:39:39 bb2122 Exp $
 * @modified from Hanhua Feng - hf2048@columbia.edu
 */
class CasPreFunction extends CasDataType {
    // we need a reference to the AST for the function entry
    Vector<CasArgument> args;
    AST body;            // body = null means an internal function.
    CasPreSymbolTable pst;   // the symbol table of static parent
    CasDataType type;
    int brackets;

    public CasPreFunction(String name, Vector<CasArgument> args, AST body, CasPreSymbolTable pst, CasDataType r, int b) {
        super(name);
        this.args = args;
        this.body = body;
        this.pst = pst;
        type = r;
        brackets = b;
    }

    public String typename() {
        return "function";
    }

    public CasDataType copy() {
        return new CasPreFunction(name, args, body, pst, type, brackets);
    }

    public void print(PrintWriter w) {
        if (name != null) w.print(name + " = ");
        w.print("<function>(");
        for ( int i=0; i < args.size(); i++ ) {
            w.print( args.elementAt(i) );
            w.print( "," );
        }
        w.println( ")" );
    }

    public Vector<CasArgument> getArgs() {
        return args;
    }

    public CasDataType getReturnType() {
        return type;
    }

    public int getBrackets() {
        return brackets;
    }

    public String getName() {
        return name;
    }

    public CasPreSymbolTable getParentSymbolTable() {
        return pst;
    }

    public AST getBody() {
        return body;
    }
}

