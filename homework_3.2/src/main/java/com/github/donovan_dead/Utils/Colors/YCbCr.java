package com.github.donovan_dead.Utils.Colors;

import java.awt.Color;

/**
 * Represents a color in the YCbCr color space. Y represents luminance (brightness),
 * while Cb and Cr represent the blue-difference and red-difference chrominance components, respectively.
 * This class provides methods for converting between RGB and YCbCr color spaces.
 */
public class YCbCr {
    /**
     * Luminance component (brightness).
     */
    private double Y;
    /**
     * Blue-difference chrominance component.
     */
    private double Cb;
    /**
     * Red-difference chrominance component.
     */
    private double Cr;

    /**
     * Constructs a YCbCr object with specified Y, Cb, and Cr values.
     * @param Y Luminance value.
     * @param Cb Blue-difference chrominance value.
     * @param Cr Red-difference chrominance value.
     */
    public YCbCr(double Y, double Cb, double Cr){
        this.Y = Y;
        this.Cb = Cb;
        this.Cr = Cr;
    }


    /**
     * Constructs a YCbCr object from an RGB Color object.
     * It converts the RGB color components to their corresponding Y, Cb, and Cr values.
     * @param c The Color object to convert.
     */
    public YCbCr(Color c){        
        double y  = 0.299*(double)c.getRed() + 0.587*(double)c.getGreen() + 0.114*(double)c.getBlue();
        double cb = 128 - 0.168736*(double)c.getRed() - 0.331264*(double)c.getGreen() + 0.5*(double)c.getBlue();
        double cr = 128 + 0.5*(double)c.getRed() - 0.418688*(double)c.getGreen() - 0.081312*(double)c.getBlue();

        this.Y = y;
        this.Cb = cb;
        this.Cr = cr;
    }

    /**
     * Converts the YCbCr color components back to an RGB Color object.
     * @return The RGB Color object.
     */
    public Color ToColor(){
        return new Color(
             YCbCr.clamp(this.Y + 1.402 * (this.Cr - 128)),
             YCbCr.clamp(this.Y - 0.34414 * (this.Cb - 128) - 0.71414 * (this.Cr - 128)),
             YCbCr.clamp(this.Y + 1.772 * (this.Cb - 128))
            );
    }

    /**
     * Clamps a double value to the valid range of an RGB color component (0-255).
     * @param val The double value to clamp.
     * @return The clamped integer value.
     */
    private static int clamp(double val) {
        return (int)Math.max(0, Math.min(255, Math.round(val)));
    }

    /**
     * Gets the Luminance (Y) component.
     * @return The Y component value.
     */
    public double getY() {
        return Y;
    }
    
    /**
     * Sets the Luminance (Y) component.
     * @param y The new Y component value.
     */
    public void setY(double y) {
        Y = y;
    }

    /**
     * Gets the Blue-difference chrominance (Cb) component.
     * @return The Cb component value.
     */
    public double getCb() {
        return Cb;
    }

    /**
     * Sets the Blue-difference chrominance (Cb) component.
     * @param cb The new Cb component value.
     */
    public void setCb(double cb) {
        Cb = cb;
    }

    /**
     * Gets the Red-difference chrominance (Cr) component.
     * @return The Cr component value.
     */
    public double getCr() {
        return Cr;
    }

    /**
     * Sets the Red-difference chrominance (Cr) component.
     * @param cr The new Cr component value.
     */
    public void setCr(double cr) {
        Cr = cr;
    }
}
