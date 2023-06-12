package com.brunomnsilva.yacl.density;

import com.brunomnsilva.yacl.core.*;

import java.util.*;

/**
 * An implementation of the DBSCAN clustering algorithm:
 * <a href="https://becominghuman.ai/dbscan-clustering-algorithm-implementation-from-scratch-python-9950af5eed97">How-To</a>
 * <br/>
 * This implementation relies on the distance of data points, hence the clusterable items must define
 * the {@link Clusterable#clusterablePoint()} method.
 *
 * @param <T> the type of objects to be clustered, must implement the {@link Clusterable} interface.
 *
 * @author brunomnsilva
 */
public class DBSCANClustering<T extends Clusterable<T>> {

    private final double eps;

    private final int minPts;

    private final Distance distanceMetric;

    /**
     * Constructs a DBSCANClustering instance with the specified epsilon and minimum points and
     * default Euclidean distance metric.
     * @param eps the maximum distance between two points to be considered neighbors
     * @param minPts the minimum number of points required to form a dense region
     */
    public DBSCANClustering(double eps, int minPts) {
        this(eps, minPts, new EuclideanDistance());
    }

    /**
     * Constructs a DBSCANClustering instance with the specified epsilon, minimum points, and distance metric.
     *
     * @param eps the maximum distance between two points to be considered neighbors
     * @param minPts the minimum number of points required to form a dense region
     * @param distanceMetric the distance metric used to calculate distances between points
     */
    public DBSCANClustering(double eps, int minPts, Distance distanceMetric) {
        this.eps = eps;
        this.minPts = minPts;
        this.distanceMetric = distanceMetric;
    }

    /**
     * Performs DBSCAN clustering on the given list of items.
     *
     * @param items the list of items to be clustered
     * @return a list of clusters containing the clustered items
     */
    public List<Cluster<T>> cluster(List<T> items) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}
