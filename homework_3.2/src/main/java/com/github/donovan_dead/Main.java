package com.github.donovan_dead;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;

import com.github.donovan_dead.Utils.DoubleParser;
import com.github.donovan_dead.tools.Compressor;
import com.github.donovan_dead.tools.Decompressor;

public class Main{
    private static double[] eps = new double[]{ 0.1d, 0.1d, 0.1d};
    private static BufferedImage imgToCompress;
    private static FileInputStream imgToDecompress;
    private static Path pathOut = Paths.get("./");
    private static boolean compressing = true;
    private static boolean help = false;

    private static void PrintArgumentsHelp(){
        System.out.println("The arguments to run the program are the next:");
        System.out.println("-dc  Flag to indicate if it is decompressing. default compressing true without flag. It comes first");
        System.out.println("-pi Flag that indicates the path to the image to apply conversion ");
        System.out.println("-po Flag that indicates the path where is going put the result. default actual directory.");
        System.out.println("-ep Flag that indicates the epsilon values for each channel, it can be 1 or 3 floats in the range of 0 to 1. default 0.1 in each channel.");
        System.out.println("-h  Flag to print the options for this program to run");
        System.out.println();
        System.out.println("Only -pi is necessary for the compressor to work.");
        System.out.println();

        Main.help = true;
    }

    private static void ArgsParser(String[] args) throws RuntimeException{
        if( args.length == 0 ) throw new Error("Not enough arguments to run program");
        Path p;

        for(int i = 0; i < args.length; i++){
            switch (args[i]) {
                case "-h":
                    Main.PrintArgumentsHelp();
                    return;

                case "-pi":
                    p = Paths.get(args[i+1]);
                    
                    if(Main.compressing){
                        if(Files.exists(p) && Files.isRegularFile(p)){
                            try{
                                BufferedImage img  = ImageIO.read(p.toFile());
                                Main.imgToCompress = img;
                            } catch(Exception e){
                                throw new RuntimeException("Error on  -pi argument:" + e .getMessage());
                            }
                        } else throw new RuntimeException("Error on  -pi argument: The path is not valid or the file doesnt exist.");
                    
                    } else {
                        
                        if (Files.exists(p) && Files.isRegularFile(p)) {
                    
                            String nombre = p.getFileName().toString().toLowerCase();
                            if (nombre.endsWith(".bimg")) {
                                try {
                                    FileInputStream fis = new FileInputStream(p.toFile());
                                    Main.imgToDecompress = fis;
                                } catch (Exception e) {
                                    throw new RuntimeException("Error on -pi argument: " + e.getMessage());
                                }
                            } else throw new RuntimeException("Error on -pi argument: The file is not a .bimg binary.");
  
                        } else  throw new RuntimeException("Error on -pi argument: The path is not valid or the file doesn't exist.");
                    
                    }

                    i += 1;
                    break;
                
                case "-po":
                    p = Paths.get(args[i+1]);
                    
                    if(Files.exists(p) && Files.isDirectory(p)) Main.pathOut = p;
                    
                    else if (Files.isDirectory(p)){
                        try { 
                            Files.createDirectories(p);
                        } catch (Exception e) {
                            System.out.println("Error on  -po argument: Error creating the directories for the output image.");
                        }               
                    } 
                    
                    else throw new RuntimeException("Error on  -po argument: The argument passed was not a directory.");
                    
                    i += 1;
                    break;

                case "-ep":
                    try {
                        
                        DoubleParser.ParseDouble(args[i+1], 0d, 1d , eps, 0, -1d);
                        DoubleParser.ParseDouble(args[i+2], 0d, 1d , eps, 1, -1d);
                        DoubleParser.ParseDouble(args[i+3], 0d, 1d , eps, 2, -1d);
                    } catch (Exception e) {
                        
                    }

                    if(eps[2] != -1d){ 
                        i += 3;
                        break;
                    }
                    else if (eps[0] == -1d) {
                        System.out.println("No epsilon was setted so default values are going to be used");
                        eps[0] = 0.1d;
                        eps[1] = 0.1d;
                        eps[2] = 0.1d;
                    }
                    
                    i += 1;
                    break;

                case "-dc":
                    Main.compressing = false;
                    break;
                default:
                    break;
            }
        }

    }

    public static void main(String[] args) {
        
        try { 
            ArgsParser(args);
            
            if(Main.help) return;

            if(Main.compressing){
                Compressor.InitCompressor(eps, imgToCompress, pathOut.toString());
                Compressor.RunCompression();
            } else {
                if (imgToDecompress == null) {
                    System.err.println("Error: Decompression flag '-dc' was used, but no input file was provided with '-pi'.");
                    return;
                }
                Decompressor.runDecompression(imgToDecompress, pathOut);
            }

        } catch(Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return;
        }
    }
}