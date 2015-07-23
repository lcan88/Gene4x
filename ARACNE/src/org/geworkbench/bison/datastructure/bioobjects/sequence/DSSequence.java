package org.geworkbench.bison.datastructure.bioobjects.sequence;

import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;

/**
 * This interface defines a generic sequence. It is either a DNA or a protein sequence.
 */
public interface DSSequence extends DSBioObject {

    /**
     * Gets the sequence as a String.
     *
     * @return the string representation of this sequence.
     */
    public String getSequence();

    /**
     * Sets this sequence.
     *
     * @param sequence the String representation of the sequence.
     */
    public void setSequence(String sequence);

    /**
     * Gets the length of the sequence.
     *
     * @return the number of characters in the sequence.
     */
    public int length();

    /**
     * Gets the composition of the sequence.
     *
     * @return <code>true</code> if it is a DNA sequence, <code>false</code> if it is a protein sequence.
     */
    public boolean isDNA();

    /**
     * Randomize the order of the characters in the sequence
     */
    public void shuffle();

}
