package org.geworkbench.util;

import org.geworkbench.bison.datastructure.biocollections.sequences.DSSequenceSet;
import org.geworkbench.bison.datastructure.bioobjects.sequence.CSSequence;
import org.geworkbench.bison.datastructure.bioobjects.sequence.DSSequence;

public class RandomSequenceGenerator {
    String seq = null;
    double[][] countTable;
    char[] symbols = {'A', 'C', 'G', 'T'};
    char[] tokens = null;

    /**
     * @param sequencedb CSSequenceSet
     * @param pValue     double
     */
    public RandomSequenceGenerator(DSSequenceSet<? extends DSSequence> sequencedb, double pValue) {
        final int maxLen = 20000;
        if (true) {
            int length = 0;
            for (DSSequence seq : sequencedb) {
                length += seq.length();
            }
            length = Math.min(length, maxLen);
            tokens = new char[length];
            int pos = 0;
            for (DSSequence seq : sequencedb) {
                for (int i = 0; i < seq.length(); i++) {
                    if (pos >= length) break;
                    tokens[pos++] = seq.getSequence().charAt(i);
                }
            }

        } else {
            int length = 10000; //(int) (1000.0 / pValue);
            tokens = new char[length];
            int i = 0;
            while (i < length) {
                int seqId = (int) (Math.random() * sequencedb.size());
                DSSequence sequence = sequencedb.getSequence(seqId);
                int pos = (int) (Math.random() * sequence.length()) - 1;
                char c = sequence.getSequence().charAt(pos);
                if (c != '#') {
                    tokens[i++] = c;
                }
            }
        }
        seq = new String(tokens).toUpperCase();
        seq.replace("#", "");
        seq.replace("N", "");
        getCounts();
    }

    private void getCounts() {
        int[][] counts = new int[symbols.length][symbols.length + 2];
        for (int i = 1; i < seq.length(); ++i) {
            char x1 = seq.charAt(i - 1);
            char x2 = seq.charAt(i);
            int col = 0;
            int row = 0;
            for (int m = 0; m < symbols.length; ++m) {
                if (x1 == symbols[m]) {
                    col = m;
                    ++counts[col][symbols.length + 1];
                }
                if (x2 == symbols[m]) {
                    row = m;

                }
            }
            ++counts[col][row];
        }

        for (int i = 0; i < symbols.length; i++) {

            for (int k = 0; k < symbols.length; k++) {
                counts[i][symbols.length] += counts[i][k];
            }

        }

        countTable = new double[symbols.length][symbols.length + 1];
        for (int i = 0; i < symbols.length; i++) {
            if (i == 0) {
                countTable[i][symbols.length] = (double) counts[i][symbols.length + 1] / (double) seq.length();
            } else {
                countTable[i][symbols.length] = (double) counts[i][symbols.length + 1] / (double) seq.length() + countTable[i - 1][symbols.length];

            }
            for (int k = 0; k < symbols.length; k++) {
                if (k == 0) {
                    countTable[i][k] = (double) counts[i][k] / (double) counts[i][symbols.length];
                } else {
                    countTable[i][k] = (double) counts[i][k] / (double) counts[i][symbols.length] + countTable[i][k - 1];

                }
            }

        }
    }

    public CSSequence getRandomSequence(int length) {
        String seq = "";
        int previousindx = 0;
        int currentindx = 0;
        double rand = Math.random();
        for (int k = 0; k < symbols.length; k++) {
            double lower = 0;
            if (k == 0) {
                lower = 0;
            } else {
                lower = countTable[k - 1][symbols.length];
            }
            if ((rand > lower) && rand < countTable[k][symbols.length]) {
                previousindx = k;
                break;
            }
        }

        seq = seq + symbols[previousindx];
        for (int i = 1; i < length; i++) {
            rand = Math.random();
            for (int k = 0; k < symbols.length; k++) {
                double lower = 0;
                if (k == 0) {
                    lower = 0;
                } else {
                    lower = countTable[previousindx][k - 1];
                }
                if ((rand > lower) && rand < countTable[previousindx][k]) {
                    currentindx = k;
                    break;
                }
            }
            seq = seq + symbols[currentindx];
            previousindx = currentindx;
        }
        CSSequence result = new CSSequence();
        result.setSequence(seq);
        return result;

    }
}
