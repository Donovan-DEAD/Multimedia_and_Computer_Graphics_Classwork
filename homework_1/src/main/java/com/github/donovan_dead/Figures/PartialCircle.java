package com.github.donovan_dead.Figures;

public class PartialCircle extends Figure {
    private long radio;
    private long angle;

    public PartialCircle() {
        super();
    }

    @Override
    public void calculateArea() {
        setArea((long) ((Math.PI * getRadio() * getRadio() * getAngle()) / 360));
    }

    @Override
    public void calculatePerimeter() {
        setPerimeter((long) ((2 * Math.PI * getRadio() * getAngle()) / 360 + (2*getRadio())));
    }

    @Override
    public void getData() {
        System.out.println("Introduce the next data for the partial circle:");
        System.out.print("Radio: ");
        setRadio(scanner.nextLong());
        System.out.print("Angle: ");
        setAngle(scanner.nextLong());
        scanner.nextLine();
    }

    @Override
    public String toString() {
        return "PartialCircle{" +
                "radio=" + radio +
                ", angle=" + angle +
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

    public long getAngle() {
        return angle;
    }

    public void setAngle(long angle) {
        if (angle < 0) this.angle = 0;
        else this.angle = angle%360;
    }
}
