package org.geworkbench.events;

import org.geworkbench.engine.config.events.Event;
import org.geworkbench.engine.config.events.EventSource;

import java.io.File;

public class ModuleDiscoveryEvent extends Event {
    File datafile = null;

    public ModuleDiscoveryEvent(EventSource ss, File data) {
        super(ss);
        datafile = data;
    }

    public File getDataFile() {
        return datafile;
    }
}
