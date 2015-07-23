package org.geworkbench.util.network;

import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;

import java.io.Serializable;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public interface GeneNetworkEdge extends Serializable {
    public boolean isActive();

    public void setActive(boolean status);

    public double getThreshold();

    public double getMI();

    public void setPValue(double pv);

    public double getPValue();

    public int getId1();

    public DSGeneMarker getMarker2();
}
