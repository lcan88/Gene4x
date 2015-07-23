package org.geworkbench.bison.util;

import java.util.ArrayList;

/**
 * @author John Watkinson
 */
public class StringUtils {

    /**
     * Performs a normal String.split(), but removes empty strings.
     */
    public static String[] splitRemovingEmptyStrings(String s, String regex) {
        String[] tokens = s.split(regex);
        ArrayList<String> list = new ArrayList<String>();
        for (String token : tokens) {
            if (token.length() > 0) {
                list.add(token);
            }
        }
        return list.toArray(new String[0]);
    }

    /**
     * Filters the string by removing those patterns that match the regex.
     */
    public static String filter(String s, String regex) {
        String[] tokens = s.split(regex);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < tokens.length; i++) {
            sb.append(tokens[i]);
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        String test = ">one<two<A>three><>four<five/>";
        System.out.println("Result: " + filter(test, "(<.*?>)|[<>]"));
    }

}
