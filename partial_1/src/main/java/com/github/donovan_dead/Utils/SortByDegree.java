package com.github.donovan_dead.Utils;

import java.util.Comparator;
import java.awt.geom.Point2D;

/**
 * The `SortByDegree` class is a `Comparator` used to sort an array of `Point2D` objects
 * in a clockwise order around a central point (centroid). The centroid must be calculated
 * before the comparator is used.
 */
public class SortByDegree implements Comparator<Point2D> {
    /** The central point around which the sorting is performed. */
    private static Point2D centroid;

    /**
     * Calculates the centroid (average position) of an array of points.
     * The result is stored in the static `centroid` field.
     *
     * @param points An array of `Point2D` objects.
     */
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

    /**
     * Returns the last calculated centroid.
     *
     * @return The `Point2D` representing the centroid.
     */
    public static Point2D getCentroid(){
        return centroid;
    }

    /**
     * Compares two points based on their angle relative to the static centroid.
     * This method is used by sorting algorithms to order the points clockwise.
     *
     * @param a The first point to compare.
     * @param b The second point to compare.
     * @return -1, 0, or 1 if the angle of the first point is less than, equal to,
     *         or greater than the angle of the second point.
     */
    @Override
    public int compare (Point2D a, Point2D b){

        double anglePointA = Math.atan2(a.getY() - centroid.getY(), a.getX() - centroid.getX());
        double anglePointB = Math.atan2(b.getY() - centroid.getY(), b.getX() - centroid.getX());

        if(anglePointA > anglePointB) return 1;
        if(anglePointA < anglePointB) return -1;
        return 0;
    }
    
}
