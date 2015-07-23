package org.geworkbench.bison.datastructure.properties;

/**
 * Implementing classes are ordered according to an index value, or <b>serial</b>.
 */
public interface DSSequential extends DSNamed {
    /**
     * Gets the serial for this object.
     *
     * @return the serial index.
     */
    int getSerial();

    /**
     * Sets the serial for this object
     *
     * @param serial the new serial.
     */
    void setSerial(int serial);
}
