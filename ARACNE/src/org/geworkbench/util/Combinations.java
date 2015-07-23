package org.geworkbench.util;

/*

Copyright @ 1999-2003, The Institute for Genomic Research (TIGR).

All rights reserved.

*/

/*

 * $RCSfile: Combinations.java,v $

 * $Revision: 1.1.1.1 $

 * $Date: 2005/07/28 22:36:27 $

 * $Author: watkin $

 * $State: Exp $

 */

public class Combinations {

    /*-------------------------------------------------------------------

    Adapted from: http://www.brent.worden.org/algorithm/combinatorics/enumerateCombinations.html



     Description:

       Enumerates all possible combinations of choosing k objects from n

       distint objects.  Initialize the enumeration by setting j[0] to a

       negative value.  Then, each call to enumerateCombinations will

       generate the next combinations and place it in j[0..k-1].  A

       return value of false indicates there are no more combinations to

       generate.  j needs to be allocated with a size for at least k

       elements.



     Author:

       Brent Worden



     Language:

       C++



     Usage:

       int comb[10] = {-1};

       while(enumerateCombinations(10, 5, comb)){

           // do something with comb[0..4]

       }



     Reference:

       Tucker, Allen.  Applied Combinatorics.  3rd Ed.  1994.

    -------------------------------------------------------------------*/

    public static boolean enumerateCombinations(int n, int k, int j[]) {
        int i;
        if (j[0] < 0) {
            for (i = 0; i < k; ++i) {
                j[i] = i;
            }
            return true;
        } else {
            for (i = k - 1; i >= 0 && j[i] >= n - k + i; --i) {
            }
            if (i >= 0) {
                j[i]++;
                int m;
                for (m = i + 1; m < k; ++m) {
                    j[m] = j[m - 1] + 1;
                }
                return true;
            } else {
                return false;
            }
        }
    }

    public static void main(String[] args) {
        int[] comb = new int[5];
        for (int i = 0; i < comb.length; i++) {
            comb[i] = -1;
        }
        while (enumerateCombinations(10, 5, comb)) {
            for (int i = 0; i < comb.length; i++) {
                System.out.print("" + comb[i] + " ");
            }
            System.out.println();
        }
    }
}
