package com.brunomnsilva.yacl;

import com.brunomnsilva.yacl.core.Clusterable;
import com.brunomnsilva.yacl.hierarchical.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LinkageTest {
    /*
        The linkage results are compared against the scipy library results.

        https://jupyter.org/try-jupyter/

        We can use the following jupiter notebook to get the results:

        """
        import pandas as pd
        from matplotlib import pyplot as plt
        from scipy.cluster.hierarchy import dendrogram, linkage
        import numpy as np

        # Create dataframe from vectors
        columns = ["X", "Y", "Z"]
        rows = ["0", "1", "2", "3", "4", "5", "6"]
        data = np.array([
            [0.6974643684847707, 0.6501406165319475, 0.928653065505197],
            [0.12317143042210787, 0.05631837702101916, 0.9973643546972657],
            [0.7500327232458485, 0.16376375602729865, 0.31581862398912386],
            [0.3827772070665839, 0.9853700413852534, 0.8931100269859059],
            [0.29491455818373224, 0.7570525674809238, 0.4377629644012786],
            [0.42682333579819165, 0.8248474075522084, 0.44373838937058374],
            [0.22057859298481397, 0.7067609360226192, 0.3709478478164616]]
        )
        df = pd.DataFrame(data=data, index=rows, columns=columns)

        Z = linkage(df, 'single') # single, complete, average, weighted, ward
        Z

     */
    private List<MyClusterable> items;

    @BeforeEach
    void setUp() {
        items = List.of(
                new MyClusterable(0.6974643684847707, 0.6501406165319475, 0.928653065505197),
                new MyClusterable(0.12317143042210787, 0.05631837702101916, 0.9973643546972657),
                new MyClusterable(0.7500327232458485, 0.16376375602729865, 0.31581862398912386),
                new MyClusterable(0.3827772070665839, 0.9853700413852534, 0.8931100269859059),
                new MyClusterable(0.29491455818373224, 0.7570525674809238, 0.4377629644012786),
                new MyClusterable(0.42682333579819165, 0.8248474075522084, 0.44373838937058374),
                new MyClusterable(0.22057859298481397, 0.7067609360226192, 0.3709478478164616)
        );
    }

    @Test
    void testSingleLinkage() {
        Linkage<MyClusterable> linkage = new SingleLinkage<>();
        linkage.compute(items);

        /*
        EXPECTED from scipy:
        array([[ 4.        ,  6.        ,  0.11188987,  2.        ],
               [ 5.        ,  7.        ,  0.14843103,  3.        ],
               [ 0.        ,  3.        ,  0.46116167,  2.        ],
               [ 8.        ,  9.        ,  0.47921023,  5.        ],
               [ 2.        , 10.        ,  0.74689984,  6.        ],
               [ 1.        , 11.        ,  0.82895022,  7.        ]])
         */

        assertEquals(6, linkage.size());
        assertTrue(matches(linkage.get(0), 4, 6, 0.11188987, 2));
        assertTrue(matches(linkage.get(1), 5, 7, 0.14843103, 3));
        assertTrue(matches(linkage.get(2), 0, 3, 0.46116167, 2));
        assertTrue(matches(linkage.get(3), 8, 9, 0.47921023, 5));
        assertTrue(matches(linkage.get(4), 2, 10, 0.74689984, 6));
        assertTrue(matches(linkage.get(5), 1, 11, 0.82895022, 7));
    }

    @Test
    void testCompleteLinkage() {
        Linkage<MyClusterable> linkage = new CompleteLinkage<>();
        linkage.compute(items);

        /*
        EXPECTED from scipy:
        array([[ 4.        ,  6.        ,  0.11188987,  2.        ],
               [ 5.        ,  7.        ,  0.24855537,  3.        ],
               [ 0.        ,  3.        ,  0.46116167,  2.        ],
               [ 8.        ,  9.        ,  0.73597623,  5.        ],
               [ 1.        ,  2.        ,  0.93220393,  2.        ],
               [10.        , 11.        ,  1.06919543,  7.        ]])
         */

        assertEquals(6, linkage.size());
        assertTrue(matches(linkage.get(0), 4, 6, 0.11188987, 2));
        assertTrue(matches(linkage.get(1), 5, 7, 0.24855537, 3));
        assertTrue(matches(linkage.get(2), 0, 3, 0.46116167, 2));
        assertTrue(matches(linkage.get(3), 8, 9, 0.73597623, 5));
        assertTrue(matches(linkage.get(4), 1, 2, 0.93220393, 2));
        assertTrue(matches(linkage.get(5), 10, 11, 1.06919543, 7));
    }

    @Test
    void testAverageLinkage() {
        Linkage<MyClusterable> linkage = new AverageLinkage<>();
        linkage.compute(items);

        /*
        EXPECTED from scipy:
        array([[ 4.        ,  6.        ,  0.11188987,  2.        ],
               [ 5.        ,  7.        ,  0.1984932 ,  3.        ],
               [ 0.        ,  3.        ,  0.46116167,  2.        ],
               [ 8.        ,  9.        ,  0.59528231,  5.        ],
               [ 2.        , 10.        ,  0.82365383,  6.        ],
               [ 1.        , 11.        ,  0.92456718,  7.        ]])
         */

        assertEquals(6, linkage.size());
        assertTrue(matches(linkage.get(0), 4, 6, 0.11188987, 2));
        assertTrue(matches(linkage.get(1), 5, 7, 0.1984932, 3));
        assertTrue(matches(linkage.get(2), 0, 3, 0.46116167, 2));
        assertTrue(matches(linkage.get(3), 8, 9, 0.59528231, 5));
        assertTrue(matches(linkage.get(4), 2, 10, 0.82365383, 6));
        assertTrue(matches(linkage.get(5), 1, 11, 0.92456718, 7));
    }

    @Test
    void testWardLinkage() {
        Linkage<MyClusterable> linkage = new WardLinkage<>();
        linkage.compute(items);

        /*
        EXPECTED from scipy:
        array([[ 4.        ,  6.        ,  0.11188987,  2.        ],
               [ 5.        ,  7.        ,  0.22737908,  3.        ],
               [ 0.        ,  3.        ,  0.46116167,  2.        ],
               [ 8.        ,  9.        ,  0.84503563,  5.        ],
               [ 1.        ,  2.        ,  0.93220393,  2.        ],
               [10.        , 11.        ,  1.1440763 ,  7.        ]])
         */

        assertEquals(6, linkage.size());
        assertTrue(matches(linkage.get(0), 4, 6, 0.11188987, 2));
        assertTrue(matches(linkage.get(1), 5, 7, 0.22737908, 3));
        assertTrue(matches(linkage.get(2), 0, 3, 0.46116167, 2));
        assertTrue(matches(linkage.get(3), 8, 9, 0.84503563, 5));
        assertTrue(matches(linkage.get(4), 1, 2, 0.93220393, 2));
        assertTrue(matches(linkage.get(5), 10, 11, 1.1440763, 7));
    }

    @Test
    void testWeightedLinkage() {
        Linkage<MyClusterable> linkage = new WeightedLinkage<>();
        linkage.compute(items);

        /*
        EXPECTED from scipy:
        array([[ 4.        ,  6.        ,  0.11188987,  2.        ],
               [ 5.        ,  7.        ,  0.1984932 ,  3.        ],
               [ 0.        ,  3.        ,  0.46116167,  2.        ],
               [ 8.        ,  9.        ,  0.57913312,  5.        ],
               [ 2.        , 10.        ,  0.83981436,  6.        ],
               [ 1.        , 11.        ,  0.92916852,  7.        ]])
         */

        assertEquals(6, linkage.size());
        assertTrue(matches(linkage.get(0), 4, 6, 0.11188987, 2));
        assertTrue(matches(linkage.get(1), 5, 7, 0.1984932, 3));
        assertTrue(matches(linkage.get(2), 0, 3, 0.46116167, 2));
        assertTrue(matches(linkage.get(3), 8, 9, 0.57913312, 5));
        assertTrue(matches(linkage.get(4), 2, 10, 0.83981436, 6));
        assertTrue(matches(linkage.get(5), 1, 11, 0.92916852, 7));
    }

    private boolean matches(LinkageStep step, int id1, int id2, double distance, int numMembers) {
        return (
                (step.getClusterId1() == id1 && step.getClusterId2() == id2 ||
                 step.getClusterId1() == id2 && step.getClusterId2() == id1) &&
                        step.getClusterMergeMemberCount() == numMembers &&
                        equalWithinEpsilon(step.getClusterLevel(), distance, 0.00001)
        );
    }

    private boolean equalWithinEpsilon(double a, double b, double epsilon) {
        return Math.abs( a - b) < epsilon;
    }

    private static class MyClusterable implements Clusterable<MyClusterable> {

        private final double[] vector;

        public MyClusterable(double... values) {
            vector = values;
        }

        @Override
        public double clusterableDistance(MyClusterable other) {
            return calculateDistance(vector, other.vector);
        }

        @Override
        public double[] clusterablePoint() {
            return vector;
        }

        @Override
        public String clusterableLabel() {
            return Arrays.toString(vector);
        }

        // Computes the Euclidean distance
        private static double calculateDistance(double[] point1, double[] point2) {
            double sum = 0.0;
            for (int i = 0; i < point1.length; ++i) {
                double diff = point1[i] - point2[i];
                sum += diff * diff;
            }
            return Math.sqrt(sum);
        }
    }

}