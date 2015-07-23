package org.geworkbench.util.network;

import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;

import java.io.Serializable;
import java.text.DecimalFormat;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class GeneNetworkEdgeImpl implements GeneNetworkEdge, Serializable, Comparable {
    private static DecimalFormat pValueFormatter = new DecimalFormat("#.#E0");
    private static DecimalFormat miFormatter = new DecimalFormat("#.##");
    public static boolean usePValue = false;

    private int id1 = 0;
    private DSGeneMarker m2;
    private double a = 0.0;
    private double b = 0.0;
    private double c = 0.0;
    private double pValue = 1.0;
    private boolean active = true;

    public GeneNetworkEdgeImpl() {
    }

    public String toString() {
        String cc = "";
        if (usePValue) {
            cc = pValueFormatter.format(pValue);
        } else {
            cc = miFormatter.format(c * 100);
        }
        String tt = null;
        if (m2 != null) {
            tt = m2.toString();
        } else {
            tt = "Marker m2 not set";
        }
        if (tt.length() > 100) {
            tt = tt.substring(0, 60);
        }
        return "[" + cc + "] " + tt;
    }

    public boolean isActive() {
        return active;
    }

    public double getPValue() {
        return pValue;
    }

    public double getThreshold() {
        if (usePValue) {
            return pValue;
        } else {
            return c;
        }
    }

    public void setPValue(double pv) {
        pValue = pv;
    }

    public double getA() {
        return a;
    }

    public void setA(double _a) {
        a = _a;
    }

    public double getB() {
        return b;
    }

    public void setB(double _b) {
        b = _b;
    }

    public double getMI() {
        return c;
    }

    public void setMI(double mi) {
        c = mi;
    }

    public int getId1() {
        return id1;
    }

    public void setId1(int id) {
        id1 = id;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean status) {
        active = status;
    }

    public DSGeneMarker getMarker2() {
        return m2;
    }

    public void setMarker2(DSGeneMarker m) {
        m2 = m;
    }

    public int compareTo(Object object) {
        GeneNetworkEdgeImpl rs = (GeneNetworkEdgeImpl) object;
        if (a == 0) {
            if (c < rs.c) {
                return 1;
            }
            if (c > rs.c) {
                return -1;
            }
        } else {
            if (c < rs.c) {
                return 1;
            }
            if (c > rs.c) {
                return -1;
            }
        }
        return 0;
    }

    public int hashCode() {
        return m2.hashCode();
    }

    public boolean equals(Object o) {
        if (o instanceof GeneNetworkEdgeImpl) {
            return m2.equals(((GeneNetworkEdgeImpl) o).m2);
        }
        return false;
    }

    public static void setUsePValue(boolean status) {
        usePValue = status;
    }
}
