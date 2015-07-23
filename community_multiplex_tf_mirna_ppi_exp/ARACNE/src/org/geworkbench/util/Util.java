package org.geworkbench.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Util {
    static Log log = LogFactory.getLog(Util.class);

    public Util() {
    }

    public static HashMap readHashMapFromFile(File file) {
        HashMap map = new HashMap();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(line, "\t");
                String key = st.nextToken();
                String value = st.nextToken();
                map.put(key, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    public static HashMap readHashMapFromFiles(File[] files, String commentString) {

        HashMap map = new HashMap();

        for (int fileCtr = 0; fileCtr < files.length; fileCtr++) {
            try {
                map.putAll(readHashMapFromFile(files[fileCtr], commentString));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    public static HashMap readHashMapFromFiles(String[] fileNames, String commentString) {

        HashMap map = new HashMap();

        for (int fileCtr = 0; fileCtr < fileNames.length; fileCtr++) {
            try {
                File file = new File(fileNames[fileCtr]);
                if (file.exists()) {
                    map.putAll(readHashMapFromFile(file, commentString));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    public static HashMap readHashMapFromFile(File file, String commentString) {
        HashMap map = new HashMap();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith(commentString)) {
                    StringTokenizer st = new StringTokenizer(line, "\t");
                    if (st.countTokens() == 2) {
                        String key = st.nextToken();
                        String value = st.nextToken();
                        map.put(key, value);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    public static String getString(HashMap map, String key) {
        String value = (String) map.get(key);
        return value;
    }

    public static int getInt(HashMap map, String key) {
        Object val = map.get(key);
        if (val != null) {
            int value = Integer.parseInt((String) val);
            return value;
        } else {
            return Integer.MIN_VALUE;
        }
    }

    public static double getDouble(HashMap map, String key) {
        Object val = map.get(key);
        if (val != null) {
            double value = Double.parseDouble((String) val);
            return value;
        } else {
            return Double.NaN;
        }
    }

    public static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = Util.class.getResource(path);
        return new ImageIcon(imgURL);
    }

    static public boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }

    public static List unZip(String inFile, String outDir) throws IOException {
        Enumeration entries;
        ZipFile zipFile;

        List files = new ArrayList();

        zipFile = new ZipFile(inFile);

        entries = zipFile.entries();

        while (entries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) entries.nextElement();

            if (entry.isDirectory()) {
                // Assume directories are stored parents first then children.
                log.trace("Extracting directory: " + entry.getName());
                // Check for entry that contains multiple directories - must be created individually
                String[] dirs = entry.getName().split("/");
                String dirSoFar = "";
                for (String thisDir : dirs) {
                    dirSoFar += "/" + thisDir;
                    File newdir = new File(outDir, dirSoFar);
                    log.trace("Creating directory "+dirSoFar);
                    newdir.mkdir();
                    files.add(newdir);
                }
            } else {
                log.trace("Extracting file: " + entry.getName());
                File file = new File(outDir, entry.getName());
                File path = file.getParentFile();
                // Make directory if needed
                path.mkdir();
                copyInputStream(zipFile.getInputStream(entry),
                        new BufferedOutputStream(new FileOutputStream(file)));
                files.add(file);
            }
        }

        zipFile.close();
        return files;
    }

    private static void copyInputStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int len;

        while ((len = in.read(buffer)) >= 0) out.write(buffer, 0, len);

        in.close();
        out.close();
    }

    /**
     * Generates a unique name given a desired name and a set of existing names.
     * Appends ' (n)' to the name for n = 1, 2, 3, ... until a unique name is found.
     * @param desiredName
     * @param existingNames
     * @return
     */
    public static String getUniqueName(String desiredName, Set<String> existingNames) {
        String name = desiredName;
        int i = 0;
        while (existingNames.contains(name)) {
            i++;
            name = desiredName + " (" + i + ")";
        }
        return name;
    }
}
