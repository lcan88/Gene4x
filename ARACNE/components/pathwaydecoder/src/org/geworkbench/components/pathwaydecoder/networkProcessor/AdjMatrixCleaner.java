package org.geworkbench.components.pathwaydecoder.networkProcessor;

import org.geworkbench.bison.datastructure.biocollections.microarrays.CSExprMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.util.pathwaydecoder.mutualinformation.AdjacencyMatrix;

import java.io.File;

public class AdjMatrixCleaner {
    double[] tmpArrPVals = {//        .001, .005, .01, .025, .05, .1, .25, .5, .75, 1.0};
        //    .001, .01, .05, .1, .25, .5, .75, 1.0};
        //       .00001, .0001,  .001, .01
        //                    .00001, .0001,  .001, .01, .05, .1, .25, .5, .75, 1.0};
        //        .00001, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20};
        //    200, 210, 220, 230, 240, 250, 260, 270, 280, 290, 300, 310, 320, 330, 340, 350, 360, 370, 380, 390, 400};
        1E-20, 1E-19, 1E-18, 1E-17, 1E-16, 1E-15, 1E-14, 1E-13, 1E-12, 1E-11, 1E-10, 1E-9, 1E-8, 1E-7, 1E-6, 1E-5, .0001, .0005, .00075, .001, .0025, .005, .0075, .01, .025, .05, .075, .1, .15, .2, .25, .3, .35, .4, .45, .5, .6, .65, .7, .75, .8, .85, .9, .95, 1.0};

    //        double[] arrPVals = {1E-10, 1E-9, 1E-8, 1E-7, 1E-6, 1E-5, .0001, .0005, .00075, .001, .0025, .005, .0075, .01,
    //            .025, .05, .075, .1, .15, .2, .25, .3, .35, .4, .45, .5, .6, .65, .7, .75, .8, .85, .9, .95, 1.0};

    double[] arrPVals = {1E-200, 1E-190, 1E-180, 1E-170, 1E-160, 1E-150, 1E-140, 1E-130, 1E-120, 1E-110, 1E-100, 1E-90, 1E-80, 1E-70, 1E-60, 1E-50, 1E-40, 1E-30};
    //        double[] arrPVals = {1E-20, 1E-18, 1E-16, 1E-14, 1E-12, 1E-10, 1E-8, 1E-6, 1E-5, 1E-4, 1E-3, 1E-2, 1E-1};

    double[] arrToleranceVals = {0.1, 0.0};

    //        double[] arrToleranceVals = {.01, .025, .05, .1, .15, .2, .25, .5, .75};
    //        double[] arrToleranceVals = {0};

    double[] arrSigmas = {.04};
    int[] arrNumSamples = {125, 250, 500, 750, 1000};

    public AdjMatrixCleaner() {

    }

    public static void main(String[] args) {
        new AdjMatrixCleaner().doIt(args);
    }

    void doIt(String[] args) {
        String expFileName = args[0];
        String noThresholdDirName = args[1];
        String pValueMapFileName = args[2];
        String cleanedMatrixDir = args[3];

        //        String expFileName = args[0];
        //        String networkReconstDir = args[1];
        //        String pValueMapFileName = args[2];
        double sigma = Double.parseDouble(args[4]);
        arrSigmas = new double[1];
        arrSigmas[0] = sigma;

        int samples = Integer.parseInt(args[5]);
        arrNumSamples = new int[1];
        arrNumSamples[0] = samples;

        if (args.length > 6) {
            double pVal = Double.parseDouble(args[6]);
            arrPVals = new double[1];
            arrPVals[0] = pVal;
        }

        CSExprMicroarraySet mArraySet = new CSExprMicroarraySet();
        mArraySet.readFromFile(new File(expFileName));

        cleanAllFiles(mArraySet, noThresholdDirName, new File(pValueMapFileName), cleanedMatrixDir);
    }

    public void cleanAllFiles(DSMicroarraySet mArraySet, String noThresholdDirName, File pValueMapFile, String cleanedMatrixDirName) {
        for (int numSamplesCtr = 0; numSamplesCtr < arrNumSamples.length; numSamplesCtr++) {
            int numSamples = arrNumSamples[numSamplesCtr];

            for (int sigmaCtr = 0; sigmaCtr < arrSigmas.length; sigmaCtr++) {
                double sigma = arrSigmas[sigmaCtr];

                //                HashMatrix pValueMatrix = calculatePValueMatrix(mArraySet,
                //                    numSamples, sigma);
                org.geworkbench.bison.util.HashMatrix pValueMatrix = org.geworkbench.bison.util.FileUtil.readHashMatrix(pValueMapFile);

                //                File noThresholdDir = new File(networkReconstDir + "/samples_" +
                //                                               numSamples + "/sigma_" + sigma
                //                                               + "/NoThreshold");
                File noThresholdDir = new File(noThresholdDirName);
                File[] noThresholdFiles = noThresholdDir.listFiles();
                for (int fileCtr = 0; fileCtr < noThresholdFiles.length; fileCtr++) {
                    File readFile = noThresholdFiles[fileCtr];

                    if (readFile.getName().endsWith(".adj")) {

                        //Clean the files based on difference pValues and tolerance values
                        for (int pValCtr = 0; pValCtr < arrPVals.length; pValCtr++) {
                            double pVal = arrPVals[pValCtr];
                            Object objMiThresh = pValueMatrix.get(numSamples + "", new Double(pVal));
                            double miThresh = Double.parseDouble((String) objMiThresh);

                            for (int toleranceValCtr = 0; toleranceValCtr < arrToleranceVals.length; toleranceValCtr++) {
                                double tolerance = arrToleranceVals[toleranceValCtr];

                                cleanFile(mArraySet, readFile, fileCtr, numSamples, sigma, pVal, tolerance, miThresh, cleanedMatrixDirName);
                            }
                        }
                    }
                }
            }
        }
        System.out.println("done");
    }

    void cleanFile(DSMicroarraySet mArraySet, File adjMatrixFile, int ctr, int numSamples, double sigma, double pVal, double tolerance, double miThresh, String cleanedMatrixDir) {

        //        String writeDir = networkReconstDir
        //            + "/samples_" + numSamples
        //            + "/sigma_" + sigma
        //            + "/CleanedMatrices"
        //            + "/pVal_" + pVal
        //            + "/tolerance_" + tolerance;
        String writeDir = cleanedMatrixDir + "/pVal_" + pVal + "/tolerance_" + tolerance;

        new File(writeDir).mkdirs();

        File writeFile = new File(writeDir + "/" + ctr + ".adj");

        System.out.println(writeFile);
        AdjacencyMatrix adjMatrix = new AdjacencyMatrix();
        adjMatrix.read(adjMatrixFile.getAbsolutePath(), mArraySet);
        adjMatrix.clean(mArraySet, miThresh, tolerance);
        adjMatrix.print(mArraySet, writeFile, 0.0);
    }


}
