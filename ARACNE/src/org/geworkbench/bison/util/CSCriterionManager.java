package org.geworkbench.bison.util;

import org.geworkbench.bison.annotation.CSCriteria;
import org.geworkbench.bison.annotation.DSCriteria;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.classification.phenotype.CSClassCriteria;
import org.geworkbench.bison.datastructure.biocollections.classification.phenotype.DSClassCriteria;
import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;

import java.util.HashMap;

/**
 * <p>Title: caWorkbench</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author not attributable
 * @version 3.0
 */

public class CSCriterionManager {
    /**
     * This class implements a singleton instance to manage all dataset criteria
     * a criteria (i.e. criteria collection) can be associated with a data set via
     * the manager and retrieved based on the data set.
     */
    static final CSCriterionManager manager = new CSCriterionManager();
    HashMap<DSDataSet, DSCriteria> criterionMap = new HashMap<DSDataSet, DSCriteria>();
    HashMap<DSDataSet, DSClassCriteria> classCriterionMap = new HashMap<DSDataSet, DSClassCriteria>();


    private CSCriterionManager() {
    }

    /**
     * general category for MA.
     *
     * @param dataSet DSDataSet
     * @return DSCriteria
     */

    static public DSCriteria<DSBioObject> getCriteria(DSDataSet dataSet) {
        return manager._getCriteria(dataSet);
    }

    static public void setCriteria(DSDataSet dataSet, DSCriteria<DSBioObject> criteria) {
        manager._setCriteria(dataSet, criteria);
    }

    private DSCriteria<DSBioObject> _getCriteria(DSDataSet dataSet) {
        DSCriteria<DSBioObject> criteria = criterionMap.get(dataSet);
        if (criteria == null) {
            criteria = new CSCriteria<DSBioObject>();
            criterionMap.put(dataSet, criteria);
        }
        return criteria;
    }

    private void _setCriteria(DSDataSet dataSet, DSCriteria<DSBioObject> criteria) {
        criterionMap.put(dataSet, criteria);
    }


    /**
     * Class Criterion Method
     *
     * @param dataSet DSDataSet
     * @return DSClassCriteria
     */
    static public DSClassCriteria getClassCriteria(DSDataSet dataSet) {
        return manager._getClassCriteria(dataSet);
    }

    static public void setClassCriteria(DSDataSet dataSet, DSClassCriteria criteria) {
        manager._setClassCriteria(dataSet, criteria);
    }

    private DSClassCriteria _getClassCriteria(DSDataSet dataSet) {
        DSClassCriteria criteria = classCriterionMap.get(dataSet);
        if (criteria == null) {
            criteria = new CSClassCriteria();
            classCriterionMap.put(dataSet, criteria);
        }
        return criteria;
    }

    private void _setClassCriteria(DSDataSet dataSet, DSClassCriteria criteria) {
        classCriterionMap.put(dataSet, criteria);
    }

}
