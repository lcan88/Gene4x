package org.geworkbench.util;

import javax.media.jai.InterpolationNearest;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;
import java.awt.*;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 *
 * @author First Genetic Trust Inc.
 * @version 1.0
 */
public class SaveImage {
    private Image imageToBeSaved = null;

    public SaveImage() {
    }

    public SaveImage(Image image) {
        imageToBeSaved = image;
    }

    public void setImage(Image image) {
        imageToBeSaved = image;
    }

    public void save(String filename, String ext) {
        ParameterBlock pb = new ParameterBlock();
        pb.add(imageToBeSaved);
        RenderedOp op0 = JAI.create("awtImage", pb);
        pb.removeParameters();
        pb.removeSources();
        pb.addSource(op0);
        pb.add(1.0F);
        pb.add(1.0F);
        pb.add(0.0F);
        pb.add(0.0F);
        pb.add(new InterpolationNearest());
        if (!filename.endsWith(ext))
            filename += "." + ext;
        RenderedOp op1 = JAI.create("scale", pb, null);
        RenderedOp op = null;
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(new File(filename));
        } catch (IOException ioe) {
        }

        if (ext.equals("bmp")) {
            JAI.create("encode", op1, stream, "BMP", null);
        } else if (ext.equals("png")) {
            JAI.create("encode", op1, stream, "PNG", null);
        } else if (ext.equals("jpg") || ext.equals("JPEG")) {
            JAI.create("encode", op1, stream, "JPEG", null);
        } else if (ext.equals("tif") || ext.equals("TIFF")) {
            JAI.create("encode", op1, stream, "TIFF", null);
        }

        if (stream != null) {
            try {
                stream.flush();
                stream.close();
            } catch (IOException ioe) {
            }

        }

    }

}

