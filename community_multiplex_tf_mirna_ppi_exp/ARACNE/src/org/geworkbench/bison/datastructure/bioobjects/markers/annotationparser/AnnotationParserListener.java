package org.geworkbench.bison.datastructure.bioobjects.markers.annotationparser;

/**
 * @author John Watkinson
 */
public interface AnnotationParserListener {

    /**
     * Receives an update from the annotation parser.
     * @param text the update text.
     * @return true if the update can be displayed, false otherwise.
     */
    public boolean annotationParserUpdate(String text);

}
