package org.geworkbench.engine.builder;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.geworkbench.engine.EngineIcons;
import org.geworkbench.engine.config.ComponentMetadata;
import org.geworkbench.engine.config.GUIFramework;
import org.geworkbench.engine.config.VisualPlugin;
import org.geworkbench.engine.config.rules.PluginObject;
import org.geworkbench.engine.management.ComponentRegistry;
import org.geworkbench.engine.management.ComponentResource;
import org.geworkbench.util.AutoListModel;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.List;

/**
 * @author John Watkinson
 */
public class VisualBuilder extends JDialog {

    private JList availableList;
    private JList selectedList;

    private JScrollPane availablePane;
    private JScrollPane selectedPane;

    private static class ComponentItem implements Comparable {
        private ComponentMetadata metadata;
        private String area;

        public ComponentItem(ComponentMetadata metadata, String area) {
            this.metadata = metadata;
            if (area == null) {
                this.area = GUIFramework.VISUAL_AREA;
            } else {
                this.area = area;
            }
        }

        public ComponentMetadata getMetadata() {
            return metadata;
        }

        public int compareTo(Object o) {
            ComponentItem other = (ComponentItem) o;
            return (metadata.getName().compareTo(other.metadata.getName()));
        }

        public boolean equals(Object obj) {
            ComponentItem other = (ComponentItem) obj;
            return (metadata.getClass().equals(other.metadata.getClass()));
        }

        public int hashCode() {
            return metadata.getClass().hashCode();
        }

        public String getArea() {
            return area;
        }

        public String toString() {
            return metadata.getName();
        }
    }

    private ArrayList<ComponentItem> availableComponents = new ArrayList<ComponentItem>();
    private ArrayList<ComponentItem> selectedComponents = new ArrayList<ComponentItem>();
    private AutoListModel<ComponentItem> availableModel = new AutoListModel<ComponentItem>(availableComponents);
    private AutoListModel<ComponentItem> selectedModel = new AutoListModel<ComponentItem>(selectedComponents);
    private JComboBox areaChooser;

