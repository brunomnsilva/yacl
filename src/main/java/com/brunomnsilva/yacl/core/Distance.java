package com.brunomnsilva.yacl.core;

/**
 * Interface representing a distance metric used to compute the distance between two points in a vector space.
 *
 * @author brunomnsilva
 */
public interface Distance {

    /**
     * Computes the distance between two points represented by double arrays.
     *
     * @param p1 the first point represented as a double array
     * @param p2 the second point represented as a double array
     * @return the distance between the two points
     * @throws DimensionMismatchException if the dimensions of the input arrays do not match
     */
    double compute(double[] p1, double[] p2) throws DimensionMismatchException;
}
