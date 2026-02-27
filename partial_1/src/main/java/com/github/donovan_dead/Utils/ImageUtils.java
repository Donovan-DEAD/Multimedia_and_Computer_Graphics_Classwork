package com.github.donovan_dead.Utils;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

/**
 * The `ImageUtils` class provides static utility methods for image manipulation.
 */
public class ImageUtils {
    /**
     * Creates a deep copy of a `BufferedImage`.
     * This is useful for creating a new image instance that can be modified
     * without affecting the original image.
     *
     * @param bi The `BufferedImage` to copy.
     * @return A new `BufferedImage` that is a deep copy of the original.
     */
    public static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }
}
