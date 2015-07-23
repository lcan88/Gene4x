package org.geworkbench.engine.management;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Date: Apr 25, 2005
 * Time: 5:10:30 PM
 * Company: Columbia University
 * Author: John Watkinson
 */
@Retention(RetentionPolicy.RUNTIME) @Target(ElementType.METHOD) public @interface Subscribe {
    Class<? extends SynchModel> value() default Synchronous.class;
}
