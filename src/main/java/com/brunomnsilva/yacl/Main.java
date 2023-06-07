package com.brunomnsilva.yacl;

import com.brunomnsilva.yacl.core.*;
import com.brunomnsilva.yacl.hierarchical.Dendogram;
import com.brunomnsilva.yacl.hierarchical.DendogramCut;
import com.brunomnsilva.yacl.hierarchical.HierarchicalClustering;
import com.brunomnsilva.yacl.hierarchical.HierarchicalClusteringResult;
import com.brunomnsilva.yacl.partitioning.KMeansPlusPlusClustering;
import com.brunomnsilva.yacl.view.DendogramVisualization;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Main {
    public static void main(String[] args) {

        Random rnd = new Random(667);

        int numSamples = 50;
        List<MyVectorN> samples = new ArrayList<>();
        for (int i=0; i < numSamples; ++i) {
            samples.add(new MyVectorN(3, rnd));
        }

        // Hierarchical clustering usage
        HierarchicalClustering<MyVectorN> hclust = new HierarchicalClustering<>("ward");
        HierarchicalClusteringResult<MyVectorN> hclustResult = hclust.cluster(samples);

        // Extract 3 clusters from the hierarchical clustering result
        List<Cluster<MyVectorN>> clusters = DendogramCut.byNumberOfClusters(hclustResult, 2);
        for (Cluster<MyVectorN> cluster : clusters) {
            System.out.println(cluster);
        }

        // Dendogram Visualization
        Dendogram<MyVectorN> dendogram = new Dendogram<>(hclustResult);
        DendogramVisualization<MyVectorN> viz = new DendogramVisualization<>(dendogram, DendogramVisualization.LabelType.LABEL);

        JFrame frame = new JFrame("Dendrogram Graph");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(viz);
        frame.setSize(1000, 800);
        frame.setLocationRelativeTo(null); // Center window
        frame.setVisible(true);

        // K-Means++ Usage
        System.out.println("K-MEANS++");
        KMeansPlusPlusClustering<MyVectorN> kmeans = new KMeansPlusPlusClustering<>(3 /*, new ManhattanDistance()*/);
        List<Cluster<MyVectorN>> kclusters = kmeans.cluster(samples);

        for (Cluster<MyVectorN> cluster : kclusters) {
            System.out.println(cluster);
        }
    }

    public static class MyVectorN implements Clusterable<MyVectorN> {
        private double[] vector;

        public MyVectorN(double[] vector) {
            this.vector = vector;
        }

        public MyVectorN(int dimension, Random rnd) {
            this.vector = new double[dimension];
            for(int i=0; i < dimension; ++i) {
                vector[i] = rnd.nextDouble();
            }
        }

        @Override
        public double clusterableDistance(MyVectorN other) {
            // Euclidean distance
            double sum = 0.0;
            for (int i = 0; i < vector.length; ++i) {
                double diff = vector[i] - other.vector[i];
                sum += diff * diff;
            }
            return Math.sqrt(sum);
        }

        @Override
        public double[] clusterablePoint() {
            return vector;
        }

        @Override
        public String clusterableLabel() {
            // This "shortens" the vector components to 3 decimal places
            return ArrayUtils.toString(vector, 3);
        }
    }

}
