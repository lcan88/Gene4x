package org.geworkbench.util.sequences;

import junit.framework.TestCase;

public class TestSequenceAligner extends TestCase {
    private SequenceAligner sequenceAligner = null;

    public TestSequenceAligner(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        /**@todo verify the constructors*/
        sequenceAligner = new SequenceAligner(null);
    }

    protected void tearDown() throws Exception {
        sequenceAligner = null;
        super.tearDown();
    }
}