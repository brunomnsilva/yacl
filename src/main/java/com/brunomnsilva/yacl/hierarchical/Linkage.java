package com.brunomnsilva.yacl.hierarchical;

import com.brunomnsilva.yacl.core.*;

import java.util.*;


/**
 * An abstract class representing a generic linkage procedure for hierarchical clustering.
 * It provides the basic framework for performing agglomerative clustering and storing the
 * resulting linkage steps.
 * <br/>
 * The linkage methods rely on distance matrices, hence the clusterable items must define
 * the {@link Clusterable#clusterableDistance(Object)} ()} method.
 * <br/>
 * Each linkage strategy has its own characteristics and can lead to different cluster structures. The choice of linkage strategy depends on the specific data set and the clustering objective. Ward's linkage is particularly popular due to its ability to produce well-defined, compact clusters.
 * <br/>
 * An AgglomerationMethod represents the Lance-Williams dissimilarity update formula
 * used for hierarchical agglomerative clustering.
 * The general form of the Lance-Williams matrix-update formula:
 * d[(i,j),k] = ai*d[i,k] + aj*d[j,k] + b*d[i,j] + g*|d[i,k]-d[j,k]|
 * <br/>
 * Parameters ai, aj, b, and g are defined differently for different methods:
 * <br/>
 * Method          ai                   aj                   b                          g
 * -------------   ------------------   ------------------   ------------------------   -----
 * Single          0.5                  0.5                  0                          -0.5
 * Complete        0.5                  0.5                  0                          0.5
 * Average         ci/(ci+cj)           cj/(ci+cj)           0                          0
 * <br/>
 * Centroid        ci/(ci+cj)           cj/(ci+cj)           -ci*cj/((ci+cj)*(ci+cj))   0
 * Median          0.5                  0.5                  -0.25                      0
 * Ward            (ci+ck)/(ci+cj+ck)   (cj+ck)/(ci+cj+ck)   -ck/(ci+cj+ck)             0
 * <br/>
 * WeightedAverage 0.5                  0.5                  0                          0
 * <br/>
 * (ci, cj, ck are cluster cardinalities)
 * <br/>
 * Implementation references:
 * [The data analysis handbook. By Ildiko E. Frank, Roberto Todeschini. Pages 152-155]
 * <a href="https://geodacenter.github.io/workbook/7bh_clusters_2a/lab7bh.html#appendix">geodacenter</a>
 * <a href="https://docs.scipy.org/doc/scipy/reference/generated/scipy.cluster.hierarchy.linkage.html#scipy.cluster.hierarchy.linkage">scipy</a>
 *
 * @param <T> the type of objects to be clustered, must implement the {@link Clusterable} interface
 *
 * @author brunomnsilva
 */
public abstract class Linkage<T extends Clusterable<T>> implements Iterable<LinkageStep> {

    private final List<LinkageStep> steps;
    private final Map<Integer, Integer> clusterCardinality;

    /* The distance matrix will contain the distances between clusters identified
       by their "ID"s. Initially, in the agglomerative clustering procedure we'll
       have singleton clusters for each sample. The ID of each cluster follows the
       index of the list of items to cluster.
     */
    private DistanceMatrix<Integer> distanceMatrix;

    /**
     * Constructs a new Linkage object.
     */
    public Linkage() {
        this.steps = new ArrayList<>();
        this.clusterCardinality = new HashMap<>();
        this.distanceMatrix = null;
    }

