package com.github.donovan_dead.Writter.Items.PathPoints;

import com.github.donovan_dead.Writter.Items.PathPoint;

public class VerticalLineTo implements PathPoint {
    private float y;

    public VerticalLineTo(float y) {
        this.y = y;
    }

    @Override
    public String toPathString() {
        return "V" + y;
    }
}
