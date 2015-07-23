package org.geworkbench.bison.util;

import java.io.ObjectStreamField;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Vector;

/**
 *
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust, Inc.</p>
 * @author First Genetic Trust, Inc.
 * @version 1.0
 */

/**
 * This class provides a baseline implementation to be used by objects that
 * implement the {@link org.geworkbench.engine.util.Identifiable Identifiable} interface.
 * It is intended to support the needs of multiple collections
 * of objects. For each collection, it guarantees uniqueness of ids as long as
 * (1) the collection has a unique name, (2) objects within the collection
 * request IDs by providing the collection's name, and (3) objects are members
 * of a single collection for the entirety of their lifespan.
 */
public class DefaultIdentifiable implements Serializable {
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
     * Keeps track of ids currently in use. Each key in the map corresponds to
     * a name for a collection of objects. The key is mapped to a value that is
     * a <code>Vector</code> and stores the ids currently in use for that
     * named collection.
     * <p/>
     * NOTE: Serializable implementations of the <code>Identifiable</code> interface
     * that make use of <code>DefaultIdentifiable</code> should explicitly handle
     * the restoration of <code>usedIds</code>.
     */
    protected static HashMap usedIds = new HashMap();
    /**
     * Serializable fields.
     */
    private final static ObjectStreamField[] serialPersistentFields = {new ObjectStreamField("name", String.class), new ObjectStreamField("id", String.class)};
    // ---------------------------------------------------------------------------
    // --------------- Constructors
    // ---------------------------------------------------------------------------
    /**
     * Default constructor
     */
    public DefaultIdentifiable() {
    }

    /**
     * Instantiates using the prescribed id.
     *
     * @param someID The ID to use for initialization
     */
    public DefaultIdentifiable(String someID, String collectionName) {
        //    if (idExists(someID, collectionName))
        //      throw new BaseRuntimeException("DefaultIdentifiable::DefaultIdentifiable() - " +
        //            "Attempt to reuse already existing id.");
        this.setID(someID, collectionName);
    }

    /**
     * Instantiates using the prescribed id and name
     *
     * @param someID   The ID to use for initialization
     * @param someName The name to use for initialization
     */
    public DefaultIdentifiable(String someID, String someName, String collectionName) {
        //    if (idExists(someID, collectionName))
        //      throw new BaseRuntimeException(
        //        "DefaultIdentifiable::DefaultIdentifiable(someID, someName) - " +
        //        "Attempt to reuse already existing id.");
        this.setID(someID, collectionName);
        name = someName;
    }

    // ---------------------------------------------------------------------------
    // --------------- Methods
    // ---------------------------------------------------------------------------
    /**
     * Checks if the <code>someID</code> has already been assigned to an object
     * of the designated named collection.
     *
     * @param someId         The id to check.
     * @param collectionName The name of the reference collection.
     * @return True if the ID exists, false otherwise.
     */
    public static boolean idExists(String someId, String collectionName) {
        if (usedIds.containsKey(collectionName) && ((Vector) usedIds.get(collectionName)).contains(someId))
            return true;
        else
            return false;
    }

    /**
     * @return The ID for this object.
     */
    public String getID() {
        return id;
    }

    /**
     * Sets the id for the current object.
     *
     * @param someId         The id to use.
     * @param collectionName The name of the collection where the object belongs.
     */
    public void setID(String someId, String collectionName) {
        //    try {
        // Check that the id we try to assign is unique
        if (idExists(someId, collectionName))
            throw new RuntimeException("Identifiable::setId: Attempt to assign already existing id.");
        //      String prevId = this.id;
        this.id = someId;
        // If this is the first ID added for the collectionName
        if (!usedIds.containsKey(collectionName))
            usedIds.put(collectionName, new Vector());
        // Maintain the uniqeness of Ids.
        //      ((Vector)usedIds.get(collectionName)).remove(prevId);
        //      if (this.id != null)
        //        ((Vector)usedIds.get(collectionName)).add(this.id);
        //    }

        //    catch(Exception e) {
        //      e.printStackTrace();
        //    }

    }

    /**
     * @return The 'name' of this object.
     */
    public String getLabel() {
        return name;
    }

    /**
     * Sets the name of this object.
     *
     * @param someName The name to use.
     */
    public void setLabel(String someName) {
        this.name = someName;
    }

    /**
     * Resets the id to null. It may be necessary to manually release the ID of an
     * object that is being serialized if, (1) the object is destroyed after the
     * serialization, and (2) the object can be deserialized in the same working
     * session. If the id does not get so released, then the subsequent attempt to
     * deserialize will find the ID still present in <code>usedIds</code>.
     * <p/>
     * This explicit handling of ID release is unfortunately made necessary due to
     * the latent nature of garbage collection (and the corresponsing latency
     * in calling the <code>finalize()</code> method, which is where the ID release
     * would naturally occur).
     *
     * @param someId         The ID to be released.
     * @param collectionName The reference collection name.
     */
    public void clearID(String someId, String collectionName) {
        if (someId != null && usedIds.containsKey(collectionName))
            ((Vector) usedIds.get(collectionName)).remove(someId);
        this.id = null;
    }

}
