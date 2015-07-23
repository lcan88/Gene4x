package org.geworkbench.events;

import org.geworkbench.engine.config.events.Event;

/**
 * <p>Title: Synteny Event
 *
 * @author
 *
 */

public class SyntenyEvent extends Event {
    String _text = null;

    public SyntenyEvent(String text) {
        super(null);
        _text = text;
    }

    public String getText() {
        return (_text);
    }
}
