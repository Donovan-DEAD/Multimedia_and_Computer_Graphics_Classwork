package com.github.donovan_dead.Figures.Factory;

public class FigureFactory {
    public static com.github.donovan_dead.Figures.Figure getFigure(int option) {
        switch (option) {
            case 1:
                return new com.github.donovan_dead.Figures.Circle();
            case 2:
                return new com.github.donovan_dead.Figures.Rectangule();
            case 3:
                return new com.github.donovan_dead.Figures.Square();
            case 4:
                return new com.github.donovan_dead.Figures.Triangule();
            case 5:
                return new com.github.donovan_dead.Figures.Pentagon();
            case 6:
                return new com.github.donovan_dead.Figures.PartialCircle();
            default:
                return null;
        }
    }
}
