package org.geworkbench.util.function.tp;

import java.io.File;
import java.io.FileWriter;
import java.util.Vector;


public class CompareSigmaEstimates {
    public CompareSigmaEstimates() {
    }

    public static void main(String[] args) {
        new CompareSigmaEstimates().compareEstimates();
    }

    void compareEstimates() {
        try {
            String readFileBase = "Z:/Adam/MI/BimodalGaussian/BimodalGaussianMIs/MpiTest/Accurate/MisForSigmas";

            File writeFile = new File(readFileBase + "/EstimateCompare/Comparison.txt");
            writeFile.getParentFile().mkdirs();
            FileWriter writer = new FileWriter(writeFile);

            File[] dataFiles = new File[4];
            dataFiles[0] = new File(readFileBase + "/Params1/6_7.txt");
            dataFiles[1] = new File(readFileBase + "/Params2/6_7.txt");
            dataFiles[2] = new File(readFileBase + "/Params3/6_7.txt");
            dataFiles[3] = new File(readFileBase + "/Params4/6_7.txt");

            Vector[] allFileDatas = new Vector[4];
            for (int i = 0; i < allFileDatas.length; i++) {
                allFileDatas[i] = org.geworkbench.bison.util.FileUtil.readFile(dataFiles[i]);
            }

            for (int fileCtr1 = 0; fileCtr1 < allFileDatas.length; fileCtr1++) {
                for (int fileCtr2 = fileCtr1; fileCtr2 < allFileDatas.length; fileCtr2++) {
                    Vector file1Data = allFileDatas[fileCtr1];
                    Vector file2Data = allFileDatas[fileCtr2];

                    for (int file1DataCtr = 0; file1DataCtr < file1Data.size(); file1DataCtr++) {
                        String[] data1Line = (String[]) file1Data.get(file1DataCtr);
                        for (int file2DataCtr = 0; file2DataCtr < file2Data.size(); file2DataCtr++) {
                            String[] data2Line = (String[]) file2Data.get(file2DataCtr);
                            for (int headerCtr = 0; headerCtr < 6; headerCtr++) {
                                writer.write(data1Line[headerCtr] + "\t");
                            }
                            for (int headerCtr = 0; headerCtr < 6; headerCtr++) {
                                writer.write(data2Line[headerCtr] + "\t");
                            }

                            for (int dataCtr = 6; dataCtr < data1Line.length; dataCtr++) {
                                double mi1 = Double.parseDouble(data1Line[dataCtr]);
                                double mi2 = Double.parseDouble(data2Line[dataCtr]);
                                double miDiff = mi2 - mi1;
                                writer.write(miDiff + "\t");
                                System.out.print(miDiff + "\t");
                            }
                            writer.write("\n");
                            System.out.println();
                        }
                    }
                }
            }
            writer.close();
            //            Vector fileData = Util.readFile(readFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
