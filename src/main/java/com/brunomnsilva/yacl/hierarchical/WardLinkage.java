package com.brunomnsilva.yacl.hierarchical;

import com.brunomnsilva.yacl.core.Clusterable;

/**
 * Ward's Linkage:
 * <br/>
 * Distance between clusters: Based on the increase in the sum of squared distances within clusters when merging them.
 * Characteristics: Ward's linkage aims to minimize the variance within clusters during the merging process. It tends to create balanced, compact clusters and is often used in variance-based clustering objectives.
 * <br/>
 * Ward linkage is only correct when using the Euclidean distance between samples.
 *
 * @param <T> the type of objects to be clustered, must implement the {@link Clusterable} interface
 *
 * @author brunomnsilva
 */
public class WardLinkage<T extends Clusterable<T>> extends Linkage<T> {

    public WardLinkage() {
    }

    @Override
    protected double computeMergeDistance(double dik, double djk, double dij, int ci, int cj, int ck) {
        // Require squared distances
        double dik2 = dik * dik;
        double djk2 = djk * djk;
        double dij2 = dij * dij;

        double T = ci + cj + ck;

        double dist2 = ((ck+ci)/T) * dik2 + ((ck+cj)/T) * djk2 - (ck/T) * dij2;
        return Math.sqrt(dist2);
    }


}
