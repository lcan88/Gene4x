package org.geworkbench.engine.config.rules;

import org.apache.commons.digester.ObjectCreateRule;
import org.xml.sax.Attributes;

import java.util.Vector;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust, Inc.</p>
 * @author First Genetic Trust, Inc.
 * @version 1.0
 */

/**
 * Invoked to process the the pattern "geaw-config/plugin". It
 * instantiated and pushes into the <code>Digester</code> an object of type
 * <code>PluginConfigObject</code>.
 */
public class PluginRule extends ObjectCreateRule {
    // ---------------------------------------------------------------------------
    // --------------- Instance and static variables
    // ---------------------------------------------------------------------------
    /**
     * Collection of all <code>PluginObject</code>s pushed on the
     * <code>Digester</code> stack. This collection will be used at the end of
     * parsing, from within the {@link org.geworkbench.engine.config.rules.PluginConfigRule#finish
     * finish} method, in order to allow the each <code>PluginObject</code> to
     * perform any final postprocessing.
     */
    Vector pluginObjects = new Vector();

    // ---------------------------------------------------------------------------
    // --------------- Constructors
    // ---------------------------------------------------------------------------
    public PluginRule(String className) {
        super(className);
    }

    // ---------------------------------------------------------------------------
    // --------------- Methods
    // ---------------------------------------------------------------------------
    /**
     * Overrides the corresponding method from <code>ObjectCreateRule</code>.
     * Called after a new <code>PluginObject</code> has been pushed to the
     * stack, in order to add the new object in the <code>pluginObjects</code>
     * vector.
     *
     * @throws Exception
     */
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        PluginObject pginObj;
        super.begin(namespace, name, attributes);
        pginObj = (PluginObject) super.getDigester().peek();
        pluginObjects.add(pginObj);
        // We need to instantiate the plugin descriptor before the various
        // CallMethod rules are called.
        pginObj.createPlugin(attributes.getValue("id"), attributes.getValue("name"), attributes.getValue("class"), attributes.getValue("source"));
    }

    /**
     * Overrides the corresponding method from <code>ObjectCreateRule</code>.
     * Called at the end of parsing, in order to finish up. It invokes the
     * {@link org.geworkbench.engine.config.rules.PluginObject#finish finish} method of each
     * <code>PluginObject</code> generated through the parsing of the
     * application configuration file.
     *
     * @throws Exception
     */
    public void finish() throws Exception {
        int size = pluginObjects.size();
        for (int i = 0; i < size; ++i)
            ((PluginObject) pluginObjects.get(i)).finish();
    }

}

