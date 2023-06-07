package com.brunomnsilva.yacl.core;

/**
 * An implementation of the Manhattan distance.
 *
 * @see Distance
 *
 * @author brunomnsilva
 */
public class ManhattanDistance implements Distance {
    @Override
    public double compute(double[] p1, double[] p2) throws DimensionMismatchException {
        Args.nullNotPermitted(p1, "p1");
        Args.nullNotPermitted(p2, "p2");
        Args.requireDimensionMatch(p1, "p1", p2, "p2");

        double distance = 0.0;
        for (int i = 0; i < p1.length; ++i) {
            distance += Math.abs(p1[i] - p2[i]);
        }
        return Math.sqrt(distance);
    }
}
