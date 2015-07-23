package org.geworkbench.engine.config;

import org.geworkbench.bison.datastructure.bioobjects.microarray.CSGenepixMarkerValue;
import org.geworkbench.engine.preferences.GlobalPreferences;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Manages the global preferences
 *
 * @author John Watkinson
 */
public class PreferencesMenu implements MenuListener {

    public ActionListener getActionListener(String var) {
        if (var.equalsIgnoreCase("Tools.Preferences")) {
            return new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    GlobalPreferences globalPreferences = GlobalPreferences.getInstance();
                    int oldGenepix = globalPreferences.getGenepixComputationMethod();
                    globalPreferences.displayPreferencesDialog();
                    int newGenepix = globalPreferences.getGenepixComputationMethod();
                    if (newGenepix != oldGenepix) {
                        CSGenepixMarkerValue.setComputeSignalMethod(CSGenepixMarkerValue.ComputeSignalMethod.fromIndex(newGenepix));
                    }
                }
            };
        }
        return null;
    }

}
