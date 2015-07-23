package org.geworkbench.components.pathwaydecoder.networks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class PrintCompiledInferredTargets {
    String pValueDirRoot;
    String resultsDirBase;

    public PrintCompiledInferredTargets() {
    }

    public static void main(String[] args) {
        new PrintCompiledInferredTargets().doIt(args);
    }

    void doIt(String[] args) {
        String networkReconstDir = args[0];
        File writeFile = new File(args[1]);

        writeCompiledResults(networkReconstDir, writeFile);
    }

    public void writeCompiledResults(String networkReconstDir, File writeFile) {
        try {
            FileWriter writer = new FileWriter(writeFile);
            File[] miThreshDirs = new File(networkReconstDir).listFiles();
            for (int miThreshCtr = 0; miThreshCtr < miThreshDirs.length; miThreshCtr++) {

                File miThreshDir = miThreshDirs[miThreshCtr];
                if (!miThreshDir.isDirectory()) {
                    continue;
                }

                String miThresh = miThreshDir.getName().replaceFirst("miThresh_", "");
                File[] toleranceDirs = miThreshDir.listFiles();
                for (int tolCtr = 0; tolCtr < toleranceDirs.length; tolCtr++) {
                    File toleranceDir = toleranceDirs[tolCtr];
                    if (!toleranceDir.isDirectory()) {
                        continue;
                    }
                    String tolerance = toleranceDir.getName().replaceFirst("tolerance_", "");
                    File resultsFile = new File(toleranceDir + "/InferredTargets.txt");
                    if (resultsFile.exists()) {
                        BufferedReader resultsReader = new BufferedReader(new FileReader(resultsFile));
                        String line = resultsReader.readLine();
                        writer.write(miThresh + "\t" + tolerance + "\t" + line + "\n");
                    } else {
                        System.out.println(resultsFile + "\tNot Found");
                    }
                }

            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
