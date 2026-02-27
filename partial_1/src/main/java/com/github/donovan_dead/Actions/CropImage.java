package com.github.donovan_dead.Actions;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadFactory;

import com.github.donovan_dead.Utils.Calculator;
import com.github.donovan_dead.Utils.SortByDegree;

/**
 * The `CropImage` class implements the `Actionable` interface to provide image cropping functionality.
 * It supports both rectangular and irregular quadrilateral cropping based on provided points.
 */
public class CropImage implements Actionable{
    /** An array of `Point2D` objects defining the cropping area. */
    private Point2D[] points = new Point2D[4];
    /** A boolean flag indicating whether the crop operation is a simple rectangle. */
    private boolean isRectangle =false;

    /**
     * Constructor for rectangular cropping.
     *
     * @param p1 The first point defining the rectangle.
     * @param p2 The second point defining the rectangle.
     */
    public CropImage(Point2D p1, Point2D p2){
        points[0] = p1;
        points[1] = p2;
        isRectangle = true;
    }

    /**
     * Constructor for irregular quadrilateral cropping.
     *
     * @param p1 The first point of the quadrilateral.
     * @param p2 The second point of the quadrilateral.
     * @param p3 The third point of the quadrilateral.
     * @param p4 The fourth point of the quadrilateral.
     */
    public CropImage(Point2D p1, Point2D p2, Point2D p3, Point2D p4){
        points[0] = p1;
        points[1] = p2;
        points[2] = p3;
        points[3] = p4;
    }

    /**
     * Corrects the given points to be within the bounds of the image and adjusts for coordinate system differences.
     *
     * @param img The `BufferedImage` to use for boundary checking.
     * @return An array of `Point2D` objects with corrected coordinates.
     */
    private Point2D[] getCorrectedPoints(BufferedImage img){
        Point2D[] correctedPoints = new Point2D[points.length];
        for(int i = 0; i < points.length; i++) {
            if(points[i] == null) continue;
            
            double x = points[i].getX();
            double y = points[i].getY();

            if(y > img.getHeight()) y = img.getHeight();
            if(y < 0) y = 0;

            if(x < 0) x = 0;
            if(x > img.getWidth()) x = img.getWidth();

            correctedPoints[i] = new Point2D.Double(x, img.getHeight() - y);
        }
        return correctedPoints;
    }

    /**
     * Applies the cropping action to the input image.
     * Depending on the constructor used, it performs either a rectangular or an irregular quadrilateral crop.
     *
     * @param img The `BufferedImage` to be cropped.
     * @return A new `BufferedImage` representing the cropped image.
     */
    @Override
    public BufferedImage ApplyAction( BufferedImage img ){
        Point2D[] correctedPoints = getCorrectedPoints(img);

        if(isRectangle) return cropRectangle(img, correctedPoints);
        else {
            SortByDegree.calculateCentroid(correctedPoints);
            SortByDegree sd = new SortByDegree();
            Arrays.sort(correctedPoints, sd);

            return cropIrregularRectangule(img, correctedPoints);
        }
    }

    /**
     * Crops a rectangular portion of the image.
     *
     * @param img The original `BufferedImage`.
     * @param pts An array containing two `Point2D` objects defining the rectangle.
     * @return A new `BufferedImage` representing the rectangularly cropped image.
     */
    private BufferedImage cropRectangle( BufferedImage img, Point2D[] pts ){

        BufferedImage newImg = img.getSubimage(
            (int)Math.min(pts[0].getX(), pts[1].getX()), 
            (int)Math.min(pts[0].getY(), pts[1].getY()),
            (int)Math.abs(pts[1].getX()-pts[0].getX()), 
            (int)Math.abs(pts[1].getY()-pts[0].getY())
        );
        
        return newImg;
    }

    /**
     * Crops an irregular quadrilateral portion of the image.
     * This method uses multithreading to process the image pixels efficiently.
     *
     * @param img The original `BufferedImage`.
     * @param pts An array containing four `Point2D` objects defining the irregular quadrilateral.
     * @return A new `BufferedImage` representing the irregularly cropped image.
     */
    private BufferedImage cropIrregularRectangule( BufferedImage img, Point2D[] pts ){
        double minX = img.getWidth() + 1;
        double minY = img.getHeight() + 1;
        double maxX = -1;
        double maxY = -1;


        for(Point2D p : pts){
            if(p == null) continue;

            if(p.getX() < minX) minX = p.getX();
            if(p.getX() > maxX) maxX = p.getX();

            if(p.getY() < minY) minY = p.getY();
            if(p.getY() > maxY) maxY = p.getY();
        
        }

        BufferedImage newImg = new BufferedImage(
            (int) Math.max(1, Math.abs(maxX - minX)),
            (int) Math.max(1, Math.abs(maxY - minY)),
            BufferedImage.TYPE_INT_RGB
        );

        ArrayList<Thread> threads = new ArrayList<>();
        ThreadFactory factory = Thread.ofVirtual().factory();

        final double cropArea = Calculator.CalculateAreaOfRectangule(pts);
        final double tolerance = 1d + 1e-6;
        
        for (int h = (int) minY; h < (int) maxY; h++) {
            final int height = h;
            final int startW = (int) minX;
            final int endW = (int) maxX;
            final int mY = (int) minY;

            Runnable r = () -> {
                for (int w = startW; w < endW; w++) {
                    Point2D p = new Point2D.Double(w, height);

                    double areaSum =
                      Calculator.CalculateAreaOfTriangule(pts[0], pts[1], p)
                    + Calculator.CalculateAreaOfTriangule(pts[1], pts[2], p)
                    + Calculator.CalculateAreaOfTriangule(pts[2], pts[3], p)
                    + Calculator.CalculateAreaOfTriangule(pts[3], pts[0], p);

                    boolean dentro = cropArea * tolerance >= areaSum;

                    if (dentro) {
                        newImg.setRGB(w - startW, height - mY, img.getRGB(w, height));
                    } else {
                        newImg.setRGB(w - startW, height - mY, Color.BLACK.getRGB());
                    }
                }
            };

            Thread t = factory.newThread(r);
            threads.add(t);
            t.start();
        }

        for(Thread t : threads){
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return newImg;
    }
}
