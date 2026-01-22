package com.github.donovan_dead.Figures;

public class Triangule extends Figure {
    private long base;
    private long height;
    private long side1;
    private long side2;

    public Triangule() {
        super();
    }

    @Override
    public void calculateArea() {
        setArea((getBase() * getHeight()) / 2);
    }

    @Override
    public void calculatePerimeter() {
        setPerimeter(getBase() + getSide1() + getSide2());
    }

    @Override
    public void getData() {
        System.out.println("Introduce the next data for the triangle:");
        System.out.print("Base: ");
        setBase(scanner.nextLong());
        System.out.print("Height: ");
        setHeight(scanner.nextLong());
        System.out.print("Side 1: ");
        setSide1(scanner.nextLong());
        System.out.print("Side 2: ");
        setSide2(scanner.nextLong());
        scanner.nextLine();
    }

    @Override
    public String toString() {
        return "Triangule{" +
                "base=" + base +
                ", height=" + height +
                ", side1=" + side1 +
                ", side2=" + side2 +
                ", area=" + getArea() +
                ", perimeter=" + getPerimeter() +
                '}';
    }

    public long getBase() {
        return base;
    }

    public void setBase(long base) {
        if (base < 0) this.base = 0;
        else this.base = base;
    }

    public long getHeight() {
        return height;
    }

    public void setHeight(long height) {
        if (height < 0) this.height = 0;
        else this.height = height;
    }

    public long getSide1() {
        return side1;
    }

    public void setSide1(long side1) {
        if (side1 < 0) this.side1 = 0;
        else this.side1 = side1;
    }

    public long getSide2() {
        return side2;
    }

    public void setSide2(long side2) {
        if (side2 < 0) this.side2 = 0;
        else this.side2 = side2;
    }
}
