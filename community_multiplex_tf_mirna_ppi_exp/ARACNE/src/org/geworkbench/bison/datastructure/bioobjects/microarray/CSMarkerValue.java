package org.geworkbench.bison.datastructure.bioobjects.microarray;

/**
 * <p>
 * Title: Plug And Play Framework
 * </p>
 * <p>
 * Description: Architecture for enGenious Plug&Play
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company: First Genetic Trust
 * </p>
 * 
 * @author Andrea Califano
 * @version 1.0
 */

abstract public class CSMarkerValue implements DSMutableMarkerValue {

	/**
	 * Used to define how present/marginal/absent calls should be made based on
	 * the call p-value: 
	 *          p-value < p_threshold --> Present 
	 *          p_threshold <= p-values < m_threshold --> Marginal 
	 *          p-value >= m_threshold --> Absent
	 */
	// Fixme: These value should not be hardcoded, rather specified by the user.
	// <AF>This way of overloading confidence to infer detection status and
	// missing status may save storage space but is not clean and can be
	// confusing to external developers trying to extend this class. I'd rather
	// we spared the extra byte needed to explicitly store this information</AF>
	protected static float p_threshold = (float) 0.04;

	protected static float m_threshold = (float) 0.06;
	
	/**
	 * Value which is assigned to the field <code>confidence</code> to indicate
	 * that the marker value is marked as "missing".
	 */
	protected static final float MISSING = 1000;

	protected float value = 0.0F;

	/**
	 * Positive values are normal, negative values are masked. <code>MISSING</code> 
	 * indicates missing value.
	 */
	protected float confidence = MISSING;

	/**
	 * Default Constructor
	 */
	public CSMarkerValue() {
	}

	public CSMarkerValue(CSMarkerValue m) {
		this.value = m.value;
		this.confidence = m.confidence;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double s) {
		value = (float) s;
	}

	public void setMissing(boolean flag) {
		if (flag)
			confidence = MISSING;
		else 
			setPresent();
	}

	// This method should be overwritten by subclasses who do not infer
	// detection status from the confidence value.
	public void setAbsent() {
		if (isMasked())
			confidence = -(float) m_threshold;
		else
			confidence = (float) m_threshold;
	}

	// This method should be overwritten by subclasses who do not infer
	// detection status from the confidence value.
	public void setPresent() {
		if (isMasked())
			confidence = -(float) (p_threshold - 0.00005);
		else
			confidence = (float) (p_threshold - 0.00005);
	}

	// This method should be overwritten by subclasses who do not infer
	// detection status from the confidence value.
	public void setMarginal() {
		if (isMasked())
			confidence = -(float) (p_threshold);
		else
			confidence = (float) (p_threshold);

	}

	public boolean isValid() {
		return (!isMasked() && !isMissing());
	}

	public boolean isAbsent() {
		return (confidence >= m_threshold);
	}

	public boolean isMarginal() {
		return ((confidence >= p_threshold) && (confidence < m_threshold));
	}

	public boolean isPresent() {
		return (confidence > 0.0 && confidence < p_threshold);
	}

	public boolean isMissing() {
		return (confidence == MISSING);
	}

	public boolean isMasked() {
		return (confidence < 0.0);
	}

	public void mask() {
		if (confidence > 0) {
			confidence *= -1.0;
		}
	}

	public void unmask() {
		if (confidence < 0) {
			confidence *= -1.0;
		}
	}

	public char getStatusAsChar() {
		char result = 'U';
		float tempConf = confidence;
		confidence = Math.abs(confidence);
		if (isAbsent()) {
			result = 'A';
		} else if (isMarginal()) {
			result = 'M';
		} else if (isPresent()) {
			result = 'P';
		} else if (isMissing()) {
			result = 'U';
		}
		if (tempConf < 0.0) {
			result = Character.toLowerCase(result);
		}
		confidence = tempConf;

		return result;
	}

	public double getConfidence() {
		return confidence;
	}

	public void setConfidence(double c) {
		confidence = (float) c;
	}

	/**
	 * Set the cutoff p-value used for a Present call
	 * 
	 * @param pThreshold
	 */
	public void setPresentThreshold(double pThreshold) {
		if (pThreshold > 0 && pThreshold <= 1)
			p_threshold = (float) pThreshold;
	}

	/**
	 * Set the cutoff p-value used for a Marginal call
	 * 
	 * @param mThreshold
	 */
	public void setMarginalThreshold(double mThreshold) {
		if (mThreshold > 0 && mThreshold <= 1)
			m_threshold = (float) mThreshold;
	}

	/**
	 * Compares two expression markers
	 * 
	 * @param m
	 *            MarkerValue marker to be compared to
	 * @return boolean equality
	 */
	public boolean equals(Object obj) {
		if (obj instanceof DSMarkerValue) {
			DSMarkerValue m = (DSMarkerValue) obj;
			if ((isMissing() || m.isMissing()) || (!isValid() || !m.isValid())) {
				return false;
			} else {
				return (getValue() == m.getValue());
			}
		} else {
			return false;
		}
	}
}
