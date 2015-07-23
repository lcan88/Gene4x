package org.geworkbench.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Displays a splash screen containing one bitmap file and an optional progress bar.
 * The splash screen can be selected to be hidden after a certain amount of time, when
 * the user clicks it or by calling a method.<br>
 * The bitmap used is loaded from a file. Valid file formats are GIF, JPEG and PNG.<br>
 * The progress bar can be determinate or indeterminate. When determinate, it completes
 * when the set timeout has passed.<br>
 * Everything including showing, configuration, updating and hiding the splash screen
 * can also be done manually.<br>
 * <br>
 * Example #1:<br>
 * <br><code>
 * // specify a bitmap file<br>
 * SplashBitmap splash = new SplashBitmap(pathToFileOnFilesystem);<br>
 * <br>
 * // make it close when clicked<br>
 * splash.hideOnClick();<br>
 * <br>
 * // show the whole thing<br>
 * splash.showSplash();<br>
 * </code><br><br>
 * Example #2:<br>
 * <br><code>
 * // specify a bitmap file<br>
 * SplashBitmap splash = new SplashBitmap(new URL("http://some.host.and/file.png"));<br>
 * <br>
 * // close after 2 seconds<br>
 * splash.hideOnTimeout(2000);<br>
 * <br>
 * // interrupt the timeout and close when the splash screen is clicked<br>
 * splash.hideOnClick();<br>
 * <br>
 * // add a progress bar that completes within these 2 seconds<br>
 * splash.addAutoProgressBar();<br>
 * <br>
 * // display a string in the progress bar<br>
 * splash.setProgressBarString("loading...");<br>
 * <br>
 * // show the whole thing<br>
 * splash.showSplash();<br>
 * </code>
 *
 * @author Kai Blankenhorn &lt;<a href="mailto:pub01@bitfolge.de">pub01@bitfolge.de</a>&gt;
 */
public class SplashBitmap extends JWindow {

    private URL bitmapURL = null;
    private JLabel bitmapLabel;
    private Timer timeoutTimer = null;
    private Timer progressTimer = null;
    private int timeout = 0;
    protected JProgressBar bar = null;
    private int progressBarSmoothness = 100;

    /**
     * Constructs a new splash screen with the bitmap from the given URL. To display this splash screen,
     * use <code>showSplash()</code>.
     *
     * @param filename the URL of the image file to display
     */
    public SplashBitmap(URL filename) {
        this.bitmapURL = filename;
        this.init();
    }

    /**
     * Constructs a new splash screen with the bitmap from the given file name. To display this splash screen,
     * use <code>showSplash()</code>.
     *
     * @param filename the name of the image file to display
     */
    public SplashBitmap(String filename) {
        File bitmapFile = new File(filename);

        try {
            this.bitmapURL = bitmapFile.toURL();
        } catch (MalformedURLException ignore) {
        }

        this.init();
    }

