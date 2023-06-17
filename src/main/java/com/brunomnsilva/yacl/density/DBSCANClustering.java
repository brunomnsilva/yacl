package com.brunomnsilva.yacl.density;

import com.brunomnsilva.yacl.core.*;

import java.util.*;

/**
 * An implementation of the DBSCAN clustering algorithm:
 * <ul>
 *     <li><a href="https://becominghuman.ai/dbscan-clustering-algorithm-implementation-from-scratch-python-9950af5eed97">Explanation</a></li>
 *     <li><a href="https://www.researchgate.net/figure/Pseudocode-of-the-DBSCAN-algorithm_fig2_325059373">Pseudo-code</a>/li>
 * </ul>
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

        // We'll be returning a list of clusters
        List<Cluster<T>> clusters = new ArrayList<>();

        // Create the distance matrix between all items
        DistanceMatrix<T> distanceMatrix = DistanceMatrix.fromPointDistances(items, distanceMetric);

        // Track assigned items to clusters or noise (null)
        Map<T, Cluster<T>> clusterAssignments = new HashMap<>();

        // Track the unvisited items (points)
        List<T> unvisited = new ArrayList<>(items);

        // Each iteration will produce at most a cluster
        int clusterSeqId = 0;
        while(!unvisited.isEmpty()) {
            // 1.  Select a random unvisited point and mark it as visited
            //T p = unvisited.remove(randomNumber(0, unvisited.size() - 1)); // O(1)
            T p = unvisited.remove(0); // O(1)

            List<T> neighborPts = regionQuery(p, this.eps, distanceMatrix);
            if(neighborPts.size() < this.minPts) {
                // Mark 'p' as noise
                clusterAssignments.put(p, null); // 'null' represents the "noise" cluster
            } else {
                // Create a new cluster and find all reachable points
                Cluster<T> C = new Cluster<>(clusterSeqId++);
                clusters.add(C);
                // The following call will "populate" this clusters
                expandCluster(p, neighborPts, C, distanceMatrix, unvisited, clusterAssignments, this.eps, this.minPts);
            }

            // TODO: is it safe to delete the core points from the distance matrix? I believe so.
        }

        //System.out.println(clusterAssignments);

        return clusters;
    }

    private void expandCluster(T p, List<T> neighborPts, Cluster<T> C, DistanceMatrix<T> distanceMatrix,
                               List<T> unvisited, Map<T, Cluster<T>> clusterAssignments, double eps, int minPts) {
        // Add p to cluster C
        C.addMember(p);
        clusterAssignments.put(p, C);

        while(!neighborPts.isEmpty()) {
            T q = neighborPts.remove(0);

            if(unvisited.contains(q)) {
                unvisited.remove(q);

            //if(!visited.contains(q)) {
            //    visited.add(q);

                List<T> qNeighbors = regionQuery(q, eps, distanceMatrix);
                if(qNeighbors.size() >= minPts) {
                    addAllUnique(neighborPts, qNeighbors);
                }
            }

            // If unclustered or previously marked as noise
            if( (!clusterAssignments.containsKey(q)) || clusterAssignments.get(q) == null) {
                C.addMember(q);
                clusterAssignments.put(q, C);
            }
        }
    }

    private void addAllUnique(List<T> dest, List<T> src) {
        for (T e : src) {
            if(!dest.contains(e)) {
                dest.add(e);
            }
        }
    }

    /**
     * Return the list of neighboring points of <code>p</code> within eps radius, including <code>p</code>.
     * @param p the point to check is neighbors
     * @param eps the radius
     * @param distanceMatrix the distance matrix containing all pair-wise distances
     * @return the list of neighboring points of <code>p</code> within eps radius, including <code>p</code>
     */
    private List<T> regionQuery(T p, double eps, DistanceMatrix<T> distanceMatrix) {
        List<T> region = new ArrayList<>();
        // Remember, including p! This is important for the current implementation of the algorithm
        for (T q : distanceMatrix.items()) {
            double distance = distanceMatrix.getDistance(p, q);
            if( distance <= eps ) {
                region.add(q);
            }
        }
        return region;
    }

    private static int randomNumber(int lower, int upper) {
        if (lower > upper) {
            throw new IllegalArgumentException("Upper bound must be greater than lower bound.");
        }

        Random random = new Random();
        return random.nextInt(upper - lower + 1) + lower;
    }
}
