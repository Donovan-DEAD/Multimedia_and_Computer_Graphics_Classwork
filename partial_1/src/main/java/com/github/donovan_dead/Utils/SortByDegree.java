package com.github.donovan_dead.Utils;

import java.util.Comparator;
import java.awt.geom.Point2D;

public class SortByDegree implements Comparator<Point2D> {
    private static Point2D centroid;

    public static void calculateCentroid(Point2D[] points){
        double x = 0;
        double y = 0;

        for(Point2D p : points){
            x += p.getX();
            y += p.getY();
        }
        x /= points.length;
        y /= points.length;

        centroid = new Point2D.Double(x, y);
    }

    public static Point2D getCentroid(){
        return centroid;
    }

    @Override
    public int compare (Point2D a, Point2D b){

        double anglePointA = Math.atan2(a.getY() - centroid.getY(), a.getX() - centroid.getX());
        double anglePointB = Math.atan2(b.getY() - centroid.getY(), b.getX() - centroid.getX());

        if(anglePointA > anglePointB) return 1;
        if(anglePointA < anglePointB) return -1;
        return 0;
    }
    
}
