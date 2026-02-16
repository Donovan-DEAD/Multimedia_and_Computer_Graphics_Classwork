package com.github.donovan_dead.Writter.Items.PathPoints;

import com.github.donovan_dead.Writter.Items.PathPoint;

public class CubicBezierCurveTo implements PathPoint {
    private float x1;
    private float y1;
    private float x2;
    private float y2;
    private float x;
    private float y;

    public CubicBezierCurveTo(float x1, float y1, float x2, float y2, float x, float y) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.x = x;
        this.y = y;
    }

    @Override
    public String toPathString() {
        return "C" + x1 + "," + y1 + " " + x2 + "," + y2 + " " + x + "," + y;
    }
}
