package org.geworkbench.util.pathwaydecoder;

import java.util.Comparator;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class RankSorter3D extends org.geworkbench.util.pathwaydecoder.RankSorter {
    public double z;
    public int iz;
    static public final Comparator SORT_Z = new Comparator() {
        public int compare(Object o1, Object o2) {
            RankSorter3D rs1 = (RankSorter3D) o1;
            RankSorter3D rs2 = (RankSorter3D) o2;
            if (rs1.z < rs2.z)
                return -1;
            if (rs1.z > rs2.z)
                return 1;
            return 0;
        }
    };

}