    /**
     * Computes the hierarchical clustering based on the provided list of items and the desired
     * number of clusters.
     *
     * @param items     the list of items to cluster
     * @param numClusters the desired number of clusters
     */
    public final void compute(List<T> items, int numClusters) {
        Args.nullNotPermitted(items, "items");
        Args.requireGreaterEqualThan(numClusters, "nClusters", 1);

        this.distanceMatrix = computeDistanceMatrixFromClusterableDistances(items);

        int mergeClusterId = items.size();
        while(distanceMatrix.size() > numClusters) {

            // Select next pair to merge
            Tuple<Integer, Integer> stPair = selectMinimumDistanceClusterPair(distanceMatrix);

            // Get distance (cluster level) between the pair of merged clusters, before merging
            Integer sClusterId = stPair.getFirst();
            Integer tClusterId = stPair.getSecond();
            double clusterLevel = distanceMatrix.getDistance(sClusterId, tClusterId);

            // Merge clusters. Subclasses should update the distance matrix
            mergeClusters(stPair, mergeClusterId, distanceMatrix);

            // Update cluster cardinalities for next iteration
            int sCardinality = clusterCardinality.get(sClusterId);
            int tCardinality = clusterCardinality.get(tClusterId);
            int mergeCardinality = sCardinality + tCardinality;

            clusterCardinality.remove(sClusterId);
            clusterCardinality.remove(tClusterId);
            clusterCardinality.put(mergeClusterId, mergeCardinality);

            // Save linkage step
            LinkageStep step = new LinkageStep(sClusterId, tClusterId, mergeClusterId, mergeCardinality, clusterLevel);
            steps.add(step);

            mergeClusterId++;
        }
    }

    /**
     * Computes the hierarchical clustering based on the provided list of items until only one cluster remains.
     *
     * @param items the list of items to cluster
     */
    public final void compute(List<T> items) {
        // Perform full agglomerative procedure until one cluster remains
        compute(items, 1);
    }

    protected void mergeClusters(Tuple<Integer, Integer> pair, int newClusterId,
                                 DistanceMatrix<Integer> distanceMatrix) {
        int iClusterId = pair.getFirst();
        int jClusterId = pair.getSecond();

        int ci = getClusterCardinality(iClusterId);
        int cj = getClusterCardinality(jClusterId);

        // We'll need to remove the iClusterId and jClusterId from the matrix and put a new entry for newClusterId
        // The new distances for newClusterId to the remaining clusters will be the minimum
        List<Integer> clusterIds = distanceMatrix.items();
        for (int kClusterId : clusterIds) {
            if(kClusterId == iClusterId || kClusterId == jClusterId)
                continue;

            double dik = distanceMatrix.getDistance(iClusterId, kClusterId);
            double djk = distanceMatrix.getDistance(jClusterId, kClusterId);
            double dij = distanceMatrix.getDistance(iClusterId, jClusterId);

            int ck = getClusterCardinality(kClusterId);

            double distance = computeMergeDistance(dik, djk, dij, ci, cj, ck);

            distanceMatrix.setDistance(newClusterId, kClusterId, distance);
        }

        distanceMatrix.setDistance(newClusterId, newClusterId, 0);
        distanceMatrix.removeDistancesOf(iClusterId);
        distanceMatrix.removeDistancesOf(jClusterId);
    }

    /**
     * Compute the distance between the
     * newly formed cluster (i,j) and the existing cluster k.
     *
     * @param dik dissimilarity between clusters i and k
     * @param djk dissimilarity between clusters j and k
     * @param dij dissimilarity between clusters i and j
     * @param ci cardinality of cluster i
     * @param cj cardinality of cluster j
     * @param ck cardinality of cluster k
     *
     * @return dissimilarity between cluster (i,j) and cluster k.
     */
    protected abstract double computeMergeDistance(double dik, double djk, double dij, int ci, int cj, int ck);

    /**
     * Checks if the computed linkage is valid, i.e., if there is at least one linkage step.
     *
     * @return true if the linkage is valid, false otherwise
     */
    public boolean isValid() {
        return steps.size() >= 1;
    }

    /**
     * Returns the number of linkage steps in the computed linkage.
     *
     * @return the number of linkage steps
     */
    public int size() {
        return steps.size();
    }

