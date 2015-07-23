package org.geworkbench.bison.parsers;

import java.io.Serializable;

/**
 * <p>Title: Plug And Play</p>
 * <p>Description: Dynamic Proxy Implementation of enGenious</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: First Genetic Trust Inc.</p>
 *
 * @author Manjunath Kustagi
 * @version 1.0
 */

public class DataParseContext implements Serializable {
    /**
     * This is a BIG HACK! The context should be represented by a sub-marker type specific version.
     * That is. there should be a context for gene expression, one for genotypes, etc.
     * Right now, they are all packed into one. Suggestion, use a class that is returned by the IMarker
     * interface
     */

    /**
     * The marker type (e.g., genotype, gene expression, etc)
     */
    public int type = 0;
    /**
     * The value used to encode genotypes as allele1 + allele2 * gtBase
     */
    public int gtBase = 256;
    /**
     * Is a genotype or two alleles with phase?
     */
    public boolean isGenotype = true;
    /**
     * used to replace the value v with Log(Math.max(v + addValue) , minValue)
     */
    public boolean isLog = false;
    public double addValue = 0;
    public double minValue = 0;
    /**
     * Various parameters used in the computationa of the statistical distributions of
     * gene expression data. Used only by pattern discovery.
     */
    public double sigma0 = 0;
    public double maxSigma = 0;
    public double deltaSigma = 0;
    public String pdfMode = "FromControl";

    /**
     * Place holders for default values for some of the analytical tools. E.g., the minSupport
     * and minMarkers for pattern discovery, whether patterns should be sorted
     */
    public int minSupport = 2;
    public int minMarkers = 2;
    public String sortKey = "";

    /**
     * constructor
     */
    public DataParseContext() {
    }
}
