package com.github.donovan_dead.Utils;

import java.awt.Color;

public class ColorPoint {
    private Color c;
    private float[] coordinates = {0f, 0f};

    public ColorPoint(Color c, float x, float y){
        this.c = c;
        this.coordinates[0] = x;
        this.coordinates[1] = y;
    }


    public int obtainR(){
        return this.c.getRed();
    }
    public int obtainG(){
        return this.c.getGreen();
    }
    public int obtainB(){
        return this.c.getBlue();
    }

    public float posX(){return this.coordinates[0];}
    public float posY(){return this.coordinates[1];}

}
