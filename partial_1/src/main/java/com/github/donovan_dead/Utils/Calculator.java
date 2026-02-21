package com.github.donovan_dead.Utils;

import java.awt.geom.Point2D;

public class Calculator {
    public static double CalculateAreaOfTriangule(Point2D a, Point2D b, Point2D c){
        if(a == null || b == null || c == null) return -1d;
        
        return Math.abs((b.getX() - a.getX())*( c.getY() - a.getY()) - (c.getX() - a.getX())*(b.getY() -  a.getY()))/2d;
    }

    public static double CalculateAreaOfRectangule(Point2D a, Point2D b){
        if(a == null || b == null) return -1d;

        return Math.abs(b.getX() - a.getX()) * Math.abs(b.getY() - a.getY());
    }
    
    public static double CalculateAreaOfRectangule(Point2D[] points){
        if(points.length != 4) return -1d;
        if(points[0] == null || points[1] == null || points[2] == null || points[3] == null) return -1d;
        
        return Calculator.CalculateAreaOfTriangule(points[0], points[1], points[2]) + Calculator.CalculateAreaOfTriangule(points[2], points[3], points[0]);
    }
    
}
