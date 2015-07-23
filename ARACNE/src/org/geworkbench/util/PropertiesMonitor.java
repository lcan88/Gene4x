/**
 * <p>PropertiesMonitor</p>
 * <p>Reads initialization file, and creates it if it doesn't exist</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author
 * @version 1.0
 */

package org.geworkbench.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

public class PropertiesMonitor {
    public static PropertiesMonitor instance = null;
    //the properties
    Properties properties = new Properties();
    public static final String SOAP_CONFIG_FILE = "soap.ini";
    public static final String SOAP_CONFIG_PATH = System.getProperty("temporary.files.directory");
    private String path = "";

    private String absolutePath() {
        if (path != "") {
            return path;
        } else {
            return (SOAP_CONFIG_PATH + File.separator + SOAP_CONFIG_FILE);
        }
    }

    // Constructor reads the initialization file
    private PropertiesMonitor() {
        readProperties();
    }

    public PropertiesMonitor(String file) {
        path = file;
        readProperties();
    }

    /**
     * This method returns and instance to a shared PropertiesMonitor object.
     *
     * @return PropertiesMonitor object
     */
    public static synchronized PropertiesMonitor getPropertiesMonitor() {
        if (instance == null) {
            instance = new PropertiesMonitor();
        }
        return (instance);
    }

    /*
     * Reads the initialization file into a properties object.
     */
    protected void readProperties() {
        File initFile = null;
        try {
            initFile = new File(absolutePath());
            if (initFile.canRead()) {
                FileInputStream input = new FileInputStream(initFile);
                properties.load(input);
            } else {
            }
            validate();
        } catch (IOException ex1) {
            System.out.println("PropertiesMonitor::::::readProperties(): " + ex1.toString());
        }
    }

    /**
     * Validates the properties and inserts default values.
     *
     * @throws IOException
     */
    protected void validate() throws IOException {
        String tmp = null;

        tmp = properties.getProperty("host");
        if (tmp == null) {
            properties.setProperty("host", "localhost");
        }

        tmp = properties.getProperty("port");
        if (tmp == null) {
            set("port", "8001");
        } else {
            //we check that we really have a valid port
            tmp = tmp.trim();
            try {
                Integer.parseInt(tmp);
                set("port", "" + tmp);
            } catch (NumberFormatException exp) {
                //if we get here, bad number, use default
                set("port", "8001");
            }
        }

        tmp = properties.getProperty("userName");
        if (tmp == null || tmp.equals("")) {
            try {
                set("userName", System.getProperties().getProperty("user.name"));
            } catch (SecurityException exp) {
                set("userName", "");
            }
        }

        tmp = properties.getProperty("defPath");
        if (tmp == null) {
            set("defPath", System.getProperties().getProperty("user.home"));
        }
    }

    /**
     * Write to a SOAP_CONFIG_FILE file with the current set properties.
     */
    public synchronized void writeProperties() {
        try {
            FileOutputStream output = new FileOutputStream(new File(absolutePath()));
            properties.store(output, "Properties");
        } catch (IOException ex2) {
            /** @todo the exception should propegate */
            System.out.println("Error: " + ex2);
        }
    }

    /**
     * Save the last host name that was selected in the project.
     *
     * @param host
     */
    public synchronized void setHostSelected(String host) {
        if (host != null && !host.equals(""))
            set("lastHost", host);
    }

    public synchronized String getHostSelected() {
        return properties.getProperty("lastHost");
    }

    public synchronized void addHost(String ht) {
        if (null != ht && !ht.equals("")) {
            //get the set of hosts
            Set hostSet = getHosts();
            if (hostSet.add(ht)) {
                //we did not see this host before, add it
                String hostList = properties.getProperty("host");
                hostList += " " + ht;
                set("host", hostList);
            }
        }
    }

    /**
     * Get the set of hosts.
     *
     * @return Set - the list of hosts.
     */
    public synchronized Set getHosts() {
        //note: this is a bit expensive, but really the list of hosts is minimal
        String hostList = properties.getProperty("host");
        StringTokenizer st = new StringTokenizer(hostList);
        Set hostSet = new TreeSet();
        while (st.hasMoreTokens()) {
            hostSet.add(st.nextToken());
        }
        return (hostSet);
    }

    /**
     * The method sets the current port. The port number must be positive.
     *
     * @param port
     */
    public void setPort(int port) {
        if (port >= 0)
            set("port", "" + port);
    }

    public String getPort() {
        return properties.getProperty("port");
    }

    public void setUserName(String user) {
        set("userName", user);
    }

    public String getUserName() {
        return properties.getProperty("userName");
    }

    public void setDefPath(String dPath) {
        set("defPath", dPath);
    }

    public String getDefPath() {
        return properties.getProperty("defPath");
    }

    public void set(String prop, String val) {
        if (val != null) {
            properties.setProperty(prop, val);
            writeProperties();
        }
    }

    public String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}
