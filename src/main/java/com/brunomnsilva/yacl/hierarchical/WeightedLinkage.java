package com.brunomnsilva.yacl.hierarchical;

import com.brunomnsilva.yacl.core.Clusterable;

/**
 * Weighted Linkage:
 * <br/>
 * Distance between clusters: Weighted combination of the distances between data points from the two clusters, taking into account the number of data points in each cluster.
 * Characteristics: Weighted linkage allows incorporating weights or importance of data points in the clustering process. It can be useful when some data points have more significance than others.
 *
 * @param <T> the type of objects to be clustered, must implement the {@link Clusterable} interface
 *
 * @author brunomnsilva
 */
public class WeightedLinkage<T extends Clusterable<T>> extends Linkage<T> {

    public WeightedLinkage() {
    }

    @Override
    protected double computeMergeDistance(double dik, double djk, double dij, int ci, int cj, int ck) {
        return (dik + djk) / 2;
    }
}