    /**
     * Initializes the splash screen by constructing a JWindow and adding a JLabel containing the bitmap to it.
     * Sets the window position to the center of the screen.
     */
    private void init() {
        this.bitmapLabel = new JLabel();

        ImageIcon bitmapImage = new ImageIcon(Toolkit.getDefaultToolkit().createImage(this.bitmapURL));
        int height = bitmapImage.getIconHeight();
        int width = bitmapImage.getIconWidth();
        bitmapLabel.setIcon(bitmapImage);
        bitmapLabel.setSize(width, height);
        bitmapLabel.setLocation(0, 0);
        this.getContentPane().setLayout(null);
        this.getContentPane().add(bitmapLabel, null);
        this.setSize(width, height);
        this.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - width) / 2, (Toolkit.getDefaultToolkit().getScreenSize().height - height) / 2);
    }

    /**
     * Shows this splash screen. The window will be centered on the screen. Notice that all
     * timers will start when this method is called, not when they are set. The splash screen
     * will be centered on the screen.
     */
    public void showSplash() {
        if (timeoutTimer != null) {
            timeoutTimer.start();
        }

        if (progressTimer != null) {
            progressTimer.start();
        }

        this.setVisible(true);
    }

    /**
     * Hides this splash screen and stops all running timers.
     */
    public void hideSplash() {
        this.setVisible(false);

        if (timeoutTimer != null) {
            timeoutTimer.stop();
            timeoutTimer = null;
        }

        if (progressTimer != null) {
            progressTimer.stop();
            progressTimer = null;
        }
    }

    /**
     * Tells this splash screen to close after a certain time has passed.
     * A splash screen can also be set to be hidden on timeout <b>and<b> on
     * click at the same time, either of which occurs first.
     *
     * @param timeout the time this splash screen is to be displayed in milliseconds
     */
    public void hideOnTimeout(int timeout) {
        this.timeout = timeout;
        timeoutTimer = new Timer(timeout, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                hideSplash();
            }
        });
        timeoutTimer.setRepeats(false);
    }

    /**
     * Tells this splash screen to close when being clicked.
     * A splash screen can also be set to be hidden on timeout <b>and<b> on
     * click at the same time, either of which occurs first.
     */
    public void hideOnClick() {
        this.addMouseListener(new MouseAdapter() {

            /**
             * Hides the splash screen when the mouse is clicked. Used internally for on-click closing.
             *
             * @see java.awt.event.MouseListener#mouseClicked(MouseEvent)
             */
            public void mouseClicked(MouseEvent e) {
                hideSplash();
            }
        });
    }

    /**
     * Adds a JProgressBar to this splash screen. If another one has been added before,
     * it is replaced by the new one. The progress bar will be at the below the bitmap.
     * It does not yet have minimum or maximum values. These are set in one of the following methods.
     */
    private void addProgressBar() {
        if (bar != null) {
            this.remove(bar);
            this.setSize(this.getWidth(), this.getHeight() - 16);
        }

        progressTimer = null;
        bar = new JProgressBar();
        bar.setSize(this.getWidth(), 16);
        bar.setLocation(0, this.getHeight());
        this.setSize(this.getWidth(), this.getHeight() + 16);
        this.getContentPane().add(bar, null);
    }

    /**
     * Adds a JProgressBar to the splash screen that is automatically updated.
     * The progress bar is configured so that its progress is complete when
     * the timeout set by <code>hideOnTimeout(int)</code> has expired. Not specifying this timeout
     * will let the progress bar complete as fast as possible.<br>
     * Only one progress bar can be added to the splash screen at a time.
     * Adding a second one will overwrite the first one.
     *
     * @see #hideOnTimeout(int)
     */
    public void addAutoProgressBar() {
        this.addManualProgressBar(0, progressBarSmoothness);
        progressTimer = new Timer(timeout / progressBarSmoothness, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setProgressBarValue(getProgressBarValue() + 1);
            }
        });
    }

    /**
     * Adds a indeterminate JProgressBar to the splash screen that will be updated while
     * the splash screen is displayed.<br>
     * Only one progress bar can be added to the splash screen at a time.
     * Adding a second one will overwrite the first one.
     */
    public void addAutoProgressBarIndeterminate() {
        this.addProgressBar();
        bar.setIndeterminate(true);
    }

    /**
     * Adds a JProgressBar to the splash screen that can be updated manually.<br>
     * Only one progress bar can be added to the splash screen at a time.
     * Adding a second one will overwrite the first one.
     *
     * @param min the JProgressBar's minimum value
     * @param max the JProgressBar's maximum value
     * @see #setProgressBarValue(int)
     * @see #getProgressBarValue()
     */
    public void addManualProgressBar(int min, int max) {
        this.addProgressBar();
        bar.setMinimum(min);
        bar.setMaximum(max);
    }

    /**
     * Sets the value of the progress bar in this splash screen to a new value.
     *
     * @param value the new value of the progress bar
     */
    public void setProgressBarValue(int value) {
        if (bar != null) {
            bar.setValue(value);
        } else {
            throw new IllegalStateException("Add a progress bar before trying to change it.");
        }
    }

    /**
     * Gets the current value of the progress bar in this splash screen.
     *
     * @return the current value of the progress bar
     */
    public int getProgressBarValue() {
        if (bar != null) {

            return bar.getValue();
        } else {
            throw new IllegalStateException("Add a progress bar before trying to access it.");
        }
    }

    /**
     * Sets the string that is displayed in the progress bar. Setting it to an empty string
     * will hide it. Setting it to null will restore the default behaviour (display percent).
     *
     * @param text the string to be displayed in the progress bar
     */
    public void setProgressBarString(String text) {
        if (bar != null) {

            if (text != null) {
                bar.setStringPainted(!text.equals(""));
            }

            bar.setString(text);
        } else {
            throw new IllegalStateException("Add a progress bar before trying to change it.");
        }
    }

    /**
     * Displays the percentage of progress in the progress bar.
     */
    public void addProgressBarPercent() {
        if (bar != null) {
            bar.setString(null);
            bar.setStringPainted(true);
        } else {
            throw new IllegalStateException("Add a progress bar before trying to change it.");
        }
    }
}
