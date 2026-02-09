package com.github.donovan_dead.tools.Cluster;

import java.util.Comparator;

/**
 * A comparator for sorting ColorCluster objects based on the number of points they contain.
 * It sorts clusters in descending order of their point count.
 */
public class ClusterComparator implements Comparator<ColorCluster> {
    
    /**
     * Compares two ColorCluster objects based on their point count.
     * Clusters with more points come before clusters with fewer points.
     *
     * @param o1 The first ColorCluster to compare.
     * @param o2 The second ColorCluster to compare.
     * @return A negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
     */
    @Override
    public int compare(ColorCluster o1, ColorCluster o2){
        return Integer.compare(o2.getPoints(), o1.getPoints());
    }
}
