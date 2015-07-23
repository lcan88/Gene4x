package org.geworkbench.bison.model.analysis;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * @author First Genetic Trust
 * @version 1.0
 */

/**
 * Return value for calls to <code>Analysis.validateParameters()</code>.
 * Encapsulates the results of validating the parameter settings for an
 * <code>Analysis</code> and provides for an (optional)
 * error message in case that the validation fails.
 */
public class ParamValidationResults {
    /**
     * The results of a parameter settings validation.
     */
    private boolean validationResult = true;
    /**
     * Error message (optional) that can be used for displaying to a
     * user.
     */
    private String errorMessage = "No Error";

    /**
     * Constructor.
     *
     * @param res
     * @param mesg
     */
    public ParamValidationResults(boolean res, String mesg) {
        validationResult = res;
        errorMessage = mesg;
    }

    /**
     * Return the parameter validation outcome.
     *
     * @return
     */
    public boolean isValid() {
        return validationResult;
    }

    /**
     * Get the error message provided (if any).
     *
     * @return
     */
    public String getMessage() {
        return errorMessage;
    }

}