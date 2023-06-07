package com.brunomnsilva.yacl.core;

import java.util.*;

/**
 * Represents a distance matrix that stores pairwise distances (double) between items of type T.
 *
 * @param <T> the type of items in the distance matrix
 *
 * @author brunomnsilva
 */
public class DistanceMatrix<T> {

    private final Map<T, Map<T, Double>> matrix;

    /**
     * Constructs a new empty DistanceMatrix.
     */
    public DistanceMatrix() {
        this.matrix = new HashMap<>();
    }

    /**
     * Creates a DistanceMatrix from a list of items.
     * <br/>
     * The distances between pairs of items is obtained through the {@link Clusterable#clusterableDistance(Object)} method.
     *
     * @param <E> the type of items, must implement the {@link Clusterable} interface
     * @param items the list of items from which to create the distance matrix
     *
     * @return a DistanceMatrix object containing pairwise distances between the items
     * @throws IllegalArgumentException if the items list is null
     */
    public static <E extends Clusterable<E>> DistanceMatrix<E> fromClusterableDistances(List<E> items) throws IllegalArgumentException {
        Args.nullNotPermitted(items, "samples");
        int numSamples = items.size();

        DistanceMatrix<E> matrix = new DistanceMatrix<>();
        for (int i = 0; i < numSamples; ++i) {
            for (int j = i; j < numSamples; ++j) {
                E sample1 = items.get(i);
                E sample2 = items.get(j);
                double distance = sample1.clusterableDistance(sample2);

                matrix.setDistance(sample1, sample2, distance);
            }
        }

        return matrix;
    }

    /**
     * Creates a DistanceMatrix from a list of items.
     * <br/>
     * The distances between pairs of items is obtained by
     * using the provided <code>distanceMetric</code> and the respective {@link Clusterable#clusterablePoint()} vectors.
     *
     * @param <E> the type of items, must implement the {@link Clusterable} interface
     * @param items the list of items from which to create the distance matrix
     * @param distanceMetric the distance metric to use
     * @return a DistanceMatrix object containing pairwise distances between the items
     * @throws IllegalArgumentException if the items list is null
     */
    public static <E extends Clusterable<E>> DistanceMatrix<E> fromPointDistances(List<E> items, Distance distanceMetric) throws IllegalArgumentException {
        Args.nullNotPermitted(items, "samples");
        Args.nullNotPermitted(distanceMetric, "distanceMetric");

        int numSamples = items.size();

        DistanceMatrix<E> matrix = new DistanceMatrix<>();
        for (int i = 0; i < numSamples; ++i) {
            for (int j = i; j < numSamples; ++j) {
                E sample1 = items.get(i);
                E sample2 = items.get(j);
                double distance = distanceMetric.compute(sample1.clusterablePoint(), sample2.clusterablePoint());

                matrix.setDistance(sample1, sample2, distance);
            }
        }

        return matrix;
    }

    /**
     * Sets the distance between two items in the distance matrix.
     *
     * @param item1    the first item
     * @param item2    the second item
     * @param distance the distance between the items
     */
    public void setDistance(T item1, T item2, double distance) {
        matrix.computeIfAbsent(item1, k -> new HashMap<>()).put(item2, distance);
        matrix.computeIfAbsent(item2, k -> new HashMap<>()).put(item1, distance);
    }

    /**
     * Gets the distance between two items in the distance matrix.
     *
     * @param item1 the first item
     * @param item2 the second item
     * @return the distance between the items, or Double.NaN if the distance is not available
     */
    public double getDistance(T item1, T item2) {
        return matrix.getOrDefault(item1, new HashMap<>()).getOrDefault(item2, Double.NaN);
    }

    /**
     * Removes the distances associated with a specific item from the distance matrix.
     *
     * @param item the item for which to remove distances
     */
    public void removeDistancesOf(T item) {
        matrix.remove(item);
    }

    /**
     * Returns the size of the distance matrix, i.e., number lines/columns.
     *
     * @return the size of the distance matrix
     */
    public int size() {
        return matrix.size();
    }

    /**
     * Returns the list of items in the distance matrix.
     *
     * @return the list of items in the distance matrix
     */
    public List<T> items() {
        return new ArrayList<>(matrix.keySet());
    }

    /**
     * Returns a string representation of the DistanceMatrix object.
     * This will generate a tabular representation of the distance matrix, where each line is
     * preceded by the toString() information of the respective T item.
     *
     * @return a string representation of the DistanceMatrix object
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Set<T> keySet = matrix.keySet();
        int maxToStringWidth = 0;

        // Get maximum T.toString() width
        for (T item : keySet) {
            int length = item.toString().length();
            if (length > maxToStringWidth) maxToStringWidth = length;
        }
        String format = "%" + maxToStringWidth + "s ";

        for (T item1 : keySet) {
            sb.append(String.format(format, item1.toString()));
            for (T item2 : keySet) {
                double distance = getDistance(item1, item2);
                sb.append(String.format(" %4.3f ", distance));
            }
            sb.append("\n");
        }

        return sb.toString();
    }
}

