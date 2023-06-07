package com.brunomnsilva.yacl.hierarchical;

import com.brunomnsilva.yacl.core.Clusterable;

/**
 * Single Linkage (also known as Minimum Linkage):
 * <br/>
 * Distance between clusters: Minimum distance between any pair of data points from the two clusters.
 * Characteristics: Single linkage tends to create long, elongated clusters and is sensitive to noise and outliers. It can lead to the "chaining effect" where clusters are connected by a single point.
 *
 * @param <T> the type of objects to be clustered, must implement the {@link Clusterable} interface
 *
 * @author brunomnsilva
 */
public class SingleLinkage<T extends Clusterable<T>> extends Linkage<T> {

    public SingleLinkage() {
    }

    @Override
    protected double computeMergeDistance(double dik, double djk, double dij, int ci, int cj, int ck) {
        return Math.min(dik, djk);
    }


}
