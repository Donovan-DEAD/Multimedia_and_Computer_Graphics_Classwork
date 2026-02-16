package com.github.donovan_dead.Writter.Items.PathPoints;

import com.github.donovan_dead.Writter.Items.PathPoint;

public class QuadraticBezierCurveTo implements PathPoint {
    private float x1;
    private float y1;
    private float x;
    private float y;

    public QuadraticBezierCurveTo(float x1, float y1, float x, float y) {
        this.x1 = x1;
        this.y1 = y1;
        this.x = x;
        this.y = y;
    }

    @Override
    public String toPathString() {
        return "Q" + x1 + "," + y1 + " " + x + "," + y;
    }
}
