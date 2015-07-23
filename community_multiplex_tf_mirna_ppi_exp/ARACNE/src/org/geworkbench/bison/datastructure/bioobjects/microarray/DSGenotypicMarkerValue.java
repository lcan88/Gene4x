package org.geworkbench.bison.datastructure.bioobjects.microarray;

public interface DSGenotypicMarkerValue extends DSMutableMarkerValue {
    /**
     * Gets either of the two alleles
     *
     * @param id int either of the two dimensions
     * @return int allele as int
     */
    public short getAllele(int id);

    /**
     * Sets the primary allele
     *
     * @param allele int
     */
    public void setAllele(int allele);

    /**
     * Sets genotype
     *
     * @param allele0 int
     * @param allele1 int
     */
    public void setGenotype(int allele0, int allele1);
}
