package com.github.donovan_dead.SysyemCoordinates;

public class PolarCoord {
    private double angleInRad = 0;
    private double radius = 0;
    private int id;
    private static int count = 0;

    public PolarCoord(){
        this.radius = 0;
        this.angleInRad = 0;
        
        this.id = PolarCoord.count;
        PolarCoord.count++;
    }

    public PolarCoord(double angle, double r){
        this.angleInRad = angle;
        this.radius = r;

        this.id = PolarCoord.count;
        PolarCoord.count++;
    }

    public PolarCoord(double angle, double r, boolean isDegree){
        if(isDegree) this.angleInRad = Math.toRadians(angle);
        else this.angleInRad = angle;
        
        this.id = PolarCoord.count;
        PolarCoord.count++;
    }

    @Override
    public String  toString(){
        return "[ " + this.id + " ] PolarCoord = ( " + this.radius + ", " + this.angleInRad + ") ";
    }

    public double getRadius(){
        return this.radius;
    }

    public double getAngle(){
        return this.angleInRad;
    }

    public double getAngle(boolean inDegrees){
        if(inDegrees) return Math.toDegrees(this.angleInRad);
        else return this.angleInRad;
    }

    public int getId(){
        return this.id;
    }
}
