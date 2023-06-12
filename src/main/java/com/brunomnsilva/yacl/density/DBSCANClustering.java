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
        Args.nullNotPermitted(items, "items");

        // TODO: Remove after first implementation attempt
        if(true) throw new UnsupportedOperationException("Not implemented yet.");

        // Create the distance matrix between all items
        DistanceMatrix<T> distanceMatrix = DistanceMatrix.fromPointDistances(items, distanceMetric);

        // Track the unvisited items (points)
        List<T> unvisited = new ArrayList<>(items);

        // Each iteration will produce at most a cluster
        while(!unvisited.isEmpty()) {
            // 1.  Select a random unvisited point
            T currentItem = unvisited.get(randomNumber(0, unvisited.size() - 1)); // O(1)

            // Check other items where dist(currentItem, otherItem) < eps
            List<T> reachable = new ArrayList<>();
            for(T otherItem : distanceMatrix.items()) {
                if(currentItem == otherItem) continue;

                double distance = distanceMatrix.getDistance(currentItem, otherItem);
                if(distance <= eps) {
                    reachable.add(otherItem);
                }
            }

            int numNeighbors = reachable.size();
            if(numNeighbors == 0) {
                // Noise

            } else if(numNeighbors < minPts) {
                // Reachable, but not core point

            } else {
                // Core point

            }


            unvisited.remove(currentItem); // O(n)

            // TODO: is it safe to delete the core points from the distance matrix? I believe so.
        }

        return null;
    }

    private static int randomNumber(int lower, int upper) {
        if (lower >= upper) {
            throw new IllegalArgumentException("Upper bound must be greater than lower bound.");
        }

        Random random = new Random();
        return random.nextInt(upper - lower + 1) + lower;
    }
}
