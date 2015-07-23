package org.geworkbench.events;

import org.geworkbench.engine.config.events.Event;

import javax.swing.*;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 *
 * @author First Genetic Trust
 * @version 1.0
 */
public class ImageSnapshotEvent extends Event {

    public enum Action {
        SAVE, SHOW
    };

    String message = null;
    ImageIcon image = null;
    Action action;

    public ImageSnapshotEvent(String message, ImageIcon image, Action action) {
        super(null);
        this.message = message;
        this.image = image;
        this.action = action;
    }

    public String getMessage() {
        return message;
    }

    public ImageIcon getImage() {
        return image;
    }

    public Action getAction() {
        return action;
    }

}