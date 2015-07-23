package org.geworkbench.analysis;

import org.geworkbench.bison.model.analysis.ParamValidationResults;
import org.geworkbench.bison.model.analysis.ParameterPanel;

import java.io.IOException;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * @author First Genetic Trust Inc.
 * @version 1.0
 */

/**
 * This class is used to
 * (1) store the parameters of an analysis, and
 * (2) manage the visual representation (gui) that will be
 * offered to the user in order to provide values for the parameters.
 * This abstract class provides default implementations for a number of tasks,
 * including:
 * <UL>
 * <LI>Assigning a name to a set of parameter values:
 * per the relevant use case, the settings used in performing an analysis
 * can be stored under user-specified name and later potentially retrieved
 * to be reused in another application of that same analysis.</LI>
 * </UL>
 */
public class AbstractSaveableParameterPanel extends ParameterPanel {
    String name = null;

    public AbstractSaveableParameterPanel() {
        setName("Parameters");
    }

    public String getName() {
        assert name != null;
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Return the result of validating the values of the parameters managed
     * by this panel. This method will have to be overriden by classes that
     * extend <code>AbstractSaveableParameterPanel</code>.
     *
     * @return
     */
    public ParamValidationResults validateParameters() {
        return new ParamValidationResults(true, "No Error");
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeObject(name);
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        name = (String) in.readObject();
    }

}

