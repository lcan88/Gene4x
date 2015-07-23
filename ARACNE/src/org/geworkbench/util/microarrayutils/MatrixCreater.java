package org.geworkbench.util.microarrayutils;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.geworkbench.bison.datastructure.bioobjects.markers.annotationparser.ExampleFilter;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class MatrixCreater {

    //    JFileChooser fc2 = null;
    //    JFileChooser fc3 = null;
    //  JFileChooser fc4 = null;
    static File output = null;
    static File phenofile = null;

    //    File[] files = null;

    public static void loadData() {
        final JFileChooser fc2 = new JFileChooser();
        ExampleFilter filter = new ExampleFilter();
        filter.addExtension("xls");
        filter.setDescription("Excel file");
        fc2.setFileFilter(filter);
        ExampleFilter filter2 = new ExampleFilter();
        filter2.addExtension("txt");
        filter2.setDescription("Tab delimited file");
        fc2.setFileFilter(filter2);

        fc2.setMultiSelectionEnabled(true);
        fc2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand() == JFileChooser.APPROVE_SELECTION) {
                    final File[] temps = fc2.getSelectedFiles();
                    int option = 0;
                    if (temps[0].getName().endsWith(".xls")) {
                        option = JOptionPane.showConfirmDialog(null, "Excel file input will be slow and requires more resource.\n" + "It is highly recommanded that you use tab delimated text format.\n"

                                + "Continue anyway?", "Recommandation", JOptionPane.CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

                    }
                    if (option == 0) {
                        final JFileChooser fc = new JFileChooser();
                        ExampleFilter filter = new ExampleFilter();
                        filter.addExtension("exp");
                        filter.setDescription("Matrix file");
                        fc.setFileFilter(filter);

                        fc.addActionListener(new ActionListener() {
                            /**
                             * actionPerformed
                             *
                             * @param e ActionEvent
                             */
                            public void actionPerformed(ActionEvent e2) {
                                if (e2.getActionCommand() == JFileChooser.APPROVE_SELECTION) {
                                    Thread t = new Thread() {
                                        public void run() {

                                            readFile(temps, fc.getSelectedFile());
                                        }
                                    };
                                    t.setPriority(Thread.MIN_PRIORITY);
                                    t.start();

                                }
                            }
                        });
                        fc.setDialogTitle("Select the output file");
                        fc.showSaveDialog(null);
                    }

                }

            }

        });
        fc2.setDialogTitle("Select the Data files");
        fc2.showOpenDialog(null);

    }

    private static void parseData(HashMap al, int probsetindx, int valueindx, int pvalueindx, int probsetNo, String[] cols) {
        probsetNo++;
        String id = cols[probsetindx];
        String data = cols[valueindx] + '\t' + cols[pvalueindx];
        String result = (String) al.get(id);
        if (result == null) {
            result = data;
        } else {
            result = result + '\t' + data;
        }
        al.put(id, result);
    }

    private static void writeResults(String filenames, HashMap al) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(output));
            bw.write("AffyID" + '\t' + "Annotation" + '\t' + filenames + '\n');
            getPhenotypes(filenames, bw);
            for (Iterator it = al.keySet().iterator(); it.hasNext();) {
                String id = (String) it.next();
                String data = (String) al.get(id);
                id = id + '\t' + id + '\t' + data;
                bw.write(id + '\n');
            }
            bw.close();
            System.out.println("done");
        } catch (IOException ex2) {
            ex2.printStackTrace();
        }
    }

    private static void getPhenotypes(String filenames, BufferedWriter bw) throws FileNotFoundException, IOException {
        if (phenofile != null) {
            BufferedReader pheno = new BufferedReader(new FileReader(phenofile));
            String phenos = pheno.readLine();
            int phenoNo = phenos.split("\t").length;

            int totalchip = 0;
            for (String one = " "; one != null; one = pheno.readLine()) {
                totalchip++;
            }
            pheno.close();
            BufferedReader pheno2 = new BufferedReader(new FileReader(phenofile));
            String[][] phenoTable = new String[(totalchip)][phenoNo];
            for (int i = 0; i < totalchip; i++) {
                String oneline = pheno2.readLine();
                String tk[] = oneline.split("\t");
                for (int j = 0; j < phenoNo; j++) {
                    phenoTable[i][j] = tk[j].trim();
                    //System.out.println(phenoTable[i][j] + " i=" + i + " j=" + j);
                }
            }

            // now everything in phenoTable
            String[] phenotypes = new String[phenoNo];
            for (int j = 0; j < phenoNo; j++) {
                phenotypes[j] = phenoTable[0][j] + '\t';
            }
            String[] nameToken = filenames.split("\t");

            String onename = "";
            for (int indx = 0; indx < nameToken.length; indx++) {
                onename = nameToken[indx];
                boolean notfound = true;
                for (int i = 0; i < totalchip; i++) {
                    if (onename.equalsIgnoreCase(phenoTable[i][0])) {
                        for (int j = 0; j < phenoNo; j++) {
                            phenotypes[j] = phenotypes[j] + '\t' + phenoTable[i][j];
                        }
                        notfound = false;
                    }
                }
                if (notfound) {
                    System.out.println("NOT FOUND!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + onename);
                    for (int j = 0; j < phenoNo; j++) {
                        phenotypes[j] = phenotypes[j] + '\t' + "NA";
                    }
                }

            }
            for (int j = 1; j < phenoNo; j++) {
                bw.write("Description" + '\t' + phenotypes[j] + '\n');
            }
        }
    }

    public static void addPhenotype() {

        final JFileChooser fc3 = new JFileChooser();
        ExampleFilter filter = new ExampleFilter();
        filter.addExtension("xls");
        filter.setDescription("Excel file");
        fc3.setFileFilter(filter);

        fc3.setMultiSelectionEnabled(false);
        fc3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand() == JFileChooser.APPROVE_SELECTION) {
                    phenofile = fc3.getSelectedFile();

                    final JFileChooser fc2 = new JFileChooser();
                    ExampleFilter filter = new ExampleFilter();
                    filter.addExtension("exp");
                    filter.setDescription("Matrix file");
                    fc2.setFileFilter(filter);

                    fc2.setMultiSelectionEnabled(false);
                    fc2.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            if (e.getActionCommand() == JFileChooser.APPROVE_SELECTION) {
                                File datafile = fc2.getSelectedFile();
                                try {
                                    addPhenotype(datafile, phenofile);
                                } catch (FileNotFoundException ex) {
                                    ex.printStackTrace();
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }

                            }

                        }

                    });

                    fc2.setDialogTitle("Please Select The Matrix Data File");
                    fc2.showOpenDialog(null);

                }

            }

        });
        fc3.setDialogTitle("Please Select The Phenotype File");
        fc3.showOpenDialog(null);
    }

    //    void jStartBttn_actionPerformed(ActionEvent e) {
    //        String filenames = "";
    //        HashMap al = new HashMap();
    //        if (files != null) {
    //            String outputpath = JOptionPane.showInputDialog(
    //                "Please enter the name of the output file");
    //            output = new File(outputpath);
    //
    //            for (int i = 0; i < files.length; i++) {
    //                int probsetindx = 0;
    //                int valueindx = 0;
    //                int pvalueindx = 0;
    //                int probsetNo = 0;
    //                boolean istitleLine = false;
    //                try {
    //
    //                    filenames = filenames + files[i].getLabel().split("\\.")[0] +
    //                        '\t';
    //
    //                    BufferedReader br = new BufferedReader(new FileReader(files[
    //                        i]));
    //                    String line = "";
    //                    while ( (line = br.readLine()) != null) {
    //                        String[] cols = line.split("\t");
    //                        if ( (cols.length > 2)) { //col titles or data line
    //                            if (!istitleLine) { //title line
    //                                for (int j = 0; j < cols.length; j++) {
    //                                    if (cols[j].compareTo("Probe Set Name") ==
    //                                        0) {
    //                                        probsetindx = j;
    //                                        istitleLine = true;
    //                                    }
    //                                    if (cols[j].compareTo("Signal") == 0) {
    //                                        valueindx = j;
    //                                    }
    //                                    if (cols[j].compareTo("Detection p-value") ==
    //                                        0) {
    //                                        pvalueindx = j;
    //                                    }
    //                                }
    //                            }
    //                            else { //data line
    //                                parseData(al, probsetindx, valueindx,
    //                                          pvalueindx, probsetNo,
    //                                          cols);
    //                            }
    //                        }
    //                    }
    //
    //                }
    //                catch (FileNotFoundException ex) {
    //                    ex.printStackTrace();
    //                }
    //                catch (IOException ex1) {
    //                    ex1.printStackTrace();
    //                }
    //            }
    //            writeResults(filenames, al);
    //
    //        }
    //        else {
    //            JOptionPane.showMessageDialog(this,
    //                                          "Please select data files first.");
    //
    //        }
    //    }

    public static void readFile(File[] infiles, File outfile) {
        File[] tempfiles = new File[infiles.length];
        for (int f = 0; f < infiles.length; f++) {
            File infile = infiles[f];
            if (infile.getName().endsWith(".xls")) {
                tempfiles[f] = parseExcel(infile);
            } else {

                tempfiles[f] = parseText(infile);
            }
        }
        try {
            mergeFiles(tempfiles, outfile);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static File parseExcel(File infile) {
        File tempfile = null;
        try {
            String filename = infile.getName();
            filename = filename.substring(0, filename.indexOf("."));

            tempfile = File.createTempFile(filename, "exp");
            BufferedWriter bw = new BufferedWriter(new FileWriter(tempfile));

            POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(infile));
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            HSSFSheet hs = wb.getSheetAt(0);

            //                int totalrows = wb.getSheetAt(0).getPhysicalNumberOfRows();
            ArrayList signals = new ArrayList();
            ArrayList pvalues = new ArrayList();
            for (short h = 0; h < hs.getLastRowNum() + 1; h++) {

                StringBuffer line = new StringBuffer();

                HSSFRow row = hs.getRow(h);

                if (row.getRowNum() == 0) { //first line

                    line.append("AffyID").append("\t").append("Annotation");

                    for (short it = 1; it < row.getLastCellNum() + 1; it++) {
                        //get the position difference between signal and pvalue;
                        HSSFCell cell = row.getCell(it);
                        if (cell != null) {
                            String value = cell.getStringCellValue();

                            if (value.endsWith("_Signal")) {
                                signals.add(new Integer(cell.getCellNum()));

                            } else if (value.endsWith("p-value")) {
                                pvalues.add(new Integer(cell.getCellNum()));

                            }
                        }

                    }

                    for (short c = 0; c < signals.size(); c++) { //get the chipname
                        String value = row.getCell(((Integer) signals.get(c)).shortValue()).getStringCellValue();
                        value = value.substring(0, value.indexOf("_Signal")); //"_Detection"  "_Detection p-value"

                        line.append('\t').append(value);
                    }

                    bw.write(line.toString());
                    bw.newLine();

                } else { //if not the first line
                    for (short c = 0; c < 2; c++) {
                        HSSFCell cell = row.getCell(c);

                        addToLine(line, cell);

                    }
                    for (short c = 0; c < signals.size(); c++) {

                        HSSFCell cell = row.getCell(((Integer) signals.get(c)).shortValue());
                        if (cell != null) {
                            addToLine(line, cell);

                            cell = row.getCell(((Integer) pvalues.get(c)).shortValue());
                            if (cell != null) {
                                addToLine(line, cell);
                            } else {
                                System.out.println("Error!!");
                            }
                        } else {

                            System.out.println("Error!!");
                        }

                    }

                    bw.write(line.toString());
                    bw.newLine();
                }
            }

            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tempfile;
    }

    private static File parseText(File infile) { // if the file is in tab delimited format

        File tempfile = null;
        try {
            String filename = infile.getName();
            ProgressMonitorInputStream progressIn = new ProgressMonitorInputStream(null, "Parsing file : " + filename, new FileInputStream(infile));

            filename = filename.substring(0, filename.indexOf("."));

            tempfile = File.createTempFile(filename, "exp");
            //            tempfile=new File(filename+".exp");
            BufferedWriter bw = new BufferedWriter(new FileWriter(tempfile));

            BufferedReader br = new BufferedReader(new InputStreamReader(progressIn));

            //                int totalrows = wb.getSheetAt(0).getPhysicalNumberOfRows();
            ArrayList signals = new ArrayList();
            ArrayList pvalues = new ArrayList();
            String oneline = br.readLine();
            StringBuffer line = new StringBuffer();
            line.append("AffyID").append('\t').append("Annotation");
            String[] data = oneline.split("\t");

            for (int it = 0; it < data.length; it++) {
                //get the position difference between signal and pvalue;
                String value = data[it];

                if (value.endsWith("_Signal")) {
                    signals.add(new Integer(it));
                    value = value.substring(0, value.indexOf("_Signal")); //"_Detection"  "_Detection p-value"

                    line.append('\t').append(value);

                } else if (value.endsWith("p-value")) {
                    pvalues.add(new Integer(it));

                }

            }

            bw.write(line.toString());
            bw.newLine();

            while ((oneline = br.readLine()) != null) {
                line.delete(0, line.length());
                data = oneline.split("\t");

                line.append(data[0]).append('\t');
                line.append(data[1]);

                for (short c = 0; c < signals.size(); c++) {
                    int s = ((Integer) (signals.get(c))).intValue();
                    int p = ((Integer) (pvalues.get(c))).intValue();
                    line.append('\t').append(data[s]);
                    line.append('\t').append(data[p]);
                }

                bw.write(line.toString());
                bw.newLine();
            }

            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tempfile;

    }

    public static void mergeFiles(File[] infiles, File outfile) throws FileNotFoundException, IOException {
        ArrayList values = new ArrayList();
        ProgressMonitor progressMonitor = new ProgressMonitor(null, "Merging individual data files...", "", 0, infiles.length);
        progressMonitor.setProgress(0);
        progressMonitor.setMillisToDecideToPopup(1);

        for (int i = 0; i < infiles.length; i++) {
            //           System.out.println(i);
            BufferedReader br = new BufferedReader(new FileReader(infiles[i]));
            String line;
            if (i == 0) {
                while ((line = br.readLine()) != null) {
                    //                    line=line.trim();
                    values.add(line.trim());
                }
            } else {
                int k = 0;
                while ((line = br.readLine()) != null) {
                    line = line.substring(line.indexOf("\t") + 1);
                    line = line.substring(line.indexOf("\t"));
                    String one = (String) values.get(k);
                    one = one + line;
                    values.set(k, one.trim());
                    k++;

                }
            }
            br.close();
            infiles[i].delete();
            progressMonitor.setProgress(i + 1);

        }
        progressMonitor.setMaximum(values.size());
        progressMonitor.setNote("Writing to file...");
        BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));
        for (int i = 0; i < values.size(); i++) {
            progressMonitor.setProgress(i);
            bw.write((String) values.get(i));
            bw.newLine();

        }
        bw.close();
        progressMonitor.close();

    }

    public static void addPhenotype(File expfile, File pheno) throws FileNotFoundException, IOException {
        File expFile = expfile.getAbsoluteFile();
        BufferedReader exp = new BufferedReader(new FileReader(expfile));
        POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(pheno));
        HSSFWorkbook wb = new HSSFWorkbook(fs);
        //        int totalchip = wb.getSheetAt(0).getPhysicalNumberOfRows() - 1;
        ArrayList cols = new ArrayList();
        HashMap[] hashes = null;
        HSSFSheet hs = wb.getSheetAt(0);
        for (int i = 0; i <= hs.getLastRowNum(); i++) {
            HSSFRow row = hs.getRow(i);

            if (row.getRowNum() == 0) {
                for (short n = 1; n < row.getLastCellNum(); n++) {// this could be a problem.

                    HSSFCell cell = row.getCell(n);
                    if (cell != null)
                        cols.add(cell.getStringCellValue());

                }
                int phenoNo = cols.size();
                hashes = new HashMap[phenoNo];
                for (int k = 0; k < hashes.length; k++) {
                    hashes[k] = new HashMap();
                }

            } else {
                String chip = null;
                int k = 0;
                for (short n = 0; n < row.getPhysicalNumberOfCells(); n++) {
                    HSSFCell cell = row.getCell(n);
                    if (cell.getCellNum() == 0) {
                        chip = cell.getStringCellValue();
                    } else {
                        String cellstring = null;
                        switch (cell.getCellType()) {

                            case HSSFCell.CELL_TYPE_NUMERIC:
                                cellstring = "" + cell.getNumericCellValue();
                                break;

                            case HSSFCell.CELL_TYPE_STRING:
                                cellstring = cell.getStringCellValue();
                                break;

                            default:
                                ;
                        }
                        //                        System.out.println(k+" "+cellstring+" "+chip);
                        hashes[k].put(chip, cellstring);
                        k++;
                    }
                }

            }

        }
        String firstline = exp.readLine();
        File tempfile = File.createTempFile("temp", "temp");
        BufferedWriter bw = new BufferedWriter(new FileWriter(tempfile));
        bw.write(firstline);
        bw.newLine();
        String[] columns = firstline.split("\t");
        //        System.out.println(columns.length);
        String errorline = "";
        for (int m = 0; m < cols.size(); m++) {
            StringBuffer temp = new StringBuffer().append("Description").append('\t').append((String) cols.get(m)).append('\t');
            for (int i = 2; i < columns.length; i++) {
                String one = (String) hashes[m].get(columns[i]);
                hashes[m].remove(columns[i]);
                if (one == null) {
                    errorline = errorline + '\t' + columns[i];
                    //                    JOptionPane.showMessageDialog(null,
                    //                                                  "Error with " + columns[i] +
                    //                                                  ".");
                    //                    return;
                } else {
                    hashes[m].remove(columns[i]);
                    temp.append(one).append('\t');
                }

            }
            if (errorline.compareTo("") != 0) {
                errorline = "Mismatch chipnames in datafile:\n" + errorline + "\n" + "Mismatch chipnames in phenotype file:\n";
                for (Iterator it = hashes[0].keySet().iterator(); it.hasNext();) {
                    errorline = errorline + '\t' + (String) it.next();

                }
                JOptionPane.showMessageDialog(null, errorline);

                return;
            }
            bw.write(temp.toString());
            bw.newLine();

        }

        while ((firstline = exp.readLine()) != null) {
            bw.write(firstline);
            bw.newLine();
        }
        exp.close();
        bw.close();
        expfile.delete();
        tempfile.renameTo(expFile);

    }

    private static void addToLine(StringBuffer line, HSSFCell cell) {
        switch (cell.getCellType()) {

            case HSSFCell.CELL_TYPE_NUMERIC:
                line.append(cell.getNumericCellValue()).append('\t');
                break;

            case HSSFCell.CELL_TYPE_STRING:
                line.append(cell.getStringCellValue()).append('\t');
                break;

            default:
                ;
        }
    }

    /**
     * the follwing method is for special purpose, there are some samples in the dataset that have some external bcl6
     * @param e ActionEvent
     */
    //    void jButton1_actionPerformed(ActionEvent e) {
    //        try {
    //            BufferedReader br = new BufferedReader(new FileReader("all.exp"));
    //            String line;
    //            String line10="";
    //            String exo="";
    //
    //            try {
    //                String firstline=br.readLine();
    //                while ( (line = br.readLine()) != null) {
    //                    if (line.startsWith("Description	exoBCL6")) {
    //                        exo = line;
    //                    }
    //                    if (line.startsWith("40091_at\t")) {
    //                        line10 = line;
    //                        br.close();
    //                        break;
    //                    }
    //                }
    //                String[] exos=exo.split("\t");
    //                String[] firsts=firstline.split("\t");
    //                String[] signals=line10.split("\t");
    //                System.out.println(exos.length);
    //                System.out.println(signals.length);
    //                StringBuffer exoline=new StringBuffer().append("exoBCL6").append('\t').append(signals[1]);
    //                StringBuffer allline=new StringBuffer().append("exo+endoBCL6").append('\t').append(signals[1]);
    //                for(int i=0;i<exos.length-2;i++){
    //                    System.out.println(firsts[i+2]);
    //                    double signal=Double.parseDouble(signals[i*2+2]);
    //                    double exovalue=Double.parseDouble(exos[i+2])*signal;
    //                    double allvalue=signal+exovalue;
    //                    exoline.append('\t').append(exovalue).append('\t').append(signals[i*2+3]);
    //                    allline.append('\t').append(allvalue).append('\t').append(signals[i*2+3]);
    //                }
    //                BufferedWriter bw=new BufferedWriter(new FileWriter("all.exp",true));
    //                bw.write(exoline.toString());
    //                bw.newLine();
    //                bw.write(allline.toString());
    //                bw.newLine();
    //                bw.close();
    //
    //            }
    //            catch (IOException ex1) {
    //            }
    //        }
    //        catch (FileNotFoundException ex) {
    //        }
    //    }

}
