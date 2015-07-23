package org.geworkbench.components.pathwaydecoder.networkProcessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;


public class Temp {
    public Temp() {
    }

    void convertFile(File readFile) {
        System.out.println(readFile.getAbsolutePath());
        try {
            BufferedReader reader = new BufferedReader(new FileReader(readFile));
            //          Vector lines = new Vector(20);
            String[] lines = new String[20];
            String line;
            //            int lineCtr = 0;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split("\t");
                int tokenIndex = Integer.parseInt(tokens[0]);
                tokens[0] = "G" + tokens[0] + ":" + tokenIndex;
                //                lineCtr++;
                String newLine = "";
                for (int i = 0; i < tokens.length; i++) {
                    newLine += tokens[i] + "\t";
                }
                //              lines.insertElementAt(newLine, tokenIndex);
                lines[tokenIndex] = newLine;
            }
            reader.close();

            FileWriter writer = new FileWriter(readFile);
            //          Iterator it = lines.iterator();
            //          while(it.hasNext()){
            //              line = (String)it.next();
            for (int i = 0; i < lines.length; i++) {
                line = lines[i];
                writer.write(line + "\n");
            }
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeFilteredCompiledResultsFile() {
        int column = 3;
        String value = 0.5 + "";

        File compiledResultsFile = new File("Z:\\BayesData\\ComparisonResults\\CleanedMatrices\\Accurate\\CompiledResults\\CompiledResults.txt");
        File writeFileDirectory = new File("Z:\\BayesData\\ComparisonResults\\CleanedMatrices\\Accurate\\CompiledResults\\Tolerance0.5");

        writeFileDirectory.mkdirs();

        String writeFileName = "CompiledResults_tolerance0.5.txt";

        File writeFile = new File(writeFileDirectory + "\\" + writeFileName);

        try {
            FileWriter writer = new FileWriter(writeFile);

            BufferedReader reader = new BufferedReader(new FileReader(compiledResultsFile));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] arrLine = line.split("\t");
                if (value.equals(arrLine[column])) {
                    writer.write(line + "\n");
                }
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    static double getValueInFile(BufferedReader reader, double val1, int index1, double val2, int index2, double val3, int index3, int returnIndex) {
        String line;
        try {
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] arrLine = line.split("\t");
                double fileVal1 = Double.parseDouble(arrLine[index1]);
                double fileVal2 = Double.parseDouble(arrLine[index2]);
                double fileVal3 = Double.parseDouble(arrLine[index3]);
                if (fileVal1 == val1 && fileVal2 == val2 && fileVal3 == val3) {
                    return Double.parseDouble(arrLine[returnIndex]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }


}
