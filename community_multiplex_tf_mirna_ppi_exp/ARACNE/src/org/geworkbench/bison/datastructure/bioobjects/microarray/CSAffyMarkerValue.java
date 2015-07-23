package org.geworkbench.bison.datastructure.bioobjects.microarray;

import org.geworkbench.bison.parsers.AffyParseContext;
import org.geworkbench.bison.parsers.NCIParseContext;

import java.io.ObjectStreamField;
import java.io.Serializable;
import java.util.Map;


/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * @author First Genetic Trust Inc.
 * @version 1.0
 */

/**
 * Implementation of {@link org.geworkbench.bison.model.microarray.AffyMarkerValue
 * AffyMarkerValue}.
 */
public class CSAffyMarkerValue extends CSExpressionMarkerValue implements
        DSAffyMarkerValue, Serializable {

    /**
     * Constants used to represent detection call status
     */
    static final char ABSENT    = 'A';
    static final char PRESENT   = 'P';
    static final char MARGINAL  = 'M';
    static final char UNDEFINED = '\0';    


    /**
     * Stores the contents of the "Detection" column from the Affy input file that
     * corresponds to this value.
     */
    char detectionStatus = UNDEFINED;
    /**
     * Serializable fields.
     */
    private final static ObjectStreamField[] serialPersistentFields = {
          new ObjectStreamField("detectionStatus", char.class)};
    
      public CSAffyMarkerValue(){
      }

      /**
       * Constructs a <code>CSAffyMarkerValue</code> object from the contents of
       * the <code>AffyParseContext</code> argument.
       * @param val    The parse context used for the initialization.
       */
      public CSAffyMarkerValue(AffyParseContext val) {
    	  if (val != null)
    		  init(val.getColumnsToUse());
      }

      /**
       * Constructs a <code>CSAffyMarkerValue</code> object from the contents of
       * the <code>NCIParseContext</code> argument.
       * @param val    The parse context used for the initialization.
       */
      public CSAffyMarkerValue(NCIParseContext val) {
    	  if (val != null)
    		  init(val.getColumnsToUse());    	  
      }

      /**
       * Creates a copy of the designated <code>AffyMarkerValueImpl</code>. The
       * copy maintains the physical link to the argument's associated
       * <code>MarkerInfo</code> and <code>Microarray</code> objects.
       *
       * @param amvi The value to copy.
       */
      CSAffyMarkerValue(CSAffyMarkerValue  amvi)
      {
        setValue(amvi.value);
        this.confidence       = amvi.confidence;
        this.detectionStatus  = amvi.detectionStatus;
      }


      /**
       * Initializes class attributes from the contents of
       * the <code>AffyParseContext</code> argument.
       * @param val    The parse context used for the initialization.
       */
      public void init(AffyParseContext val) {
    	  if (val != null)
    		  init(val.getColumnsToUse());
      }
      
      
      protected void init(Map columns) {
    	  Object value = null;
    	  boolean pValueFound = false;  // indicates if the "Detection p-value" column is used
    	  boolean absCallFound = false;  // indicates if either of the "Detection" or "Abs Call" columns are used    	  
    	  
    	  if (columns.containsKey("Probe Set Name")){
    		  value = columns.get("Probe Set Name");
    	  }
    	  
    	  // Notice below that there are values "competing" for the same semantic concept.
    	  // E.g., "Avg Diff", "Signal" can both populate AffyMarkerValue.signal. The
    	  // relative ordering of the if-blocks corresponding to such values imposes
    	  // a relative importance that resolves conflicts: e.g., if
    	  // both "Avg Diff" and "Signal" are present, "Signal" will be preferred.
    	  if (columns.containsKey("Avg Diff")) {
    		  value = columns.get("Avg Diff");
    		  if (value instanceof Double){
    			  setValue(( (Double) value).doubleValue());
    		  }
    		  
    	  }
    	  
    	  if (columns.containsKey("Signal")) {
    		  value = columns.get("Signal");
    		  if (value instanceof Double){
    			  setValue(( (Double) value).doubleValue());
    		  }
    		  
    	  }
    	  
    	  if (columns.containsKey("Log2(ratio)")) {
    		  value = columns.get("Log2(ratio)");
    		  if (value instanceof Double){
    			  setValue(( (Double) value).doubleValue());
    		  }
    		  
    	  }
    	  
    	  if (columns.containsKey("Detection p-value")) {
    		  value = columns.get("Detection p-value");
    		  if (value instanceof Double){
    			  setConfidence(( (Double) value).doubleValue());
    			  pValueFound = true;
    		  }
    	  }
    	  
    	  if (columns.containsKey("Abs Call")) {
    		  value = columns.get("Abs Call");
    		  if (value instanceof Character){
    			  Character ch = Character.toUpperCase(((Character) value).charValue());
    			  if (ch == PRESENT || ch == ABSENT || ch == MARGINAL){
    				  this.detectionStatus = ch;
    				  absCallFound = true;
    				  // If there is no p-value column explicitly used, then apply 
    				  // the p-value conventions of the superclass.
    				  if (!pValueFound)
    					  switch (ch){
    					  case PRESENT: 
    						  super.setPresent();
    						  break;
    					  case ABSENT:
    						  super.setAbsent();
    						  break;
    					  case MARGINAL:
    						  super.setMarginal();
    						  break;
    					  }
    			  }
    		  }
    	  }
    	  
    	  if (columns.containsKey("Detection")) {
    		  value = columns.get("Detection");
    		  if (value instanceof Character){
    			  Character ch = Character.toUpperCase(((Character) value).charValue());
    			  if (ch == PRESENT || ch == ABSENT || ch == MARGINAL){
    				  this.detectionStatus = ch;
    				  absCallFound = true;
    				  // If there is no p-value column explicitly specified, then apply 
    				  // the p-value conventions of the superclass.
    				  if (!pValueFound)
    					  switch (ch){
    					  case PRESENT: 
    						  super.setPresent();
    						  break;
    					  case ABSENT:
    						  super.setAbsent();
    						  break;
    					  case MARGINAL:
    						  super.setMarginal();
    						  break;
    					  }
    			  }
    		  }
    	  }
    	  
    	  if (!absCallFound && !pValueFound){
    		  super.setPresent();
    	  }
    	  
      }

      public double getDisplayValue() {
        return getValue();
      }

      public boolean isPresent(){
        return (detectionStatus == PRESENT)? true : 
        	(detectionStatus == UNDEFINED? super.isPresent() : false);
      }

      /**
       * Sets the detection level of this affy marker value to "Present".
       */
      public void setPresent(){
    	  if (isMissing())
    		  super.setPresent(); // make sure the confidence field is set consistently
    	  detectionStatus = PRESENT;
      }

      public boolean isMarginal(){
          return (detectionStatus == MARGINAL)? true : 
          	(detectionStatus == UNDEFINED? super.isMarginal() : false);
      }

      /**
       * Sets the detection level of this affy marker value to "Marginal".
       */
      public void setMarginal(){
    	  if (isMissing())
    		  super.setMarginal(); // make sure the confidence field is set consistently    	  
        detectionStatus = MARGINAL;
      }

      public boolean isAbsent() {
          return (detectionStatus == ABSENT)? true : 
          	(detectionStatus == UNDEFINED? super.isAbsent() : false);
      }

      /**
       * Sets the detection level of this affy marker value to "Absent".

       */
      public void setAbsent() {
    	  if (isMissing())
    		  super.setAbsent(); // make sure the confidence field is set consistently    	  
        detectionStatus = ABSENT;
      }

      /**
       *
       * @return A copy of the marker value. The associated <code>MarkerInfo</code>
       *         is copied as well.
       */
      public DSMarkerValue deepCopy(){
        CSAffyMarkerValue copy = new CSAffyMarkerValue(this);
        return (DSMarkerValue) copy;
      }

      /**
       * This method returns the dimensionality of the marker. E.g., Genotype markers are 2-dimensional
       * while Allele/Haplotype markers are 1-dimensional
       * @return int the dimensionality of the marker.
       */
      public int getDimensionality(){
          return 1;
      }

       /**
        * Tests if two markers are equal
        * @param m marker to be compared
        * @return boolean equality
        */
       public boolean equals(DSMarkerValue m){
           return this.getValue() == m.getValue();
       }

       public char getStatusAsChar() {
    	   if (detectionStatus == UNDEFINED)
    		   return super.getStatusAsChar();
    	   else if (isMasked())
    		   return Character.toLowerCase(detectionStatus);
    	   else 
    		   return detectionStatus;
       }

       public int compareTo(Object o) {
           return Double.compare(((CSAffyMarkerValue) o).getValue(), getValue());
       }


    }
