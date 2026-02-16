package com.github.donovan_dead.Writter.Items;

import java.awt.geom.Point2D;

public class Polygon extends SvgComponent {
    
    private Point2D[] points;
    
    private Float fillOpacity;
    private String fill;

    private Integer strokeWidth;
    private Float strokeOpacity;
    private String stroke;

    public Polygon(Point2D[] points) throws RuntimeException{
        if( points == null  || points.length == 0 ) throw new RuntimeException("Null pointer pass to Polygon constructor or empty array");

        this.points = points;
    }

    public Polygon(Point2D[] points,   Float fillOpacity, String fill, int strokeWidth, Float strokeOpacity, String stroke) throws RuntimeException{
        
        if( points == null  || points.length == 0 ) throw new RuntimeException("Null pointer pass to Polygon constructor or empty array");
        this.points = points;

        this.fillOpacity = fillOpacity < 0  || fillOpacity > 1 ? 1 : fillOpacity;
        this.fill = fill;
        this.strokeWidth = strokeWidth < 0 ? 1 : strokeWidth;
        this.strokeOpacity = strokeOpacity < 0  || strokeOpacity > 1 ? 1 : strokeOpacity;
        this.stroke = stroke;
    }

    @Override 
    public byte[] Data() {

        StringBuilder finalString = new StringBuilder(100);


        finalString.append(
            "<polygon points=\"" 
        );

        for(Point2D point : points){
            finalString.append(point.getX() + "," + point.getY() + " ");
        }
        finalString.append("\" ");

        if(fill != null) finalString.append("fill=\"" + fill + "\" ");
        if(fillOpacity != null) finalString.append("fill-opacity=\"" + fillOpacity + "\" ");

        if(strokeWidth != null) finalString.append("stroke-width=\"" + strokeWidth + "\" ");
        if(strokeOpacity != null) finalString.append("stroke-opacity=\"" + strokeOpacity + "\" ");
        if(stroke != null) finalString.append("stroke=\"" + stroke + "\" ");

        finalString.append(
            "/>"
        );

        return finalString.toString().getBytes();
    }

}
