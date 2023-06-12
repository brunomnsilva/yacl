package com.brunomnsilva.yacl.core;

import java.util.Arrays;

/**
 * Represents a cluster of objects around a centroid point.
 *
 * @param <T> the type of objects contained in the cluster, must implement the {@link Clusterable} interface
 *
 * @author brunomnsilva
 */
public class CentroidCluster<T extends Clusterable<T>> extends Cluster<T> {

    private double[] centroid;

    /**
     * Constructs a new Cluster with the specified ID and initial centroid point.
     *
     * @param id the ID of the cluster
     * @param initialCentroid the initial centroid point
     */
    public CentroidCluster(int id, double[] initialCentroid) {
        super(id);
        this.centroid = initialCentroid;
    }

    /**
     * Updates the centroid of the cluster.
     * @param newCentroid the new centroid
     */
    public void updateCentroid(double[] newCentroid) {
        centroid = newCentroid;
    }

    /**
     * Returns the centroid point of the cluster.
     * @return the centroid point of the cluster
     */
    public double[] getCentroid() {
        return centroid;
    }

    /**
     * Returns a string representation of the cluster.
     *
     * @return a string representation of the cluster
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Cluster Id = %d\n", getId()));
        sb.append(String.format("Cluster centroid = %s\n", Arrays.toString(centroid)));
        sb.append(String.format("Members (%d) = {\n", size()));
        for (T member : getMembers()) {
            sb.append("\t").append(member.clusterableLabel()).append("\n");
        }
        sb.append("}\n");
        return sb.toString();
    }
}
