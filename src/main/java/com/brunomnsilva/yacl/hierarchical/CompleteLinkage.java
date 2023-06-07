package com.brunomnsilva.yacl.hierarchical;

import com.brunomnsilva.yacl.core.Clusterable;

/**
 * Complete Linkage (also known as Maximum Linkage):
 * <br/>
 * Distance between clusters: Maximum distance between any pair of data points from the two clusters.
 * Characteristics: Complete linkage tends to create compact, spherical clusters. It is less sensitive to noise and outliers compared to single linkage.
 *
 * @param <T> the type of objects to be clustered, must implement the {@link Clusterable} interface
 *
 * @author brunomnsilva
 */
public class CompleteLinkage<T extends Clusterable<T>> extends Linkage<T> {

    public CompleteLinkage() {
    }

    @Override
    protected double computeMergeDistance(double dik, double djk, double dij, int ci, int cj, int ck) {
        return Math.max(dik, djk);
    }


}
