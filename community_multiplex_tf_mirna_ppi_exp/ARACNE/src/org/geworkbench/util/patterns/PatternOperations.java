package org.geworkbench.util.patterns;

import org.geworkbench.bison.datastructure.biocollections.sequences.DSSequenceSet;
import org.geworkbench.bison.datastructure.complex.pattern.sequence.DSMatchedSeqPattern;
import polgara.soapPD_wsdl.SOAPOffset;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class PatternOperations {
    static private ArrayList<Color> colors = makeColors();
    //{Color.red, Color.blue, Color.cyan, Color.black, Color.green,
    //Color.magenta, Color.orange, Color.pink, Color.yellow};
    static private ArrayList<Color> store = new ArrayList<Color>();
    static private HashMap<Integer, Color> colorPatternMap = new HashMap<Integer, Color>();

    static ArrayList<Color> makeColors() {
        Color[] c = {new Color(0, 0, 255), new Color(138, 43, 226), new Color(165, 42, 42), new Color(222, 184, 135), new Color(95, 158, 160), new Color(127, 255, 0), new Color(210, 105, 30), new Color(255, 127, 80), new Color(100, 149, 237), new Color(220, 20, 60), new Color(0, 255, 255), new Color(0, 0, 139), new Color(184, 134, 11), new Color(238, 130, 238), new Color(0, 100, 0), new Color(189, 183, 107), new Color(139, 0, 139), new Color(85, 107, 47), new Color(255, 140, 0), new Color(153, 50, 204), new Color(139, 0, 0), new Color(233, 150, 122), new Color(255, 255, 0), new Color(255, 0, 0), new Color(0, 0, 0), new Color(128, 0, 128)};
        ArrayList<Color> list = new ArrayList<Color>();
        for (int i = 0; i < c.length; i++) {
            list.add(c[i]);
        }
        return list;
    }


    static public Color getPatternColor(int i) {
        if (colors.isEmpty()) {
            colors.addAll(store);
            store.clear();
        }

        Color c = colorPatternMap.get(i);
        if (c != null) {
            return c;
        }
        int index = Math.abs(i) % colors.size();
        c = colors.remove(index);
        store.add(c);
        colorPatternMap.put(new Integer(i), c);
        return c;
    }

    static public void fill(DSMatchedSeqPattern pattern, DSSequenceSet sDB) {
        if (pattern.getClass().isAssignableFrom(org.geworkbench.util.patterns.CSMatchedSeqPattern.class)) {
            CSMatchedSeqPattern p = (CSMatchedSeqPattern) pattern;
            String ascii = new String();
            SOAPOffset[] offsets = p.offset.value;
            int j = offsets[0].getDx();
            for (int i = 0; i < offsets.length; i++, j++) {
                int dx = offsets[i].getDx();
                for (; j < dx; j++) {
                    ascii += '.';
                }
                String tokString = offsets[i].getToken();
                if (tokString.length() > 1) {
                    ascii += '[' + tokString + ']';
                } else {
                    ascii += tokString;
                }
            }
            p.ascii = ascii;
        }
    }
    /*
       public int intersection(Pattern p0, Pattern p1) {
      // creates the intersection of two patterns
      int prevId = -1;
      Locus[] locus = new Locus[p0.locus.value.length];
      int i = 0;
      int j = 0;
      while((i < p0.locus.value.length) && (j < p1.locus.value.length)){
        SOAPLocus l_0 = p0.locus.value[i];
        SOAPLocus l_1 = p1.locus.value[j];
        if(l_0.getId() > l1.getId()) {
          j++;
        } else if (l_0.getId() < prevId) {
          i++;
        } else {
          locus[j] = locus.value[i_0++].getId();
          i_0++;
        }
      }
      int[] finalSupport = new int[j];
      for(int i = 0; i < j; i++) finalSupport[i] = support[i];
      return finalSupport;
       }
     */
}
