package org.geworkbench.bison.datastructure.bioobjects.microarray;


/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * <p/>
 * Defines a Genepix Gene Marker generalization of
 * {@link org.geworkbench.bison.model.microarray.MarkerValue}
 *
 * @author First Genetic Trust
 * @version 1.0
 */
public interface DSGenepixMarkerValue extends DSMutableMarkerValue {
    /**
     * Sets the Channel 1 foreground value
     *
     * @param ch1fg channel 1 foreground value
     */
    void setCh1Fg(double ch1fg);

    /**
     * Sets the Channel 1 background value
     *
     * @param ch1bg channel 1 background value
     */
    void setCh1Bg(double ch1bg);

    /**
     * Sets the Channel 2 foreground value
     *
     * @param ch2fg channel 2 foreground value
     */
    void setCh2Fg(double ch2fg);

    /**
     * Sets the Channel 2 background value
     *
     * @param ch2bg channel 2 background value
     */
    void setCh2Bg(double ch2bg);

    /**
     * Sets the flag value
     *
     * @param flag
     */
    void setFlag(String flag);

    /**
     * Gets the flag value
     */

    String getFlag();

    /**
     * Gets the Channel 1 foreground value
     *
     * @return channel 1 foreground value
     */
    double getCh1Fg();

    /**
     * Gets the Channel 1 background value
     *
     * @return channel 1 background value
     */
    double getCh1Bg();

    /**
     * Gets the Channel 2 foreground value
     *
     * @return channel 2 foreground value
     */
    double getCh2Fg();

    /**
     * Gets the Channel 2 background value
     *
     * @return channel 2 background value
     */
    double getCh2Bg();

    void computeSignal();
}
