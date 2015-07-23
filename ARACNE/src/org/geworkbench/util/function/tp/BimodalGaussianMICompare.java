package org.geworkbench.util.function.tp;

import cern.colt.list.DoubleArrayList;
import cern.jet.stat.Descriptive;
import org.geworkbench.util.function.MultivariateGaussianBimodal;
import org.geworkbench.util.function.functionParameters.bimodalGaussianParams.BimodalGaussianParamsBase;
import org.geworkbench.util.function.functionParameters.bimodalGaussianParams.BimodalGaussianParamsFactory;
import org.geworkbench.util.network.GeneNetworkEdgeImpl;
import org.geworkbench.util.pathwaydecoder.GeneGeneRelationship;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

public class BimodalGaussianMICompare {

    File writeFile;
    BufferedWriter writer;
    int firstDataCtr;

    public void compareAllMIs(int firstDataCtr) {
        this.firstDataCtr = firstDataCtr;

        String dirBase = "/users/aam2110/MI/BimodalGaussian/BimodalGaussianMIs";
        this.writeFile = new File(dirBase + "/MpiTest/Accurate_TEST/StartIndex" + firstDataCtr + ".txt");
        //        this.writeFile = new File(dirBase + "/MpiTest/Fast/StartIndex" + firstDataCtr + ".txt");
        writeFile.getParentFile().mkdirs();

        Vector params1Data = org.geworkbench.bison.util.FileUtil.readFile(new File(dirBase + "/Params1.txt"));
        Vector params2Data = org.geworkbench.bison.util.FileUtil.readFile(new File(dirBase + "/Params2.txt"));
        Vector params3Data = org.geworkbench.bison.util.FileUtil.readFile(new File(dirBase + "/Params3.txt"));
        Vector params4Data = org.geworkbench.bison.util.FileUtil.readFile(new File(dirBase + "/Params4.txt"));

        Vector[] allData = {params1Data, params2Data, params3Data, params4Data};

        //        try{
        //            writer = new BufferedWriter(new FileWriter(writeFile));
        for (int firstParamCtr = 0; firstParamCtr < allData.length - 1; firstParamCtr++) {
            Vector firstParamsData = allData[firstParamCtr];
            if (firstDataCtr >= firstParamsData.size()) {
                System.out.println("Data counter " + firstDataCtr + " greater than " + firstParamsData.size() + " for params " + firstParamCtr);
                continue;
            }


            String[] curFirstParamsData = (String[]) firstParamsData.get(firstDataCtr);
            for (int secondParamCtr = firstParamCtr + 1; secondParamCtr < allData.length; secondParamCtr++) {
                Vector secondParamsData = allData[secondParamCtr];
                processMI(curFirstParamsData, secondParamsData);
            }
        }
        System.out.println("*********************FINISHED " + firstDataCtr + "*******************************");
        //            writer.close();
        //        }catch(Exception e){
        //            e.printStackTrace();
        //        }
    }


    void compareAllMIs() {
        //        String dirBase = "Z:\\MI\\BimodalGaussian\\BimodalGaussianMIs";
        String dirBase = "/users/aam2110/MI/BimodalGaussian/BimodalGaussianMIs";

        Vector params1Data = org.geworkbench.bison.util.FileUtil.readFile(new File(dirBase + "/Params1.txt"));
        Vector params2Data = org.geworkbench.bison.util.FileUtil.readFile(new File(dirBase + "/Params2.txt"));
        Vector params3Data = org.geworkbench.bison.util.FileUtil.readFile(new File(dirBase + "/Params3.txt"));
        Vector params4Data = org.geworkbench.bison.util.FileUtil.readFile(new File(dirBase + "/Params4.txt"));

        Vector[] allData = {params1Data, params2Data, params3Data, params4Data};

        for (int firstParamCtr = 0; firstParamCtr < allData.length - 1; firstParamCtr++) {
            Vector firstParamsData = allData[firstParamCtr];
            for (int firstDataCtr = 0; firstDataCtr < firstParamsData.size(); firstDataCtr += 20) {
                String[] curFirstParamsData = (String[]) firstParamsData.get(firstDataCtr);
                for (int secondParamCtr = firstParamCtr + 1; secondParamCtr < allData.length; secondParamCtr++) {
                    Vector secondParamsData = allData[secondParamCtr];
                    processMI(curFirstParamsData, secondParamsData);
                }
            }
        }
    }

