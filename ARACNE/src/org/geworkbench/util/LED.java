/*
 * $Id: LED.java,v 1.10 2000/09/16 14:01:49 roirex Exp $
 *
 * Copyright (c) 1999 Per Jensen.
 * <pj@image.dk>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * If you modify this file, please send us a copy.
 */

package org.geworkbench.util;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

/**
 *	Synopsis:	LED button will flash in different colors at
 *                      specified intervals<br>
 *	License:	GNU GPL2, look for details in file  "COPYING"<br><br>
 *
 *
 * 	@author	   	Per Jensen, pj@image.dk
 */
public class LED extends JComponent implements Runnable {
    private Dimension dim;
    private Color c, c1, c2;
    private Insets insets;
    private int interv, curWidth, curHeight;
    /** If LED is resized, set calculatePaintArea to true in order to update */
    public boolean calculatePaintArea = true;
    private boolean show = false;
    private boolean goOn = true;
    private boolean enabled = true;
    private boolean blink = false;
    private Thread t;
    private Color deeperGreen = new Color(0, 225, 25);
    
    /**
     * Create a LED which switches between red and gray.<BR>
     * Switch period = 1000 milli seconds. Color is set to gray
     *
     * @param		None
     *
     * @exception	None
     */
    
    public LED() {
        this(Color.red, new Color(0, 225, 25), 500);
    }
    
    /**
     * Create a LED which switches between primary and secondary color.<BR>
     * Switch interval is in milli seconds. Color is initated to the
     * secondary color
     *
     * @param		primary
     * @param		secondary
     * @param		interval interval in milliseconds between color switch, no less than 200
     *
     * @exception	None
     */
    public LED(Color primary, Color secondary, int interval) {
        c1 = primary;
        c2 = secondary;
        blink = true;
        setPreferredSize(new Dimension(15,15));
        
        c = c2;
        repaint();
        
        if ( interval > 200 )
            interv = interval;
        else
            interv = 200;
        t = new Thread(this);
        
    }
    
    
    /**
     * Create a LED which doesn't blink.<BR>
     *
     * @param		primary
     * @param		secondary
     *
     * @exception	None
     */
    public LED(Color primary, Color secondary) {
        super();
        c1 = primary;
        c2 = secondary;
        c = c2;
        blink = false;
        interv = 100000;
        setPreferredSize(new Dimension(15,15));
        repaint();
        t = new Thread(this);
    }
    
    public void usePrimary() {
        c = c1;
        repaint();
    }
    
    
    public void useSecondary() {
        c = c2;
        repaint();
    }
    
    protected void paintComponent(Graphics g) {
        if ( calculatePaintArea ) {
            insets = getInsets();
            curWidth = getWidth() - insets.left - insets.right-1;
            curHeight = getHeight() - insets.top - insets.bottom-1;
            calculatePaintArea = false;
        }
        super.paintComponent(g);
        g.setColor(c);
        g.fillRect(insets.left,insets.top,curWidth,curHeight);
    }
    
    /**
     * Thread switches between the specified colors. Sleeps between switches.
     * Switch interval is in milli seconds. If a stop command has been issued
     * ealier, the LED will turn gray.
     *
     * @param           none
     *
     * @exception	None
     */
    public void run() {
        try {
            while ( true ){
                if ( !goOn ) {
                    c = deeperGreen;
                    repaint();
                    break;
                }
                show = ! show;
                if ( show )
                    c = c1;
                else
                    c = c2;
                repaint();
                t.sleep(interv);
            }
        } catch ( InterruptedException ie ) {
            ie.printStackTrace();
        }
    }
    
    /**
     * Stop switching between two colors. Thread will die in max interval milliseconds
     *
     * @param           none
     *
     * @exception	None
     */
    
    public void stop() {
        goOn = false;
    }
    
    /**
     * Start switching between two colors.
     *
     * @param           none
     *
     * @exception	None
     */
    public void start() {
        // don't make a new thread, if we have one already
        if ( ! t.isAlive() ) {
            goOn = true;
            t = new Thread(this, "LED");
            t.start();
        } else {
            goOn = true;
        }
    }    
}
