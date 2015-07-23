package org.geworkbench.bison.datastructure.complex.pattern;


/**
 * <p>Title: Bioworks</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence
 * and Genotype Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CSPatternMatch <T,R> implements DSPatternMatch<T, R> {
    protected T object = null;
    protected R registration = null;
    protected double pValue = 1.0;

    public CSPatternMatch(T object) {
        this.object = object;
    }

    public T getObject() throws IndexOutOfBoundsException {
        if (object != null) {
            return object;
        }
        throw new IndexOutOfBoundsException();
    }

    public R getRegistration() throws IndexOutOfBoundsException {
        if (registration != null) {
            return registration;
        }
        throw new IndexOutOfBoundsException();
    }

    public void setObject(T _object) {
        object = _object;
    }

    public void setRegistration(R _registration) {
        registration = _registration;
    }

    public double getPValue() {
        return pValue;
    }

    public void setPValue(double _pValue) {
        pValue = _pValue;
    }
}
