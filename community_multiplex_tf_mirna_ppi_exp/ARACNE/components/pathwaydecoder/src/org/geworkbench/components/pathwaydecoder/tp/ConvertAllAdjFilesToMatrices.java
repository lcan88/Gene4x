package org.geworkbench.components.pathwaydecoder.tp;

import org.geworkbench.components.pathwaydecoder.util.AdjToMatrixConverter;

import java.io.File;

public class ConvertAllAdjFilesToMatrices {
    String expFileName = "C:/Simulations/Results_new/AGN-century/SF/SF-001/reactNoise_1.0/langNoise_0.0/SF-001.exp";

    public ConvertAllAdjFilesToMatrices() {
    }

    public static void main(String[] args) {
        new ConvertAllAdjFilesToMatrices().doIt();
    }

    void doIt() {
        File directoryBase = new File("C:/Simulations/Results_new/AGN-century/SF/SF-001/reactNoise_1.0/langNoise_0.0/NetworkReconstruction/samples_1000");
        convertDirectory(directoryBase);
    }

    void convertDirectory(File directory) {
        File[] children = directory.listFiles();
        for (int childCtr = 0; childCtr < children.length; childCtr++) {
            File child = children[childCtr];
            if (child.isDirectory()) {
                convertDirectory(child);
            } else {
                if (child.getName().endsWith(".adj")) {
                    System.out.println("Converting " + child.getAbsolutePath());
                    AdjToMatrixConverter converter = new AdjToMatrixConverter();
                    String writeFileName = child.getParentFile().getAbsolutePath() + "/" + child.getName().replaceAll(".adj", "") + "_matrix.txt";
                    converter.convertFile(expFileName, child.getAbsolutePath(), writeFileName);
                }
            }
        }

    }
}
