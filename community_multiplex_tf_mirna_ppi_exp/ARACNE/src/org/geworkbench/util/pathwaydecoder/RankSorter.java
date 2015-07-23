package org.geworkbench.util.pathwaydecoder;

import java.util.BitSet;
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

public class RankSorter {
    final public static int active = 1;
    final public static int filter = 2;
    final public static int high = 3;
    final public static int low = 4;
    final public static int plotted = 5;
    public double x;
    public double y;
    public int ix;
    public int iy;
    public int id;
    BitSet bits = new BitSet(8);

    public void setPlotted() {
        bits.set(plotted);
    }

    public boolean isPlotted() {
        return bits.get(plotted);
    }

    public void setActive(boolean status) {
        bits.set(active, status);
    }

    public void setFilter(boolean status) {
        bits.set(filter, status);
    }

    public void setHigh() {
        bits.set(high);
        bits.clear(low);
    }

    public boolean isActive() {
        return bits.get(active);
    }

    public boolean isFiltered() {
        return bits.get(filter);
    }

    public boolean isHigh() {
        return bits.get(high);
    }

    public boolean isLow() {
        return bits.get(low);
    }

    public static final Comparator<RankSorter> SORT_X = new Comparator<RankSorter>() {
        public int compare(RankSorter rs1, RankSorter rs2) {
            if (rs1.x < rs2.x)
                return -1;
            if (rs1.x > rs2.x)
                return 1;
            if (rs1.id < rs2.id)
                return -1;
            if (rs1.id > rs2.id)
                return 1;
            return 0;
        }
    };
    public static final Comparator<RankSorter> SORT_Y = new Comparator<RankSorter>() {

        public int compare(RankSorter rs1, RankSorter rs2) {
            if (rs1.y < rs2.y)
                return -1;
            if (rs1.y > rs2.y)
                return 1;
            if (rs1.id < rs2.id)
                return -1;
            if (rs1.id > rs2.id)
                return 1;
            return 0;
        }
    };

    public static final Comparator SORT_ID = new Comparator() {
        public int compare(Object o1, Object o2) {
            RankSorter rs1 = (RankSorter) o1;
            RankSorter rs2 = (RankSorter) o2;
            if (rs1.id < rs2.id)
                return -1;
            if (rs1.id > rs2.id)
                return 1;
            return 0;
        }
    };
}
