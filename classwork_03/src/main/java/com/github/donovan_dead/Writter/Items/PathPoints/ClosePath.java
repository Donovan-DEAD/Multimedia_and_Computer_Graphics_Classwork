package com.github.donovan_dead.Writter.Items.PathPoints;

import com.github.donovan_dead.Writter.Items.PathPoint;

public class ClosePath implements PathPoint {

    public ClosePath() {
    }

    @Override
    public String toPathString() {
        return "Z";
    }
}