    void processMI(String[] firstParamsData, Vector secondParamsData) {
        double firstParamsMI = Double.parseDouble(firstParamsData[3]);
        int secondParamsIndex = getClosestMI(firstParamsMI, secondParamsData);
        if (secondParamsIndex == -1) {
            return;
        }
        String[] curSecondParamsData = (String[]) secondParamsData.get(secondParamsIndex);
        compareCalculatedMi(firstParamsData, curSecondParamsData);
    }

    int getClosestMI(double targetMi, Vector data) {
        double lowestMiDiff = Double.MAX_VALUE;
        int lowestMiIndex = -1;

        for (int miCtr = 0; miCtr < data.size(); miCtr++) {

            String[] dataVals = (String[]) data.get(miCtr);
            double curMi = Double.parseDouble(dataVals[3]);
            double curMiDiff = Math.abs(curMi - targetMi);
            if (miCtr == 0) {
                if (curMi > targetMi) {
                    return -1;
                }
            }

            if (curMiDiff <= lowestMiDiff) {
                lowestMiDiff = curMiDiff;
                lowestMiIndex = miCtr;
            } else {
                return lowestMiIndex;
            }

        }
        return -1;
    }


    void compareCalculatedMi(String[] data1, String[] data2) {
        String gaussian1ParamsName = data1[0];
        String gaussian2ParamsName = data2[0];

        BimodalGaussianParamsBase params1 = BimodalGaussianParamsFactory.getParams(data1[0]);
        BimodalGaussianParamsBase params2 = BimodalGaussianParamsFactory.getParams(data2[0]);

        double gaussian1Covar1 = Double.parseDouble(data1[1]);
        double gaussian1Covar2 = Double.parseDouble(data1[2]);
        params1.setCovariance1(gaussian1Covar1);
        params1.setCovariance2(gaussian1Covar2);

        double gaussian2Covar1 = Double.parseDouble(data2[1]);
        double gaussian2Covar2 = Double.parseDouble(data2[2]);
        params2.setCovariance1(gaussian2Covar1);
        params2.setCovariance2(gaussian2Covar2);

        double gaussian1ExpectedMi = Double.parseDouble(data1[3]);
        double gaussian2ExpectedMi = Double.parseDouble(data2[3]);


        MultivariateGaussianBimodal bimodalGaussian1 = new MultivariateGaussianBimodal(params1);
        MultivariateGaussianBimodal bimodalGaussian2 = new MultivariateGaussianBimodal(params2);

        //        int[] numSamplesArr = {
        //            100, 200, 300, 400, 500, 600, 800, 1000, 1200, 1400, 1600, 1800,
        //            2000};
        int[] numSamplesArr = {125, 250, 500, 1000, 1500, 2000};
        //        int[] numSamplesArr = {125, 250};
        //        int[] numSamplesArr = {100,200,400, 600, 1000, 1500, 2000};
        //int[] numSamplesArr = {600, 1800, 2000};

        int numIterations = 10;

        DoubleArrayList dalMiDifferences = new DoubleArrayList();
        DoubleArrayList dalGaussian1 = new DoubleArrayList();
        DoubleArrayList dalGaussian2 = new DoubleArrayList();

        for (int maCtr = 0; maCtr < numSamplesArr.length; maCtr++) {
            int microarrayNo = numSamplesArr[maCtr];
            bimodalGaussian1.setNumDataPoints(microarrayNo);
            bimodalGaussian2.setNumDataPoints(microarrayNo);
            for (int i = 0; i < numIterations; i++) {
                try {
                    double[][] gaussian1Data = bimodalGaussian1.getData();
                    double[][] gaussian2Data = bimodalGaussian2.getData();

                    GeneGeneRelationship ggr = new org.geworkbench.util.pathwaydecoder.GeneGeneRelationship(null, null, microarrayNo, 0, true);

                    GeneNetworkEdgeImpl edge = ggr.newMIScore(gaussian1Data[0], gaussian1Data[1], 0.0);
                    //                    GeneNetworkEdgeImpl edge = ggr.miScore(gaussian1Data[0], gaussian1Data[1], 0.0);
                    double gaussian1MiScore = edge.getMI();
                    //correction
                    gaussian1MiScore = gaussian1MiScore * Math.log(microarrayNo) * 2;

                    edge = ggr.newMIScore(gaussian2Data[0], gaussian2Data[1], 0.0);
                    //                    edge = ggr.miScore(gaussian2Data[0], gaussian2Data[1], 0.0);
                    double gaussian2MiScore = edge.getMI();
                    //correction
                    gaussian2MiScore = gaussian2MiScore * Math.log(microarrayNo) * 2;

                    double diff = gaussian1MiScore - gaussian2MiScore;
                    dalMiDifferences.add(diff);
                    dalGaussian1.add(gaussian1MiScore);
                    dalGaussian2.add(gaussian2MiScore);
                    //                System.out.print(i + " ");
                } catch (Exception e) {
                    System.out.println("Exception " + e.getMessage());
                }
            }
            //            System.out.println();
            //            System.out.println();

            double miDifferenceMean = Descriptive.mean(dalMiDifferences);
            double miDifferenceVar = Descriptive.sampleVariance(dalMiDifferences, miDifferenceMean);
            double miDifferenceSd = Descriptive.standardDeviation(miDifferenceVar);

            double gaussian1Mean = Descriptive.mean(dalGaussian1);
            double gaussian1Var = Descriptive.sampleVariance(dalGaussian1, gaussian1Mean);
            double gaussian1Sd = Descriptive.standardDeviation(gaussian1Var);

            double gaussian2Mean = Descriptive.mean(dalGaussian2);
            double gaussian2Var = Descriptive.sampleVariance(dalGaussian2, gaussian1Mean);
            double gaussian2Sd = Descriptive.standardDeviation(gaussian2Var);

            StringBuffer sb = new StringBuffer();

            sb.append("Gaussian1 Name\t" + gaussian1ParamsName);
            sb.append("\tGaussian1 Covariance 1\t" + gaussian1Covar1 + "\tGaussian1 Covariance 2\t" + gaussian1Covar2);
            sb.append("\tGaussian1 Expected MI\t" + gaussian1ExpectedMi);

            sb.append("\tGaussian2 Name\t" + gaussian2ParamsName);
            sb.append("\tGaussian2 Covariance 1\t" + gaussian2Covar1 + "\tGaussian2 Covariance 2\t" + gaussian2Covar2);
            sb.append("\tGaussian2 Expected MI\t" + gaussian2ExpectedMi);

            sb.append("\tExpected MI Difference\t" + (gaussian1ExpectedMi - gaussian2ExpectedMi));

            sb.append("\tData Points\t" + microarrayNo + "\tDifference Mean\t" + miDifferenceMean + "\tDifference sd\t" + miDifferenceSd);
            sb.append("\tGaussian1 Mean\t" + gaussian1Mean + "\tGaussian1 Sd\t" + gaussian1Sd);
            sb.append("\tGaussian2 Mean\t" + gaussian2Mean + "\tGaussian2 Sd\t" + gaussian2Sd + "\n");
            //            System.out.println(sb.toString());
            System.out.println("Processed " + firstDataCtr + " Gaussian1 params " + gaussian1ParamsName + " Gaussian2 params " + gaussian2ParamsName + "Data points " + microarrayNo);
            try {
                writer = new BufferedWriter(new FileWriter(writeFile, true));
                writer.write(sb.toString());
                writer.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

    public static void main(String[] args) {
        int index = Integer.parseInt(args[0]);
        new BimodalGaussianMICompare().compareAllMIs(index);
    }

}
