package org.geworkbench.bison.util;

import java.util.Random;

/**
 * Implementation of a <b>randomX</b>-compliant class based upon the
 * built-in <tt>Java.bisonparsers.Random</tt> generator.  Note that since the higher
 * level result composition methods are different from those in the
 * (undocumented) standard library, <b>randomJava</b> results will differ
 * from those of the standard library for a given seed.
 * <p/>
 * Designed and implemented in July 1996 by
 * <a href="http://www.fourmilab.ch/hotbits">John Walker</a>,
 * <a href="mailto:kelvin@fourmilab.ch">kelvin@fourmilab.ch</a>.
 */
public class RandomNumberGenerator {
    private static Random r = new Random();
    private static int ibytes = 0;
    private static int nbits = 0;
    private static boolean iset = false;

    public static void setSeed(long seed) {
        nbits = 0;
        iset = false;
        r.setSeed(seed);
        ibytes = 0;                   // Clear bytes in nextByte buffer
    }

    private static int idat;

    /**
     * Get next byte from generator.  To minimise calls on the
     * underlying Java generator, integers are generated and
     * individual bytes returned on subsequent calls.  A call
     * on <tt>setSeed()</tt> discards any bytes in the buffer.
     *
     * @return the next byte from the generator.
     */
    private static byte nextByte() {
        byte d;
        if (ibytes <= 0) {
            idat = r.nextInt();
            ibytes = 4;
        }

        d = (byte) idat;
        idat >>= 8;
        ibytes--;
        return (byte) (d + Byte.MAX_VALUE);
    }

    private static int nextInt() {
        return r.nextInt();
    }

    public static String getID() {
        return new Integer(nextInt()).toString();
    }

}