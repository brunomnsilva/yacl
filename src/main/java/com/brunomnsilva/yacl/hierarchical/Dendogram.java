package com.brunomnsilva.yacl.hierarchical;

import com.brunomnsilva.yacl.core.Args;
import com.brunomnsilva.yacl.core.Clusterable;

import java.util.*;

/**
 * A dendrogram represents the hierarchical structure of clusters obtained from hierarchical clustering.
 *
 * @param <T> the type of objects in the dendrogram, must implement the {@link Clusterable} interface
 *
 * @author brunomnsilva
 */
public class Dendogram<T extends Clusterable<T>> {

    private final HierarchicalClusteringResult<T> clusteringResult;

    private final DendogramNode<T> dendogramRoot;

    public Dendogram(HierarchicalClusteringResult<T> clusteringResult) {
        Args.nullNotPermitted(clusteringResult, "clusteringResult");

        this.clusteringResult = clusteringResult;
        this.dendogramRoot = generateTree(clusteringResult);
    }

    /**
     * Returns the dendogram tree root node.
     *
     * @return the dendogram tree root node
     */
    public DendogramNode<T> getRoot() {
        return dendogramRoot;
    }

    /**
     * Cuts the dendrogram tree at a specified height, resulting in a list of clusters.
     *
     * @param numClusters the desired number of clusters
     * @return a list of dendrogram nodes representing the clusters
     * @throws IllegalArgumentException if the number of clusters is not within the valid range
     */
    public List<DendogramNode<T>> cutByHeight(int numClusters) {
        Args.requireInRange(numClusters, "numClusters", 1, getLeafNodeCount());

        // Must maintain a sorted queue of nodes (by distance) so to choose with child to "split" next,
        // until we get numClusters clusters.
        PriorityQueue<DendogramNode<T>> unvisited = new PriorityQueue<>();
        unvisited.add(dendogramRoot);

        while(unvisited.size() < numClusters) {
            DendogramNode<T> splitCandidate = unvisited.poll();

            unvisited.remove(splitCandidate);
            DendogramNode<T> left = splitCandidate.getLeft();
            DendogramNode<T> right = splitCandidate.getRight();

            if(left != null) unvisited.offer( left );
            if(right != null) unvisited.offer( right );
        }

        // Return as List
        return new ArrayList<>(unvisited);
    }

    /**
     * Returns a list of leaf nodes in the dendrogram tree in a depth-first order.
     * <br/>
     * The order of the nodes in the returned list will follow the dendogram structure.
     *
     * @param treeRoot the root node of the dendrogram tree
     * @return a list of leaf nodes in the dendrogram tree
     */
    public List<DendogramNode<T>> getLeafNodesOf(DendogramNode<T> treeRoot) {
        List<DendogramNode<T>> orderedLeaves = new ArrayList<>();

        Stack<Dendogram.DendogramNode<T>> stack = new Stack<>();
        stack.push(treeRoot);

        while (!stack.isEmpty()) {
            Dendogram.DendogramNode<T> node = stack.pop();

            // Leaf node check
            if (node.isLeaf()) {
                orderedLeaves.add(node);
            }

            if (node.getLeft() != null) {
                stack.push(node.getLeft());
            }
            if (node.getRight() != null) {
                stack.push(node.getRight());
            }
        }

        return orderedLeaves;
    }


    private DendogramNode<T> generateTree(HierarchicalClusteringResult<T> clusteringResult) {

        Linkage<T> linkageResult = clusteringResult.getLinkageResult();

        // Cache clusterings by step
        Map<Integer, LinkageStep> mapMergeIds = new HashMap<>();
        for (LinkageStep step : linkageResult) {
            mapMergeIds.put( step.getClusterIdMerge(), step);
        }

        // This is important to check if a cluster (by its ID) is a singleton, or not.
        // If (clusterId < numSamples), then it is a singleton cluster
        int numSamples = clusteringResult.getClusteredSamples().size();

        // Last step is the dendogram root node
        LinkageStep linkageRoot = linkageResult.last();
        int rootId = linkageRoot.getClusterIdMerge();
        double rootDistance = linkageRoot.getClusterLevel();

        // Perform depth-first generation
        Stack<DendogramNode<T>> unvisited = new Stack<>();
        DendogramNode<T> root = new DendogramNode<>(rootId, rootDistance);
        unvisited.push(root);

        while(!unvisited.isEmpty()) {
            DendogramNode<T> node = unvisited.pop();
            int clusterId = node.clusterId;
            LinkageStep step = mapMergeIds.get(clusterId);

            LinkageStep step1 = mapMergeIds.get(step.getClusterId1());
            LinkageStep step2 = mapMergeIds.get(step.getClusterId2());

            double distance1 = 0;
            double distance2 = 0;

            if(step1 != null) distance1 = step1.getClusterLevel();
            if(step2 != null) distance2 = step2.getClusterLevel();

            DendogramNode<T> leftChild = new DendogramNode<>(step.getClusterId1(), distance1);
            DendogramNode<T> rightChild = new DendogramNode<>(step.getClusterId2(), distance2);

            leftChild.setParent(node);
            rightChild.setParent(node);

            node.setLeft(leftChild);
            node.setRight(rightChild);

            // If not singleton clusters (see comment above),
            // push them to the stack to be visited. Otherwise, decorate them
            // with the respective clusterable item
            if(step.getClusterId1() >= numSamples) {
                unvisited.push(leftChild);
            } else {
                leftChild.setClusterable(getElementByClusterId(step.getClusterId1()));
            }

            if(step.getClusterId2() >= numSamples) {
                unvisited.push(rightChild);
            } else {
                rightChild.setClusterable(getElementByClusterId(step.getClusterId2()));
            }
        }

        // Return the dendogram root node
        return root;
    }

