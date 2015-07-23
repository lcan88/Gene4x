package org.geworkbench.engine.cascript;

/**
 * Exception class: messages are generated in various classes
 *
 * @author Behrooz Badii - badiib@gmail.com
 * @version $Id: CasException.java,v 1.3 2006/01/05 21:39:49 bb2122 Exp $
 * @modified from Hanhua Feng - hf2048@columbia.edu
 */
public class CasException extends RuntimeException {
    String msg;
    CasException(String m) {
        //System.err.println("Error: " + msg); used to use this for command prompt
        msg = ("Error: " + m);
    }
    
    public String getMsg() {
        return msg;
    }
}
