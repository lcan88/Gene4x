package org.geworkbench.engine.preferences;

import java.awt.*;
import java.io.*;
import java.util.List;

/**
 * @author John Watkinson
 */
public class PreferencesManager {

    private static final String PREF_DIR = "preferences";

    private static final String GLOBAL_PREFERENCES = "__global";

    private static final String PREFERENCES_EXTENSION = ".prefs";

    private static final String TEMP_DIR = "temporary.files.directory";

    private static PreferencesManager instance;

    public static PreferencesManager getPreferencesManager() {
        synchronized (PreferencesManager.class) {
            if (instance == null) {
                instance = new PreferencesManager();
            }
        }
        return instance;
    }

    private File prefDir;

    private PreferencesManager() {
        String tempDir = System.getProperty(TEMP_DIR);
        prefDir = new File(tempDir, PREF_DIR);
        if (!prefDir.exists()) {
            prefDir.mkdirs();
        }
    }

    /**
     * Fills out the preferences (which should contain default values) with saved preferences values,
     * if there are any. Also persists the resulting preferences. Only field included in the passed preferences
     * will be filled in. Old fields in the saved preferences that do not appear in the new object supplied here
     * will be dropped.
     * @param component the component class for these preferences, or null for global preferences.
     * @param preferences the preferences with all appropriate fields added.
     */
    public void fillPreferences(Class component, Preferences preferences) {
        String fileName;
        if (component == null) {
            fileName = GLOBAL_PREFERENCES + PREFERENCES_EXTENSION;
        } else {
            fileName = component.getName() + PREFERENCES_EXTENSION;
        }
        File prefFile = new File(prefDir, fileName);
        if (prefFile.exists()) {
            Preferences oldPreferences = readPreferences(prefFile);
            updatePreferences(preferences, oldPreferences);
        } else {
            // No -op
        }
        savePreferences(component, preferences);
    }

    /**
     * Opens up a dialog allowing the user to modify preferences.  The result is validated, then saved.
     *
     * @param component the component for which the preferences are being displayed.
     * @param preferences the preferences containing the previous values.
     * @param owner the owner of the dialog (can be null).
     * @return the preferences resulting after the user's edits.
     */
    public Preferences showPreferencesDialog(Class component, Preferences preferences, Frame owner) {
        PreferencesDialog dialog = new PreferencesDialog(preferences);
        Preferences result = dialog.showPreferencesDialog(owner);
        savePreferences(component, result);
        return result;
    }

    public void savePreferences(Class component, Preferences preferences) {
        String fileName;
        if (component == null) {
            fileName = GLOBAL_PREFERENCES + PREFERENCES_EXTENSION;
        } else {
            fileName = component.getName() + PREFERENCES_EXTENSION;
        }
        File prefFile = new File(prefDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(prefFile);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(preferences);
            oos.flush();
            oos.close();
        } catch (IOException e) {
            System.out.println("Error persisting preferences:");
            e.printStackTrace();
        }
    }

    private void updatePreferences(Preferences target, Preferences source) {
        List<Field> fields = target.getFields();
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            Field other = source.getField(field.getName());
            if (other != null) {
                field.copyValueFrom(other);
            }
        }
    }

    private Preferences readPreferences(File fromFile) {
        try {
            FileInputStream fis = new FileInputStream(fromFile);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Preferences preferences = (Preferences) ois.readObject();
            ois.close();
            return preferences;
        } catch (IOException e) {
            // Not able to read, ignore
        } catch (ClassNotFoundException e) {
            System.out.println("Unexpected error when reading preferences:");
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        System.setProperty(TEMP_DIR, ".");
        TextField field1 = new TextField("Field 1");
        field1.setValue("default");
        DoubleField field2 = new DoubleField("Field 2");
        field2.setValue(0.5D);
        Preferences preferences = new Preferences();
        preferences.addField(field1);
        preferences.addField(field2);
        Class prefClass = PreferencesManager.class;
        PreferencesManager manager = PreferencesManager.getPreferencesManager();
        manager.fillPreferences(prefClass, preferences);
        Preferences pref2 = new Preferences();
        DoubleField field3 = new DoubleField("Field 3");
        field3.setValue(10D);
        pref2.addField(field1);
        pref2.addField(field2);
        pref2.addField(field3);
        manager.fillPreferences(prefClass, pref2);
        System.out.println("" + pref2);
    }
}
