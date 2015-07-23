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
 * Provides an abstration for an input/output character resource.
 */
public interface Resource {
    /**
     * Return the <code>Reader</code> modelled by this resource.
     *
     * @return
     */
    Reader getReader();

    /**
     * Return the <code>Writer</code> modelled by this resource.
     *
     * @return
     */
    Writer getWriter();

    /**
     * Set the <code>Reader</code> that the resource uses for input.
     *
     * @param reader
     */
    void setReader(Reader reader);

    /**
     * Set the <code>Writer</code> that hte resource uses for output.
     *
     * @param writer
     */
    void setWriter(Writer writer);
}

