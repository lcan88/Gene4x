package org.geworkbench.util.sequences;

import Jama.Matrix;
import org.geworkbench.bison.datastructure.biocollections.sequences.CSSequenceSet;
import org.geworkbench.bison.datastructure.biocollections.sequences.DSSequenceSet;
import polgara.soapPD_wsdl.SoapPDPortType;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Title: Preliminary class written to calculate sequence entropy and profileHMMs</p>
 * <p>Description: Still in development phase</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Califano Lab</p>
 *
 * @author not attributable
 * @version 1.0
 */

public class SequenceAligner { //rename to entropy calculator?
    private DSSequenceSet sequenceDB = null;
    private SoapPDPortType port = null;
    private int handle = -1;
    private int index = -1;
    private int patLength = -1;
    private int maxSeqLength = -1;
    private int distToBegin, distToEnd = 0;
    private int startToRight, startToLeft = 0;
    private String pattern = null;
    private Vector indexArray = new Vector();
    private HashMap seqIndex = new HashMap();
    private HashMap aminoAcids = new HashMap();
    private Matrix rightProb = null;
    private Matrix leftProb = null;
    private Matrix seqDBProb = null;
    private int winLength = 10;
    private Matrix doubleRightProb = null;
    private Matrix doubleLeftProb = null;
    private Matrix doubleSeqDBProb = null;
    final private int seqPos = 0; //location of sequence in vector in seqIndex
    final private int indPos = 1; //location of pattern starting position
    final private int labelPos = 2; //location of label of sequence in seqIndex
    private Integer patStartPos = null;
    private String sequence = null;
    private double Ecenter, EToLeft, EToRight, EMax = 0.0;

    //private BufferedWriter out = null;
    private String absolutePath = "C:/C2B2PNP/matchingSubSequences.fa";
    private String splashAlignFile = null;
    private String hmmAlignFile = null;
    private BufferedWriter out = null;

    private static int profileHmmCount = 0;
    private static int splashAlignCount = 0;
    //private static int hmmBuildOutputCount = 0;

    public SequenceAligner(DSSequenceSet sDB) {
        sequenceDB = sDB;
        aminoAcids.put("A", new Integer(0)); //Alanine
        aminoAcids.put("R", new Integer(1)); //Arginine
        aminoAcids.put("N", new Integer(2)); //Asparagine
        aminoAcids.put("D", new Integer(3)); //Aspartic acid
        aminoAcids.put("B", new Integer(4)); //Asparagine or Aspartic Acid
        aminoAcids.put("C", new Integer(5)); //Cysteine
        aminoAcids.put("Q", new Integer(6)); //Glutamine
        aminoAcids.put("E", new Integer(7)); //Glutamic Acid
        aminoAcids.put("Z", new Integer(8)); //Glutamine or Glutamic Acid
        aminoAcids.put("G", new Integer(9)); //Glycine
        aminoAcids.put("H", new Integer(10)); //Histidine
        aminoAcids.put("I", new Integer(11)); //Isoleucine
        aminoAcids.put("L", new Integer(12)); //Leucine
        aminoAcids.put("K", new Integer(13)); //Lysine
        aminoAcids.put("M", new Integer(14)); //Methionine
        aminoAcids.put("F", new Integer(15)); //Phenylalanine
        aminoAcids.put("P", new Integer(16)); //Proline
        aminoAcids.put("S", new Integer(17)); //Serine
        aminoAcids.put("T", new Integer(18)); //Threonine
        aminoAcids.put("W", new Integer(19)); //Tryptophan
        aminoAcids.put("Y", new Integer(20)); //Tyrosine
        aminoAcids.put("V", new Integer(21)); //Valine
        aminoAcids.put("X", new Integer(22)); //Don't know

    } //end constructor

