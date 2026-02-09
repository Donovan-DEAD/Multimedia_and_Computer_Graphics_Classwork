package com.github.donovan_dead.tools.Cluster;

import java.awt.Color;

import com.github.donovan_dead.Utils.Colors.YCbCr;

public class ColorCluster {
    private double[] tolerance = new double[]{0.1d, 0.1d, 0.1d};
    private float count_points;
    private YCbCr centroid;

    public ColorCluster(double[] tolerance, Color initColor){
        this.tolerance = tolerance;
        
        this.count_points = 0;

        this.centroid = new YCbCr(initColor);
    }

    public int getPoints(){
        return (int)this.count_points;
    }

    public Color getCentroid(){
        return this.centroid.ToColor();
    }

    public boolean isInRange(Color c){
        YCbCr ycbcr = new YCbCr(c);

        if(
            Math.abs(ycbcr.getY() - this.centroid.getY()) <= this.tolerance[0] &&
            Math.abs(ycbcr.getCb() - this.centroid.getCb()) <= this.tolerance[1] &&
            Math.abs(ycbcr.getCr() - this.centroid.getCr()) <= this.tolerance[2]
        ) return true;

        return false;
    }

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
