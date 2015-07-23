package org.geworkbench.bison.testing;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 Runs all Bison test cases.
 * User: mhall
 * Date: Aug 16, 2005
 * Time: 1:17:42 PM
 */
public class TestAll extends TestCase {
    public TestAll(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(TestCSPanel.suite());
        return suite;
    }
}
