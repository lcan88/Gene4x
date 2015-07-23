package org.geworkbench.engine.config.rules;

import org.apache.commons.digester.ObjectCreateRule;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust, Inc.</p>
 * @author First Genetic Trust, Inc.
 * @version 1.0
 */

/**
 * Invoked to process the top-most tag, namely the pattern "geaw-config". It
 * instantiated and pushes into the <code>Digester</code> an object of type
 * <code>GeawConfigObject</code>.
 */
public class GeawConfigRule extends ObjectCreateRule {
    // ---------------------------------------------------------------------------
    // --------------- Instance variables
    // ---------------------------------------------------------------------------
    // ---------------------------------------------------------------------------
    // --------------- Constructors
    // ---------------------------------------------------------------------------
    public GeawConfigRule(String className) {
        super(className);
    }

    // ---------------------------------------------------------------------------
    // --------------- Methods
    // ---------------------------------------------------------------------------
    /**
     * Overrides the corresponding method from <code>ObjectCreateRule</code>.
     * Called at the end of parsing, in order to finish up.
     *
     * @throws Exception
     */
    public void finish() throws Exception {
        GeawConfigObject.finish();
    }

}

