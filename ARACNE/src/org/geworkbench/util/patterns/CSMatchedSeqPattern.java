package org.geworkbench.util.patterns;

import org.geworkbench.bison.datastructure.biocollections.sequences.DSSequenceSet;
import org.geworkbench.bison.datastructure.bioobjects.sequence.DSSequence;
import org.geworkbench.bison.datastructure.complex.pattern.CSMatchedPattern;
import org.geworkbench.bison.datastructure.complex.pattern.DSMatchedPattern;
import org.geworkbench.bison.datastructure.complex.pattern.DSPattern;
import org.geworkbench.bison.datastructure.complex.pattern.DSPatternMatch;
import org.geworkbench.bison.datastructure.complex.pattern.sequence.CSSeqPatternMatch;
import org.geworkbench.bison.datastructure.complex.pattern.sequence.DSMatchedSeqPattern;
import org.geworkbench.bison.datastructure.complex.pattern.sequence.DSSeqRegistration;
import polgara.soapPD_wsdl.SOAPOffset;
import polgara.soapPD_wsdl.holders.ArrayOfSOAPOffsetHolder;

import javax.xml.rpc.holders.IntHolder;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */


public class CSMatchedSeqPattern extends CSMatchedPattern<DSSequence, DSSeqRegistration> implements DSMatchedSeqPattern, DSPattern<DSSequence, DSSeqRegistration> {

    static java.util.regex.Pattern headerPattern = java.util.regex.Pattern.compile("\\[\\d+\\]\\s+(\\S+)\\s+\\[(\\d+)\\D*(\\d+)\\D*(\\d+)\\D*(\\d+\\.?\\d*E?\\d*)\\]");
    static java.util.regex.Pattern locusPattern = java.util.regex.Pattern.compile("\\[(\\d+)\\D*(\\d+)\\]");
    static java.util.regex.Pattern offsetPattern = java.util.regex.Pattern.compile("([a-zA-Z]|\\.|\\[[^\\]]+\\])");

    public IntHolder idNo = new IntHolder();
    public IntHolder seqNo = new IntHolder();
    protected DSSequenceSet seqDB = null;
    //private ArrayOfintHolder       locusId  = new ArrayOfintHolder();
    //private ArrayOfintHolder       locusOff = new ArrayOfintHolder();
    // locus is a bytecoded int array, where each group of 8 bytes
    // encode a locusId (4 bytes) and a locusOffset (4 bytes). This
    // is decoded using the get32BitInteger method and it is used to
    // dramatically speed up the transfer of patterns over a SOAP
    // connection.
    public byte[] locus = null;
    public ArrayOfSOAPOffsetHolder offset = new ArrayOfSOAPOffsetHolder();
    public String ascii = null;
    public int maxLen = 0;
    protected int rand_hash;

    protected CSMatchedSeqPattern() {
        pattern = this;
        rand_hash = new java.util.Random().nextInt();
    }

    public CSMatchedSeqPattern(DSSequenceSet _seqDB) {
        seqDB = _seqDB;
        pattern = this;
        rand_hash = new java.util.Random().nextInt();
    }

    public String toString() {
        String s = new String();
        if (ascii != null) {
            s += ascii + "    ";
        }
        s += "(" + idNo.value + "," + offset.value.length + ")";
        return s;
    }

    public CSMatchedSeqPattern(String s) {
        pattern = this;
        Matcher m0 = headerPattern.matcher(s);
        rand_hash = new java.util.Random().nextInt();
        if (m0.find()) {
            Matcher m1 = locusPattern.matcher(s);
            String pat = m0.group(1);
            int seqNo = Integer.parseInt(m0.group(2));
            int idNo = Integer.parseInt(m0.group(3));
            int tokNo = Integer.parseInt(m0.group(4));
            double pVal = Double.parseDouble(m0.group(5));
            this.zScore = pVal;
            this.seqNo.value = seqNo;
            this.idNo.value = idNo;
            this.offset.value = new SOAPOffset[tokNo];
            int offDx = 0;
            int j = 0;
            Matcher m2 = offsetPattern.matcher(pat);
            while (m2.find()) {
                String token = m2.group(1);
                if (!token.equalsIgnoreCase(".")) {
                    this.offset.value[j] = new SOAPOffset();
                    this.offset.value[j].setDx(offDx);
                    this.offset.value[j].setToken(token);
                    j++;
                }
                offDx++;
            }
            this.ascii = pat;
            int index = m0.end();
            boolean found = m1.find(index);
            int locId = 0;
            locus = new byte[idNo * 8]; // 4 bytes per int for id and offset
            while (found) {
                int id = Integer.parseInt(m1.group(1));
                int dx = Integer.parseInt(m1.group(2));
                locus[locId++] = (byte) (id % 256);
                id /= 256;
                locus[locId++] = (byte) (id % 256);
                id /= 256;
                locus[locId++] = (byte) (id % 256);
                id /= 256;
                locus[locId++] = (byte) (id % 256);
                locus[locId++] = (byte) (dx % 256);
                dx /= 256;
                locus[locId++] = (byte) (dx % 256);
                dx /= 256;
                locus[locId++] = (byte) (dx % 256);
                dx /= 256;
                locus[locId++] = (byte) (dx % 256);
                found = m1.find();
            }
        }
    }

