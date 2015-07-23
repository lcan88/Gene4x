package org.geworkbench.util.associationdiscovery.clusterlogic;

import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.util.associationdiscovery.PSSM.CSMatchedPSSMMatrixPattern;
import org.geworkbench.util.associationdiscovery.cluster.CSMatchedMatrixPattern;

public interface ClusterLogic {
    void pssmScoreProfile(CSMatchedPSSMMatrixPattern pssm, DSMicroarraySet<DSMicroarray> arrays);

    void extendGenes(CSMatchedMatrixPattern[] patterns, DSMicroarraySet<DSMicroarray> set);

    CSMatchedMatrixPattern extendChips(CSMatchedMatrixPattern pattern, DSMicroarraySet<DSMicroarray> set);
}