    /**
     * Returns the <code>nth</code> step in the linkage
     * @param nth step number, starting at 0
     * @return the LinkageStep
     */
    public LinkageStep get(int nth) {
        return steps.get(nth);
    }

    /**
     * Returns the last linkage step in the computed linkage.
     *
     * @return the last linkage step
     */
    public LinkageStep last() {
        return steps.get( size() - 1);
    }

    /**
     * Default method for selecting the pair of clusters with the minimum distance.
     *
     * @param distanceMatrix the distance matrix to search for the minimum distance
     * @return the pair of clusters with the minimum distance
     */
    protected Tuple<Integer, Integer> selectMinimumDistanceClusterPair(DistanceMatrix<Integer> distanceMatrix) {
        // By default, all linkages minimize the merge distances, to this must work for much
        // of the subclasses.
        List<Integer> ids = distanceMatrix.items();

        Tuple<Integer, Integer> candidateIds = new Tuple<>(-1, -1);
        double minDistance = Double.MAX_VALUE;
        for(int i=0; i < ids.size() - 1; ++i) {
            for(int j=i+1; j < ids.size(); ++j) {

                int id1 = ids.get(i);
                int id2 = ids.get(j);

                double distance = distanceMatrix.getDistance(id1, id2);
                if(distance < minDistance) {
                    minDistance = distance;
                    candidateIds.setFirst(id1);
                    candidateIds.setSecond(id2);
                }
            }
        }
        return candidateIds;
    }

    /**
     * Returns the cardinality (number of members) of the specified cluster.
     *
     * @param clusterId the ID of the cluster
     * @return the cardinality of the cluster
     */
    protected int getClusterCardinality(int clusterId) {
        return clusterCardinality.get(clusterId);
    }


    private DistanceMatrix<Integer> computeDistanceMatrixFromClusterableDistances(List<T> items) {
        Args.nullNotPermitted(items, "items");
        // The distance matrix will contain the distances between clusters identified
        // by their "ID"s. Initially, in the agglomerative clustering procedure we'll
        // have singleton clusters for each sample. The ID of each cluster follows the
        // index of the list of items to cluster.
        DistanceMatrix<Integer> matrix = new DistanceMatrix<>();

        int numSamples = items.size();
        for(int i = 0; i < numSamples; ++i) {
            for(int j = i; j < numSamples; ++j) {
                T iSample = items.get(i);
                T jSample = items.get(j);
                double distance = iSample.clusterableDistance(jSample);
                matrix.setDistance(i, j, distance);
            }
            // Initially, all clusters are singletons
            clusterCardinality.put(i, 1);
        }
        return matrix;
    }

    private DistanceMatrix<Integer> computeDistanceMatrixFromClusterablePoints(List<T> items, Distance distanceMetric) {
        Args.nullNotPermitted(items, "items");
        Args.nullNotPermitted(distanceMetric, "distanceMetric");
        // The distance matrix will contain the distances between clusters identified
        // by their "ID"s. Initially, in the agglomerative clustering procedure we'll
        // have singleton clusters for each sample. The ID of each cluster follows the
        // index of the list of items to cluster.
        DistanceMatrix<Integer> matrix = new DistanceMatrix<>();

        int numSamples = items.size();
        for(int i = 0; i < numSamples; ++i) {
            for(int j = i; j < numSamples; ++j) {
                T iSample = items.get(i);
                T jSample = items.get(j);
                double distance = distanceMetric.compute(iSample.clusterablePoint(), jSample.clusterablePoint());
                matrix.setDistance(i, j, distance);
            }
            // Initially, all clusters are singletons
            clusterCardinality.put(i, 1);
        }
        return matrix;
    }

    @Override
    public Iterator<LinkageStep> iterator() {
        return steps.iterator();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("Linkage: %s", this.getClass().getSimpleName())).append("\n");
        for (LinkageStep step : this) {
            sb.append(step).append("\n");
        }

        return sb.toString();
    }
}
