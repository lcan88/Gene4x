package org.geworkbench.components.pathwaydecoder.util;

import org.geworkbench.bison.util.FileUtil;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

public class CompareToNetFile {
    public CompareToNetFile() {
    }

    public static void main(String[] args) {
        new CompareToNetFile().doIt();
    }

    void doIt() {
        String file1 = "C:/Simulations/FullResults/AGN-century/SF/SF-001/reactNoise_1.0/langNoise_0.0/hr.txt";
        String file2 = "C:/Simulations/FullResults/AGN-century/SF/SF-001/reactNoise_1.0/langNoise_0.0/arcs_tab.txt";
        compareFiles(new File(file1), new File(file2));
    }

    void compareFiles(File file1, File file2) {
        HashSet set = new HashSet();
        Vector file1Data = FileUtil.readFile(file1);
        Vector file2Data = FileUtil.readFile(file2);

        Iterator it = file1Data.iterator();
        while (it.hasNext()) {
            String[] data1Line = (String[]) it.next();
            if (data1Line == null || data1Line.length < 2) {
                continue;
            }

            //            for(int i = 0; i < data1Line.length; i++){
            //                System.out.print(data1Line[i] + "\t");
            //            }
            //            System.out.println();

            Iterator it2 = file2Data.iterator();
            while (it2.hasNext()) {
                String[] data2Line = (String[]) it2.next();
                if (data1Line[0].equals("G" + data2Line[0]) && data1Line[1].equals("G" + data2Line[1])) {
                    set.add(data2Line);
                    //                    for(int i = 0; i < data2Line.length; i++){
                    //                        System.out.print(data2Line[i] + "\t");
                    //                    }
                    //                    System.out.println();
                }
            }

        }

        Iterator setIt = set.iterator();
        while (setIt.hasNext()) {
            String[] data2Line = (String[]) setIt.next();
            for (int i = 0; i < data2Line.length; i++) {
                System.out.print(data2Line[i] + "\t");
            }
            System.out.println();
        }
    }
}
