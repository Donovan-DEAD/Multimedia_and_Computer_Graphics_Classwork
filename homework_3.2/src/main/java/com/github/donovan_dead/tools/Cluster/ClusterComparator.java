package com.github.donovan_dead.tools.Cluster;

import java.util.Comparator;

public class ClusterComparator implements Comparator<ColorCluster> {
    
    @Override
    public int compare(ColorCluster o1, ColorCluster o2){
        return Integer.compare(o2.getPoints(), o1.getPoints());
    }
}
