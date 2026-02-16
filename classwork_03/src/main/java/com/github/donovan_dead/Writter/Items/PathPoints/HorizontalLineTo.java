package com.github.donovan_dead.Writter.Items.PathPoints;

import com.github.donovan_dead.Writter.Items.PathPoint;

public class HorizontalLineTo implements PathPoint {
    private float x;

    public HorizontalLineTo(float x) {
        this.x = x;
    }

    @Override
    public String toPathString() {
        return "H" + x;
    }
}
