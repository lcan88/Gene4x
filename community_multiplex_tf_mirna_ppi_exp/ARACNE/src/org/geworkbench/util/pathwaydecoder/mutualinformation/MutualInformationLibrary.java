package org.geworkbench.util.pathwaydecoder.mutualinformation;

/**
 * <p>Title: Bioworks</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class MutualInformationLibrary {
    static private boolean libraryLoaded = false;

    MutualInformationLibrary() {
        try {
            System.loadLibrary("mutualinfo");
            System.out.println("mutualinfo library loaded");
            libraryLoaded = true;
        } catch (UnsatisfiedLinkError ex) {
            System.out.println("Exception: " + ex);
        } catch (Exception ex) {

        }
    }

    public boolean initialized() {
        return libraryLoaded;
    }

    /**
     * Create the adjacency matrix from a file based matrix
     *
     * @param theMatrix String the name of the Gene Expression matrix
     * @param output    String    the name of the output Adjancency Matrix
     * @param mArrayNo  int     the number of microarrays to use
     * @param mArrayId  int[]   the ids of the microarrays to use
     * @param reduce    boolean   if true, remove non-nearest neighbors
     */

    // threshold        => the minimum MI of a relationship
    // percent          => the percent to compute the high and low range (e.g. 33%)
    // miErrorPercent   => the error tolerance (e.g. .1)

    native public void create(String theMatrix, String output, int mArrayNo, int[] mArrayId);

    native public void createConstrainedHigh(String theMatrix, int controlId, double percent);

    native public void createConstrainedLow(String theMatrix, int controlId, double percent);

    native public void createConstrainedHigh(String theMatrix, String geneAffyId, double percent);

    native public void createConstrainedLow(String theMatrix, String geneAffyId, double percent);

    native public void setParams(double mean, double sigma, double threshold, double miErrorPercent, boolean reduce);

    native public double getCompletion();

    native public void stop();

    native public void test();

}
