package org.geworkbench.bison.datastructure.bioobjects.microarray;

import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.properties.CSDescribable;
import org.geworkbench.bison.datastructure.properties.CSExtendable;
import org.geworkbench.bison.util.DefaultIdentifiable;
import org.geworkbench.bison.util.RandomNumberGenerator;

import java.io.Serializable;
import java.util.Iterator;

public class CSMicroarray implements DSMicroarray, Serializable {
    /**
     *     Serializable fields.
     */
    //    private final static ObjectStreamField[] serialPersistentFields = {
    //        new ObjectStreamField("serial", int.class),
    //        new ObjectStreamField("arrayId", DefaultIdentifiable.class),
    //        new ObjectStreamField("descriptions", CSDescribable.class),
    //        new ObjectStreamField("extend", CSExtendable.class),
    //        new ObjectStreamField("maSet", DSMicroarraySet.class)
    //    };

    //protected DSMicroarraySet microarraySet = null;
    /**
     * Microarray type, defined in DSMicroarraySet
     */
    protected int type = 0;
    /**
     * The relateive position (index) of the microarray within its host
     * <code>MicroarraySet</code>.
     */
    protected int serial = -1;
    /**
     * Used in the implementation of the <code>Describable</code> interface.
     */
    protected CSDescribable descriptions = new CSDescribable();
    /**
     * Used in the implementation of the <code>Identifiable</code> interface.
     */
    protected org.geworkbench.bison.util.DefaultIdentifiable arrayId = new DefaultIdentifiable();
    /**
     * Used in the implementation of the <code>Extendable</code> interface.
     */
    protected CSExtendable extend = new CSExtendable();


    /**
     * Label of the Microarray
     */
    protected String label = null;

    /**
     * Array of JMarkers containing the actual Microarray data
     */
    public CSMarkerValue[] markerArray = null;

    /**
     * A microarray that is not Enabled will be ignored
     */
    protected boolean enabled = true;

    public CSMicroarray(int markerNo) {
        markerArray = new CSMarkerValue[markerNo];
    }

    public CSMicroarray(int serial, int markerNo, String description, String[] label, String[] accession, boolean randomize, int type) {
        this.serial = serial;
        this.label = description;
        this.type = type;
        markerArray = new CSMarkerValue[markerNo];

        for (int i = 0; i < markerNo; i++) {
            if (type == DSMicroarraySet.snpType) {
                markerArray[i] = new CSGenotypicMarkerValue((int) (Math.random() * 2) + 1, (int) (Math.random() * 2) + 1);
            } else if (type == DSMicroarraySet.alleleType) {
                markerArray[i] = new CSGenotypicMarkerValue(0);
            } else if (type == DSMicroarraySet.expPvalueType) {
                markerArray[i] = new CSExpressionMarkerValue(0);
            } else if (type == DSMicroarraySet.genepixGPRType) {
                markerArray[i] = new CSGenepixMarkerValue(0);
            } else if (type == DSMicroarraySet.affyTxtType){
                markerArray[i] = new CSAffyMarkerValue();
            } else {
                markerArray[i] = new CSExpressionMarkerValue(0);
                if (randomize) {
                    double v = Math.random() * 10000;
                    double s = Math.random();
                    ((CSExpressionMarkerValue) markerArray[i]).setValue(v);
                    if (s < 0.9) {
                        markerArray[i].setPresent();
                    } else {
                        markerArray[i].setAbsent();
                    }
                }
            }
        }
    }

    public boolean isMarkerValid(int i) {
        if (i >= markerArray.length) {
            return false;
        }
        return markerArray[i].isValid();
    }

    public boolean isMarkerUndefined(int i) {
        if (i >= markerArray.length) {
            return true;
        }
        return markerArray[i].isMissing();
    }

    public int getMarkerNo() {
        return markerArray.length;
    }

    public void setMarkerValue(int index, DSMarkerValue markerValue) {
        markerArray[index] = (CSMarkerValue) markerValue;
    }

    public void setLabel(String label) {
        this.label = new String(label);
    }

    public String getLabel() {
        return label;
    }

