package org.geworkbench.bison.datastructure.complex.pattern.matrix;

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import org.geworkbench.bison.datastructure.biocollections.CSMarkerVector;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.CSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.complex.panels.DSItemList;

import java.util.Vector;

/**
 * <p>Title: caWorkbench</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype
 * Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author Adam Margolin
 * @version 3.0
 */
public class MatrixVectorCreation {
    public MatrixVectorCreation() {
    }

    public GeneMatrixVector createMatrixVector(Vector<DSMicroarraySet> microarraySets) {
        //        CSMarkerVector commonMarkers = new CSMarkerVector();
        //        commonMarkers.addAll(microarraySets.get(0).markers());
        //        for(int i = 1; i < microarraySets.size(); i++){
        //            commonMarkers.retainAll(microarraySets.get(i).markers());
        //        }
        CSMarkerVector commonMarkers = getCommonMarkersForArrays(microarraySets);

        Vector<DoubleMatrix2D> matrixVector = new Vector<DoubleMatrix2D>(microarraySets.size());
        for (int i = 0; i < microarraySets.size(); i++) {
            DSMicroarraySet refMaSet = microarraySets.get(i);
            matrixVector.add(new SparseDoubleMatrix2D(commonMarkers.size(), refMaSet.size()));
        }

        for (int markerCtr = 0; markerCtr < commonMarkers.size(); markerCtr++) {
            DSGeneMarker marker = commonMarkers.get(markerCtr);
            for (int maSetCtr = 0; maSetCtr < microarraySets.size(); maSetCtr++) {
                DSMicroarraySet maSet = microarraySets.get(maSetCtr);
                double[] expressionProfile = maSet.getRow(marker);
                for (int colCtr = 0; colCtr < expressionProfile.length; colCtr++) {
                    matrixVector.get(maSetCtr).set(markerCtr, colCtr, expressionProfile[colCtr]);
                }
            }
        }

        GeneMatrixVector geneMatrixVector = new GeneMatrixVector();
        geneMatrixVector.setMarkerVector(commonMarkers);
        geneMatrixVector.setMatrices(matrixVector);

        DoubleMatrix2D maSet = matrixVector.get(1);
        //        for (int i = 0; i < 20; i++) {
        //            DoubleMatrix1D row = maSet.viewRow(i);
        //            DSMarker commonMarker = commonMarkers.get(i);
        //            DSItemList<DSMarker> markerList = microarraySets.get(1).markers();
        //            DSMarker maMarker = markerList.get(commonMarker);
        //            System.out.print(maMarker.getLabel() + "\t" + maMarker.getGeneName());
        //            for (int j = 0; j < row.size(); j++) {
        //                System.out.print("\t" + row.get(j));
        //            }
        //            System.out.println("");
        //        }

        return geneMatrixVector;
    }

    public CSMarkerVector getCommonMarkers(Vector<DSItemList<DSGeneMarker>> allMarkerVectors) {
        CSMarkerVector commonMarkers = new CSMarkerVector();


        DSItemList<DSGeneMarker> firstMarkerVector = allMarkerVectors.get(0);
        for (DSGeneMarker marker : firstMarkerVector) {
            for (int i = 1; i < allMarkerVectors.size(); i++) {
                if (allMarkerVectors.get(i).get(marker) == null) {
                    break;
                }
                CSGeneMarker newMarker = new CSGeneMarker();
                //                String geneName = marker.getGeneName();
                //                System.out.println(geneName);
                newMarker.setGeneName(marker.getGeneName());
                newMarker.setGeneId(marker.getGeneId());
                if (!"---".equals(marker.getGeneName()) && marker.getGeneId() != -1) {
                    commonMarkers.add(newMarker);
                }
            }
        }
        System.out.println("Num markers " + commonMarkers.size());
        return commonMarkers;

    }

    public CSMarkerVector getCommonMarkersForArrays(Vector<DSMicroarraySet> microarraySets) {
        Vector<DSItemList<DSGeneMarker>> allMarkerVectors = new Vector<DSItemList<DSGeneMarker>>(microarraySets.size());
        for (int i = 0; i < microarraySets.size(); i++) {
            allMarkerVectors.add(microarraySets.get(i).getMarkers());
        }

        return getCommonMarkers(allMarkerVectors);
    }
}
