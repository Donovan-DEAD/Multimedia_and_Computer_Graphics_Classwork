package com.github.donovan_dead.Utils;

import java.awt.geom.Point2D;

/**
 * The `Calculator` class provides static utility methods for performing geometric calculations,
 * such as calculating the area of triangles and quadrilaterals.
 */
public class Calculator {
    /**
     * Calculates the area of a triangle defined by three points using the shoelace formula.
     *
     * @param a The first vertex of the triangle.
     * @param b The second vertex of the triangle.
     * @param c The third vertex of the triangle.
     * @return The area of the triangle, or -1 if any point is null.
     */
    public static double CalculateAreaOfTriangule(Point2D a, Point2D b, Point2D c){
        if(a == null || b == null || c == null) return -1d;
        
        return Math.abs((b.getX() - a.getX())*( c.getY() - a.getY()) - (c.getX() - a.getX())*(b.getY() -  a.getY()))/2d;
    }

    /**
     * Calculates the area of an axis-aligned rectangle defined by two opposite corner points.
     *
     * @param a The first corner point of the rectangle.
     * @param b The second, opposite corner point of the rectangle.
     * @return The area of the rectangle, or -1 if any point is null.
     */
    public static double CalculateAreaOfRectangule(Point2D a, Point2D b){
        if(a == null || b == null) return -1d;

        return Math.abs(b.getX() - a.getX()) * Math.abs(b.getY() - a.getY());
    }
    
    /**
     * Calculates the area of a quadrilateral defined by an array of four points.
     * It does this by splitting the quadrilateral into two triangles and summing their areas.
     *
     * @param points An array of four `Point2D` objects representing the vertices of the quadrilateral.
     * @return The area of the quadrilateral, or -1 if the input is invalid (not 4 points or contains nulls).
     */
    public static double CalculateAreaOfRectangule(Point2D[] points){
        if(points.length != 4) return -1d;
        if(points[0] == null || points[1] == null || points[2] == null || points[3] == null) return -1d;
        
        return Calculator.CalculateAreaOfTriangule(points[0], points[1], points[2]) + Calculator.CalculateAreaOfTriangule(points[2], points[3], points[0]);
    }
    
}
