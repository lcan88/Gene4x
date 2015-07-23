package org.geworkbench.analysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geworkbench.bison.datastructure.properties.CSDescribable;
import org.geworkbench.bison.model.analysis.Analysis;
import org.geworkbench.bison.model.analysis.ParamValidationResults;
import org.geworkbench.bison.model.analysis.ParameterPanel;
import org.geworkbench.bison.util.DefaultIdentifiable;
import org.geworkbench.engine.management.ComponentObjectInputStream;


/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * <p/>
 * Implementation of <code>Analysis</code> customized for use within the
 * applications. It handles the saving of all named parameters sets
 * (from within the saveParametersUnderName method).
 * It also provides a default implementation for the validateParameters method
 * by calling the corresponding method in the
 * <code>AbstractSaveableParameterPanel</code>.
 *
 * @author First Genetic Trust
 * @version 1.0
 */
public abstract class AbstractAnalysis implements Analysis, Serializable, java.util.Observer {
	
	private Log log = LogFactory.getLog(this.getClass());
	
    public static final int AFFY_DETECTION_CALL_FILTER = 0;
    public static final int MISSING_VALUES_FILTER_TYPE = 1;
    public static final int DEVIATION_BASED_FILTER_TYPE = 2;
    public static final int EXPRESSION_THRESHOLD_FILTER_TYPE = 3;
    public static final int LOG_TRANSFORMATION_NORMALIZER_TYPE = 4;
    public static final int THRESHOLD_NORMALIZER_TYPE = 5;
    public static final int MARKER_MEAN_MEDIAN_CENTERING_NORMALIZER_TYPE = 6;
    public static final int MICROARRAY_MEAN_MEDIAN_CENTERING_NORMALIZER_TYPE = 7;
    public static final int MARKER_MEAN_VARIANCE_NORMALIZER_TYPE = 8;
    public static final int MISSING_VALUE_NORMALIZER_TYPE = 9;
    public static final int SOM_CLUSTERING_TYPE = 10;
    public static final int HIERARCHICAL_CLUSTERING_TYPE = 11;
    public static final int IGNORE_TYPE = 12;
    public static final int REPLACE_TYPE = 13;
    public static final int MIN_TYPE = 14;
    public static final int MAX_TYPE = 15;
    public static final int ZERO_TYPE = 16;
    public static final int MEAN_TYPE = 17;
    public static final int MEDIAN_TYPE = 18;
    public static final int TWO_CHANNEL_THRESHOLD_FILTER_TYPE = 19;
    public static final int TTEST_TYPE = 20;
    public static final int HOUSEKEEPINGGENES_VALUE_NORMALIZER_TYPE = 21;
    public static final int GENEPIX_FlAGS_FILTER_TYPE = 22;
    public static final int QUANTILE_NORMALIZER_TYPE = 23;
    /**
     * The parameters panel to be use from within the AnalysisPane in order
     * to collect the analysis parameters from the user.
     */
    protected AbstractSaveableParameterPanel aspp = null;
    /**
     * Contains indices that are used in order to recover the set of named
     * parameted settings that have been saved for a particular analysis. The
     * indices are (key, value) tuples, where 'key', 'value' are defined as:
     * <UL>
     * <LI>key = (this.getIndex(), parameterSetName),</LI>
     * <LI>value =  parameterSetFileName</LI>
     * This is a static variable, used by all classes that extend
     * <code>AbstractAnaysis</code>
     * </UL>
     */
    protected static Hashtable indices = null;
    /**
     * The file where the <code>indices</code> are stored.
     */
    protected File dbFile = null;
    /**
     * Temporary directory name that is obtained from
     * <code>System.properties</code>. This is the place where the named
     * parameter files will be stored.
     */
    protected String tmpDir = null;
    /**
     * Used in the implementation of the <code>Describable</code> interface.
     */
    CSDescribable descriptions = new CSDescribable();
    /**
     * Used in the implementation of the <code>Identifiable</code> interface.
     */
    DefaultIdentifiable analysisId = new DefaultIdentifiable();

    public boolean stopAlgorithm;

    /**
     * Default Constructor
     */
    public AbstractAnalysis() {
        init();
    }

