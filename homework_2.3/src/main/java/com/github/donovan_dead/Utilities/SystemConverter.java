package com.github.donovan_dead.Utilities;

import com.github.donovan_dead.SysyemCoordinates.CartesianCoord;
import com.github.donovan_dead.SysyemCoordinates.PolarCoord;

public class SystemConverter {
    
    public static PolarCoord ToPolar(CartesianCoord c){
        double radius =  Math.sqrt(Math.pow(c.getX(), 2) + Math.pow(c.getY(), 2));
        double rads = Math.acos(c.getX()/radius);

        return new PolarCoord(rads,radius);
    }

    public static CartesianCoord ToCartesian(PolarCoord p){
        return new CartesianCoord(
            p.getRadius() * Math.cos(p.getAngle()),
            p.getRadius() * Math.sin(p.getAngle())
        );
    }
}
