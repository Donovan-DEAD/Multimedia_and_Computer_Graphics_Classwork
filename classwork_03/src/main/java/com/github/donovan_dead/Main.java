package com.github.donovan_dead;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import com.github.donovan_dead.Writter.SvgWritter;
import com.github.donovan_dead.Writter.Items.Circle;
import com.github.donovan_dead.Writter.Items.Line;
import com.github.donovan_dead.Writter.Items.Polygon;
import com.github.donovan_dead.Writter.Items.SvgPath;
import com.github.donovan_dead.Writter.Items.PathPoint;
import com.github.donovan_dead.Writter.Items.PathPoints.ClosePath;
import com.github.donovan_dead.Writter.Items.PathPoints.LineTo;
import com.github.donovan_dead.Writter.Items.PathPoints.MoveTo;

public class Main {

    public static void drawImageOne(int w, int h) throws Exception{
        

        SvgWritter img1 = new SvgWritter(w, h, "half.svg");
        
        com.github.donovan_dead.Writter.Items.Polygon triangule1 = new com.github.donovan_dead.Writter.Items.Polygon(
            new Point2D[]{
                new Point2D.Double(0, 0),
                new Point2D.Double((int)w, 0),
                new Point2D.Double((int)w, (int)h),
            },
            1f, "red", 1, 1f, "red"
        );

        com.github.donovan_dead.Writter.Items.Polygon triangule2 = new com.github.donovan_dead.Writter.Items.Polygon(
            new Point2D[]{
                new Point2D.Double((int)0, 0),
                new Point2D.Double(0, (int)h),
                new Point2D.Double((int)w, (int)h),
            },
            1f, "blue", 1, 1f, "blue"
        );

        img1.AppendComponent(triangule1);
        img1.AppendComponent(triangule2);
        img1.SaveOnFile();
    }

    public static void drawImageTwo(int w, int h) throws Exception{
        SvgWritter img2 = new SvgWritter(w, h, "sun.svg");

        int cx = 200;
        int cy = 165;

        Polygon background = new Polygon(
            new Point2D[]{
                new Point2D.Double(0, 0),
                new Point2D.Double(w, 0),
                new Point2D.Double(w, h),
                new Point2D.Double(0, h)
            }, 1f, "#b2e8ff", 1, 1f, "#b2e8ff"
        );

        // Lines that represents the ray lights comming from sun
        Line l1 = new Line(cx,cy+110,cx, cy-110, 1f, "orange", 2);
        Line l2 = new Line(cx-110,cy,cx+110, cy, 1f, "orange",2);
        Line l3 = new Line(cx-65, cy-65, cx+65, cy+65, 1f, "orange", 2);
        Line l4 = new Line(cx-65, cy+65, cx+65, cy-65, 1f, "orange", 2);
        // The sun itself
        Circle sun = new Circle(cx, cy, 60, 1f, "yellow", 3, 1f, "yellow");
        
        List<PathPoint> grassPathPoints = new ArrayList<>();

        float baseLineY = h * 0.7f; 
        float amplitude = 20f;
        float frequency = 0.08f; 
        float stepX = 2f;

        // Start the path at the bottom left
        grassPathPoints.add(new MoveTo(0, baseLineY));

        // Draw the wavy top of the grass
        for (float x = 0; x <= w; x += stepX) {
            float y = baseLineY + amplitude * (float) Math.sin(x * frequency);
            grassPathPoints.add(new LineTo(x, y));
        }

        // Close the path to form a filled shape
        grassPathPoints.add(new LineTo(w, h)); 
        grassPathPoints.add(new LineTo(0, h)); 
        grassPathPoints.add(new ClosePath()); 

        SvgPath grass = new SvgPath(grassPathPoints, 1f, "#4CAF50", 1, 1f, "#4CAF50"); // Green grass


        img2.AppendComponent(background);
        
        img2.AppendComponent(l1);
        img2.AppendComponent(l2);
        img2.AppendComponent(l3);
        img2.AppendComponent(l4);
        img2.AppendComponent(sun);

        img2.AppendComponent(grass); 

        img2.SaveOnFile();
    }

    public static void main(String[] args) {
        try{
            drawImageOne(800,600);
            drawImageTwo(800,600);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}