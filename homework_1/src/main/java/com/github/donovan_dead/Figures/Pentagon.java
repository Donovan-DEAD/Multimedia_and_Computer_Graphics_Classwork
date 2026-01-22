package com.github.donovan_dead.Figures;

public class Pentagon extends Figure {
    private long side;
    private long apothem;

    public Pentagon() {
        super();
    }

    @Override
    public void calculateArea() {
        setArea((5 * getSide() * getApothem()) / 2);
    }

    @Override
    public void calculatePerimeter() {
        setPerimeter(5 * getSide());
    }

    @Override
    public void getData() {
        System.out.println("Introduce the next data for the pentagon:");
        System.out.print("Side: ");
        setSide(scanner.nextLong());
        System.out.print("Apothem: ");
        setApothem(scanner.nextLong());
        scanner.nextLine();
    }

    @Override
    public String toString() {
        return "Pentagon{" +
                "side=" + side +
                ", apothem=" + apothem +
                ", area=" + getArea() +
                ", perimeter=" + getPerimeter() +
                '}';
    }

    public long getSide() {
        return side;
    }

    public void setSide(long side) {
        if (side < 0) this.side = 0;
        else this.side = side;
    }

    public long getApothem() {
        return apothem;
    }

    public void setApothem(long apothem) {
        if (apothem < 0) this.apothem = 0;
        else this.apothem = apothem;
    }
}