    public Vector alignByPattern(String pat) {
        splashAlignCount++;
        splashAlignFile = "C:/hmmer/splashAlign" + splashAlignCount + ".out";
        String seqStr = null;
        int indexMatcher = -10;
        setPattern(pat);
        int i = 0;
        int j = 0;
        int foundCount = 0;
        try {
            out = new BufferedWriter(new FileWriter(splashAlignFile));
            while (i != sequenceDB.getSequenceNo()) {
                seqStr = sequenceDB.getSequence(i).getSequence();
                if (seqStr.length() > maxSeqLength) {
                    maxSeqLength = seqStr.length();
                } //end if
                Pattern p = Pattern.compile(getPattern());
                Matcher m = p.matcher(seqStr);
                if (m.find()) {
                    foundCount++;
                    indexMatcher = m.start();
                    Vector v = new Vector();
                    v.add(seqStr);
                    v.add(new Integer(indexMatcher));
                    v.add(sequenceDB.getSequence(i).getLabel());
                    seqIndex.put(new Integer(j), v);
                    j++;
                } //end if
                i++;
            } //end while
            calcSlidingWindowEntropies(winLength);
            out.close();
            //System.out.println("foundCount = " + foundCount);
        } catch (Exception e) {
            System.out.println("SequenceAligner::::alignByPattern(): " + e.toString());
            e.printStackTrace();
        }
        return indexArray;
    } //end findIndex()

    public void calcSlidingWindowEntropies(int winSize) { //IS THERE A BETTER NAME FOR THIS METHOD?
        try {
            Integer h = null;
            int i = 0; //position in window
            int j = 0; //sequence number in matching sequences hash map
            double p = 0;
            int end = 0;
            int row = 0;
            Vector v = null;
            winLength = winSize;
            createProbMatrices();
            for (j = 0; j < seqIndex.size(); j++) { //j is the jth sequence in the matching set)
                initializePatternBoundaries(j); //sets the sequence also
                checkSequenceLength(getSequence());
                writeToFile(j);
                row = 0;
                for (i = startToLeft; i > startToLeft - 2 * winLength; i--) {
                    //i is the windowPosition

                    /* if(i==-1){
                       System.out.println("calcSlidingWindowEntropies(): startToLeft = " +startToLeft);
                       System.out.println("calcSlidingWindowEntropies(): getSequence().length() = "+getSequence().length());
                       System.out.println("calcSlidingWindowEntropies(): getSequence() = " +getSequence());
                       break;
                     }*/
                    setProbMatrix(getSequence(), i, row, leftProb);
                    row++; //position
                } //end for (i--)
                row = 0;
                for (i = startToRight; i < startToRight + 2 * winLength; i++) { //for loop going through all positions in window shifting right
                    //out.write
                    setProbMatrix(getSequence(), i, row, rightProb);
                    row++;
                } //end for(i++)
            } //end for(j)
            double tot = (double) 1 / seqIndex.size();
            doubleRightProb = rightProb.times(tot);
            doubleLeftProb = leftProb.times(tot);
            EToRight = calcWinEntropy(doubleRightProb);

            EToLeft = calcWinEntropy(doubleLeftProb);

            calcDBEntropy();
        } catch (Exception e) {
            System.out.println("SequenceAligner::::calcSlidingWindowEntropies(): " + e.toString());
            e.printStackTrace();
        }
    } //end calcProbPatMatch

