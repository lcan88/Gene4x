package org.geworkbench.events;

import org.geworkbench.engine.config.events.Event;

/**
 * <p>Title: Gene Expression Analysis Toolkit</p>
 * <p>Description: medusa Implementation of enGenious</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 *
 * @author First Genetic Trust
 * @version $Id: CommentsEvent.java,v 1.3 2006/01/13 22:48:37 watkin Exp $
 */

public class CommentsEvent extends Event {
    String _text = null;

    public CommentsEvent(String text) {
        super(null);
        _text = text;
    }

    public String getText() {
        return (_text);
    }
}
