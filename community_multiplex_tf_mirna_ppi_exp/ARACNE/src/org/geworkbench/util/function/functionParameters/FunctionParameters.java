package org.geworkbench.util.function.functionParameters;

import java.util.HashMap;

public class FunctionParameters {
    HashMap parameters = new HashMap();

    public FunctionParameters() {
    }

    public Object get(String key) {
        return parameters.get(key);
    }

    public void put(String key, Object value) {
        parameters.put(key, value);
    }
}
