package com.github.donovan_dead.Utils.Colors;

import java.awt.Color;

public class YCbCr {
    private double Y;
    private double Cb;
    private double Cr;

    public YCbCr(double Y, double Cb, double Cr){
        this.Y = Y;
        this.Cb = Cb;
        this.Cr = Cr;
    }

    public YCbCr(Color c){        
        double y  = 0.299*(double)c.getRed() + 0.587*(double)c.getGreen() + 0.114*(double)c.getBlue();
        double cb = 128 - 0.168736*(double)c.getRed() - 0.331264*(double)c.getGreen() + 0.5*(double)c.getBlue();
        double cr = 128 + 0.5*(double)c.getRed() - 0.418688*(double)c.getGreen() - 0.081312*(double)c.getBlue();

        this.Y = y;
        this.Cb = cb;
        this.Cr = cr;
    }

    public Color ToColor(){
        return new Color(
             YCbCr.clamp(this.Y + 1.402 * (this.Cr - 128)),
             YCbCr.clamp(this.Y - 0.34414 * (this.Cb - 128) - 0.71414 * (this.Cr - 128)),
             YCbCr.clamp(this.Y + 1.772 * (this.Cb - 128))
            );
    }

    private static int clamp(double val) {
        return (int)Math.max(0, Math.min(255, Math.round(val)));
    }

    public double getY() {
        return Y;
    }
    
    public void setY(double y) {
        Y = y;
    }

    public double getCb() {
        return Cb;
    }

    public void setCb(double cb) {
        Cb = cb;
    }

    public double getCr() {
        return Cr;
    }

    public void setCr(double cr) {
        Cr = cr;
    }
}
