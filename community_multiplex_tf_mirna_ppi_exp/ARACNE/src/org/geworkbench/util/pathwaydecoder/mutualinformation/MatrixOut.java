package org.geworkbench.util.pathwaydecoder.mutualinformation;

import java.util.HashMap;
import java.util.Set;

public interface MatrixOut {

    HashMap get(int geneId);

    float get(int geneId1, int geneId2);

    int getEdgeNo(int bin);

    double getThreshold(int bin);

    Set getKeys();
}
