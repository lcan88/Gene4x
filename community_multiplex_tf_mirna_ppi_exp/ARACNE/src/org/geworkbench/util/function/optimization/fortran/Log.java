package org.geworkbench.util.function.optimization.fortran;

import java.io.PrintStream;

/**
 * Minimalist logging support.
 */
public class Log {
    public static final int LEVEL_QUIET = 0;
    public static final int LEVEL_ERROR = 1;
    public static final int LEVEL_WARNING = 2;
    public static final int LEVEL_INFO = 3;
    public static final int LEVEL_DEBUG = 4;

    private static int s_level = LEVEL_ERROR;
    private static PrintStream s_stream = System.out;

    /**
     * Set the java.io.Writer to log to.
     */
    public static void setLoggingStream(PrintStream stream) {
        s_stream = stream;
    }

    /**
     * Set the minimal level of messages to log.
     */
    public static void setLogLevel(int level) {
        s_level = level;
    }

    public static void log(int level, String message) {
        if (level <= s_level && null != s_stream) s_stream.print(message);
    }

    public static void log(String message) {
        log(LEVEL_INFO, message);
    }
}