package org.geworkbench.bison.testing.clusters;

import junit.framework.TestCase;
import org.geworkbench.bison.model.clusters.Cluster;
import org.geworkbench.bison.model.clusters.DefaultHierCluster;

import java.util.List;
import java.util.Map;

/**
 * Test default hierarchical cluster.
 * User: mhall
 * Date: Oct 21, 2005
 * Time: 12:51:46 PM
 */
public class TestDefaultHierCluster extends TestCase {
    DefaultHierCluster c1 = new DefaultHierCluster();
    DefaultHierCluster c2 = new DefaultHierCluster();
    DefaultHierCluster c3 = new DefaultHierCluster();
    DefaultHierCluster c4 = new DefaultHierCluster();
    DefaultHierCluster c5 = new DefaultHierCluster();
    DefaultHierCluster c6 = new DefaultHierCluster();
    DefaultHierCluster c7 = new DefaultHierCluster();
    DefaultHierCluster c8 = new DefaultHierCluster();
    DefaultHierCluster c9 = new DefaultHierCluster();
    DefaultHierCluster c10 = new DefaultHierCluster();
    DefaultHierCluster c11 = new DefaultHierCluster();

    {
        c1.setID("c1");
        c2.setID("c2");
        c3.setID("c3");
        c4.setID("c4");
        c5.setID("c5");
        c6.setID("c6");
        c7.setID("c7");
        c8.setID("c8");
        c9.setID("c9");
        c10.setID("c10");
        c11.setID("c11");
    }

    private DefaultHierCluster makeTestCluster() {
        DefaultHierCluster cluster = new DefaultHierCluster();
        cluster.setID("Root");
        c1.setDepth(1);
        cluster.addNode(c1);
        c6.setDepth(2);
        c1.addNode(c6);
        c9.setDepth(3);
        c6.addNode(c9);
        c10.setDepth(3);
        c6.addNode(c10);
        c8.setDepth(2);
        c1.addNode(c8);
        c2.setDepth(1);
        cluster.addNode(c2);
        c3.setDepth(2);
        c2.addNode(c3);
        c7.setDepth(3);
        c3.addNode(c7);
        c4.setDepth(2);
        c2.addNode(c4);
        c5.setDepth(3);
        c4.addNode(c5);
        c11.setDepth(4);
        c5.addNode(c11);

        return cluster;
    }

    public void testLeafChildCountMap() {
        DefaultHierCluster cluster = makeTestCluster();
        Map<Cluster, Integer> map = cluster.getLeafChildrenCountMap();
        assertEquals(c1.getID()+" failed.", map.get(c1).intValue(), c1.getLeafChildrenCount());
        assertEquals(c2.getID()+" failed.", map.get(c2).intValue(), c2.getLeafChildrenCount());
        assertEquals(c6.getID()+" failed.", map.get(c6).intValue(), c6.getLeafChildrenCount());
    }

    public void testLeafOrdering() {
        DefaultHierCluster cluster = makeTestCluster();
        assertTrue(checkOrder(cluster.getLeafChildren(), c1.getLeafChildren()));
        assertTrue(checkOrder(cluster.getLeafChildren(), c2.getLeafChildren()));
        assertTrue(checkOrder(cluster.getLeafChildren(), c6.getLeafChildren()));
        assertTrue(checkOrder(cluster.getLeafChildren(), c8.getLeafChildren()));
    }

    public static boolean checkOrder(List<Cluster> list1ContainsList2, List<Cluster> list2) {
        int count = 0;
        for (Cluster outercluster : list1ContainsList2) {
            DefaultHierCluster c1 = (DefaultHierCluster) outercluster;
            // Find common start point
            if (c1.getID().equals(((DefaultHierCluster) list2.get(0)).getID())) {
                // Compare from this point on
                for (Cluster cluster : list2) {
                    DefaultHierCluster c1check = (DefaultHierCluster) cluster;
                    if (!c1check.getID().equals(((DefaultHierCluster) list1ContainsList2.get(count)).getID())) {
                        return false;
                    }
                    count++;
                }
                return true;
            }
            count++;
        }
        return false;
    }

/*
    public void testGetLeafChildrenConsistency() {
        DefaultHierCluster cluster = makeTestCluster();
        Cluster[] origleafs = cluster.getLeafChildren();
        Cluster[] newleafs = cluster.getLeafChildren();
        assertEquals("Lengths different.", origleafs.length, newleafs.length);
        for (int i = 0; i < newleafs.length; i++) {
            Cluster newleaf = newleafs[i];
            Cluster origleaf = origleafs[i];
            assertEquals("Order different.", newleaf, origleaf);
        }
    }
*/
}
