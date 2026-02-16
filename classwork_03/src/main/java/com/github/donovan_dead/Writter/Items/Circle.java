package com.github.donovan_dead.Writter.Items;

public class Circle extends SvgComponent {
    private Integer centerX;
    private Integer centerY;
    private Integer radius;
    
    private Float fillOpacity;
    private String fill;

    private Integer strokeWidth;
    private Float strokeOpacity;
    private String stroke;

    public Circle(int centerX, int centerY, int radius){
        this.centerX = centerX < 0 ? 0 : centerX;
        this.centerY = centerY < 0 ? 0 : centerY;
        this.radius = radius < 0 ? 0 : radius;
    }

    public Circle(int centerX, int centerY, int radius, Float fillOpacity, String fill, int strokeWidth, Float strokeOpacity, String stroke){
        this.centerX = centerX < 0 ? 0 : centerX;
        this.centerY = centerY < 0 ? 0 : centerY;
        this.radius = radius < 0 ? 0 : radius;

        this.fillOpacity = fillOpacity < 0  || fillOpacity > 1 ? 1 : fillOpacity;
        this.fill = fill;
        this.strokeWidth = strokeWidth < 0 ? 1 : strokeWidth;
        this.strokeOpacity = strokeOpacity < 0  || strokeOpacity > 1 ? 1 : strokeOpacity;
        this.stroke = stroke;
    }

    public byte[] Data(){
        StringBuilder finalString = new StringBuilder(100);

        finalString.append(
            "<circle cx=\"" + this.centerX + "\" cy=\"" + this.centerY + "\" r=\"" + this.radius + "\" "
        );

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
