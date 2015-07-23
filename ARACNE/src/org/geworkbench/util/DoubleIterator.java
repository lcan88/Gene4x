package org.geworkbench.util;

/**
 * <p>Title: caWorkbench</p>
 * <p/>
 * <p>Description: Almost same as java.bisonparsers.Iterator, but only for double and
 * there is no <code>remove()</code> method.</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author Frank Wei Guo
 * @version 3.0
 */
public interface DoubleIterator {
    boolean hasNext();

    double next();
}
