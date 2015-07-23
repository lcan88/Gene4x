package org.geworkbench.util.pathwaydecoder;

import distributions.BetaDistribution;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.views.DSMicroarraySetView;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.complex.panels.CSItemList;
import org.geworkbench.bison.datastructure.complex.panels.DSItemList;
import org.geworkbench.util.network.GeneNetworkEdgeImpl;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.DecimalFormat;
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

public class PathwayDecoderUtil <T extends DSGeneMarker,Q extends DSMicroarray> {
    protected DSMicroarraySetView<T, Q> view = null;
    private double[] bgHisto = null;
    private double[] fgHisto = null;

    public PathwayDecoderUtil(DSMicroarraySetView<T, Q> view) { //, DSItemList<DSMicroarray> marV,  MicroarrayVisualizer mrkV) {
        this.view = view;
    }

    public DSItemList<DSGeneMarker> matchingMarkers(String markerLabel) {
        DSItemList<DSGeneMarker> markers = new CSItemList<DSGeneMarker>();
        if (markerLabel.length() > 0) {
            for (int i = 0; i < view.markers().size(); i++) {
                DSGeneMarker mi = view.markers().get(i);
                if ((mi.getLabel().equalsIgnoreCase(markerLabel)) || (mi.getDescription().toLowerCase().indexOf(markerLabel) >= 0)) {
                    markers.add(mi);
                }
            }
        }
        return markers;
    }

    public static <Q extends DSMicroarray> DSItemList<DSGeneMarker> matchingMarkers(String markerLabel, DSMicroarraySetView<DSGeneMarker, Q> view) {
        DSItemList<DSGeneMarker> markers = new CSItemList<DSGeneMarker>();
        DSMicroarraySet<Q> maSet = view.getMicroarraySet();
        if (view.getDataSet() == null) {
            System.out.println("Must load a microarray set before drawing a network");
        } else {
            int markerNo = maSet.getMarkers().size();
            if (markerLabel.length() > 0) {
                try {
                    int firstGeneId = Integer.parseInt(markerLabel);
                    markers.add(maSet.getMarkers().get(firstGeneId));
                } catch (NumberFormatException ex) {
                    // In this case, the markerLabe is not a gene Id and must be parsed
                    // as an AffyId or other gene label identifier
                    for (int i = 0; i < markerNo; i++) {
                        DSGeneMarker mi = maSet.getMarkers().get(i);
                        if ((mi.getLabel().equalsIgnoreCase(markerLabel)) || (mi.getDescription().toLowerCase().equals(markerLabel))) {
                            markers.add(mi);
                        }
                    }
                }
            }
        }
        return markers;
    }

    public ArrayList relatedMarkers(DSGeneMarker mi1, DSGeneMarker mi2, DSItemList<DSMicroarray> ma, double threshold, int type, boolean computeBkd) {
        int firstGeneId = mi1.getSerial();
        int secondGeneId = -1;
        if (mi2 != null) {
            secondGeneId = mi2.getSerial();
        }
        ArrayList coefficients = new ArrayList();
        GeneGeneRelationship ggr = new org.geworkbench.util.pathwaydecoder.GeneGeneRelationship(view, type, computeBkd);
        coefficients = ggr.regression(firstGeneId, secondGeneId, threshold, ma);
        return coefficients;
    }

    public static void writeEdgeList(BufferedWriter writer, String description, ArrayList list) throws IOException {
        DecimalFormat miFormat = new DecimalFormat("#.###");
        DecimalFormat pvFormat = new DecimalFormat("#.##E0");
        writer.write(description);
        writer.newLine();
        for (int i = 0; i < list.size(); i++) {
            GeneNetworkEdgeImpl edge = (GeneNetworkEdgeImpl) list.get(i);
            writer.write(miFormat.format(edge.getMI() * 100) + "\t" + pvFormat.format(Math.pow(10.0, -edge.getPValue())) + "\t" + edge.getMarker2().getLabel() + "\t" + edge.getMarker2().toString());
            writer.newLine();
        }
    }

    public static double getPValue(double mi, int maNo) {
        double pValue = 1.0;
        double alpha = 1.0 / (0.139456 + Math.sqrt((double) maNo) * 0.0059);
        double beta = 6.5 * (double) maNo;
        BetaDistribution bd = new BetaDistribution(alpha, beta);
        pValue = 1.0 - bd.getCDF(mi);
        return pValue;
    }

    public static double getP(double mi, int maNo) {
        double p = 0.0;
        double alpha = 1.0 / (0.139456 + Math.sqrt((double) maNo) * 0.0059);
        double beta = 6.5 * (double) maNo;
        BetaDistribution bd = new BetaDistribution(alpha, beta);
        p = bd.getDensity(mi);
        return p;
    }

    public static double getLogP(double mi, int maNo) {
        double p = 0.0;
        double alpha = 1.0 / (0.139456 + Math.sqrt((double) maNo) * 0.0059);
        double beta = 6.5 * (double) maNo;
        BetaDistribution bd = new BetaDistribution(alpha, beta);
        p = bd.getCDF(mi);
        if (p == 1.0) {
            p = 0.0;
            double p0 = bd.getDensity(mi);
            for (double x = mi + 0.001; x < mi + 0.01; x += 0.001) {
                double p1 = bd.getDensity(x);
                p += (p1 + p0) * 0.001 / 2.0;
                p0 = p1;
            }
            p = -Math.log(p) / Math.log(10.0);
        } else {
            p = -Math.log(1.0 - p) / Math.log(10.0);
        }
        return p;
    }

}
