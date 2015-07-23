package org.geworkbench.util.patterns;

import org.geworkbench.bison.datastructure.biocollections.sequences.DSSequenceSet;
import org.geworkbench.bison.datastructure.bioobjects.sequence.DSSequence;
import org.geworkbench.bison.datastructure.complex.pattern.DSMatchedPattern;
import org.geworkbench.bison.datastructure.complex.pattern.DSPattern;
import org.geworkbench.bison.datastructure.complex.pattern.DSPatternMatch;
import org.geworkbench.bison.datastructure.complex.pattern.sequence.DSMatchedSeqPattern;
import org.geworkbench.bison.datastructure.complex.pattern.sequence.DSSeqRegistration;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class FlexiblePattern extends ArrayList<DSPatternMatch<DSSequence, DSSeqRegistration>> implements DSMatchedSeqPattern {
    /**
     * This class implements flexible patterns having more than one
     * set of offsets. This is implemented by having an array of N offsets
     * and N loci
     */
    private DSSequenceSet seqDB = null;
    public int seqNo = 0;
    public ArrayList patterns = new ArrayList(); // contains the individual patterns
    public ArrayList mLocus = new ArrayList();
    public double pValue = 1.0;
    public String ascii = null;
    public int length = 0;
    public int maxLen = 0;

    /*
       public String toString() {
      String s = new String();
      if(ascii != null) {
        s += ascii + "    ";
      }
      s += "(" + locus.value.length + "," + offset.value.length + ")";
      return s;
       }
     */
    public FlexiblePattern(org.geworkbench.util.patterns.CSMatchedSeqPattern p0, CSMatchedSeqPattern p1) {
        //add(p);
        mLocus = merge(p0, p1);
        patterns.add(p0);
        patterns.add(p1);
        length += p0.getLength() + p1.getLength();
        maxLen = Math.max(maxLen, p0.getMaxLength());
        maxLen = Math.max(maxLen, p1.getMaxLength());
    }

    /*
       public void add(Pattern p) {
      int id = patterns.size();
      locus.addLocus(id, p);
      patterns.add(id, p);
      length += p.getLength();
      maxLen = Math.max(maxLen, p.getMaxLength());
       }
     */
    public int getSupport() {
        return mLocus.size();
    }

    public int getLength() {
        return length;
    }

    public int getMaxLength() {
        return maxLen;
    }

    public int getUniqueSupport() {
        return getSupport();
    }

    public int getSeqNo() {
        return getSupport();
    }

    public double getPValue() {
        return pValue;
    }

    public void setPValue(double pValue) {
        this.pValue = pValue;
    }

    public String getASCII() {
        if (ascii == null) {
            ascii = new String();
            boolean skipFirst = false;
            Iterator it = patterns.iterator();
            while (it.hasNext()) {
                if (skipFirst) {
                    ascii += " >--< ";
                }
                CSMatchedSeqPattern p = (CSMatchedSeqPattern) it.next();
                ascii += p.getASCII();
                skipFirst = true;
            }
        }
        return ascii;
    }

    public void write(BufferedWriter writer) throws IOException {
        writer.write(ascii);
        writer.write("\t");
        writer.write("[" + getSeqNo() + "," + getSupport() + "," + getLength() + "," + getPValue() + "]\t");
        writer.newLine();
    }

    public void buildHistogram(int[] yAxis, int step, int maxBin) {
        Iterator it = mLocus.iterator();
        while (it.hasNext()) {
            TwoLocus tl = (TwoLocus) it.next();
            int x = (tl.dx1 - tl.dx0);
            if ((x < -8) || (x > 8)) {
                yAxis[maxBin + (x / step)]++;
            }
        }
    }

    public class TwoLocus {
        public int seqId;
        public int dx0;
        public int dx1;
    }

    public ArrayList merge(CSMatchedSeqPattern p0, CSMatchedSeqPattern p1) {
        ArrayList mLocus = new ArrayList();
        int i_0 = 0;
        int i_1 = 0;
        while ((i_0 < p0.getSupport()) && (i_1 < p1.getSupport())) {
            //SOAPLocus l_0 = p0.locus.value[i_0];
            //SOAPLocus l_1 = p1.locus.value[i_1];
            if (p0.getId(i_0) < p1.getId(i_1)) {
                i_0++;
            } else if (p0.getId(i_0) > p1.getId(i_1)) {
                i_1++;
            } else {
                int from_1 = i_1;
                int id = p0.getId(i_0);
                while (p0.getId(i_0) == id) {
                    i_1 = from_1;
                    //l_1 = p1.locus.value[i_1];
                    while (p1.getId(i_1) == id) {
                        TwoLocus tl = new TwoLocus();
                        tl.seqId = id;
                        tl.dx0 = p0.getAbsoluteOffset(i_0);
                        tl.dx1 = p1.getAbsoluteOffset(i_1);
                        mLocus.add(tl);
                        i_1++;
                        if (i_1 >= p1.getSupport()) break;
                        //l_1 = p1.locus.value[i_1];
                    }
                    i_0++;
                    if (i_0 >= p0.getSupport()) break;
                    //l_0 = p0.locus.value[i_0];
                }
            }
        }
        return mLocus;
    }

    public String toString(int i) {
        return getASCII();
    }

    public DSSeqRegistration getRegistration(int i) throws IndexOutOfBoundsException {
        if (i < getSupport()) {
            DSSeqRegistration seqReg = new DSSeqRegistration();
            patterns.get(0);
            seqReg.x1 = ((TwoLocus) mLocus.get(i)).dx0;
            seqReg.x2 = ((TwoLocus) mLocus.get(i)).dx1 + ((DSMatchedSeqPattern) patterns.get(1)).getLength();
            return seqReg;

        }
        throw new IndexOutOfBoundsException();
    }

    public DSSequence getObject(int i) throws IndexOutOfBoundsException {
        if (i < getSupport()) {
            TwoLocus locus = (TwoLocus) mLocus.get(i);
            return seqDB.getSequence(locus.seqId);
        }
        throw new IndexOutOfBoundsException();
    }

    public DSPattern<DSSequence, DSSeqRegistration> getPattern() {
        /** @todo Fix Patterns */
        return null;
    }

    public int hashCode() {
        if (ascii != null) {
            return ascii.hashCode();
        } else {
            return super.hashCode();
        }
    }

    public void addAll(DSMatchedPattern<DSSequence, DSSeqRegistration> matches) {

    }

    //  public void add(DSPatternMatch<DSSequence, DSSeqRegistration> match) {
    //  }
    public List<DSPatternMatch<DSSequence, DSSeqRegistration>> matches() {
        return this;
    }

    public void setLabel(String label) {

    }

    public String getLabel() {
        return ascii;
    }
}