    public VisualBuilder(InputStream startConfigStream) throws HeadlessException {
        setTitle("Visual Builder");

        //// Prepare data
        findAllComponents();
        if (startConfigStream != null) {
            try {
                parseConfigFile(startConfigStream);
            } catch (Exception e) {
                System.out.println("Warning -- problem parsing config stream.");
                e.printStackTrace();
            }
        }

        Collections.sort(availableComponents);

        //// Content Pane
        setModal(true);
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        getContentPane().add(contentPanel);
        JPanel mainPanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        contentPanel.add(mainPanel);
        contentPanel.add(buttonPanel);
        //// Main Panel
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
        mainPanel.setBorder(new EmptyBorder(4, 4, 4, 4));

        FormLayout layout = new FormLayout("150dlu,5dlu,60dlu,5dlu,150dlu", "p,8dlu,p,60dlu,p,p,p,p,60dlu,8dlu,p,8dlu,p");
        layout.setColumnGroups(new int[][]{{1, 5}});
        PanelBuilder builder = new PanelBuilder(layout);
        builder.addSeparator("Components");
        CellConstraints cc = new CellConstraints();
        int row = 3;
        builder.add(new JLabel("Available"), cc.xy(1, row));
        builder.add(new JLabel("Selected"), cc.xy(5, row));
        availableList = new JList(availableModel);
        selectedList = new JList(selectedModel);
        availablePane = new JScrollPane(availableList);
        selectedPane = new JScrollPane(selectedList);
        row++;
        builder.add(availablePane, cc.xywh(1, row, 1, 6));
        builder.add(selectedPane, cc.xywh(5, row, 1, 6));
        JButton addAllButton = new JButton("Add All");
        JButton addButton = new JButton(">>");
        JButton removeButton = new JButton("<<");
        JButton removeAllButton = new JButton("Remove All");
        row++;
        builder.add(addAllButton, cc.xy(3, row++));
        builder.add(addButton, cc.xy(3, row++));
        builder.add(removeButton, cc.xy(3, row++));
        builder.add(removeAllButton, cc.xy(3, row++));
        row = 11;
        builder.addSeparator("Component Detail", cc.xywh(1, row++, 5, 1));
        row++;
        String[] areas = {
                GUIFramework.PROJECT_AREA,
                GUIFramework.SELECTION_AREA,
                GUIFramework.VISUAL_AREA,
                GUIFramework.COMMAND_AREA
        };
        areaChooser = new JComboBox(areas) {
            public Dimension getMaximumSize() {
                Dimension pref = super.getMaximumSize();
                pref.height = super.getMinimumSize().height;
                return pref;
            }
        };
        areaChooser.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value.equals(GUIFramework.PROJECT_AREA)) {
                    label.setIcon(EngineIcons.PROJECT_AREA_ICON);
                    label.setText("Project Area");
                } else if (value.equals(GUIFramework.SELECTION_AREA)) {
                    label.setIcon(EngineIcons.SELECTION_AREA_ICON);
                    label.setText("Selection Area");
                } else if (value.equals(GUIFramework.VISUAL_AREA)) {
                    label.setIcon(EngineIcons.VISUAL_AREA_ICON);
                    label.setText("Visual Area");
                } else if (value.equals(GUIFramework.COMMAND_AREA)) {
                    label.setIcon(EngineIcons.COMMAND_AREA_ICON);
                    label.setText("Command Area");
                }
                return label;
            }
        });
        builder.add(areaChooser, cc.xywh(1, row, 1, 1));
        mainPanel.add(builder.getPanel());
        //// Button Panel
        FormLayout buttonLayout = new FormLayout("fill:default, 4dlu, fill:default, 4dlu, fill:default, 4dlu", "");
        DefaultFormBuilder buttonBuilder = new DefaultFormBuilder(buttonLayout);
        JButton saveButton = new JButton("Save...");
        JButton cancelButton = new JButton("Cancel");
        buttonBuilder.append(Box.createHorizontalGlue());
        buttonBuilder.append(saveButton);
        buttonBuilder.append(cancelButton);
        buttonPanel.add(buttonBuilder.getPanel());
    }

    private void findAllComponents() {
        ComponentRegistry registry = ComponentRegistry.getRegistry();
        Collection<ComponentResource> resources = registry.getAllComponentResources();
        for (Iterator<ComponentResource> iterator = resources.iterator(); iterator.hasNext();) {
            ComponentResource resource = iterator.next();
            if (resource.getClassSearcher() == null) {
                continue;
            }
            String[] classNames = resource.getClassSearcher().getAllClassesAssignableTo(VisualPlugin.class, true);
            for (int i = 0; i < classNames.length; i++) {
                String className = classNames[i];
                try {
                    Class type = resource.getClassLoader().loadClass(className);
                    ComponentMetadata metadata = PluginObject.processComponentDescriptor(resource.getName(), type);
                    // todo - apply a default area from the config file
                    ComponentItem item = new ComponentItem(metadata, null);
                    availableComponents.add(item);
                } catch (ClassNotFoundException cnfe) {
                    System.out.println("Class '" + className + "' expected in resource '" + resource.getName() + "' but not found.");
                } catch (Exception e) {
                    System.out.println("Problem processing metadata.");
                    e.printStackTrace();
                }
            }
        }

    }

    private List<ComponentItem> parseConfigFile(InputStream configStream) throws IOException, JDOMException {
        ComponentRegistry registry = ComponentRegistry.getRegistry();
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(configStream);
        ArrayList<ComponentItem> items = new ArrayList<ComponentItem>();
        if (doc != null) {
            Element root = doc.getRootElement();
            List children = root.getChildren();
            for (int i = 0; i < children.size(); i++) {
                Element element = (Element) children.get(i);
                if (element.getName().equals("plugin")) {
                    String name = element.getAttributeValue("name");
                    String className = element.getAttributeValue("class");
                    String source = element.getAttributeValue("source");
                    if (source == null) {
                        source = ".";
                    }
                    Element guiArea = element.getChild("gui-area");
                    String area = null;
                    if (guiArea != null) {
                        area = guiArea.getAttributeValue("name");
                    }
                    ComponentResource resource = registry.getComponentResourceByName(source);
                    if (resource == null) {
                        System.out.println("Warnging -- component resource not found: " + source);
                    } else {
                        try {
                            Class type = resource.getClassLoader().loadClass(className);
                            ComponentMetadata metadata = new ComponentMetadata(type, source);
                            metadata.setName(name);
                            ComponentItem item = new ComponentItem(metadata, area);
                            items.add(item);
                        } catch (ClassNotFoundException e) {
                            System.out.println("Warning -- component not found: " + className);
                        }
                    }
                }
            }
        }
        return items;
    }

    public static void main(String[] args) {
        VisualBuilder builder = new VisualBuilder(null);
        builder.pack();
        builder.setSize(920, 600);
        builder.setVisible(true);
    }
}
