package org.geworkbench.engine.config;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * @author John Watkinson
 */
public class ConfigWriter {

    public static void writeConfigForDescriptors(Collection<PluginDescriptor> descriptors, String filename) throws IOException {
        // Create document
        Document doc = DocumentHelper.createDocument();
        // Add root element
        Element root = doc.addElement("geaw-config");
        root.addAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        root.addAttribute("xsi:noNamespaceSchemaLocation", "ConfigurationFile Schema.xsd");
        // Add gui-window element
        Element guiWindow = root.addElement("gui-window");
        guiWindow.addAttribute("name", "Main GUI");
        guiWindow.addAttribute("class", org.geworkbench.engine.skin.Skin.class.getName());
        // For each plugin descriptor, add appropriate element and sub-elements
        for (Iterator<PluginDescriptor> iterator = descriptors.iterator(); iterator.hasNext();) {
            PluginDescriptor descriptor = iterator.next();
            Element plugin = root.addElement("plugin");
            plugin.addAttribute("id", descriptor.getID());
            plugin.addAttribute("name", descriptor.getLabel());
            plugin.addAttribute("class", descriptor.getPluginClass().getName());
            if (descriptor.getVisualLocation() != null) {
                Element guiArea = plugin.addElement("gui-area");
                guiArea.addAttribute("name", descriptor.getVisualLocation());
            }
            Map<String, String> moduleMappings = descriptor.getModuleMappings();
            Iterator<String> moduleNames = moduleMappings.keySet().iterator();
            while (moduleNames.hasNext()) {
                String name = moduleNames.next();
                Element useModule = plugin.addElement("use-module");
                useModule.addAttribute("name", name);
                useModule.addAttribute("id", moduleMappings.get(name));
            }
        }
        // Write file to disk
        XMLWriter writer = new XMLWriter(new FileWriter(filename));
        writer.write(doc);
        writer.close();
    }
}
