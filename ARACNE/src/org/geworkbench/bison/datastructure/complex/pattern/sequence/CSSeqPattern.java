package org.geworkbench.bison.datastructure.complex.pattern.sequence;

import org.geworkbench.bison.datastructure.bioobjects.sequence.DSSequence;
import org.geworkbench.bison.datastructure.complex.pattern.CSPatternMatch;
import org.geworkbench.bison.datastructure.complex.pattern.DSPattern;
import org.geworkbench.bison.datastructure.complex.pattern.DSPatternMatch;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: Bioworks</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author not attributable
 * @version 1.0
 */

public class CSSeqPattern implements DSPattern<DSSequence, DSSeqRegistration> {

    ArrayList<Integer> locus = new ArrayList<Integer>();
    ArrayList<String> value = new ArrayList<String>();

    public DSSeqRegistration match(DSSequence object) {
        return null;
    }

    public List<DSPatternMatch<DSSequence, DSSeqRegistration>> match(DSSequence sequence, double p) {
        // last offset
        ArrayList<DSPatternMatch<DSSequence, DSSeqRegistration>> matches = new ArrayList<DSPatternMatch<DSSequence, DSSeqRegistration>>();
        int lastOffset = locus.get(locus.size() - 1);
        for (int i = 0; i < sequence.length(); i++) {
            boolean matched = true;
            for (int k = 0; k < locus.size(); k++) {
                if (value.get(k).indexOf(sequence.getSequence().charAt(i + locus.get(k))) == -1) {
                    matched = false;
                    break;
                }
            }
            if (matched) {
                DSPatternMatch<DSSequence, DSSeqRegistration> match = new CSPatternMatch<DSSequence, DSSeqRegistration>(sequence);
                match.getRegistration().x1 = i;
                match.getRegistration().x2 = i + lastOffset;
                matches.add(match);
            }
        }
        return matches;
    }

    public String toString(DSSequence object, DSSeqRegistration registration) {
        return "";
    }
}