    public double calcWinEntropy(Matrix matx) { //window sliding to right or left
        int i = 0;
        int j = 0;
        int win = 0;
        int k = 0;
        double p = 0.0;
        double E = 0.0;

        for (k = 0; k < winLength; k++) {

            for (i = k; i < k + winLength; i++) {

                for (j = 0; j < aminoAcids.size(); j++) {
                    try {
                        if (matx.get(i, j) == 0) {
                            //do nothing
                        } else {
                            E = E + -1 * matx.get(i, j) * java.lang.StrictMath.log(matx.get(i, j));
                        }
                        if (k == 0) {
                            //this is getting the E of the anchored pattern itself
                            if (Ecenter > 0.0) {
                                //do nothing
                            } else {
                                Ecenter = E;
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("SequenceAligner::::calcWinEntropy(): " + e.toString());
                        e.printStackTrace();
                    }
                } //end for j
            } //end for i
        } //end k
        return E;
    }

    public void setPattern(String pat) {
        pattern = pat;
    } //end setPattern

    public String getPattern() {
        return pattern;
    } //end getPattern

    /**
     * setPatternLength obtains the length of the pattern of interest and sets its length
     *
     * @param String pat: the pattern of interest.
     * @return void
     */

    public void setPatternLength(String pat) {
        patLength = pat.length();
    } //

    /**
     * append blanks BEFORE inserting blanks!!!! otherwise the position of matching patterns
     * gets shifted over by number of inserted 0s.
     *
     * @param double emax: the entire sequence database entropy
     * @param double e: e is the anchored window entropy (i.e. entropy obtained when all sequences are aligned to the zero
     *               sliding window position of the pattern of interest)
     * @return Ethresh: the value which the sliding window entropies will be compared to
     */
    public double calcThresh(double emax, double e) {
        double Ethresh = 0.0;
        Ethresh = (emax - e) / 16.0;
        return Ethresh;
    }

    /**
     * Calculates the distToBegin of the sequence from the right boundary of the pattern and the distToEnd from the left boundary
     * of the pattern. Initializes the sequence padding to start with the left boundary.
     *
     * @param String seq: The amino acid String of the sequence of interest.
     * @return void: returns when padSequence() has finished examining the distance to the beginning and end of the sequence and
     *         padding the sequence as required
     */

    public void checkSequenceLength(String seq) {
        try {
            /* System.out.println("checkSequenceLength: getSequence() "+getSequence());
         System.out.println("checkSequenceLength: seq =  "+seq);
             System.out.println("checkSequenceLength: seq length =  "+seq.length()); */
            String windowSlide = "right";
            boolean finished = false;
            distToEnd = getSequence().length() - startToRight;
            distToBegin = getSequence().subSequence(0, startToLeft).length();
            int patBoundary = distToEnd;
            while (!finished) {
                finished = padSequence(getSequence(), windowSlide, patBoundary);
                windowSlide = "left";
                patBoundary = distToBegin;
            }
        } catch (Exception e) {
            System.out.println("checkSequenceLength:::getSequence().length = " + getSequence().length());
            System.out.println("checkSequenceLength:::startToRight = " + startToRight);
            System.out.println("checkSequenceLength:::startToLeft = " + startToLeft);
            System.out.println("checkSequenceLength:::distToEnd = " + distToEnd);
            System.out.println("checkSequenceLength:::distToBegin = " + distToBegin);
            e.printStackTrace();
        }
    }

    /**
     * patBoundary is the distance to the beginning of the sequence or end of sequence
     * from right or left pattern boundary (respectively)
     *
     * @param int    patBoundary: the distance to the beginning or end of the sequence from the respective edge of the pattern boundary
     * @param String seq: the amino acid String value of the Sequence
     * @param String windowSlide: indicates which way window is sliding - "right" or "left"
     * @return boolean finished: indicates whether the Sequence has been finished padding both from the left and right edge of the pattern boundary
     */
    public boolean padSequence(String seq, String windowSlide, int patBoundary) {
        boolean finished = false;
        // System.out.println("padSequence: seq = "+seq);
        System.out.println("padSequence: patBoundary = " + patBoundary);

        if (patBoundary < (2 * winLength)) {
            //if(patBoundary<=(2*winLength)){
            int g = (2 * winLength) - patBoundary;
            System.out.println("padSequence: g = " + g);
            int u = 0;
            StringBuffer s = new StringBuffer(seq);
            if (!(windowSlide == "right")) { //windowSlide == left
                s.append(seq); //need to copy sequence before prepending 0s
            }
            //for (u = 0; u < g; u++) {
            for (u = 0; u <= g; u++) {
                if (windowSlide == "right") {
                    //s.append(' ');
                    s.append("-");
                } else { //windowSlide == left
                    //s.insert(u, ' ');
                    s.insert(u, "-");
                }
            } //end for appending or prepending blanks to sequence
            setSequence(s.toString());
        } //end if sequence shorter than required
        if (windowSlide == "left") {
            return true;
        }
        return finished;
    } //end padSequence

    /**
     * Creates 2 matrices - leftProb and rightProb; both are 2WinLength x 23 (num aminoAcid symbols) both matrices will
     * hold the probabilities of the amino acids occuring per position as the
     * window slides to the left and the window slides to the right
     *
     * @return void: nothing returned right now that the 2 matrices have been created succesfully
     */
    public void createProbMatrices() {
        rightProb = new Matrix(2 * winLength, aminoAcids.size());
        leftProb = new Matrix(2 * winLength, aminoAcids.size());
    }

    /**
     * Gets the position of the left pattern boundary, calculates the right pattern boundary, and sets the amino acid String value
     * of the current Sequence of interest so that it is accessible by all other methods that need to process the Sequence.
     *
     * @param int j: the index pointer in the seqIndex (the HashMap that contains all the matching sequences and the left boundary position
     * @return void: at the moment no checks to make sure that pattern boundaries have been initialized correctly
     */

    public void initializePatternBoundaries(int j) {
        try {
            Vector v = null; //contains matching sequence and starting position of pattern in sequence
            v = (Vector) seqIndex.get(new Integer(j)); //initializePatternBoundaries

            setSequence((String) v.elementAt(seqPos)); //initializePatternBoundaries
            String s = getSequence();
            patStartPos = (Integer) v.elementAt(indPos); //initializePatternBoundaries
            startToRight = patStartPos.intValue(); //initializePatternBoundaries
            startToLeft = startToRight + (getPattern().length() - 1); //initializePatternBoundaries
            if (startToLeft > s.length()) {
                System.out.println("initializePatBou: startToLeft > s.length");
                StringBuffer bu = new StringBuffer(s);
                for (int i = s.length(); i <= startToLeft; i++) {
                    bu.append('-');
                }
                setSequence(bu.toString());
            }
        } catch (Exception e) {
            System.out.println("initializePatBou: getSequence():" + getSequence());
            System.out.println("initializePatBou: getSequence().length():" + getSequence().length());
            System.out.println("initializePatBou: patStartPos = " + patStartPos);
            System.out.println("initializePatBou: startToLeft = " + startToLeft);
            System.out.println("initializePatBou: startToRight = " + startToRight);
            e.printStackTrace();
        }
    }

    public void writeToFile(int j) {
        int i = 0;
        int extendedPatStartPos = startToRight - winLength;
        Vector v = null;
        String label = null;
        String seqStr = null;
        int sequenceLength = getSequence().length();
        try {
            v = (Vector) seqIndex.get(new Integer(j));
            StringBuffer seq = new StringBuffer(30);
            for (i = extendedPatStartPos; i < startToLeft + winLength - 1; i++) {
                //  for (i = extendedPatStartPos; i < sequenceLength && i < startToLeft + winLength+1; i++) {
                if (i >= sequenceLength) {
                    seq.append("-");
                    continue;
                }
                seq.append(getSequence().charAt(i));
            }
            seqStr = seq.toString();
            label = (String) v.elementAt(labelPos);
            System.out.println("writeToFile: " + seqStr);
            out.write(label);
            out.newLine();
            out.write(seqStr);
            out.newLine();
            out.flush();
        } catch (Exception e) {
            System.out.println("i = " + i);
            System.out.println("extendedPatStartPos = " + extendedPatStartPos);
            System.out.println("startToRight = " + startToRight);
            System.out.println("startToLeft = " + startToLeft);
            System.out.println("winLength = " + winLength);
            System.out.println("sequence = " + getSequence());
            System.out.println("sequence length = " + getSequence().length());
            e.printStackTrace();
        }
    }


    public void setSequence(String seq) {
        sequence = seq;
    }

    public String getSequence() {
        return sequence;
    }

    public double calcDBEntropy() {
        int pos, i = 0;
        String seq = null;
        seqDBProb = new Matrix(1, aminoAcids.size());
        EMax = 0.0;

        int totPositions = 0;
        for (i = 0; i < sequenceDB.getSequenceNo(); i++) {
            seq = sequenceDB.getSequence(i).getSequence();
            totPositions = totPositions + seq.length();
            for (pos = 0; pos < seq.length(); pos++) {
                try { //try a
                    setProbMatrix(seq, pos, 0, seqDBProb);
                } //end try a
                catch (NullPointerException n) { // catch a.1
                    System.out.println("SequenceAligner:::findIndex(): " + n.toString());
                    n.printStackTrace();
                } //end catch a.1
                catch (Exception e) { // catch a.1
                    System.out.println("SequenceAligner:::findIndex(): " + e.toString());
                    e.printStackTrace();
                } //end catch a.1
            }

        } //end for
        double tot = 1.0 / totPositions;
        doubleSeqDBProb = seqDBProb.times(tot);
        doubleSeqDBProb.print(0, aminoAcids.size());
        for (i = 0; i < 23; i++) {

            if (doubleSeqDBProb.get(0, i) != 0) {
                EMax = EMax + -1 * doubleSeqDBProb.get(0, i) * java.lang.StrictMath.log(doubleSeqDBProb.get(0, i));
            }
            EMax = winLength * EMax;
        }

        return EMax;
    } //end calcDBEntropy

    public void setProbMatrix(String seq, int position, int row, Matrix matx) {
        Integer h = null;
        double p = 0.0;
        char t = ' ';
        try {
            if (position == -1) { //take this whole if clause out -> just for debugging!
                //System.out.println("SequenceAligner:::setProbMatrix():" + s.toString());
                System.out.println("SequenceAligner:::setProbMatrix(): position = " + position);
                System.out.println("SequenceAligner:::setProbMatrix(): t = " + t);
                System.out.println("SequenceAligner:::setProbMatrix(): sequence = " + getSequence());
                System.out.println("SequenceAligner:::setProbMatrix(): sequence.charAt(i) = " + getSequence().charAt(position));
                System.out.println("SequenceAligner:::setProbMatrix():  i = " + position + " row = " + row);

            }

            t = seq.charAt(position);
            if (Character.isLetterOrDigit(seq.charAt(position))) {
                h = (Integer) aminoAcids.get(String.valueOf(seq.charAt(position))); //h is the numerical value associated with the amino acid
                p = matx.get(row, h.intValue()) + (double) 1;
                matx.set(row, h.intValue(), p);
            } // end if
            // System.out.println("SequenceAligner:::setProbMatrix():" + s.toString());
            /*  System.out.println("SequenceAligner:::setProbMatrix(): position = " + position);
              System.out.println("SequenceAligner:::setProbMatrix(): t = "+t);
              System.out.println("SequenceAligner:::setProbMatrix(): sequence = " +
                                 getSequence());
              System.out.println(
                  "SequenceAligner:::setProbMatrix(): sequence.charAt(i) = " +
                  getSequence().charAt(position));
              System.out.println("SequenceAligner:::setProbMatrix():  i = " +
                                 position + " row = " + row); */

        } catch (StringIndexOutOfBoundsException s) {
            System.out.println("SequenceAligner:::setProbMatrix():" + s.toString());
            System.out.println("SequenceAligner:::setProbMatrix(): position = " + position);
            System.out.println("SequenceAligner:::setProbMatrix(): t = " + t);
            System.out.println("SequenceAligner:::setProbMatrix(): sequence = " + getSequence());

            System.out.println("SequenceAligner:::setProbMatrix():  i = " + position + " row = " + row);

            s.printStackTrace();
        } catch (Exception e) {
            System.out.println("SequenceAligner:::setProbMatrix():" + e.toString());
            System.out.println("SequenceAligner:::setProbMatrix(): position = " + position);
            System.out.println("SequenceAligner:::setProbMatrix(): t = " + t);
            System.out.println("SequenceAligner:::setProbMatrix(): sequence = " + getSequence());
            System.out.println("SequenceAligner:::setProbMatrix(): sequence.charAt(i) = " + getSequence().charAt(position));
            System.out.println("SequenceAligner:::setProbMatrix():  i = " + position + " row = " + row);
            e.printStackTrace();
        }
    } //end setProbabilityMatrix


    public void setSequenceDB(String fileName) {
        File f = new File(fileName);
        sequenceDB = CSSequenceSet.getSequenceDB(f);
    }

    public String getSPLASHalignFileName() {
        return splashAlignFile;
    }

    public String getHMMalignFileName() {
        return hmmAlignFile;
    }

    public void setSequenceDB(DSSequenceSet sDB) {
        sequenceDB = sDB;
    }

} //end class
