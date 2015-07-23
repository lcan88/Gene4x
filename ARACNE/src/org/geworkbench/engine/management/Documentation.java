package org.geworkbench.engine.management;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to provide <code>RUNTIME</code> access to JavaDoc for use with the
 * caSCRIPT mechanism, similar to xdoclet{@link http://xdoclet.sourceforge.net/xdoclet/index.html} functionality
 * @author manjunath at genomecenter dot columbia dot edu
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Documentation {
    String value() default "";
}
