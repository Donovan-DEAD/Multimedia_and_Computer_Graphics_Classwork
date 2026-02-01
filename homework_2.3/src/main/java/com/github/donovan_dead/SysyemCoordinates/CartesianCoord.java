package com.github.donovan_dead.SysyemCoordinates;

public class CartesianCoord {
    private double[] coords;
    private static int count = 0;
    private int id;

    public CartesianCoord(){
        this.coords = new double[] {0,0};
        
        this.id = CartesianCoord.count;
        CartesianCoord.count++;
    }

    public CartesianCoord(double x, double y){
        this.coords = new double[]{x,y};
        
        this.id = CartesianCoord.count;
        CartesianCoord.count++;
    }

    public double getX(){
        return this.coords[0];
    }

    public double getY(){
        return this.coords[1];
    }

    public int getId(){
        return this.id;
    }
    
    @Override
    public String  toString(){
        return "[ " + this.id + " ] CartesianCoord = ( " + this.coords[0] + ", " + this.coords[1] + ") ";
    }
}