    /**
     * Initializes the indices <code>Properties</code> object and storedParameters
     * <code>Vector</code> object
     */
    protected void init() {
        String fileName = System.getProperty("analyses.parameter.file.name");
        tmpDir = System.getProperty("temporary.files.directory");
        if (tmpDir == null) {
            tmpDir = System.getProperty("java.io.tmpdir");
        }
        assert tmpDir != null && fileName != null :
                "Parameter file and / or temporary file entries in application.properties " + "file missing";
        File td = new File(tmpDir);
        if (!td.exists())
            td.mkdirs();
        dbFile = new File(tmpDir + fileName);
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        if (!dbFile.exists()) {
            try {
                fos = new FileOutputStream(dbFile);
                oos = new ObjectOutputStream(fos);
                oos.writeInt(0); // Initialize an empty database
                oos.flush();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

        }

        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = new FileInputStream(dbFile);
            ois = new ObjectInputStream(fis);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        // The first time that an AbstractAnalysis gets instantiated, read from
        // the dbFile the list of available named parameter sets.
        if (indices != null)
            return;

        indices = new Hashtable();
        try {
            int indexCount = ois.readInt();
            for (int i = 0; i < indexCount; ++i) {
                Tuple key = (Tuple) ois.readObject();
                String paramFileName = (String) ois.readObject();
                indices.put(key, paramFileName);
            }
        } catch (Exception exc) {
            System.err.println("\n\nThe format of the parameters file \"" + tmpDir + fileName + "\" is not compatible with the current version of the program. " + "Please remove this file from your disk.");
        }
    }

    public void setDefaultPanel(AbstractSaveableParameterPanel panel) {
        aspp = panel;
        if (aspp != null)
            aspp.setVisible(true);
    }

    /**
     * Return the parameter panel associated with this analysis..
     */
    public ParameterPanel getParameterPanel() {
        return aspp;
    }

    /**
     * Stores (under the designated 'name') the parameters values currently
     * in 'aspp'.
     */
    public void saveParametersUnderName(String name) {
        aspp.setName(name);
        //    Tuple key = new Tuple(this.getClass().getSimpleName(), name);
        Tuple key = new Tuple(getIndex(), name);
        assert tmpDir != null;
        String fileName = tmpDir + name + System.currentTimeMillis();
        if (!indices.containsKey(key))
            indices.put(key, fileName);
        else
            fileName = (String) indices.get(key);
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = new FileOutputStream(dbFile);
            oos = new ObjectOutputStream(fos);
            int indexCount = indices.size();
            oos.writeInt(indexCount);
            for (Enumeration e = indices.keys(); e.hasMoreElements();) {
                Tuple t = (Tuple) e.nextElement();
                String paramFileName = (String) indices.get(t);
                oos.writeObject(t);
                oos.writeObject(paramFileName);
            }
            oos.flush();
            fos = new FileOutputStream(new File(fileName));
            oos = new ObjectOutputStream(fos);
            oos.writeObject(aspp);
            oos.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

    /**
     * Returns the names of all parameter sets that were ever saved through
     * a call to saveParametersUnderName().
     */
    public String[] getNamesOfStoredParameterSets() {
        // Query the database for all strings XXX, where (this.getClass(), XXX)
        // is an existing index.
        Tuple key = null;
        Vector pn = new Vector();
        for (Enumeration e = indices.keys(); e.hasMoreElements();) {
            key = (Tuple) e.nextElement();
            if (key.className.equals(getIndex())) {
                pn.add(key.parameterName);
            }

        }

        String[] toBeReturned = new String[pn.size()];
        pn.toArray(toBeReturned);
        return toBeReturned;
    }

    /**
     * Returns the parameters panel populated with the parameter values
     * that where stored under the designated 'name'.
     */
    public ParameterPanel getNamedParameterSetPanel(String name) {
        // Look in the database for the 'fileName' that is mapped
        // to the index (this.getClass(), name).
        Tuple key = new Tuple(getIndex(), name);
        if (indices.containsKey(key)) {
            File file = new File((String) indices.get(key));
            try {
                FileInputStream fis = new FileInputStream(file);
                ClassLoader classLoader = getClass().getClassLoader();
                ComponentObjectInputStream ois = new ComponentObjectInputStream(fis, classLoader);
                aspp = (AbstractSaveableParameterPanel) ois.readObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //aspp.setResource(res);
        //aspp.readFromResource(); // Reconstitute the stored values.
        return aspp;
    }

    /**
     * Validates the user-entered parameter values.
     */
    public ParamValidationResults validateParameters() {
        // Delegates the validation to the panel.
        if (aspp == null)
            return new ParamValidationResults(true, null);
        else
            return aspp.validateParameters();
    }

    public void addDescription(String desc) {
        descriptions.addDescription(desc);
    }

    public String[] getDescriptions() {
        return descriptions.getDescriptions();
    }

    public void removeDescription(String desc) {
        descriptions.removeDescription(desc);
    }

    public String getID() {
        return analysisId.getID();
    }

    public void setID(String id) {
        analysisId.setID(id, "Analysis");
    }

    public String getLabel() {
        return analysisId.getLabel();
    }

    public void setLabel(String name) {
        analysisId.setLabel(name);
    }

    public void update(java.util.Observable ob, Object o) {
    	
    	log.debug("initiated close");
    	
        stopAlgorithm = true;
    }

    /**
     * Convenience method - returns a string which should be unique for each
     * subclass of <code>AbstractAnalysis</code>. This string is used as part
     * of the hash that is used to store/recover named parameter sets.
     *
     * @return String Unique "tagging" string for an Analysis type.
     */
    private String getIndex() {
        // Using the display name of the analysis as its index is not entirely
        // appropriate. Ideally we would like to use some sorts of a hash based
        // on its corresponding .class file.
        return getLabel();
    }

    /**
     * Return a code identifying the type of the analysis.
     *
     * @return
     */
    public abstract int getAnalysisType();

    /**
     * Convenience class modeling the 'key' part of the (key, value) pairs
     * stored in the variable <code>indices</code>.
     */
    static class Tuple implements Serializable {
        protected String className = null;
        protected String parameterName = null;

        public Tuple(String cn, String pn) {
            className = cn;
            parameterName = pn;
        }

        public int hashCode() {
            return (className + parameterName).hashCode();
        }

        public boolean equals(Object tuple) {
            if (tuple instanceof Tuple) {
                return ((Tuple) tuple).className.equals(className) && ((Tuple) tuple).parameterName.equals(parameterName);
            }

            return false;
        }

        public String toString() {
            return "Key: " + className + "+" + parameterName;
        }

    }

}

