package org.geworkbench.util.patterns;

import org.geworkbench.bison.datastructure.biocollections.sequences.DSSequenceSet;
import org.geworkbench.bison.datastructure.bioobjects.sequence.DSSequence;
import org.geworkbench.bison.datastructure.complex.pattern.CSMatchedPattern;
import org.geworkbench.bison.datastructure.complex.pattern.DSMatchedPattern;
import org.geworkbench.bison.datastructure.complex.pattern.DSPatternMatch;
import org.geworkbench.bison.datastructure.complex.pattern.sequence.CSSeqPatternMatch;
import org.geworkbench.bison.datastructure.complex.pattern.sequence.DSSeqRegistration;
import polgara.soapPD_wsdl.HMMLoci;
import polgara.soapPD_wsdl.SOAPOffset;

import javax.xml.rpc.holders.IntHolder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * <p>Title: caWorkbench</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence
 * and Genotype Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author not attributable
 * @version 3.0
 */
public class CSMatchedHMMSeqPattern extends org.geworkbench.util.patterns.CSMatchedSeqPattern {
    private int max;
    private HMMLoci[] loci;
    private List<HMMLoci> lociList;

    public CSMatchedHMMSeqPattern(DSSequenceSet _seqDB, String _consensusSequence, HMMLoci[] _loci) {
        super(_seqDB);
        this.ascii = _consensusSequence;
        this.loci = _loci;
        this.idNo = new IntHolder(loci.length);
        max = 0;
        lociList = new ArrayList<HMMLoci>();
        HashSet<String> set = new HashSet<String>();
        SOAPOffset[] offsetArr = new SOAPOffset[loci.length];

        //sort the array (simple insertion sort)
        for (int i = 0; i < loci.length; i++) {
            for (int j = i + 1; j < loci.length; j++) {
                if (loci[j].getSeqId() < loci[i].getSeqId()) {
                    HMMLoci temp = loci[i];
                    loci[i] = loci[j];
                    loci[j] = temp;
                }
            }
        }

        int count = 0;
        int sid = -1;
        for (int i = 0; i < loci.length; i++) {
            if (sid != loci[i].getSeqId()) {
                count++;
                sid = loci[i].getSeqId();
            }
        }

        for (int i = 0; i < loci.length; i++) {
            max = Math.max(max, loci[i].getEnd() - loci[i].getStart());
            DSSequence seq = seqDB.getSequence(loci[i].getSeqId());
            if (seq != null) {
                String str = seq.getSequence().substring(loci[i].getStart(),
                                                         loci[i].getEnd());
                DSSeqRegistration regis = new DSSeqRegistration();
                regis.x1 = loci[i].getStart();
                regis.x2 = loci[i].getEnd();
                CSSeqPatternMatch pmatch = new CSSeqPatternMatch(seq);
                pmatch.setRegistration(regis);

                lociList.add(loci[i]);
                matches.add(pmatch);
                offsetArr[i] = new SOAPOffset();
                offsetArr[i].setDx(loci[i].getStart());
                offsetArr[i].setToken(str);
            }else{
                System.out.println("seq is null");
            }
            offset.value = offsetArr;

            this.seqNo = new IntHolder(count);
            this.idNo = new IntHolder(loci.length);
        }
    }

    public int getLength() {
        return ascii.length();
    }

    public int getMaxLength() {
        return max;
    }

    public int getSupport() {
        return idNo.value;
    }

    public int getUniqueSupport() {
        return seqNo.value;
    }

    public int getId(int i) {
        assert i >= 0 && i < lociList.size();
        return lociList.get(i).getSeqId();
    }

    public int getAbsoluteOffset(int i) {
        assert i >= 0 && i < lociList.size();
        return lociList.get(i).getStart();
    }


    public int getOffset(int i) {
        assert i >= 0 && i < lociList.size();
        return lociList.get(i).getStart();
    }


    public int getStart(int i) {
        assert i >= 0 && i < lociList.size();
        return lociList.get(i).getStart();
    }

    public int getEnd(int i) {
        assert i >= 0 && i < lociList.size();
        return lociList.get(i).getEnd();
    }

    public DSPatternMatch<DSSequence, DSSeqRegistration> get(int i) {
        return matches.get(i);
    }

    public List<DSPatternMatch<DSSequence, DSSeqRegistration>> match(DSSequence object, double p) {
        List<DSPatternMatch<DSSequence, DSSeqRegistration>> matchResults = new ArrayList<DSPatternMatch<DSSequence, DSSeqRegistration>>();
        for (DSPatternMatch<DSSequence, DSSeqRegistration> match : matches) {
            if (match.getObject().equals(object) && match.getRegistration().getPValue() > p) {
                matchResults.add(match);
            }
        }
        return matchResults;
    }

    public DSSeqRegistration match(DSSequence object) {
        DSSeqRegistration result = new DSSeqRegistration();
        DSMatchedPattern<DSSequence, DSSeqRegistration> matchResults = new CSMatchedPattern<DSSequence, DSSeqRegistration>(this);
        for (DSPatternMatch<DSSequence, ? extends DSSeqRegistration> match : matches) {
            if (match.getObject().equals(object)) {
                match.getRegistration().setPValue(0.0);
                return match.getRegistration();
            }
        }
        return null;
    }
}
