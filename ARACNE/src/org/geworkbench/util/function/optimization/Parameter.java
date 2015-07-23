package org.geworkbench.util.function.optimization;

import java.lang.reflect.Field;

/**
 * Mutable parameter.
 * The minimizer calls the set() method on this class during minimization.
 * This has the effect of setting the field on the object that implements
 * the Function interface, so a subsequent Fuction.calculate() call will
 * work with the given values.
 *
 * @author Dejan Vucinic
 */
public class Parameter {

    private Field m_field;
    private Function m_function;
    private String m_name;

    /**
     * Constructor.
     * Remembers which object this parameter belongs to, and constructs
     */
    public Parameter(Function function, String fieldname) throws NoSuchFieldException {
        m_function = function;
        m_field = function.getClass().getField("m_" + fieldname);
        m_name = function.getName() + "." + fieldname;
    }

    public void set(double value) throws IllegalAccessException {
        m_field.setDouble(m_function, value);
    }

    public double get() throws IllegalAccessException {
        return m_field.getDouble(m_function);
    }

}
