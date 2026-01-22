package com.github.donovan_dead.Figures;

public class Circle extends Figure {
    private long radio;

    public Circle() {
        super();
    }

    @Override
    public void calculateArea() {
        setArea((long) (Math.PI * getRadio() * getRadio()));
    }

    @Override
    public void calculatePerimeter() {
        setPerimeter((long) (2 * Math.PI * getRadio()));
    }

    @Override
    public void getData() {
        System.out.println("Introduce the next data for the circle:");
        System.out.print("Radio: ");
        setRadio(scanner.nextLong());
        scanner.nextLine();
    }

    @Override
    public String toString() {
        return "Circle{" +
                "radio=" + radio +
                ", area=" + getArea() +
                ", perimeter=" + getPerimeter() +
                '}';
    }

    public long getRadio() {
        return radio;
    }

    public void setRadio(long radio) {
        if (radio < 0) this.radio = 0;
        else this.radio = radio;
    }
}
