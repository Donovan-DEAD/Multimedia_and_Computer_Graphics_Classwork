package com.github.donovan_dead.Utilities;

import com.github.donovan_dead.SysyemCoordinates.CartesianCoord;
import com.github.donovan_dead.SysyemCoordinates.PolarCoord;

public class PolarRoseEq {
    private double amplitude;
    private double frequency;
    private double initialRotation;
    private int id;

    private static int count;

    public PolarRoseEq(){
        this.amplitude = 1d;
        this.frequency = 1d;
        this.initialRotation = 0;

        this.id = PolarRoseEq.count;
        PolarRoseEq.count++;
    }
    
    public PolarRoseEq(double amplitude){
        this.amplitude = amplitude;
        this.frequency = 1d;
        this.initialRotation = 0;

        this.id = PolarRoseEq.count;
        PolarRoseEq.count++;
    }
    
    public PolarRoseEq(double amplitude, double frequency){
        this.amplitude = amplitude;
        this.frequency = frequency;
        this.initialRotation = 0;

        this.id = PolarRoseEq.count;
        PolarRoseEq.count++;
    }
    
    public PolarRoseEq(double amplitude, double frequency, double initialRotation){
        this.amplitude = amplitude;
        this.frequency = frequency;
        this.initialRotation = initialRotation;

        this.id = PolarRoseEq.count;
        PolarRoseEq.count++;
    }  

    public PolarRoseEq(double amplitude, double frequency, double initialRotation, boolean inDegree){
        this.amplitude = amplitude;
        this.frequency = frequency;
        this.initialRotation = (inDegree)? Math.toRadians(initialRotation) : initialRotation;

        this.id = PolarRoseEq.count;
        PolarRoseEq.count++;
    }

    public double computeEquation(double angle){
        return this.amplitude  * Math.cos(this.frequency * angle + this.initialRotation);
    }

    public double computeEquation(double angle, boolean inDegree){
        return this.amplitude  * Math.cos(this.frequency * Math.toRadians(angle) + this.initialRotation);
    }

    public CartesianCoord computeToCartesian(double angle){
        double radius = this.computeEquation(angle);

        return new CartesianCoord(
            radius * Math.cos(angle),
            radius * Math.sin(angle)
        );
    }

    public CartesianCoord computeToCartesian(double angle, boolean inDegree){
        if (inDegree) angle = Math.toRadians(angle);
        double radius = this.computeEquation(angle);
        
        return new CartesianCoord(
            radius * Math.cos(angle),
            radius * Math.sin(angle)
        );
    }

    public PolarCoord computeToPolar(double angle){
        double radius = this.computeEquation(angle);

        return  new PolarCoord(angle, radius);
    }

    public PolarCoord computeToPolar(double angle, boolean inDegree){
        if (inDegree) angle = Math.toRadians(angle);
        double radius = this.computeEquation(angle);

        return  new PolarCoord(angle, radius);
    }

    public int getId(){
        return this.id;
    }

    public double getAmplitude(){
        return this.amplitude;
    }

    public double getFrequency(){
        return this.frequency;
    }

    public double getInitialRotation(){
        return this.initialRotation;
    }
}
