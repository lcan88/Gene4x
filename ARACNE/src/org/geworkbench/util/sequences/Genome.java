package org.geworkbench.util.sequences;

/**
 * <p>Title: Bioworks</p>
 * <p>Class: Genome</p>
 * <p>Description: Genome data for genome assembly. </p>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p>Company: Columbia University</p>
 *
 * @author not attributable
 * @version 1.0
 */

public class Genome {

    public static int num = 4;
    public static String[] fullName = {"human (GoldenPath)", "mouse (GoldenPath)", "rat (GoldenPath)", "chimpanzee (GoldenPath)"};

    public static String[] shortName = {"hg16", "mm6", "rn3", "panTro1"};

    public static int[] numConventionalChromosomes = {22, // hg16
                                                      19, // mm4
                                                      20, // rn3
                                                      23 // panTro1
    };

    public static int[] numNonConventionalChromosomes = {2, // hg16
                                                         2, // mm4
                                                         2, // rn3
                                                         2 // panTro1
    };

    public static String[][] nonConventionalChromNames = {{"chrX", "chrY"}, {// hg16
        "chrX", "chrY"}, {// mm4
            "chrX", "chrY"}, {// rn3
                "chrX", "chrY"} // panTro1
    };

    public static int[][] chromSizes = {{245522847, 243018229, 199505740, 191411218, 180857866, 170975699, 158628139, 146274826, 138429268, 135413628, 134452384, 132449811, 114142980, 106368585, 100338915, 88827254, 78774742, 76117153, 63811651, 62435964, 46944323, 49554710, 154824264, 57701691} // hg16
                                        , {195203927, 181686250, 160575607, 154141344, 149219885, 149721531, 133051633, 128688707, 124177049, 130633972, 121648857, 115071072, 116458020, 117079080, 104138553, 98801893, 93559791, 91083707, 60688862, 160634946, 47900188} // mm4
                                        , {268121971, 258222147, 170969371, 187371129, 173106704, 147642806, 143082968, 129061546, 113649943, 110733352, 87800381, 111348958, 46649226, 109774626, 90224819, 97307196, 87338544, 59223525, 55296979, 160775580, 1} // rn34
                                        , {229575298, 203813066, 209662276, 188378868, 175429504, 161576975, 149542033, 138322177, 136640551, 135301796, 123086034, 117159028, 134309081, 97804244, 106954593, 101535987, 73346066, 83875239, 82489036, 61571712, 65473740, 47338174, 50034486, 160174553, 50597644} // panTro1
    };

    public static int getGenomeNum(String nm) {
        int i;

        for (i = 0; i < num; i++) {
            if (nm.equalsIgnoreCase(shortName[i])) {
                return i;
            }
        }
        return -1;
    }

    public static int getChrNum(String genome_name) {
        int n = 0, i = 0;

        i = getGenomeNum(genome_name);
        if (i == -1)
            return 0;
        n = numConventionalChromosomes[i] + numNonConventionalChromosomes[i];
        return n;
    }

    public static String getChrName(String genome_name, int n) {
        int m, g;
        String ret = null;

        m = getChrNum(genome_name);
        g = getGenomeNum(genome_name);
        if (n < numConventionalChromosomes[g]) {
            // It's a conventional chromosome
            ret = new String("chr" + (n + 1));
        } else {
            if (n >= numConventionalChromosomes[g] && n <= numConventionalChromosomes[g] + numNonConventionalChromosomes[g]) {
                ret = new String(nonConventionalChromNames[g][n - numConventionalChromosomes[g]]);
            }
        }
        return ret;
    }
}
