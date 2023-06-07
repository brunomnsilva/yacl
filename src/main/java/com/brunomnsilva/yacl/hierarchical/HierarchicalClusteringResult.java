package com.brunomnsilva.yacl.hierarchical;

import com.brunomnsilva.yacl.core.Clusterable;

import java.util.List;

/**
 * Stores the result of a hierarchical clustering process. It contains the clustered samples
 * and the linkage steps that describe the clustering process.
 *
 * @param <T> the type of objects being clustered, must implement the {@link Clusterable} interface
 *
 * @author brunomnsilva
 */
public class HierarchicalClusteringResult<T extends Clusterable<T>> {

    private final List<T> clusteredSamples;

    private final Linkage<T> linkageSteps;

    /**
     * Constructs a HierarchicalClusteringResult object with the provided clustered samples and linkage steps.
     *
     * @param clusteredSamples the list of clustered samples
     * @param linkageSteps the linkage steps describing the clustering process
     */
    public HierarchicalClusteringResult(List<T> clusteredSamples, Linkage<T> linkageSteps) {
        this.clusteredSamples = clusteredSamples;
        this.linkageSteps = linkageSteps;
    }

    /**
     * Returns the list of clustered samples.
     *
     * @return the list of clustered samples
     */
    public List<T> getClusteredSamples() {
        return clusteredSamples;
    }

    /**
     * Returns the linkage result containing the linkage steps describing the clustering process.
     *
     * @return the linkage result
     */
    public Linkage<T> getLinkageResult() {
        return linkageSteps;
    }

    @Override
    public String toString() {
        return String.format("Number clustered samples: %d\n%s", clusteredSamples.size(), linkageSteps);
    }
}
