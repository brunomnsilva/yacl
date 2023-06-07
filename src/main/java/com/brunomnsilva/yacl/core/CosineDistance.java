package com.brunomnsilva.yacl.core;

/**
 * An implementation of the Cosine distance.
 *
 * @see Distance
 *
 * @author brunomnsilva
 */
public class CosineDistance implements Distance {
    @Override
    public double compute(double[] p1, double[] p2) throws DimensionMismatchException {
        Args.nullNotPermitted(p1, "p1");
        Args.nullNotPermitted(p2, "p2");
        Args.requireDimensionMatch(p1, "p1", p2, "p2");

        throw new UnsupportedOperationException("Not implemented yet.");
        /*double distance = 0.0;
        for (int i = 0; i < p1.length; ++i) {
            distance += p1[i] * p2[i];
        }
        return 1 - distance / ( magnitude of p1 * magnitude of p2);*/
    }
}
