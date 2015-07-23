package org.geworkbench.components.pathwaydecoder.networkProcessor;

import org.geworkbench.bison.datastructure.biocollections.microarrays.CSExprMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.views.CSMicroarraySetView;
import org.geworkbench.bison.datastructure.biocollections.views.DSMicroarraySetView;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.util.pathwaydecoder.GeneGeneRelationship;

import java.io.File;
import java.io.FileWriter;

public class PValueMatrixPrinter {
    double[] defaultArrPVals = {//            .00001, .0001,  .001, .01, .05, .1, .25, .5, .75, 1.0};
        .0001, .00025, .0005, .00075, .001, .0025, .005, .0075, .01, .025, .05, .075, .1, .15, .2, .25, .3, .35, .4, .45, .5, .6, .65, .7, .75, .8, .85, .9, .95, 1.0};

    public PValueMatrixPrinter() {
    }

    public static void main(String[] args) {
        new PValueMatrixPrinter().doIt(args);
    }

    void doIt(String[] args) {
        String expFileName = args[0];
        String writeFileName = args[1];
        int numSamples = Integer.parseInt(args[2]);
        double sigma = Double.parseDouble(args[3]);

        int iterations = (int) ((1.0 / defaultArrPVals[0]) * 10.0);

        CSExprMicroarraySet mArraySet = new CSExprMicroarraySet();
        mArraySet.readFromFile(new File(expFileName));

        File writeFile = new File(writeFileName);
        writeFile.getParentFile().mkdirs();

        System.out.println("Printing p-values for iterations : " + iterations + "\tSigma: " + sigma + "\tSamples: " + numSamples + "\tWrite File: " + writeFileName);
        printMiForPValues(mArraySet, writeFile, numSamples, sigma, defaultArrPVals, iterations);
    }

    public void printMiForPValues(DSMicroarraySet mArraySet, File writeFile, int numSamples, double sigma, double[] arrPVals, int numIterations) {
        try {
            FileWriter writer = new FileWriter(writeFile);
            for (int pValCtr = 0; pValCtr < arrPVals.length; pValCtr++) {
                double pValue = arrPVals[pValCtr];
                System.out.print("\t" + pValue);
                writer.write("\t" + pValue);
            }
            System.out.println();
            writer.write("\n");

            //            for (int numSamplesCtr = 0; numSamplesCtr < arrNumSamples.length;
            //                 numSamplesCtr++) {

            //                int numSamples = arrNumSamples[numSamplesCtr];
            System.out.print(numSamples + "");
            writer.write(numSamples + "");

            //                int numIterations = (int) (1.0 / pValue) * 100;

            DSMicroarraySetView<DSGeneMarker, DSMicroarray> view = new CSMicroarraySetView<DSGeneMarker, DSMicroarray>();
            view.setDataSet(mArraySet);
            org.geworkbench.util.pathwaydecoder.GeneGeneRelationship bgGgr = new org.geworkbench.util.pathwaydecoder.GeneGeneRelationship(view, 0, false, sigma);
            bgGgr.setCopulaTransform(true);
            bgGgr.computeBackground(numIterations, numSamples, GeneGeneRelationship.RANK_CHI2, org.geworkbench.util.pathwaydecoder.GeneGeneRelationship.BG_RANDOM);
            //                                        GeneGeneRelationship.RANK_MI, 1);
            for (int pValCtr = 0; pValCtr < arrPVals.length; pValCtr++) {
                double pValue = arrPVals[pValCtr];
                double miThresh = bgGgr.getMiByPValue(pValue);
                System.out.print("\t" + miThresh);
                writer.write("\t" + miThresh);
            }
            System.out.println();
            writer.write("\n");

            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
