package com.github.donovan_dead.Figures;

public class Rectangule extends Figure {
    private long width;
    private long height;

    public Rectangule(){
        super();
    }

    @Override
    public void calculateArea() {
        setArea(getWidth() * getHeight());
    }

    @Override
    public void calculatePerimeter() {
        setPerimeter((getWidth() + getHeight()) * 2);
    }

    @Override
    public void getData(){
        System.out.println("Introduce the next data for the rectangle:");
        System.out.print("Width: ");
        setWidth(scanner.nextLong());
        System.out.print("Height: ");
        setHeight(scanner.nextLong());
        scanner.nextLine();
    }

    @Override
    public String toString() {
        return "Rectangule{" +
                "width=" + width +
                ", height=" + height +
                ", area=" + getArea() +
                ", perimeter=" + getPerimeter() +
                '}';
    }

    public long getWidth() {
        return width;
    }

    public void setWidth(long width) {
        if(width < 0 ) this.width = 0;
        else this.width = width;
    }

    public long getHeight() {
        return height;
    }

    public void setHeight(long height) {
        if(height < 0 ) this.height = 0;
        else this.height = height;
    }
}