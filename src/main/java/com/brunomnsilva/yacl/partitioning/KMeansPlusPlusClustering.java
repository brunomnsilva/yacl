package com.brunomnsilva.yacl.partitioning;

import com.brunomnsilva.yacl.core.*;

import java.util.*;

/**
 * An implementation of the K-Means++ clustering algorithm:
 * <a href="https://theory.stanford.edu/~sergei/papers/kMeansPP-soda.pdf">Article</a>
 * <br/>
 * This implementation relies on the distance of data points, hence the clusterable items must define
 * the {@link Clusterable#clusterablePoint()} method.
 *
 * @param <T> the type of objects to be clustered, must implement the {@link Clusterable} interface.
 *
 * @author brunomnsilva
 */
public class KMeansPlusPlusClustering<T extends Clusterable<T>> {

    private final int numberClusters;

    private final int maxIterations;

    private final Distance distanceMetric;

    private final List<CentroidCluster<T>> centroidClusters;

    private final Map<CentroidCluster<T>, Boolean> centroidConvergence;

    /**
     * Constructs a KMeansPlusPlusClustering instance with default Euclidean distance.
     * The maximum number of iterations is set to Integer.MAX_VALUE by default, i.e. until the centroids have converged.
     *
     * @param numberClusters The number of clusters to be generated.
     */
    public KMeansPlusPlusClustering(int numberClusters) {
        this(numberClusters, Integer.MAX_VALUE, new EuclideanDistance());
    }

    /**
     * Constructs a KMeansPlusPlusClustering instance with default Euclidean distance.
     * The number of iterations is limited by <code>maxIterations</code>.
     *
     * @param numberClusters The number of clusters to be generated.
     * @param maxIterations  The maximum number of iterations allowed for the clustering algorithm.
     */
    public KMeansPlusPlusClustering(int numberClusters, int maxIterations) {
        this(numberClusters, maxIterations, new EuclideanDistance());
    }

    /**
     * Constructs a KMeansPlusPlusClustering instance with specified distance.
     * The maximum number of iterations is set to Integer.MAX_VALUE by default, i.e. until the centroids have converged.
     *
     * @param numberClusters The number of clusters to be generated.
     * @param distanceMetric The distance metric to use.
     */
    public KMeansPlusPlusClustering(int numberClusters, Distance distanceMetric) {
        this(numberClusters, Integer.MAX_VALUE, distanceMetric);
    }

    /**
     * Constructs a KMeansPlusPlusClustering instance with specified max iterations and distance.
     *
     * @param numberClusters The number of clusters to be generated.
     * @param maxIterations  The maximum number of iterations allowed for the clustering algorithm.
     * @param distanceMetric The distance metric to use.
     */
    public KMeansPlusPlusClustering(int numberClusters, int maxIterations, Distance distanceMetric) {
        Args.nullNotPermitted(distanceMetric, "distanceMetric");

        this.numberClusters = numberClusters;
        this.maxIterations = maxIterations;
        this.distanceMetric = distanceMetric;
        this.centroidClusters = new ArrayList<>();
        this.centroidConvergence = new HashMap<>();
    }


    /**
     * Performs the clustering algorithm on the given list of items and returns a list of clusters.
     * Each cluster contains a subset of the input items that are grouped together based on their similarity.
     *
     * @param items The list of items to be clustered.
     * @return A list of Cluster objects containing the resulting clusters.
     */
    public List<CentroidCluster<T>> cluster(List<T> items) {
        // Select initial centroids
        selectInitialCentroids(items, numberClusters);

        // Perform initial assignment of items
        assignItemsToClusters(items);

        int it = 0;
        boolean converged = false;
        while(!converged && it < maxIterations) {
            updateCentroids();

            assignItemsToClusters(items);

            converged = checkConvergence();

            it++;
        }

        return centroidClusters;
    }

    private void assignItemsToClusters(List<T> items) {
        // Clear current member items
        for (CentroidCluster<T> cc : centroidClusters) {
            cc.getMembers().clear();
        }

        // Assign items to clusters
        for (T item : items) {
            double[] point = item.clusterablePoint();
            int nearestCentroidIndex = findNearestCentroid(point, centroidClusters);
            centroidClusters.get(nearestCentroidIndex).addMember(item);
        }
    }

    private void updateCentroids() {
        // Compute new centroids from members
        for (CentroidCluster<T> cc : centroidClusters) {
            double[] newCentroid = calculateMean(cc.getMembers());
            // Track convergence
            double[] previousCentroid = cc.getCentroid();
            centroidConvergence.put(cc, Arrays.equals(previousCentroid, newCentroid));

            cc.updateCentroid(newCentroid);
        }
    }

    private int findNearestCentroid(double[] point, List<CentroidCluster<T>> centroidClusters) {
        double minDistance = Double.MAX_VALUE;
        int nearestCentroidIndex = 0;

        for (int i = 0; i < centroidClusters.size(); ++i) {
            double distance = distanceMetric.compute(point, centroidClusters.get(i).getCentroid());
            if (distance < minDistance) {
                minDistance = distance;
                nearestCentroidIndex = i;
            }
        }

        return nearestCentroidIndex;
    }

    private double[] calculateMean(List<T> items) {
        int dimensions = items.get(0).clusterablePoint().length;
        double[] sum = new double[dimensions];
        int count = items.size();

        for (T item : items) {
            double[] point = item.clusterablePoint();
            for (int i = 0; i < dimensions; ++i) {
                sum[i] += point[i];
            }
        }

        double[] mean = new double[dimensions];
        for (int i = 0; i < dimensions; ++i) {
            mean[i] = sum[i] / count;
        }

        return mean;
    }

    private void selectInitialCentroids(List<T> items, int numberCentroids) {
        int numPoints = items.size();

        // Step 1: Randomly select the first centroid
        Random random = new Random();
        int initialCentroidIndex = random.nextInt(numPoints);
        double[] initialCentroid = items.get(initialCentroidIndex).clusterablePoint();

        // Store the selected centroids
        List<double[]> centroids = new ArrayList<>();
        centroids.add(initialCentroid);

        // Step 2 and 3: Select remaining centroids
        while (centroids.size() < numberCentroids) {
            double[] distances = new double[numPoints];
            double totalDistance = 0.0;

            // Calculate the distance of each point from the nearest centroid
            for (int i = 0; i < numPoints; ++i) {
                double[] point = items.get(i).clusterablePoint();
                double minDistance = Double.MAX_VALUE;

                // Find the minimum distance to any existing centroid
                for (double[] centroid : centroids) {
                    double distance = distanceMetric.compute(point, centroid);
                    minDistance = Math.min(minDistance, distance);
                }

                distances[i] = minDistance;
                totalDistance += minDistance;
            }

            // Choose the next centroid based on distance probability
            double cumulativeProbability = 0.0;
            double randomValue = random.nextDouble() * totalDistance;

            for (int i = 0; i < numPoints; ++i) {
                cumulativeProbability += distances[i];
                if (cumulativeProbability >= randomValue) {
                    centroids.add(items.get(i).clusterablePoint());
                    break;
                }
            }
        }

        // Create initial centroid clusters
        for(int i=0; i < centroids.size(); ++i) {
            CentroidCluster<T> centroidCluster = new CentroidCluster<>(i, centroids.get(i));
            centroidClusters.add(centroidCluster);
            centroidConvergence.put(centroidCluster, false);
        }
    }

    private boolean checkConvergence() {
        for (CentroidCluster<T> cc : centroidClusters) {
            if(!centroidConvergence.get(cc))
                return false;
        }
        return true;
    }

}
