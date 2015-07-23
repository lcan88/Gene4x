package org.geworkbench.engine.cascript;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

/**
 * @author Behrooz Badii
 * CasDataTypeImport is used to create a list of objects that are supported by CasDataPlug
 */
class CasDataTypeImport extends HashMap {
    public CasDataTypeImport() {
        initialize();
    }

    void initialize() {
        try {
            InputStream is = CasDataTypeImport.class.getResourceAsStream("datatypes.properties");
            Properties props = new Properties();
            props.load(is);
            is.close();
            this.putAll(props);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new CasException("Error occurred loading datatypes and corresponding class locations");
        }
    }
}
