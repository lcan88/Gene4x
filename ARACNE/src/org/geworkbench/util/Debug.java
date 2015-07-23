package org.geworkbench.util;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust, Inc.</p>
 * @author First Genetic Trust, Inc.
 * @version 1.0
 */

/**
 * Simple implementation of debug statement printer.
 */
public class Debug {
    /**
     * Flag designating if debugging statement will be printed or not.
     */
    public static boolean debugStatus = false;

    /**
     * if <code>debugStatus == true</code>, print the desiganted debug message.
     * Otherwise, do nothing
     *
     * @param message
     */
    public static void debug(String message) {
        if (debugStatus) {
            System.out.println(message);
            System.out.flush();
        }

    }

}