package org.geworkbench.bison.parsers.resources;

import java.io.Reader;
import java.io.Writer;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * @author First Genetic Trust Inc.
 * @version 1.0
 */

/**
 * Baseline implementation of <code>Resource</code>.
 */
public abstract class AbstractResource implements Resource {
    protected Reader reader = null;
    protected Writer writer = null;

    public AbstractResource() {
    }

    public Reader getReader() {
        return reader;
    }

    public Writer getWriter() {
        return writer;
    }

    public void setReader(Reader is) {
        reader = is;
    }

    public void setWriter(Writer os) {
        writer = os;
    }

}

