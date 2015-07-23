package org.geworkbench.util.associationdiscovery.cluster;

import distributions.ChiSquareDistribution;
import org.geworkbench.bison.datastructure.biocollections.classification.phenotype.CSClassCriteria;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMutableMarkerValue;
import org.geworkbench.bison.datastructure.complex.panels.DSItemList;

import java.util.*;
import org.geworkbench.bison.annotation.CSAnnotationContextManager;
import org.geworkbench.bison.annotation.DSAnnotationContext;
import org.geworkbench.bison.annotation.DSCriterion;

/**
 * <p>Title: Plug And Play</p>
 * <p>Description: Dynamic Proxy Implementation of enGenious</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: First Genetic Trust Inc.</p>
 *
 * @author Manjunath Kustagi
 * @version 1.0
 */

public class PatternStats extends CSMatchedMatrixPattern {
    //static private JPatternStatistics PS = new JPatternStatistics();
    static private ArrayList frequencies = new ArrayList();
    static private ChiSquareDistribution distribution = new ChiSquareDistribution(1);
    static private int chipNo = 0;
    static private int geneNo = 0;
    
    class JPolyGene {
        int count = 1;
        String label = null;
        double frequency = 0.0;
    }
    
    private HashMap phenotype = new HashMap();
    private HashMap control = new HashMap();
    private double phNo = 0;
    private double bgNo = 0;
    
    public PatternStats(CSMatchedMatrixPattern aPattern) {
        super(aPattern.getPattern());
        matches = aPattern.matches();
        pattern = aPattern.getPattern();
        zScore = aPattern.getPValue();
        bkMatches = aPattern.bkMatches;
    }
        
    public void createContingencyTable(DSMicroarraySet chips) {
        // This routine creates a contingency table for all the
        // different incarnations of a polygenic genotype
        // or haplotype, both in the phenotype and control set.
        // todo - watkin - refactor to use CSAnnotationContext
        CSAnnotationContextManager manager = CSAnnotationContextManager.getInstance();
        DSAnnotationContext<DSMicroarray> context = manager.getCurrentContext(chips);
        
        if (!context.isUnsupervised()) {
            // Compute only if phenotype and controls are defined
            DSItemList<DSMicroarray> cases = context.getItemsWithLabel(CSClassCriteria.cases.toString());
            for (DSBioObject bioObject : cases) {               
                if (bioObject instanceof DSMicroarray) {                    
                    DSMicroarray chip = (DSMicroarray) bioObject;
                    if (chip.enabled()) {
                        String polygene = "";
                        boolean isValid = true;
                        for (DSGeneMarker marker : pattern.markers()) {
                            // get the gene identifier from the pattern
                            DSMutableMarkerValue spot = (DSMutableMarkerValue) chip.getMarkerValue(marker);
                            if (spot.isMissing()) {
                                isValid = false;
                                break;
                            }
                            double value = spot.getValue();
                            String markerString = Double.toString(value);
                            // Add the marker to the polygenic association
                            polygene += markerString;
                        }
                        if (isValid) {
                            // Check if it exists already in the contingency table
                            JPolyGene pg = (JPolyGene) phenotype.get(polygene);
                            if (pg != null) {
                                // If it exists just increment its count
                                pg.count++;
                            } else {
                                // Otherwise create a new one with Count = 1
                                pg = new JPolyGene();
                                pg.label = polygene;
                                phenotype.put(polygene, pg);
                            }
                        }
                        phNo++;
                    }
                }
            }
            // Repeat same procedure for controls
            DSItemList<DSMicroarray> controls = context.getItemsWithLabel(CSClassCriteria.controls.toString());
            for (DSBioObject bioObject : controls) {
                if (bioObject instanceof DSMicroarray) {
                    DSMicroarray chip = (DSMicroarray) bioObject;
                    if (chip.enabled()) {
                        String polygene = "";
                        boolean isValid = true;
                        for (DSGeneMarker marker : pattern.markers()) {
                            DSMutableMarkerValue spot = (DSMutableMarkerValue) chip.getMarkerValue(marker);
                            if (spot.isMissing()) {
                                isValid = false;
                                break;
                            }
                            double value = spot.getValue();
                            String markerString = Double.toString(value);
                            polygene += markerString;
                        }
                        if (isValid) {
                            JPolyGene pg = (JPolyGene) control.get(polygene);
                            if (pg != null) {
                                pg.count++;
                            } else {
                                pg = new JPolyGene();
                                pg.label = polygene;
                                control.put(polygene, pg);
                            }
                        }
                        bgNo++;
                    }
                }
            }
        }
    }
    
    public void printContingencyTable(DSMicroarraySet chips) {
        createContingencyTable(chips);
        Set phKeys = phenotype.keySet();
        Set coKeys = control.keySet();
        HashSet keys = new HashSet(phKeys);
        keys.addAll(coKeys);
        Iterator it = keys.iterator();
        while (it.hasNext()) {
            String pg = (String) it.next();
            System.out.print(pg);
            JPolyGene countPh = (JPolyGene) phenotype.get(pg);
            if (countPh != null) {
                System.out.print("\t" + countPh.count);
            } else {
                System.out.print("\t0");
            }
            JPolyGene countCo = (JPolyGene) control.get(pg);
            if (countCo != null) {
                System.out.println("\t" + countCo.count);
            } else {
                System.out.println("\t0");
            }
        }
    }
    
