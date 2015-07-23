package org.geworkbench.builtin.projects;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.Serializable;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * <p/>
 * <code>ProjectTree</code> node which represents a generic node in the Project
 * panel component
 *
 * @author First Genetic Trust
 * @version 1.0
 */
public class ProjectTreeNode extends DefaultMutableTreeNode implements Serializable {

    private String description = "";

    /**
     * Default Constructor
     */
    public ProjectTreeNode() {
    }

    /**
     * Constructor
     *
     * @param nodeName <code>Object</code> to be set as User Object
     */
    public ProjectTreeNode(Object nodeName) {
        setUserObject(nodeName);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}