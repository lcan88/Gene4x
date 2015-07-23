package org.geworkbench.events;

import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.engine.config.events.Event;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * @author First Genetic Trust
 * @version 1.0
 */

/**
 * Event thrown when the user comments associated with a microarray set are
 * modified.
 */
public class CommentsEventOld extends Event {

    private String userComments = null;
    private DSDataSet dataSet = null;

    public CommentsEventOld(DSDataSet ma, String text) {
        super(null);
        userComments = text;
        dataSet = ma;
    }

    /**
     * Gets the User comments associated with this <code>Event</code>
     *
     * @return user comments
     */
    public String getText() {
        return userComments;
    }

    /**
     * Gets the <code>MicroarraySet</code> associated with this <code>Event</code>
     *
     * @return data set on which comments were made
     */
    public DSDataSet getMicroarray() {
        return dataSet;
    }

}
