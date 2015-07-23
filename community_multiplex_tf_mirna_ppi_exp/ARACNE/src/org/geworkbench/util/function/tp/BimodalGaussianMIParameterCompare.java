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
import java.util.Iterator;
import java.util.Vector;

public class BimodalGaussianMIParameterCompare {
    File writeFile;
    BufferedWriter writer;
    double sigma;

    public BimodalGaussianMIParameterCompare() {
    }

    public static void main(String[] args) {
        new BimodalGaussianMIParameterCompare().computeMisForSigma(0.0);
    }

    public void computeMisForSigma(double sigma) {
        this.sigma = sigma;
        String dirBase = "Z:/MI/BimodalGaussian/BimodalGaussianMIs";
        //        String dirBase = "/users/aam2110/MI/BimodalGaussian/BimodalGaussianMIs";
        this.writeFile = new File(dirBase + "/SigmaCompare/Sigma" + sigma + ".txt");
        writeFile.getParentFile().mkdirs();

        Vector data = org.geworkbench.bison.util.FileUtil.readFile(new File(dirBase + "/BimodalGaussianMI_goodData.txt"));
        try {
            writer = new BufferedWriter(new FileWriter(writeFile));
            Iterator it = data.iterator();
            it.next();
            while (it.hasNext()) {
                String[] tuple = (String[]) it.next();
                calculateMi(tuple);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void calculateMi(String[] tuple) {
        String paramsName = tuple[0];
        double covar1 = Double.parseDouble(tuple[1]);
        double covar2 = Double.parseDouble(tuple[2]);
        double trueMi = Double.parseDouble(tuple[3]);

        int[] numSamplesArr = {125, 250, 500, 1000, 1500, 2000};
        int numIterations = 10;
        BimodalGaussianParamsBase params = BimodalGaussianParamsFactory.getParams(paramsName);
        params.setCovariance1(covar1);
        params.setCovariance2(covar2);
        MultivariateGaussianBimodal bimodalGaussian = new MultivariateGaussianBimodal(params);

        StringBuffer sb = new StringBuffer();

        sb.append("Params Name\t" + paramsName);
        sb.append("\tCovar1\t" + covar1);
        sb.append("\tCovar2\t" + covar2);
        sb.append("\tTrue MI\t" + trueMi);

        for (int maCtr = 0; maCtr < numSamplesArr.length; maCtr++) {
            DoubleArrayList dalMis = new DoubleArrayList();

            int microarrayNo = numSamplesArr[maCtr];
            System.out.println("processing " + microarrayNo);
            bimodalGaussian.setNumDataPoints(microarrayNo);

            for (int i = 0; i < numIterations; i++) {
                try {
                    double[][] gaussianData = bimodalGaussian.getData();

                    //NEED TO ADD THE SIGMA HERE
                    GeneGeneRelationship ggr = new org.geworkbench.util.pathwaydecoder.GeneGeneRelationship(null, null, microarrayNo, 0, true);

                    //                GeneNetworkEdgeImpl edge = ggr.newMIScore(gaussianData[0], gaussianData[1], 0.0);
                    GeneNetworkEdgeImpl edge = ggr.miScore(gaussianData[0], gaussianData[1], 0.0);
                    double miScore = edge.getMI();
                    //correction
                    miScore = miScore * Math.log(microarrayNo) * 2;

                    dalMis.add(miScore);
                } catch (Exception e) {
                    System.out.println("Exception  " + e.getMessage());
                }
            }
            double miMean = Descriptive.mean(dalMis);
            double miVar = Descriptive.sampleVariance(dalMis, miMean);
            double miSd = Descriptive.standardDeviation(miVar);

            sb.append("\tData Points " + microarrayNo + "\tMI Mean\t" + miMean);
            sb.append("\tData Points " + microarrayNo + "\tMI SD\t" + miSd);
        }
        System.out.println(sb.toString());
        //        try {
        //            writer.write(sb.toString() + "\n");
        //        }
        //        catch (IOException ex) {
        //            ex.printStackTrace();
        //        }
    }
}
