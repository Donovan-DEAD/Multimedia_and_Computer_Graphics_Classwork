package com.github.donovan_dead.Figures;

public class Square extends Figure {
    private long side;

    public Square(){
        super();
    }

    @Override
    public void calculateArea() {
        setArea(getSide() * getSide() );
    }

    @Override
    public void calculatePerimeter() {
        setPerimeter(getSide() * 4);
    }

    @Override
    public void getData(){
        System.out.println("Introduce the next data for the rectangle:");
        System.out.print("side: ");
        setSide(scanner.nextLong());
        scanner.nextLine();
    }

    @Override
    public String toString() {
        return "Rectangule{" +
                "side=" + side +
                ", area=" + getArea() +
                ", perimeter=" + getPerimeter() +
                '}';
    }

    public long getSide() {
        return side;
    }

    public void setSide(long side) {
        if(side < 0 ) this.side = 0;
        else this.side = side;
    }
}
