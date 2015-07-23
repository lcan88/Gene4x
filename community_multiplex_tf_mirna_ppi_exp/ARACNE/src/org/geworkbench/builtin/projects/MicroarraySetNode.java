package org.geworkbench.builtin.projects;

import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;

import java.io.*;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * @author First Genetic Trust
 * @version 1.0
 */

/**
 * The application project panel component makes use of the project tree,
 * a <code>JTree</code> object whose nodes correspond to application objects
 * like projects, images, microarray sets, etc.
 * <p/>
 * This class defines the project tree node used for storing microarray sets.
 */
public class MicroarraySetNode extends ProjectTreeNode {
    /**
     * The <code>MicroarraySet</code> object associated with this project
     * node.
     */
    private DSMicroarraySet microarraySet = null;
    /**
     * Stores the name of the file where the <code>microarraySet</code> gets
     * persisted when the current node becomes unselected.
     */
    private File maSetPersistFile = null;

    public MicroarraySetNode(DSMicroarraySet mArraySet) {
        microarraySet = mArraySet;
        if (microarraySet != null)
            setUserObject(microarraySet.getLabel());
        createTempFile();
        // Persists the microrray set stored in this node. The application
        // uses such persisting as a memory saving device: if a microarray
        // set is not currently selected it is stored away. It is later
        // recovered when its relevant <code>MicroarraySetNode</code> node
        // is selected in the project tree.
        this.persist();
    }

    /**
     * Serializes the microarray set stored in this node.
     */
    public void persist() {
        if (microarraySet != null && maSetPersistFile != null) {
            try {
                FileOutputStream fo = new FileOutputStream(maSetPersistFile);
                ObjectOutputStream so = new ObjectOutputStream(fo);
                so.writeObject(microarraySet);
                so.flush();
                so.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    /**
     * Releases the memory used by the microarray set stored in this node.
     * Such memory savings can be useful when many large microarray sets
     * are concurrently open within the application.
     */
    public void releaseSpace() {
        microarraySet = null;
    }

    /**
     * @return The <code>MicroarraySet</code> associated with this project node.
     *         If the <code>MicroarraySet</code> exists at a serialized state,
     *         it is first recovered.
     */
    public DSMicroarraySet getMicroarraySet() {
        if (microarraySet == null && maSetPersistFile != null) {
            try {
                FileInputStream fi = new FileInputStream(maSetPersistFile);
                ObjectInputStream si = new ObjectInputStream(fi);
                microarraySet = (DSMicroarraySet) si.readObject();
                si.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return microarraySet;
    }

    /**
     * Create the temp file where the microarray set represented by this
     * node will be saved in.
     */
    private void createTempFile() {
        String persistFileName = microarraySet.getID() + microarraySet.getLabel() + System.currentTimeMillis();
        String tempFileDirectory = System.getProperty("temporary.files.directory");
        // If the designated temp directory does not exist, save the temp files
        // at the same directory where the applcation started from.
        if (tempFileDirectory != null && (new File(tempFileDirectory)).exists())
            persistFileName = tempFileDirectory + persistFileName;
        maSetPersistFile = new File(persistFileName);
        try {
            maSetPersistFile.createNewFile();
            maSetPersistFile.deleteOnExit(); // Clean the file upon exit.
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        microarraySet = this.getMicroarraySet();
        oos.writeObject(microarraySet);
    }

    private void readObject(ObjectInputStream oos) throws IOException, ClassNotFoundException {
        microarraySet = (DSMicroarraySet) oos.readObject();
        if (microarraySet != null) {
            createTempFile();
            persist();
            releaseSpace();
        }

    }

}
