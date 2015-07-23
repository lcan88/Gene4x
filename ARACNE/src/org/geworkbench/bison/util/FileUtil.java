package org.geworkbench.bison.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

/**
 * <p>Title: caWorkbench</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence
 * and Genotype Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author Adam Margolin
 * @version 3.0
 */
public class FileUtil {
    public FileUtil() {
    }

    public static void printMatrix(String[] headerCols, String[] headerRows, double[][] matrix, String writeFileName) {
        try {
            File writeFile = new File(writeFileName);
            writeFile.getParentFile().mkdirs();
            FileWriter writer = new FileWriter(writeFile);

            if (headerRows != null) {
                writer.write("\t");
            }

            if (headerCols != null) {
                for (int headerCtr = 0; headerCtr < headerCols.length; headerCtr++) {
                    writer.write(headerCols[headerCtr] + "\t");
                }
            }
            writer.write("\n");

            for (int rowCtr = 0; rowCtr < matrix.length; rowCtr++) {
                if (headerRows != null) {
                    writer.write(headerRows[rowCtr] + "\t");
                }

                for (int colCtr = 0; colCtr < matrix[rowCtr].length; colCtr++) {
                    writer.write(matrix[rowCtr][colCtr] + "\t");
                }

                writer.write("\n");
            }

            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void printMatrix(double[][] matrix, String writeFileName) {
        File writeFile = new File(writeFileName);
        printMatrix(matrix, writeFile);
    }

    public static void printMatrix(double[][] matrix, File writeFile) {
        try {

            writeFile.getParentFile().mkdirs();
            FileWriter writer = new FileWriter(writeFile);

            for (int rowCtr = 0; rowCtr < matrix.length; rowCtr++) {

                for (int colCtr = 0; colCtr < matrix[rowCtr].length; colCtr++) {
                    writer.write(matrix[rowCtr][colCtr] + "\t");
                }

                writer.write("\n");
            }

            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static Vector readFile(File readFile, int numHeaderLines, int startIndex, int endIndex) {
        Vector data = new Vector();
        //        String[][] data = new String[endIndex - startIndex][];
        try {
            BufferedReader reader = new BufferedReader(new FileReader(readFile));
            for (int i = 0; i < numHeaderLines; i++) {
                reader.readLine();
            }

            for (int i = 0; i < startIndex; i++) {
                reader.readLine();
            }

            String line;
            for (int lineCtr = startIndex; lineCtr < endIndex; lineCtr++) {
                if (lineCtr % 100 == 0) {
                    System.out.println("Read line " + lineCtr);
                }
                if ((line = reader.readLine()) != null) {
                    String[] arrLine = line.split("\t");
                    data.add(arrLine);
                    //                    data[lineCtr] = arrLine;
                }
            }
            //            return null;
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static Vector readFile(File readFile) {
        return readFile(readFile, 0);
    }

    public static Vector readFile(File readFile, int numHeaderLines) {
        Vector data = new Vector();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(readFile));
            for (int i = 0; i < numHeaderLines; i++) {
                reader.readLine();
            }
            String line;
            while ((line = reader.readLine()) != null) {
                String[] arrLine = line.split("\t");
                data.add(arrLine);
            }
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Vector<String> readFileData(File readFile) {
        return readFileData(readFile, 0);
    }

    public static Vector<String> readFileData(File readFile, int numHeaderLines) {
        Vector<String> data = new Vector();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(readFile));
            for (int i = 0; i < numHeaderLines; i++) {
                reader.readLine();
            }
            String line;
            while ((line = reader.readLine()) != null) {
                data.add(line);
            }
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public static void printArray(double[] array, String fileName) {
        printArray(array, new File(fileName));
    }

    public static void printArray(double[] array, File file) {
        file.getParentFile().mkdirs();
        try {
            FileWriter writer = new FileWriter(file);
            for (int arrayCtr = 0; arrayCtr < array.length; arrayCtr++) {
                writer.write(array[arrayCtr] + "\t");
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void printArray(String[] array, File file) {
        file.getParentFile().mkdirs();
        try {
            FileWriter writer = new FileWriter(file);
            for (int arrayCtr = 0; arrayCtr < array.length; arrayCtr++) {
                writer.write(array[arrayCtr] + "\t");
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static double[][] readDoubleArray(File file) {
        Vector allData = new Vector();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] arrLine = line.split("\t");
                double[] arr = new double[arrLine.length];
                for (int i = 0; i < arr.length; i++) {
                    arr[i] = Double.parseDouble(arrLine[i]);
                }
                allData.add(arr);
            }

            double[][] vecData = new double[allData.size()][];
            Iterator it = allData.iterator();
            int ctr = 0;
            while (it.hasNext()) {
                double[] data = (double[]) it.next();
                vecData[ctr++] = data;
            }
            return vecData;
        } catch (Exception e) {
            return null;
        }
    }

    public static org.geworkbench.bison.util.HashMatrix readHashMatrix(File file) {
        try {
            HashMap rowMap = new HashMap();
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            String[] columnHeaders = line.split("\t");
            HashMap columnsMap = new HashMap();
            for (int i = 1; i < columnHeaders.length; i++) {
                columnsMap.put(new Double(columnHeaders[i]), new Integer(i - 1));
            }

            while ((line = reader.readLine()) != null) {
                String[] lineVals = line.split("\t");
                if (lineVals.length > 0) {
                    String[] vals = new String[lineVals.length - 1];
                    for (int i = 1; i < lineVals.length; i++) {
                        vals[i - 1] = lineVals[i];
                    }
                    rowMap.put(new String(lineVals[0]), vals);
                }
            }
            return new HashMatrix(rowMap, columnsMap);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static HashMap<String, String> readHashMap(File file) {
        try {
            HashMap map = new HashMap();
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] arrLine = line.split("\t");
                if (arrLine != null && arrLine.length > 1) {
                    String key = new String(arrLine[0]);
                    String value = new String(arrLine[1]);
                    map.put(key, value);
                }
            }
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Vector<String> readVector(File file) {
        return readVector(file, 0, 0);
    }

    public static Vector<String> readVector(File file, int index, int headerRows) {
        try {
            Vector fileData = new Vector();
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;

            for (int i = 0; i < headerRows; i++) {
                reader.readLine();
            }

            while ((line = reader.readLine()) != null) {
                String[] arrLine = new String(line).split("\t");
                fileData.add(arrLine[index]);
            }
            return fileData;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static void mergeFiles(File[] filesToMerge, File writeFile, int headerLines) {
        try {
            FileWriter writer = new FileWriter(writeFile);
            for (int fileCtr = 0; fileCtr < filesToMerge.length; fileCtr++) {
                File fileToWrite = filesToMerge[fileCtr];
                writeFile(fileToWrite, writer, headerLines);
                writer.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void mergeFilesRecursive(File directory, String fileName, File writeFile, int headerLines) {
        try {
            FileWriter writer = new FileWriter(writeFile);
            mergeFilesRecursive(directory, fileName, writer, headerLines);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void mergeFilesRecursive(File directory, String fileName, FileWriter writer, int headerLines) {
        File[] children = directory.listFiles();
        for (int fileCtr = 0; fileCtr < children.length; fileCtr++) {
            File curFile = children[fileCtr];
            if (curFile.isDirectory()) {
                mergeFilesRecursive(curFile, fileName, writer, headerLines);
            } else {
                if (curFile.getName().equals(fileName)) {
                    writeFile(curFile, writer, headerLines);
                }
            }
        }
    }

    static void writeFile(File fileToWrite, FileWriter writer, int headerLines) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileToWrite));
            for (int i = 0; i < headerLines; i++) {
                reader.readLine();
            }
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
