package org.geworkbench.util.associationdiscovery.cluster;

import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.complex.pattern.DSPattern;
import org.geworkbench.bison.util.DSPValue;

public interface DSMatrixPattern extends DSPattern<DSMicroarray, DSPValue> {
    public void add(DSMicroarray array);

    public boolean containsMarker(DSGeneMarker testMarker);

    public boolean containsMarkers(CSMatrixPattern pat);

    public DSGeneMarker[] markers();

    public void init(int size);
}
