package org.geworkbench.bison.datastructure.biocollections;

import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;

/**
 * @author John Watkinson
 */
public abstract class CSAncillaryDataSet<T extends DSBioObject> extends CSDataSet<T> implements DSAncillaryDataSet<T> {

    private DSDataSet<T> parent;

    protected CSAncillaryDataSet(DSDataSet<T> parent, String label) {
        this.parent = parent;
        setLabel(label);
    }

    public DSDataSet<T> getParentDataSet() {
        return parent;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
