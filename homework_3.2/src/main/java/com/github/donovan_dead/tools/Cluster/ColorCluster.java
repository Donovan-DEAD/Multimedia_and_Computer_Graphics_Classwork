package com.github.donovan_dead.tools.Cluster;

import java.awt.Color;

import com.github.donovan_dead.Utils.Colors.YCbCr;

/**
 * Represents a cluster of colors in YCbCr color space. This class is used
 * for color quantization, where similar colors are grouped together.
 */
public class ColorCluster {
    /**
     * An array defining the tolerance for each YCbCr component (Y, Cb, Cr).
     * Colors within this tolerance of the centroid are considered part of the cluster.
     */
    private double[] tolerance = new double[]{0.1d, 0.1d, 0.1d};
    /**
     * The number of color points currently associated with this cluster.
     */
    private float count_points;
    /**
     * The centroid of the color cluster, represented in YCbCr color space.
     */
    private YCbCr centroid;

    /**
     * Constructs a new ColorCluster with a specified tolerance and an initial color.
     * The initial color sets the first centroid of the cluster.
     *
     * @param tolerance An array of doubles representing the tolerance for Y, Cb, Cr channels.
     * @param initColor The initial Color object that defines the cluster's starting centroid.
     */
    public ColorCluster(double[] tolerance, Color initColor){
        this.tolerance = tolerance;
        
        this.count_points = 0;

        this.centroid = new YCbCr(initColor);
    }

    /**
     * Returns the number of color points that are currently part of this cluster.
     * @return The number of points in the cluster.
     */
    public int getPoints(){
        return (int)this.count_points;
    }

    /**
     * Returns the centroid color of the cluster in RGB format.
     * @return The Color object representing the cluster's centroid.
     */
    public Color getCentroid(){
        return this.centroid.ToColor();
    }

    /**
     * Checks if a given color falls within the tolerance range of this cluster's centroid
     * in the YCbCr color space.
     * @param c The Color to check.
     * @return true if the color is within range, false otherwise.
     */
    public boolean isInRange(Color c){
        YCbCr ycbcr = new YCbCr(c);

        if(
            Math.abs(ycbcr.getY() - this.centroid.getY()) <= this.tolerance[0] &&
            Math.abs(ycbcr.getCb() - this.centroid.getCb()) <= this.tolerance[1] &&
            Math.abs(ycbcr.getCr() - this.centroid.getCr()) <= this.tolerance[2]
        ) return true;

        return false;
    }

    /**
     * Adjusts the centroid of the cluster to include a new color.
     * The centroid is updated incrementally to reflect the average of all colors in the cluster.
     * @param c The new Color to include in the cluster.
     */
    public void adjustCentroid(Color c){
        YCbCr ycbcr = new YCbCr(c);

        double newY = this.centroid.getY() + (ycbcr.getY() - this.centroid.getY()) / (this.count_points + 1);
        double newCb = this.centroid.getCb() + (ycbcr.getCb() - this.centroid.getCb()) / (this.count_points + 1);
        double newCr = this.centroid.getCr() + (ycbcr.getCr() - this.centroid.getCr()) / (this.count_points + 1);

        this.centroid.setY(newY);
        this.centroid.setCb(newCb);
        this.centroid.setCr(newCr);

        this.count_points += 1;
    }
}
