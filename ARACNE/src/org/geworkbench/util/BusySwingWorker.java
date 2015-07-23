package org.geworkbench.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geworkbench.builtin.projects.Icons;

import javax.swing.*;
import java.awt.*;

/**
 * An extension of the SwingWorker class that knows how to indicate on a panel that it's busy.
 * User: mhall
 * Date: Nov 17, 2005
 * Time: 12:20:09 PM
 */
public abstract class BusySwingWorker extends SwingWorker {

    static Log log = LogFactory.getLog(BusySwingWorker.class);

    JPanel busyContainer = new JPanel();
    JProgress progress = new JProgress();
    boolean showProgress = false;

    protected void setBusy(JPanel panel) {
        panel.setBackground(Color.white);
        busyContainer.setLayout(new BoxLayout(busyContainer, BoxLayout.PAGE_AXIS));
        busyContainer.setBackground(Color.white);
        busyContainer.setMaximumSize(new Dimension(Icons.BUSY_ICON.getIconWidth(), Icons.BUSY_ICON.getIconHeight()));
        busyContainer.add(Box.createVerticalGlue());
//        busyContainer = new JPanel();
        JLabel busyGraphic = new JLabel(Icons.BUSY_ICON);
        busyGraphic.setAlignmentX(Component.CENTER_ALIGNMENT);
        busyContainer.add(busyGraphic);
        if (showProgress) {
            progress.setMaximumSize(new Dimension(Icons.BUSY_ICON.getIconWidth(), 10));
            progress.setAlignmentX(Component.CENTER_ALIGNMENT);
            busyContainer.add(progress);
            progress.setBackground(Color.white);
//            progress.getGraphics().setColor(Color.white);
//            progress.getGraphics().fillRect(0,0,progress.getWidth(), progress.getHeight());
//            progress.getGraphics().setColor(Color.black);
//            progress.getGraphics().drawRect(1, 1, progress.getWidth()-1, progress.getHeight()-1);
            log.debug("Progress indicator added.");
        }
        busyContainer.setPreferredSize(new Dimension(Icons.BUSY_ICON.getIconWidth(), Icons.BUSY_ICON.getIconHeight() + 10));
        busyContainer.add(Box.createVerticalGlue());
        busyContainer.revalidate();
        busyContainer.repaint();
        panel.add(busyContainer, BorderLayout.CENTER);
        panel.revalidate();
        panel.repaint();
    }

    public boolean isShowProgress() {
        return showProgress;
    }

    public void setShowProgress(boolean showProgress) {
        this.showProgress = showProgress;
    }

    public int getProgressMax() {
        return progress.getMaxProgress();
    }

    public void setProgressMax(int progressMax) {
        progress.setMaxProgress(progressMax);
    }

    public int getCurrentProgress() {
        return progress.getProgress();
    }

    public void setCurrentProgress(int newProgress) {
        progress.setProgress(newProgress);
        progress.repaint();
    }

    private class JProgress extends JPanel {
        int progress = 0, maxProgress = 0;

        protected void paintComponent(Graphics g) {
//            log.debug("Painting at "+progress);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.white);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.setColor(new Color(96, 96, 96));
            g2d.fillRoundRect(1, 1, (int) ((progress / (float) maxProgress) * (getWidth() - 1)), getHeight() - 1, 2, 2);
        }

        public int getProgress() {
            return progress;
        }

        public void setProgress(int progress) {
            this.progress = progress;
        }

        public int getMaxProgress() {
            return maxProgress;
        }

        public void setMaxProgress(int maxProgress) {
            this.maxProgress = maxProgress;
        }
    }
}
