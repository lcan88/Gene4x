package org.geworkbench.builtin.projects;

import javax.swing.*;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * <p/>
 * <code>ImageNode</code> node which represents a 'Image' node in the Project
 * panel component
 *
 * @author First Genetic Trust
 * @version 1.0
 */
//ProjectTreeNode
public class ImageNode extends  DataSetSubNode {
    /**
     * <code>ImageIcon</code> that this <code>ImageNode</code> refers to
     */
    public ImageIcon image;

    /**
     * Constructor
     *
     * @param im <code>ImageIcon</code> that this <code>ImageNode</code> refers to
     */
    public ImageNode(ImageIcon im) {
        super(null);
                image = im;
        super.setUserObject(image.getDescription());
        ImageData node = new ImageData(null);
        node.setImageIcon(image);
        node.addDescription(image.getDescription());
        _aDataSet = node;


    }

}
