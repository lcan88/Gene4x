package org.geworkbench.engine.config;

import org.apache.commons.digester.ObjectCreateRule;
import org.xml.sax.Attributes;

import java.util.Vector;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

/**
 * <p>Title: Plug And Play</p>
 * <p>Description: Dynamic Proxy Implementation of enGenious</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust, Inc.</p>
 *
 * @author First Genetic Trust, Inc.
 * @version 1.0
 *          <p/>
 *          Invoked to process the the pattern "geaw-config/plugin". It
 *          instantiated and pushes into the <code>Digester</code> an object of type
 *          <code>PluginConfigObject</code>.
 */
public class PluginClassRule extends ObjectCreateRule {

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
    Vector pluginClassObjects = new Vector();

    // ---------------------------------------------------------------------------
    // --------------- Constructors
    // ---------------------------------------------------------------------------

    public PluginClassRule(String className) {
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
        PluginClass pginCls;

        super.begin(namespace, name, attributes);
        pginCls = (PluginClass) super.getDigester().peek();


        // We need to instantiate the plugin descriptor before the various
        // CallMethod rules are called.
        //String classid= attributes.getValue("classtype-id");
        // String id= attributes.getValue("instance-id");
        //PluginInfoDescriptor pinfo=PluginListRegistry.getPluginInfoDescriptor(classid);

        pginCls.createPluginClass(attributes.getValue("class"), attributes.getValue("name"));

        PluginClassRegistry.addClass(attributes.getValue("class"), pginCls);

    }


}
