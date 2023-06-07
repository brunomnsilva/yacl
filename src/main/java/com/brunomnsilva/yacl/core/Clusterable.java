package com.brunomnsilva.yacl.core;

/**
 * This interface represents an object that can be clustered.
 *
 * @param <T> the type of the object
 *
 * @author brunomnsilva
 */
public interface Clusterable<T> {

    /**
     * Calculates the distance (similarity) between this object and the specified other object.
     * <br/>
     * It is expected that the distance be always <code>&ge; 0</code>.
     * <br/>
     * This method must be implemented when working with clustering procedures that operate
     * over distance matrices, e.g., hierarchical clustering.
     *
     * @param other the other object to calculate the distance to
     * @return the distance between this object and the other object
     */
    double clusterableDistance(T other);

    /**
     * Returns the spatial coordinates of the clusterable.
     * <br/>
     * This method must be implemented when working with clustering procedures that operate
     * over data points, e.g., k-means clustering.
     *
     * @return the spatial coordinates of the clusterable
     */
    double[] clusterablePoint();

    /**
     * Returns the label of the clusterable.
     * <br/>
     * This label may be used to label dendogram leaves and when printing cluster members.
     *
     * @return the label of the clusterable
     */
    String clusterableLabel();
}
