package com.brunomnsilva.yacl.hierarchical;

import com.brunomnsilva.yacl.core.Cluster;
import com.brunomnsilva.yacl.core.Clusterable;

import java.util.ArrayList;
import java.util.List;

/**
 * A utility class that performs a dendrogram cut on a clustering result to obtain a specified number
 * of clusters.
 *
 * @author brunomnsilva
 */
public class DendogramCut {

    /**
     * Performs a dendrogram cut on a clustering result to obtain a specified number of clusters.
     *
     * @param clusteringResult the hierarchical clustering result
     * @param numClusters      the desired number of clusters
     * @param <T>              the type of objects in the clustering result
     * @return a list of clusters
     */
    public static <T extends Clusterable<T>> List<Cluster<T>> byNumberOfClusters(HierarchicalClusteringResult<T> clusteringResult, int numClusters) {
        // Procedure:
        // 1 - Create a dendogram for the clustering result;
        // 2 - Perform a dendogram cut for the specified number of clusters;
        // 3 - Compose the cluster members from step 2 result

        Dendogram<T> dendogram = new Dendogram<>(clusteringResult);

        List<Dendogram.DendogramNode<T>> clusterRootNodes = dendogram.cutByHeight(numClusters);

        List<Cluster<T>> clusters = new ArrayList<>();

        for (Dendogram.DendogramNode<T> rootNode : clusterRootNodes) {
            // The leaf nodes contain the clustered samples
            Cluster<T> cluster = new Cluster<>(rootNode.getClusterId());

            List<Dendogram.DendogramNode<T>> leafNodes = dendogram.getLeafNodesOf(rootNode);
            for (Dendogram.DendogramNode<T> node : leafNodes) {
                cluster.addMember( node.getClusterable() );
            }

            clusters.add(cluster);
        }

        return clusters;
    }
}
