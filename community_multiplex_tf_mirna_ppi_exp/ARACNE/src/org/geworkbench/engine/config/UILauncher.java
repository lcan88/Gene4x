package org.geworkbench.engine.config;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.theme.SkyBlue;
import org.apache.commons.digester.Digester;
import org.geworkbench.bison.datastructure.bioobjects.markers.annotationparser.AnnotationParserListener;
import org.geworkbench.engine.config.rules.GeawConfigRule;
import org.geworkbench.engine.config.rules.PluginRule;
import org.geworkbench.engine.management.ComponentRegistry;
import org.geworkbench.util.SplashBitmap;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust, Inc.</p>
 *
 * @author First Genetic Trust, Inc.
 * @version 1.0
 */

/**
 * The starting point for an application. Parses the application configuration
 * file.
 */

public class UILauncher implements AnnotationParserListener {
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
    public static final String LOOK_AND_FEEL_FLAG = "-lookandfeel";
    public static final String DEVELOPMENT_FLAG = "-dev";
    private static final String DEFAULT_COMPONENTS_DIR = "components";
    private static final String COMPONENTS_DIR_PROPERTY = "components.dir";
    private static final String DEFAULT_GEAR_DIR = DEFAULT_COMPONENTS_DIR;

    static {
        splash = new org.geworkbench.util.SplashBitmap(SplashBitmap.class.getResource("splashscreen.png"));
    }

    /**
     * Configure the rules for translating the appication configuration file.
     */
    static void configure() {
        uiLauncher.setUseContextClassLoader(true);
        // Opening tag <geaw-config>
        uiLauncher.addRule("geaw-config", new GeawConfigRule("org.geworkbench.engine.config.rules.GeawConfigObject"));
        // Creates the top-level GUI window
        uiLauncher.addObjectCreate("geaw-config/gui-window", "org.geworkbench.engine.config.rules.GUIWindowObject");
        uiLauncher.addCallMethod("geaw-config/gui-window", "createGUI", 1);
        uiLauncher.addCallParam("geaw-config/gui-window", 0, "class");
        // Instantiates a plugin and adds it in the PluginResgistry
        uiLauncher.addRule("geaw-config/plugin", new PluginRule("org.geworkbench.engine.config.rules.PluginObject"));
        // Registers a plugin with an extension point
        uiLauncher.addCallMethod("geaw-config/plugin/extension-point", "addExtensionPoint", 1);
        uiLauncher.addCallParam("geaw-config/plugin/extension-point", 0, "name");
        // Registers a visual plugin with the top-level application GUI.
        uiLauncher.addCallMethod("geaw-config/plugin/gui-area", "addGUIComponent", 1);
        uiLauncher.addCallParam("geaw-config/plugin/gui-area", 0, "name");
        // Associates the plugin's module methods to plugin modules
        uiLauncher.addCallMethod("geaw-config/plugin/use-module", "addModule", 2);
        uiLauncher.addCallParam("geaw-config/plugin/use-module", 0, "name");
        uiLauncher.addCallParam("geaw-config/plugin/use-module", 1, "id");
        // Turn subscription object on and off
        uiLauncher.addCallMethod("geaw-config/plugin/subscription", "handleSubscription", 2);
        uiLauncher.addCallParam("geaw-config/plugin/subscription", 0, "type");
        uiLauncher.addCallParam("geaw-config/plugin/subscription", 1, "enabled");
        // Sets up a coupled listener relationship involving 2 plugins.
        uiLauncher.addCallMethod("geaw-config/plugin/coupled-event", "registerCoupledListener", 2);
        uiLauncher.addCallParam("geaw-config/plugin/coupled-event", 0, "event");
        uiLauncher.addCallParam("geaw-config/plugin/coupled-event", 1, "source");
    }

    /**
     * Reads application properties from a file called
     * <bold>application.properties</bold>
     */
    static void initProperties() {
        InputStream reader = null;
        try {
            reader = Class.forName("org.geworkbench.engine.config.UILauncher").getResourceAsStream("/application.properties");
            System.getProperties().load(reader);
            if (System.getSecurityManager() == null) {
                System.setSecurityManager(new SecurityManager());
            }
            reader.close();
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        //Set system-wide ToolTip properties
        ToolTipManager.sharedInstance().setInitialDelay(100);
    }

    private static void exitOnErrorMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "Application Startup Error", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }

    /**
     * The application start point.
     *
     * @param args
     */
    public static void main(String[] args) {
        // Sort out arguments
        String configFileArg = null;
        String lookAndFeelArg = null;
        boolean devMode = false;
        for (int i = 0; i < args.length; i++) {
            if (LOOK_AND_FEEL_FLAG.equals(args[i])) {
                if (args.length == (i + 1)) {
                    exitOnErrorMessage("No look & feel parameter specified.");
                } else {
                    i++;
                    lookAndFeelArg = args[i];
                }
            } else if (DEVELOPMENT_FLAG.equals(args[i])) {
                devMode = true;
            } else {
                configFileArg = args[i];
            }
        }
        try {
            if (System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") == -1) {
                // If we're not on windows, then use native look and feel no matter what
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } else {

                if (lookAndFeelArg != null) {
                    if ("native".equals(lookAndFeelArg)) {
                        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    } else if ("plastic".equals(lookAndFeelArg)) {
                        PlasticLookAndFeel.setMyCurrentTheme(new SkyBlue());
                        UIManager.setLookAndFeel("com.jgoodies.looks.plastic.Plastic3DLookAndFeel");
                    } else {
                        UIManager.setLookAndFeel(lookAndFeelArg);
                    }
                } else {
                    // Default to plastic.
                    PlasticLookAndFeel.setMyCurrentTheme(new SkyBlue());
                    UIManager.setLookAndFeel("com.jgoodies.looks.plastic.Plastic3DLookAndFeel");
                }
            }
        } catch (Exception e) {
            System.out.println("Problem applying look and feel:");
            e.printStackTrace();
        }
        // Add hook for AnnotationParser listener

        splash.hideOnClick();
        splash.addAutoProgressBarIndeterminate();
        splash.setProgressBarString("loading...");
        splash.showSplash();

        // Read the properties file
        initProperties();

        // Initialize component classloaders

        if (!devMode) {
            System.out.println("Scanning plugins...");
            String componentsDir = System.getProperty(COMPONENTS_DIR_PROPERTY);
            if (componentsDir == null) {
                componentsDir = DEFAULT_COMPONENTS_DIR;
            }
            ComponentRegistry.getRegistry().initializeComponentResources(componentsDir);
            ComponentRegistry.getRegistry().initializeGearResources(DEFAULT_GEAR_DIR);
            System.out.println("... scan complete.");
        } else {
            System.out.println("Development mode-- skipping plugin scan.");
        }

        // Redirecting System.out and System.err to a log file
        //        System.setErr(new PrintStream(new LoggingOutputStream(Category.getRoot(), Priority.WARN), true));
        //        System.setOut(new PrintStream(new LoggingOutputStream(Category.getRoot(), Priority.INFO), true));

        // Configure the digester for the rules used in the configuration file.
        configure();
        org.geworkbench.util.Debug.debugStatus = false; // debugging toggle
        // Locate and open the appication configuration file.
        String configFileName = null;
        if (configFileArg != null) {
            configFileName = configFileArg;
        } else {
            configFileName = System.getProperty(CONFIG_FILE_NAME);
            if (configFileName == null)
                exitOnErrorMessage("Invalid or absent configuration file.");
        }

        try {
            InputStream is = Class.forName("org.geworkbench.engine.config.UILauncher").getResourceAsStream("/" + configFileName);
            if (is == null) {
                exitOnErrorMessage("Invalid or absent configuration file.");
            }
            // FIXME - Make sure that the input is validated against the proper DTD.
            org.geworkbench.util.Debug.debug("Digester Test Program");
            org.geworkbench.util.Debug.debug("Opening input stream ...");
            org.geworkbench.util.Debug.debug("Creating new digester ...");
            org.geworkbench.util.Debug.debug("Parsing input stream ...");
            uiLauncher.parse(is);
            org.geworkbench.util.Debug.debug("Closing input stream ...");
            is.close();
        } catch (Exception e) {
            System.out.println("Problem parsing configuration file: " + configFileName + ".");
            e.printStackTrace();
        }

        PluginRegistry.debugPrint();
        // Testing: write the config back out
        //        try {
        //            ConfigWriter.writeConfigForDescriptors(ComponentRegistry.getRegistry().getActivePluginDescriptors(), "src/core/config/test.cwb.xml");
        //        } catch (IOException e) {
        //            System.out.println("Failed to write test config file:");
        //            e.printStackTrace();
        //        }
        splash.hideSplash();
    }

    public boolean annotationParserUpdate(String text) {
        if (splash.isVisible()) {
            UILauncher.splash.setProgressBarString(text);
            return true;
        } else {
            return false;
        }
    }

}

