package com.github.donovan_dead.Figures;

import java.awt.Color;
import java.awt.geom.Point2D;

import com.github.donovan_dead.Utils.ColorPoint;

public class Triangule {
    private ColorPoint[] vertices;

    public Triangule(ColorPoint v1, ColorPoint v2, ColorPoint v3){
        this.vertices = new ColorPoint[]{v1,v2,v3};
    }

    public Color ReturnColorInTriangule(Point2D p) {
        float x = (float)p.getX();
        float y = (float)p.getY();

        float x1 = vertices[0].posX(), y1 = vertices[0].posY();
        float x2 = vertices[1].posX(), y2 = vertices[1].posY();
        float x3 = vertices[2].posX(), y3 = vertices[2].posY();

        float denom = (y2 - y3)*(x1 - x3) + (x3 - x2)*(y1 - y3);

        float l1 = ((y2 - y3)*(x - x3) + (x3 - x2)*(y - y3)) / denom;
        float l2 = ((y3 - y1)*(x - x3) + (x1 - x3)*(y - y3)) / denom;
        float l3 = 1 - l1 - l2;

        if (l1 < 0 || l2 < 0 || l3 < 0) {
            return Color.BLACK; // fuera del triángulo
        }

        return new Color(
            (l1 * vertices[0].obtainR() + l2 * vertices[1].obtainR() + l3 * vertices[2].obtainR()) / 255f,
            (l1 * vertices[0].obtainG() + l2 * vertices[1].obtainG() + l3 * vertices[2].obtainG()) / 255f,
            (l1 * vertices[0].obtainB() + l2 * vertices[1].obtainB() + l3 * vertices[2].obtainB()) / 255f
        );
    }
}
