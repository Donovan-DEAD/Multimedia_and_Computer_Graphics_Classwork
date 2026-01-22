package com.github.donovan_dead.Figures;

import java.util.Scanner;

public abstract class Figure {
    private long area = 0;
    private long perimeter = 0;
    protected static Scanner scanner = new Scanner(System.in);


    public Figure() {
        this.getData();
        this.calculateArea();
        this.calculatePerimeter();
    }

    public abstract void calculateArea();
    public abstract void calculatePerimeter();
    public abstract void getData();
    public abstract String toString();


    public long getArea() {
        return area;
    }

    public void setArea(long area) {
        if(area < 0 ) this.area = 0;
        else this.area = area;
    }

    public long getPerimeter() {
        return perimeter;
    }

    public void setPerimeter(long perimeter) {
        if(perimeter < 0 ) this.perimeter = 0;
        else this.perimeter = perimeter;
    }
}
