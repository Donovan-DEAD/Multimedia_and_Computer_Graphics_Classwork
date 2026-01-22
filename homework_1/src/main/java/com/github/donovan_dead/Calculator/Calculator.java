package com.github.donovan_dead.Calculator;

import java.util.Scanner;

import com.github.donovan_dead.Figures.Figure;

public class Calculator {
    private static Scanner scanner = new Scanner(System.in);

    public static void run(){
        long actionOption;
        long figureOption;

        while (true) {
            printActionOptions();
            actionOption = scanner.nextLong();
            scanner.nextLine();


            if(outOfBound(1,5,actionOption)) continue;

            if(actionOption == 5) break;

            printFigureOptions();
            figureOption = scanner.nextLong();
            scanner.nextLine();
            if(figureOption==7) continue;

            if(outOfBound(1,6,figureOption)){
                System.out.println("Invalid option");
                continue;
            }
            
            Figure figure = com.github.donovan_dead.Figures.Factory.FigureFactory.getFigure((int) figureOption);
            

            switch ((int)actionOption) {
                case 1:
                    System.out.println("The area is: " + figure.getArea());
                    break;
                case 2:
                    System.out.println("The perimeter is: " + figure.getPerimeter());
                    break;

                case 3:
                    System.out.println("The area is: " + figure.getArea()); 
                    System.out.println("The perimeter is: " + figure.getPerimeter());
                    break;

                case 4:
                    System.out.println(figure.toString());
                    break;
                default:
                    System.out.println("Invalid option");
                    break;
            }
        }

        scanner.close();
    }

    private static void printActionOptions(){
        System.out.println();
        System.out.println("Select an option:");
        System.out.println("1-Calculate the area of a figure");
        System.out.println("2-Calculate the perimeter of a figure");
        System.out.println("3-Calculate both");
        System.out.println("4-Calculate both and see general data");
        System.out.println("5-Exit");
    }

    private static void printFigureOptions(){
        System.out.println();
        System.out.println("Select a figure:");
        System.out.println("1-Circle");
        System.out.println("2-Rectangule");
        System.out.println("3-Square");
        System.out.println("4-Triangule");
        System.out.println("5-Pentagon");
        System.out.println("6-Partial Circle");
        System.out.println("7-Return");
    }


    private static boolean outOfBound(long low_bound, long high_bound, long option){
        return option < low_bound || option > high_bound;
    }
}
