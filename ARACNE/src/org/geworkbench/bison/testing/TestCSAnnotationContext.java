package org.geworkbench.bison.testing;

import org.geworkbench.bison.annotation.CSAnnotationContextManager;
import org.geworkbench.bison.annotation.CSAnnotationType;
import org.geworkbench.bison.annotation.DSAnnotationContextManager;
import org.geworkbench.bison.annotation.DSAnnotationType;

/**
 * @author John Watkinson
 */
public class TestCSAnnotationContext extends TestDSAnnotationContext {

    public DSAnnotationContextManager getAnnotationContextManager() {
        return CSAnnotationContextManager.getInstance();
    }

    public <Q> DSAnnotationType<Q> createAnnotationType(String label, Class<Q> type) {
        return new CSAnnotationType(type, label);
    }
}
