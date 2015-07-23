package org.geworkbench.bison.datastructure.complex.pattern.sequence;

import org.geworkbench.bison.datastructure.bioobjects.sequence.DSSequence;
import org.geworkbench.bison.datastructure.complex.pattern.DSMatchedPattern;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public interface DSMatchedSeqPattern extends DSMatchedPattern<DSSequence, DSSeqRegistration> {
    int getLength();

    int getMaxLength();

    String getASCII();

    public void write(BufferedWriter writer) throws IOException;
}
