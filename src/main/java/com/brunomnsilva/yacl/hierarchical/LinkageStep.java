package com.brunomnsilva.yacl.hierarchical;

/**
 * Represents a step in the hierarchical clustering process, indicating the merging of two clusters.
 *
 * @author brunomnsilva
 */
public class LinkageStep {

    private final int clusterId1;
    private final int clusterId2;
    private final int clusterIdMerge;
    private final int clusterMergeMemberCount;
    private final double clusterLevel;

    /**
     * Constructs a new LinkageStep with the specified parameters.
     *
     * @param clusterId1             the ID of the first cluster being merged
     * @param clusterId2             the ID of the second cluster being merged
     * @param clusterIdMerge         the ID of the newly merged cluster
     * @param clusterMergeMemberCount the number of members in the merged cluster
     * @param clusterLevel           the level at which the clusters are merged
     */
    public LinkageStep(int clusterId1, int clusterId2, int clusterIdMerge, int clusterMergeMemberCount, double clusterLevel) {
        this.clusterId1 = clusterId1;
        this.clusterId2 = clusterId2;
        this.clusterIdMerge = clusterIdMerge;
        this.clusterMergeMemberCount = clusterMergeMemberCount;
        this.clusterLevel = clusterLevel;
    }

    /**
     * Returns the ID of the first cluster being merged.
     *
     * @return the ID of the first cluster being merged
     */
    public int getClusterId1() {
        return clusterId1;
    }

    /**
     * Returns the ID of the second cluster being merged.
     *
     * @return the ID of the second cluster being merged
     */
    public int getClusterId2() {
        return clusterId2;
    }

    /**
     * Returns the ID of the newly merged cluster.
     *
     * @return the ID of the newly merged cluster
     */
    public int getClusterIdMerge() {
        return clusterIdMerge;
    }

    /**
     * Returns the number of members in the merged cluster.
     *
     * @return the number of members in the merged cluster
     */
    public int getClusterMergeMemberCount() {
        return clusterMergeMemberCount;
    }

    /**
     * Returns the level at which the clusters are merged.
     *
     * @return the level at which the clusters are merged
     */
    public double getClusterLevel() {
        return clusterLevel;
    }

    /**
     * Returns a string representation of the LinkageStep object.
     *
     * @return a formatted string containing the cluster IDs, cluster level, merged cluster ID, and member count
     */
    @Override
    public String toString() {
        return String.format("%4d, %4d, %.4f, %4d (%d)",
                clusterId1,
                clusterId2,
                clusterLevel,
                clusterIdMerge,
                clusterMergeMemberCount);
    }
}

