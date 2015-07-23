package org.geworkbench.bison.model.analysis;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * <p/>
 * Generic wrapper for encapsulating Analyses algorithm results. The data is
 * interpreted by components having knowledge of the manner of result set
 * construction.
 *
 * @author First Genetic Trust
 * @version 1.0
 */
public class AlgorithmExecutionResults {
    /**
     * Indicates if the algorithm execution was successful or if there were
     * any problems encountered.
     */
    private boolean executionSuccessful = true;
    /**
     * Optional message (expected to be generated if
     * <code>executionSuccessful == false</code>). Can be displayed as error
     * message.
     */
    private String errorMessage = "No Error";
    /**
     * The results of the algorithm execution. This will be interpreted by the
     * caller of the algorithm execution.
     */
    private Object results = null;

    public AlgorithmExecutionResults(boolean success, String mesg, Object res) {
        executionSuccessful = success;
        errorMessage = mesg;
        results = res;
    }

    /**
     * Gets the status of the algorithn execution
     *
     * @return success or failure
     */
    public boolean isExecutionSuccessful() {
        return executionSuccessful;
    }

    /**
     * Gets any error message provided by the algorithm execution
     *
     * @return error message
     */
    public String getMessage() {
        return errorMessage;
    }

    /**
     * The <code>Object</code> containing the results of the algorithm execution
     *
     * @return results
     */
    public Object getResults() {
        return results;
    }

}

