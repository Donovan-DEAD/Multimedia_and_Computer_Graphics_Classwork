package com.github.donovan_dead.Drawer.Utils;

import java.awt.Color;

public class ColorPoint {
    private float startingPoint;
    private Color color;


    public ColorPoint(float percentege, Color color){
        this.color = color;
        this.startingPoint = percentege;
    }

    public float getStartingPoint() {return this.startingPoint;}
    public Color getColor(){return this.color;}

    public void DoSomething(){
        Color c = new Color(0); 
    }

    public static float[] CalculateDifference(ColorPoint c1 , ColorPoint c2){
        float[] diff = {
            c1.getColor().getRed()-c2.getColor().getRed(),
            c1.getColor().getGreen()-c2.getColor().getGreen(),
            c1.getColor().getBlue()-c2.getColor().getBlue()};

        return diff;
    }
}
