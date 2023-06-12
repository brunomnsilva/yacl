package com.brunomnsilva.yacl.hierarchical;

import com.brunomnsilva.yacl.core.Clusterable;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that performs hierarchical clustering on a list of samples using a specific
 * linkage type. It allows clustering based on different linkage strategies such as single,
 * complete, average, weighted, and Ward's linkage.
 * <br/>
 * The underlying linkage implementations rely on distance matrices, hence the clusterable items must define
 * the {@link Clusterable#clusterableDistance(Object)} ()} method.
 *
 * @param <T> the type of objects to be clustered, must implement the {@link Clusterable} interface
 *
 * @author brunomnsilva
 */
public class HierarchicalClustering<T extends Clusterable<T>> {

    private final Linkage<T> linkage;

    /**
     * Constructs a HierarchicalClustering object with the specified linkage type.
     *
     * @param linkageType the type of linkage to use ("single", "complete", "average", "weighted" or "ward")
     * @throws IllegalArgumentException if an invalid linkage type is provided
     */
    public HierarchicalClustering(String linkageType) {
        switch(linkageType) {
            case "single" -> linkage = new SingleLinkage<>();
            case "complete" -> linkage = new CompleteLinkage<>();
            case "average" -> linkage =  new AverageLinkage<>();
            case "weighted" -> linkage = new WeightedLinkage<>();
            case "ward" -> linkage = new WardLinkage<>();

            default -> throw new IllegalArgumentException(String.format("Invalid linkage: %s", linkageType));
        }
    }

    /**
     * Performs hierarchical clustering on the provided list of items.
     * <br/>
     * The algorithm ensures all items belong to a cluster.
     * @param items the list of samples to cluster
     * @return the result of the hierarchical clustering
     */
    public HierarchicalClusteringResult<T> cluster(List<T> items) {
        // Create a copy of the items list to keep their ordering from this point on.
        // Otherwise, if the order of 'samples' was changed externally, the linkage results would not be correct
        // Recall that Linkage uses samples/clusters IDs.
        List<T> samplesCopy = new ArrayList<>(items);

        linkage.compute(samplesCopy);

        return new HierarchicalClusteringResult<>(samplesCopy, linkage);
    }
}
