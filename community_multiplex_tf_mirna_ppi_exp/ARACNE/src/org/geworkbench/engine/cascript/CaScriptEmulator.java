package org.geworkbench.engine.cascript;

import org.apache.commons.digester.Digester;
import org.geworkbench.engine.config.PluginRegistry;
import org.geworkbench.engine.config.UILauncher;
import org.geworkbench.engine.config.rules.GeawConfigRule;
import org.geworkbench.engine.config.rules.PluginRule;
import org.geworkbench.util.Debug;
import org.geworkbench.util.SplashBitmap;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * <p>Title: caWorkbench</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence
 * and Genotype Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author not attributable
 * @version 3.0
 */

public class CaScriptEmulator {

    /**
     * The name of the string in the <code>application.properties</code> file
     * that contains the location of the application configuration file.
     */
    final static String CONFIG_FILE_NAME = "component.configuration.file";
    // ---------------------------------------------------------------------------
    // --------------- Instance variables
    // ---------------------------------------------------------------------------
    public static Digester uiLauncher;

    static {
        try {
            uiLauncher = new Digester(new org.apache.xerces.parsers.SAXParser());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------------------------------------------------------------------------
    // --------------- Methods
    // ---------------------------------------------------------------------------

    public static SplashBitmap splash = null;

    static {
        splash = new SplashBitmap(SplashBitmap.class.getResource("splashscreen.png"));
    }

    /**
     * Configure the rules for translating the appication configuration file.
     */
    static void configure() {
        uiLauncher.setUseContextClassLoader(true);
        // Opening tag <geaw-config>
        uiLauncher.addRule("geaw-config", new GeawConfigRule("core.config.rules.GeawConfigObject"));
        // Creates the top-level GUI window
        uiLauncher.addObjectCreate("geaw-config/gui-window", "core.config.rules.GUIWindowObject");
        uiLauncher.addCallMethod("geaw-config/gui-window", "createGUI", 1);
        uiLauncher.addCallParam("geaw-config/gui-window", 0, "class");
        // Instantiates a plugin and adds it in the PluginResgistry
        uiLauncher.addRule("geaw-config/plugin", new PluginRule("core.config.rules.PluginObject"));
        // Registers a plugin with an extension point
        uiLauncher.addCallMethod("geaw-config/plugin/extension-point", "addExtensionPoint", 1);
        uiLauncher.addCallParam("geaw-config/plugin/extension-point", 0, "name");
        // Registers a visual plugin with the top-level application GUI.
        uiLauncher.addCallMethod("geaw-config/plugin/gui-area", "addGUIComponent", 1);
        uiLauncher.addCallParam("geaw-config/plugin/gui-area", 0, "name");
        // Registers the broadcast listeners for a plugin.
        //    uiLauncher.addCallMethod("geaw-config/plugin/broadcast-event",
        //                             "registerBroadcastListener", 1);
        //    uiLauncher.addCallParam("geaw-config/plugin/broadcast-event", 0, "event");
        // Sets up a coupled listener relationship involving 2 plugins.
        //    uiLauncher.addCallMethod("geaw-config/plugin/coupled-event",
        //                             "registerCoupledListener", 2);
        //    uiLauncher.addCallParam("geaw-config/plugin/coupled-event", 0, "event");
        //    uiLauncher.addCallParam("geaw-config/plugin/coupled-event", 1, "source");
        //    // Register the menu item listeners.
        uiLauncher.addCallMethod("geaw-config/plugin/menu-item", "registerMenuItem", 5);
        uiLauncher.addCallParam("geaw-config/plugin/menu-item", 0, "path");
        uiLauncher.addCallParam("geaw-config/plugin/menu-item", 1, "mode");
        uiLauncher.addCallParam("geaw-config/plugin/menu-item", 2, "var");
        uiLauncher.addCallParam("geaw-config/plugin/menu-item", 3, "icon");
        uiLauncher.addCallParam("geaw-config/plugin/menu-item", 4, "accelerator");
        // Load the online help files for the various components.
        uiLauncher.addCallMethod("geaw-config/plugin/online-help", "registerHelpTopic", 1);
        uiLauncher.addCallParam("geaw-config/plugin/online-help", 0, "helpSet");
    }

    /**
     * Reads application properties from a file called
     * <bold>application.properties</bold>
     */
    static void initProperties() {
        InputStream reader = null;
        try {
            reader = UILauncher.class.
                    getResourceAsStream("/application.properties");
            System.getProperties().load(reader);
            if (System.getSecurityManager() == null) {
                System.setSecurityManager(new SecurityManager());
            }
            reader.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        //Set system-wide ToolTip properties
        ToolTipManager.sharedInstance().setInitialDelay(100);
    }

    private static void exitOnMissingAppFile() {
        JOptionPane.showMessageDialog(null, "Invalid or absent configuration file.", "Application Startup Error", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }

    /**
     * The application start point.
     *
     * @param args
     */
    public static void emulateStartup() {
        splash.hideOnClick();
        splash.addAutoProgressBarIndeterminate();
        splash.setProgressBarString("loading...");
        splash.showSplash();

        // Read the properties file
        initProperties();
        // Configure the digester for the rules used in the configuration file.
        configure();
        Debug.debugStatus = false; // debugging toggle
        // Locate and open the appication configuration file.
        String configFileName = null;
        configFileName = System.getProperty(CONFIG_FILE_NAME);
        if (configFileName == null) exitOnMissingAppFile();
        try {
            InputStream is = Class.forName("core.config.UILauncher").getResourceAsStream(configFileName);
            if (is == null) exitOnMissingAppFile();
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            // FIXME - Make sure that the input is validated against the proper DTD.
            Debug.debug("Digester Test Program");
            Debug.debug("Opening input stream ...");
            Debug.debug("Creating new digester ...");
            Debug.debug("Parsing input stream ...");
            uiLauncher.parse(is);
            Debug.debug("Closing input stream ...");
            is.close();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        PluginRegistry.debugPrint();
        splash.hideSplash();
    }
}
