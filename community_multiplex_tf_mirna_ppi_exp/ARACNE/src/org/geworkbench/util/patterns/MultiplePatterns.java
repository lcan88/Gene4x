package org.geworkbench.util.patterns;

import java.util.ArrayList;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class MultiplePatterns extends ArrayList {
    CSMatchedSeqPattern[] patterns = null;
    int[] support = null;
    int[][] offset = null;
    int idNo = 0;

    MultiplePatterns(CSMatchedSeqPattern p1, CSMatchedSeqPattern p2) {
        patterns = new CSMatchedSeqPattern[2];
        patterns[0] = p1;
        patterns[1] = p2;
        offset = new int[2][];
        merge(p1, p2, 50, 100);
    }

    void merge(CSMatchedSeqPattern p1, org.geworkbench.util.patterns.CSMatchedSeqPattern p2, int maxL, int minSupp) {
        int i = 0;
        int j = 0;
        int k = 0;

        while ((i < p1.getSupport()) && (j < p2.getSupport())) {
            int[] tmpSupport = new int[p1.getSupport()];
            int[][] tmpOffset = new int[2][];
            for (int m = 0; m < 2; m++) {
                tmpOffset[m] = new int[p1.getSupport()];
                tmpOffset[0][m] = 0;
                tmpOffset[1][m] = 0;
            }
            int id1 = p1.getId(i);
            int id2 = p2.getId(j);
            if (id1 < id2) {
                i++;
            } else if (id1 > id2) {
                j++;
            } else {
                // This is the only case where we have to scan the respective position on the same sequence
                int dx1 = p1.getAbsoluteOffset(i);
                int dx2 = p2.getAbsoluteOffset(j);
                int l = dx2 - dx1;
                if (l < -maxL) {
                    i++;
                } else if (l > maxL) {
                    j++;
                } else {
                    // Fuse the patterns
                    tmpSupport[k] = id1;
                    tmpOffset[0][k] = dx1;
                    tmpOffset[1][k] = l;
                    j++;
                    k++;
                }
            }
        }
        support = new int[k];
        //   tmpOffset = new int[2][];
        //   for(int )
    }
}