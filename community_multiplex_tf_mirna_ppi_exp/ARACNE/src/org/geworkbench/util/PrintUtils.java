package org.geworkbench.util;

import javax.swing.*;
import java.awt.*;
import java.awt.print.*;

/**
 * Utility that prints an AWT component.
 *
 * @author John Watkinson
 * @see #printComponent(java.awt.Component)
 */
public class PrintUtils implements Pageable, Printable {

    private Component componentToBePrinted;
    private PrinterJob printJob;
    private PageFormat pageFormat;

    /**
     * Prints a component after first presenting a page orientation dialog, and then a printer selection dialog.
     * Scales the drawing of the component to fit on the page.
     *
     * @param componentToPrint the component to print.
     */
    public static void printComponent(Component componentToPrint) {
        new PrintUtils(componentToPrint).print();
    }

    public PrintUtils(Component componentToBePrinted) {
        this.componentToBePrinted = componentToBePrinted;
    }

    public void print() {
        printJob = PrinterJob.getPrinterJob();
        printJob.setPageable(this);
        pageFormat = printJob.pageDialog(printJob.defaultPage());
        if (printJob.printDialog())
            try {
                printJob.print();
            } catch (PrinterException pe) {
                System.out.println("Error printing: " + pe);
            }
    }


    public int getNumberOfPages() {
        return 1;
    }

    public PageFormat getPageFormat(int pageIndex) throws IndexOutOfBoundsException {
        return pageFormat;
    }

    public Printable getPrintable(int pageIndex) throws IndexOutOfBoundsException {
        return this;
    }

    public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
        if (pageIndex > 0) {
            return (NO_SUCH_PAGE);
        } else {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            // Compute scale for component painting
            Rectangle bounds = componentToBePrinted.getBounds();
            double widthFactor = pageFormat.getImageableWidth() / (bounds.width - bounds.x);
            double heightFactor = pageFormat.getImageableHeight() / (bounds.height - bounds.y);
            if (widthFactor < heightFactor) {
                g2d.scale(widthFactor, widthFactor);
            } else {
                g2d.scale(heightFactor, heightFactor);
            }
            // Double-buffered components wreak havoc on the print system.
            disableDoubleBuffering(componentToBePrinted);
            componentToBePrinted.paint(g2d);
            enableDoubleBuffering(componentToBePrinted);
            return (PAGE_EXISTS);
        }
    }

    public static void disableDoubleBuffering(Component c) {
        RepaintManager currentManager = RepaintManager.currentManager(c);
        currentManager.setDoubleBufferingEnabled(false);
    }

    public static void enableDoubleBuffering(Component c) {
        RepaintManager currentManager = RepaintManager.currentManager(c);
        currentManager.setDoubleBufferingEnabled(true);
    }
}