    public double getPValue(DSMicroarraySet chips, boolean print) {
        createContingencyTable(chips);
        Set phKeys = phenotype.keySet();
        Set coKeys = phenotype.keySet();
        HashSet keys = new HashSet(phKeys);
        keys.addAll(coKeys);
        Iterator it = keys.iterator();
        double chi2 = 0;
        double vPh = 0;
        double vBk = 0;
        double dgrNo = 0;
        while (it.hasNext()) {
            String polygene = (String) it.next();
            JPolyGene value1 = (JPolyGene) phenotype.get(polygene);
            if (value1 == null) {
                vPh = 0;
            } else {
                vPh = value1.count;
            }
            JPolyGene value2 = (JPolyGene) control.get(polygene);
            if (value2 == null) {
                vBk = 0;
            } else {
                vBk = value2.count;
            }
            if (vPh + vBk > 1) {
                double expectedPh = (vPh + vBk) * phNo / (phNo + bgNo);
                double expectedBk = (vPh + vBk) * bgNo / (phNo + bgNo);
                chi2 += Math.pow(Math.abs(vPh - expectedPh), 2.0) / expectedPh;
                chi2 += Math.pow(Math.abs(vBk - expectedBk), 2.0) / expectedBk;
                dgrNo++;
            }
        }
        distribution.setDegrees((int) dgrNo - 1);
        if (print) {
            System.out.println("Chi2 = " + chi2 + " Deegrees: " + (dgrNo - 1));
        }
        double pvalue = 1 - distribution.getCDF(chi2);
        return pvalue;
    }
    
    public double getPValue(CSMatchedMatrixPattern pattern, DSMicroarraySet chips, boolean print) {
        createContingencyTable(chips);
        Set phKeys   = phenotype.keySet();
        Set coKeys   = phenotype.keySet();
        HashSet keys = new HashSet(phKeys);
        keys.addAll(coKeys);
        Iterator it = keys.iterator();
        double chi2  = 0;
        double vPh   = 0;
        double vBk   = 0;
        double dgrNo = 0;
        while(it.hasNext()) {
            String polygene = (String)it.next();
            JPolyGene value1   = (JPolyGene)phenotype.get(polygene);
            if(value1 == null) {
                vPh = 0;
            } else {
                vPh = value1.count;
            }
            JPolyGene value2   = (JPolyGene)control.get(polygene);
            if(value2 == null) {
                vBk = 0;
            } else {
                vBk = value2.count;
            }
            if(vPh + vBk > 1) {
                double expectedPh = (vPh + vBk)*phNo/(phNo+bgNo);
                double expectedBk = (vPh + vBk)*bgNo/(phNo+bgNo);
                chi2    += Math.pow(Math.abs(vPh - expectedPh),2.0)/expectedPh;
                chi2    += Math.pow(Math.abs(vBk - expectedBk),2.0)/expectedBk;
                dgrNo++;
            }
        }
        distribution.setDegrees((int)dgrNo - 1);
        if(print) {
            System.out.println("Chi2 = "+chi2+" Deegrees: " + (dgrNo - 1));
        }
        double pvalue = 1 - distribution.getCDF(chi2);
//        double pvalue = 1;
        return pvalue;
    }
    
    
    public void print(int i) {
        Set phKeys = phenotype.keySet();
        Set coKeys = phenotype.keySet();
        HashSet keys = new HashSet(phKeys);
        keys.addAll(coKeys);
        Iterator it = keys.iterator();
        double chi2 = 0;
        double vPh = 0;
        double vBk = 0;
        double dgrNo = -1;
        while (it.hasNext()) {
            String polygene = (String) it.next();
            //String result   = polygene + ": ";
            JPolyGene value1 = (JPolyGene) phenotype.get(polygene);
            if (value1 == null) {
                vPh = 0;
                //result = result + "[0]";
            } else {
                vPh = value1.count;
                //result = result + "[" + vPh + "] ";
            }
            JPolyGene value2 = (JPolyGene) control.get(polygene);
            if (value2 == null) {
                vBk = 0;
                //result = result + "[0]";
            } else {
                vBk = value2.count;
                //result = result + "[" + vBk + "]";
            }
            if (vPh + vBk > 5) {
                //System.out.println("A: " + result);
                double expected = (vPh + vBk) * 0.5;
                chi2 += Math.pow(Math.abs(vPh - expected), 2.0) / expected;
                dgrNo++;
            }
        }
        //System.out.println("============================");
        distribution.setDegrees((int) dgrNo);
        //double threshold = distribution.getQuantile(0.05);
        double pvalue = 1 - distribution.getCDF(chi2);
        chi2 = (double) ((int) (chi2 * 10)) / 10;
        //**JGTConsole.ListLog("[" + i + "] Pv: " + pvalue + " C2: " + chi2 + ", N: " + dgrNo);
    }
}
