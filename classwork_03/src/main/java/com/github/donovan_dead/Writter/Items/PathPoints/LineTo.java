package com.github.donovan_dead.Writter.Items.PathPoints;

import com.github.donovan_dead.Writter.Items.PathPoint;

public class LineTo implements PathPoint {
    private float x;
    private float y;

    public LineTo(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toPathString() {
        return "L" + x + "," + y;
    }
}
