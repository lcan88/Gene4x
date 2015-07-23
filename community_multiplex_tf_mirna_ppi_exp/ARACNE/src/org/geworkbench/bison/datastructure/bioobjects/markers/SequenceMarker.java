package org.geworkbench.bison.datastructure.bioobjects.markers;

import org.geworkbench.bison.util.StringUtils;

import java.io.Serializable;

public class SequenceMarker extends CSGeneMarker implements Serializable {
    public SequenceMarker() {
    }

    /**
     * parseLabel
     *
     * @param s String
     */
    public void parseLabel(String s) {
        // Ignore leading '>' and any other '<', '>' or characters enclosed by '</...>'.
        s = StringUtils.filter(s, "(</.*?>)|[<>]");
        String[] tokens = s.split("[|]");
        // String[] tokens = StringUtils.splitRemovingEmptyStrings(s, "[|><>]");

        if (tokens.length > 2) {
            if (tokens[0].equalsIgnoreCase("Affy")) {
                setDescription("Affy:" + tokens[1]);
                setLabel(tokens[1]);
            } else {

                int last = tokens.length - 1;
                if (tokens[0].equalsIgnoreCase("pir")) {
                    setDescription("PIR: " + tokens[last - 1] + tokens[last]);
                    setLabel(tokens[1]);
                } else if (tokens[0].equalsIgnoreCase("gp")) {
                    setDescription("GP: " + tokens[last - 1] + tokens[last]);
                    setLabel(tokens[1]);
                } else if (tokens[0].equalsIgnoreCase("sp")) {
                    setDescription("SP: " + tokens[last - 1] + tokens[last]);
                    setLabel(tokens[1]);
                } else if (tokens[0].equalsIgnoreCase("gi")) {
                    setDescription("GI: " + tokens[last - 1] + tokens[last]);
                    setLabel(tokens[1]);
                } else if (tokens[0].equalsIgnoreCase("gb")) {
                    setDescription("GB: " + tokens[last - 1] + tokens[last]);
                    setLabel(tokens[1]);
                } else {
                    setDescription(s);
                    setLabel(tokens[0]);
                }
            }
        } else if (tokens.length == 2) {
            setDescription(tokens[1]);
            setLabel(tokens[1]);
        } else {
            setDescription(s);
            setLabel(s);
        }
    }

    public String toString() {
        if (label == null) {
            return label;
        }
        return label;
    }

    public DSGeneMarker deepCopy() {
        return null;
    }

}
