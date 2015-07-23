package org.geworkbench.bison.datastructure.bioobjects;

import org.geworkbench.bison.datastructure.properties.*;

import java.io.Serializable;

/**
 * Implementing classes store biological data (such as a sequence or a microarray).
 */
public interface DSBioObject extends DSExtendable, DSDescribable, DSIdentifiable, DSSequential, DSNamed, DSHasActivation, Serializable {

}
