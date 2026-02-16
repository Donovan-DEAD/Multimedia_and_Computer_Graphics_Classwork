package com.github.donovan_dead.Writter.Items;

public class Line extends SvgComponent {
    private int x1;
    private int y1;
    private int x2;
    private int y2;

    private float strokeOpacity;
    private String stroke;
    private int strokeWidth;

    public Line(int x1, int y1, int x2, int y2){
        this.x1 = x1 < 0 ? 0 : x1;
        this.y1 = y1 < 0 ? 0 : y1;
        this.x2 = x2 < 0 ? 0 : x2;
        this.y2 = y2 < 0 ? 0 : y2;
    }

    public Line(int x1, int y1, int x2, int y2, float strokeOpacity, String stroke, int strokeWidth){
        this.x1 = x1 < 0 ? 0 : x1;
        this.y1 = y1 < 0 ? 0 : y1;
        this.x2 = x2 < 0 ? 0 : x2;
        this.y2 = y2 < 0 ? 0 : y2;

        this.strokeOpacity = strokeOpacity < 0  || strokeOpacity > 1 ? 1 : strokeOpacity;
        this.stroke = stroke;
        this.strokeWidth = strokeWidth < 0 ? 1 : strokeWidth;
    }


    @Override
    public byte[] Data(){
        StringBuilder finalString = new StringBuilder(100);
        finalString.append(
            "<line x1=\"" + this.x1 + "\" y1=\"" + this.y1 + "\" x2=\"" + this.x2 + "\" y2=\"" + this.y2 + "\" "
        ); 
        
        finalString.append("stroke=\"" + stroke + "\" ");
        finalString.append("stroke-opacity=\"" + strokeOpacity + "\" ");
        finalString.append("stroke-width=\"" + strokeWidth + "\" ");
        
        finalString.append(
            "/>"
        );

        return finalString.toString().getBytes();
    }

}
