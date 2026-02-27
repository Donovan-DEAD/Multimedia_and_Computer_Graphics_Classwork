package com.github.donovan_dead.Actions;

import java.awt.image.BufferedImage;;

/**
 * The `Actionable` interface defines a contract for image manipulation actions.
 * Any class implementing this interface can apply a specific transformation to a `BufferedImage`.
 */
public interface Actionable {
    
    /**
     * Applies a defined action or transformation to the input `BufferedImage`.
     *
     * @param img The `BufferedImage` to which the action will be applied.
     * @return A new `BufferedImage` representing the result of the applied action.
     */
    public BufferedImage ApplyAction(BufferedImage img);
    
} 