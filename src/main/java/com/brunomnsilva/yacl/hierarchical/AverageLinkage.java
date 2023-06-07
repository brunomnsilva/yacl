package com.brunomnsilva.yacl.hierarchical;

import com.brunomnsilva.yacl.core.Clusterable;

/**
 * Average Linkage:
 * <br/>
 * Distance between clusters: Average distance between all pairs of data points from the two clusters.
 * Characteristics: Average linkage strikes a balance between single and complete linkage. It can produce more balanced clusters compared to single linkage and can handle outliers better than complete linkage.
 *
 * @param <T> the type of objects to be clustered, must implement the {@link Clusterable} interface
 *
 * @author brunomnsilva
 */
public class AverageLinkage<T extends Clusterable<T>> extends Linkage<T> {

    public AverageLinkage() {
    }

    @Override
    protected double computeMergeDistance(double dik, double djk, double dij, int ci, int cj, int ck) {
        double T = ci + cj;
        return dik * (ci/T) + djk * (cj/T);
    }

}