    private int getLeafNodeCount() {
        // We could compute this from the dendogram, but we know we'll have
        // a leaf node for each clustered sample
        return clusteringResult.getClusteredSamples().size();
    }

    private T getElementByClusterId(int id) {
        return clusteringResult.getClusteredSamples().get(id);
    }


    private void printDendrogram(DendogramNode<T> node, String prefix, StringBuilder stringBuilder) {
        // Internal method used to build a string representation of the dendogram.
        // Since it is recursive, it may fail for a very large dendogram.
        // TODO: refactor to an iterative algorithm?

        stringBuilder.append(String.format("%sCluster %d", prefix, node.getClusterId()));
        if(node.isLeaf()) {
            stringBuilder.append("\n");
        } else {
            stringBuilder.append(String.format(" (%.3f)\n", node.getClusterLevel()));
        }

        List<DendogramNode<T>> children = node.getChildren();
        int lastIndex = children.size() - 1;
        for (int i = 0; i < children.size(); ++i) {
            DendogramNode<T> child = children.get(i);
            boolean isLastChild = (i == lastIndex);

            if (isLastChild) {
                printDendrogram(child, prefix + "└── ", stringBuilder);
            } else {
                printDendrogram(child, prefix + "├── ", stringBuilder);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        printDendrogram(dendogramRoot, "", sb);
        return sb.toString();
    }


    /**
     * Represents a node in a dendrogram, which is used for hierarchical clustering.
     *
     * @param <T> the type of objects being clustered, must implement the {@link Clusterable} interface
     */
    public static class DendogramNode<T extends Clusterable<T>> implements Comparable<DendogramNode<T>> {
        private final int clusterId;
        private final double clusterLevel;
        private DendogramNode<T> left, right;
        private DendogramNode<T> parent;
        private T clusterable; // For dendogram visualization purposes we could just store the T.toString()
                           // value. But element is just a pointer reference and will later allow us to
                           // perform a flat clustering of the samples

        /**
         * Constructs a DendogramNode with the given cluster ID and cluster level.
         *
         * @param clusterId the ID of the cluster
         * @param clusterLevel the level of the cluster
         */
        public DendogramNode(int clusterId, double clusterLevel) {
            this.clusterId = clusterId;
            this.clusterLevel = clusterLevel;
            this.left = this.right = null;
        }

        /**
         * Checks if the node is a leaf node (has no children).
         *
         * @return true if the node is a leaf node, false otherwise
         */
        public boolean isLeaf() {
            return (left == null && right == null);
        }

        /**
         * Returns the clusterable associated with the leaf node.
         *
         * @return the clusterable associated with the leaf node
         * @throws IllegalStateException if the node is not a leaf node
         */
        public T getClusterable() {
            if(!isLeaf()) throw new IllegalStateException("Not a leaf.");
            return clusterable;
        }

        /**
         * Sets the clusterable associated with the leaf node.
         *
         * @param clusterable the clusterable to set
         * @throws IllegalStateException if the node is not a leaf node
         */
        public void setClusterable(T clusterable) {
            if(!isLeaf()) throw new IllegalStateException("Not a leaf.");
            this.clusterable = clusterable;
        }

        /**
         * Returns the cluster ID of the node.
         *
         * @return the cluster ID
         */
        public int getClusterId() {
            return clusterId;
        }

        /**
         * Returns the cluster level of the node.
         *
         * @return the cluster level
         */
        public double getClusterLevel() {
            return clusterLevel;
        }

        /**
         * Returns a list of children nodes.
         *
         * @return the list of children nodes
         */
        public List<DendogramNode<T>> getChildren() {
            List<DendogramNode<T>> children = new ArrayList<>();
            if(left != null) children.add(left);
            if(right != null) children.add(right);
            return children;
        }

        /**
         * Returns the parent node.
         *
         * @return the parent node
         */
        public DendogramNode<T> getParent() {
            return parent;
        }

        /**
         * Sets the parent node.
         *
         * @param parent the parent node to set
         */
        public void setParent(DendogramNode<T> parent) {
            this.parent = parent;
        }

        /**
         * Returns the left child node.
         *
         * @return the left child node
         */
        public DendogramNode<T> getLeft() {
            return left;
        }

        /**
         * Sets the left child node.
         *
         * @param left the left child node to set
         */
        public void setLeft(DendogramNode<T> left) {
            this.left = left;
        }

        /**
         * Returns the right child node.
         *
         * @return the right child node
         */
        public DendogramNode<T> getRight() {
            return right;
        }

        /**
         * Sets the right child node.
         *
         * @param right the right child node to set
         */
        public void setRight(DendogramNode<T> right) {
            this.right = right;
        }

        @Override
        public int compareTo(DendogramNode<T> o) {
            // This is for a priority queue, so higher distances have the highest split priority
            return -1 *  Double.compare(this.clusterLevel, o.clusterLevel);
        }

        @Override
        public String toString() {
            return String.format("DendogramNode{%d}", getClusterId());
        }
    }
}
