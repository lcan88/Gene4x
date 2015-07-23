package org.geworkbench.util;

import javax.swing.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description:used to check input with a regular expression </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Califano Lab</p>
 *
 * @author Xuegong Wang
 * @version 1.0
 */

public class RegularExpressionVerifier extends InputVerifier {
    //TEXT_FIELD =  "^(\\S)(.){1,75}(\\S)$";
    //NON_NEGATIVE_INTEGER_FIELD = "(\\d){1,9}";
    //INTEGER_FIELD = "(-)?" + NON_NEGATIVE_INTEGER_FIELD;
    //NON_NEGATIVE_FLOATING_POINT_FIELD ="(\\d){1,10}(.)?(\\d){1,10}";
    //FLOATING_POINT_FIELD =  "(-)?" +NON_NEGATIVE_FLOATING_POINT_FIELD;
    //NON_NEGATIVE_MONEY_FIELD =  "(\\d){1,15}(\\.(\\d){2})?";
    //MONEY_FIELD =  "(-)?" + NON_NEGATIVE_MONEY_FIELD;
    Pattern p = null;

    public RegularExpressionVerifier(String regexp) {
        p = Pattern.compile(regexp);
    }

    public boolean shouldYieldFocus(JComponent input) {
        if (verify(input)) {
            return true;
        }
        // According to the documentation, should yield focus is allowed to cause
        // side effects.  So temporarily remove the input verifier on the text
        // field.
        input.setInputVerifier(null);
        // Pop up the message dialog.
        String message = "Data input is not valid, please check and input correct data ";
        JOptionPane.showMessageDialog(null, message, "Invalid value", JOptionPane.WARNING_MESSAGE);

        // Reinstall the input verifier.
        input.setInputVerifier(this);
        // Tell whomever called us that we don't want to yield focus.
        return false;
    }//endof shouldyieldfocus()

    public boolean verify(JComponent input) {
        JTextField tf = (JTextField) input;
        Matcher m = p.matcher(tf.getText());
        boolean match = m.matches();
        //if (!match) {
        //JOptionPane.showMessageDialog(null, "Eggs aren't supposed to be green.");
        //}
        return match;
    }//endof verify()


}
