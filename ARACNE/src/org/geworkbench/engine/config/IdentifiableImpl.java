package org.geworkbench.engine.config;

import org.geworkbench.bison.datastructure.properties.DSIdentifiable;

import java.util.Vector;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust, Inc.</p>
 * @author First Genetic Trust, Inc.
 * @version 1.0
 */

/**
 * This class provides an implementation of the {@link org.geworkbench.engine.util.Identifiable
 * Identifiable} interface to be used with <code>PluginDescriptor</code>
 * objects.
 */
public class IdentifiableImpl implements DSIdentifiable {
    // ---------------------------------------------------------------------------
    // --------------- Instance & static variables
    // ---------------------------------------------------------------------------
    /**
     * Stores a unique id.
     */
    protected String id = null;
    /**
     * Stores a (not necessarily unique) name.
     */
    protected String name = null;
    /**
     * Keeps track of ids currently in use.
     */
    protected static Vector usedIds = new Vector(100);
    // ---------------------------------------------------------------------------
    // --------------- Constructors
    // ---------------------------------------------------------------------------
    /**
     * Default constructor
     */
    public IdentifiableImpl() {
    }

    /**
     * Instantiates using the prescribed id.
     *
     * @param someID The ID to use for initialization
     */
    public IdentifiableImpl(String someID) {
        if (idExists(someID))
            throw new org.geworkbench.util.BaseRuntimeException("IdentifiableImpl::IdentifiableImpl() - " + "Attempt to reuse already existing id.");
        this.setID(someID);
    }

    /**
     * Instantiates using the prescribed id and name
     *
     * @param someID   The ID to use for initialization
     * @param someName The name to use for initialization
     */
    public IdentifiableImpl(String someID, String someName) {
        if (idExists(someID))
            throw new org.geworkbench.util.BaseRuntimeException("IdentifiableImpl::IdentifiableImpl(someID, someName) - " + "Attempt to reuse already existing id.");
        this.setID(someID);
        name = someName;  // this is OK: Strings are immutable. No need for new()
    }

    // ---------------------------------------------------------------------------
    // --------------- Methods
    // ---------------------------------------------------------------------------
    public static boolean idExists(String someId) {
        return (usedIds.contains(someId) ? true : false);
    }

    // Method from the <code>Identifiable</code> interface.
    public String getID() {
        return id; // this is OK: Strings are immutable. No need for new()
    }

    // Method from the <code>Identifiable</code> interface. Sets the
    public void setID(String someId) {
        try {
            // Check that the id we try to assign is unique
            if (idExists((String) someId))
                throw new org.geworkbench.util.BaseRuntimeException("Identifiable::setId: Attempt to assign already existing id.");
            String prevId = this.id;
            this.id = someId;
            // Maintain the uniqeness of Ids.
            usedIds.remove(prevId);
            usedIds.add(this.id);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // Implemented method from the <code>Identifiable</code> interface.
    public String getLabel() {
        return name; // this is OK: Strings are immutable. No need for new()
    }

    // Implemented method from the <code>Identifiable</code> interface. Sets the
    // name of the plugin component described by this object.
    public void setLabel(String someName) {
        this.name = someName; // this is OK: Strings are immutable. No need for new()
    }

}