    public int getId(int i) {
        if (locus != null) {
            return get32BitUnsignedInt(locus, i * 2);
        }
        return 0;
    }

    public int getAbsoluteOffset(int i) {
        if (locus != null) {
            return get32BitUnsignedInt(locus, i * 2 + 1);
        }
        return 0;
    }

    public int[] getSupportIds() {
        // creates the complement of locus1 in locus;
        int prevId = -1;
        int j = 0;
        int idNo = this.idNo.value;
        int[] support = new int[idNo];
        int i_0 = 0;
        while (i_0 < idNo) {
            int id = getId(i_0++);
            if (id > prevId) {
                prevId = support[j++] = id;
            }
        }
        int[] finalSupport = new int[j];
        for (int i = 0; i < j; i++) {
            finalSupport[i] = support[i];
        }
        return finalSupport;
    }

    public int[] getComplement(int[] base) {
        // creates the complement of locus1 in locus;
        int[] complement = new int[base.length];
        int i_0 = 0;
        int i_1 = 0;
        int j = 0;
        int idNo = this.idNo.value;
        while ((i_1 < idNo) && (i_0 < base.length)) {
            int l_0 = base[i_0];
            int l_1 = getId(i_1);
            if (l_0 < l_1) {
                complement[j++] = l_0;
                i_0++;
            } else if (l_0 == l_1) {
                i_0++;
                i_1++;
            } else {
                i_1++;
            }
        }
        while (i_0 < base.length) {
            complement[j++] = base[i_0++];
        }
        int[] finalComplement = new int[j];
        for (int i = 0; i < j; i++) {
            finalComplement[i] = complement[i];
        }
        return finalComplement;
    }

    public int getLength() {
        return offset.value.length;
    }

    public int getSupport() {
        return idNo.value;
    }

    public int getUniqueSupport() {
        return seqNo.value;
    }

    public int getSeqNo() {
        return seqNo.value;
    }

    public int getMaxLength() {
        int extent = getExtent();
        int maxLen = 0;
        if ((maxLen == 0) && (locus != null)) {
            for (int i = 0; i < idNo.value; i++) {
                //int id = getId(i);
                int dx = getOffset(i);
                int len = dx + extent;
                maxLen = Math.max(len, maxLen);
            }
        }
        return maxLen;
    }

    public String getASCII() {
        return ascii;
    }

    public void write(BufferedWriter writer) throws IOException {
        writer.write(ascii);
        writer.write("\t");
        writer.write("[" + getSeqNo() + "," + getSupport() + "," + getLength() + "," + getPValue() + "]\t");
        for (int j = 0; j < getSupport(); j++) {
            int id = getId(j);
            int dx = getOffset(j);
            writer.write(new String("[" + id + ',' + dx + ']'));
        }
        writer.newLine();
    }

    public int getExtent() {
        int baseOffset = this.offset.value[0].getDx();
        return offset.value[getLength() - 1].getDx() + 1 - baseOffset;
    }

    public int getOffset(int j) {
        int baseOffset = this.offset.value[0].getDx();
        int dx = getAbsoluteOffset(j) + baseOffset;
        return dx;
    }

    private int get32BitUnsignedInt(byte[] bytes, int offset) {
        return org.geworkbench.util.BinaryEncodeDecode.decodeUnsignedInt32(bytes, offset);
    }

    ArrayList<DSPatternMatch<DSSequence, DSSeqRegistration>> matches = new ArrayList<DSPatternMatch<DSSequence, DSSeqRegistration>>();

    public DSPatternMatch<DSSequence, DSSeqRegistration> get(int i) {
        if (matches.size() > i) {
            return matches.get(i);
        } else {
            CSSeqPatternMatch match = new CSSeqPatternMatch(getObject(i));
            DSSeqRegistration reg = match.getRegistration();
            reg.x1 = getOffset(i);
            reg.x2 = reg.x1 + getLength();
            //  System.out.println(match.getObject().getSequence());
            return match;
        }
    }

    public List<DSPatternMatch<DSSequence, DSSeqRegistration>> matches() {
        if (matches.size() < 1) {
            for (int i = 0; i < this.getSupport(); i++) {
                matches.add(i, get(i));
            }
        }
        return matches;
    }

    public int hashCode() {
        return rand_hash;
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

    public String toString(DSSequence object, DSSeqRegistration registration) {
        if (ascii != null) {
            return ascii;
        } else {
            return null;
        }
    }

    public DSSequence getObject(int i) throws IndexOutOfBoundsException {
        if ((seqDB != null) && (i < getSupport())) {
            return seqDB.getSequence(this.getId(i));
        }
        throw new IndexOutOfBoundsException();
    }

    public DSSeqRegistration getRegistration(int i) throws IndexOutOfBoundsException {
        if (i < getSupport()) {
            DSSeqRegistration seqReg = new DSSeqRegistration();
            seqReg.x1 = getOffset(i);
            seqReg.x2 = seqReg.x1 + this.getExtent();
            return seqReg;
        }
        throw new IndexOutOfBoundsException();
    }

    public String toString(int i) {
        if (ascii == null) {
            PatternOperations.fill(this, seqDB);
        }
        return getASCII();
    }

    public DSSequenceSet getSeqDB() {
        return seqDB;
    }

    public void setSeqDB(DSSequenceSet seqDB) {
        this.seqDB = seqDB;
    }

}