    /*
      public void addPropertyValue(DSAnnotLabel property, DSAnnotValue value) {
        propertyValueMap.put(property, value);
      }

      public DSAnnotValue getPropertyValue(DSAnnotLabel property) {
        return (DSAnnotValue) propertyValueMap.get(property);
      }

      public HashMap getPropertyValueMap() {
        return propertyValueMap;
      }

      public void setPropertyValueMap(HashMap map) {
        this.propertyValueMap = new HashMap(map);
      }
     */
    //  public void setPhenoClassId(int phenoClassId) {
    //    this.phenoClassId = phenoClassId;
    //  }
    //
    //  public int getPhenoClassId() {
    //    return phenoClassId;
    //  }

    public boolean enabled() {
        return enabled;
    }

    public void enable(boolean state) {
        enabled = state;
    }

    public int getSerial() {
        return serial;
    }

    public void setSerial(int id) {
        serial = id;
    }

    /*
      public void put(DSAnnotLabel property, DSAnnotValue value) {
        getPropertyValueMap().put(property, value);
      }
     */

    //    public void remove() {
    //        microarraySet.remove(this);
    //    }

    //    public DSMicroarraySet getMicroarraySet() {
    //        return microarraySet;
    //    }

    //    public void setMicroarraySet(DSMicroarraySet m) {
    //        microarraySet = m;
    //    }

    public String getID() {
        return arrayId.getID();
    }

    public void setID(String id) {
        arrayId.setID(id, "Microarray");
    }

    public String getName() {
        return arrayId.getLabel();
    }

    public void setName(String name) {
        arrayId.setLabel(name);
    }

    public String toString() {
        return getLabel();
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

    public void addNameValuePair(String name, Object value) {
        extend.addNameValuePair(name, value);
    }

    public Object[] getValuesForName(String name) {
        return extend.getValuesForName(name);
    }

    public void forceUniqueValue(String name) {
        extend.forceUniqueValue(name);
    }

    public void allowMultipleValues(String name) {
        extend.allowMultipleValues(name);
    }

    public boolean isUniqueValue(String name) {
        return extend.isUniqueValue(name);
    }

    public void clearName(String name) {
        extend.clearName(name);
    }


    public DSMutableMarkerValue[] getMarkerValues() {
        return markerArray;
    }

    public DSMutableMarkerValue getMarkerValue(DSGeneMarker mInfo) {
        int markerIndex;
        // If the current microarray belongs to a microarray set, then we
        // should be able find the requested value fast.
        //        if ( (mSet = getMicroarraySet()) != null) {
        // @todo - xiaoqing - for every new CSGeneMarker, the default markerId =0
        // so it always return the first marker to the query. Need change soon...
        if (mInfo != null) {
            if ((markerIndex = mInfo.getSerial()) >= 0) {
                return markerArray[markerIndex];
            } else{
            return null;
        }


        }
        return null;
    }

    public DSMutableMarkerValue getMarkerValue(int i) {
        try{
            return markerArray[i];
        }catch(ArrayIndexOutOfBoundsException e){
            CSMarkerValue newAbsentValue = new CSExpressionMarkerValue();
            newAbsentValue.setAbsent();
            return newAbsentValue;
        }
    }

    public DSMicroarray deepCopy() {
        CSMicroarray copy = new CSMicroarray(serial, markerArray.length, label, null, null, false, type);
        for (int i = 0; i < this.getMarkerNo(); i++) {
            copy.markerArray[i] = (CSMarkerValue) markerArray[i].deepCopy();
        }
        copy.setID(RandomNumberGenerator.getID());
        return copy;
    }

    public Iterator<DSMutableMarkerValue> iterator() {
        return new org.geworkbench.bison.util.ArrayIterator<DSMutableMarkerValue>(markerArray);
    }

    public void resize(int size) {
        markerArray = new CSMarkerValue[size];
    }

    public float[] getRawMarkerData() {
        DSMutableMarkerValue[] values = getMarkerValues();
        float[] data = new float[values.length];
        for (int j = 0; j < values.length; j++) {
            data[j] = (float) values[j].getValue();
        }
        return data;
    }
}
