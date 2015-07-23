package org.geworkbench.engine.test;

import junit.framework.TestCase;
import org.geworkbench.engine.management.ComponentResource;

import java.io.IOException;

/**
 * User: matt
 * Date: Oct 10, 2005
 * Time: 3:14:22 PM
 */
public class TestComponentClassloader extends TestCase {

    public void testComponentResource() throws IOException {
        ComponentResource cr = new ComponentResource("gears", false);
        ClassLoader loader = cr.getClassLoader();
    }
}
