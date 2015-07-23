package org.geworkbench.events;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: This event is fired to updae the statu bar of the
 * SequenceDiscoveryViewWidget</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class StatusBarEvent {
    //Satus message
    private String status;

    public StatusBarEvent() {
    }

    public StatusBarEvent(String message) {
        this.status = message;
    }

    /**
     * Gets a status message.
     */
    public String getSatus() {
        return status;
    }

    /**
     * Sets a status message.
     *
     * @param status the message.
     */
    public void setStatus(String status) {
        this.status = status;
    }
}