package com.github.donovan_dead;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;

import com.github.donovan_dead.Utils.DoubleParser;
import com.github.donovan_dead.tools.Compressor;
import com.github.donovan_dead.tools.Decompressor;

/**
 * Main class for the image compression and decompression application.
 * This class handles command-line argument parsing and orchestrates the compression
 * or decompression process based on the provided arguments.
 */
public class Main{
    /**
     * Default epsilon values for Y, Cb, Cr channels used in compression.
     * These values represent the tolerance for color clustering.
     */
    private static double[] eps = new double[]{ 0.215d, 0.135d, 0.135d};
    /**
     * The BufferedImage to be compressed. Loaded from the input path.
     */
    private static BufferedImage imgToCompress;
    /**
     * The FileInputStream for the image to be decompressed. Loaded from the input path.
     */
    private static FileInputStream imgToDecompress;
    /**
     * The output path where the compressed or decompressed file will be saved.
     * Defaults to the current directory.
     */
    private static Path pathOut = Paths.get("./");
    /**
     * Flag to indicate the operation mode. True for compression, false for decompression.
     */
    private static boolean compressing = true;
    /**
     * Flag to indicate if the help message should be displayed.
     */
    private static boolean help = false;

    /**
     * Prints the command-line arguments help information to the console.
     * Sets the {@code help} flag to true to prevent further processing.
     */
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


    /**
     * Parses the command-line arguments provided to the application.
     * It identifies flags such as operation mode (-dc), input path (-pi), output path (-po),
     * epsilon values (-ep), and help flag (-h). It performs basic validation on paths and values.
     *
     * @param args The array of command-line arguments.
     * @throws RuntimeException If critical arguments are missing or invalid.
     */
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
                            if (nombre.endsWith(".bimg") || nombre.endsWith(".bimg.gz")) {
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


    /**
     * The entry point of the application.
     * Parses command-line arguments, initializes the compressor or decompressor,
     * and executes the corresponding operation. Handles potential exceptions during parsing or execution.
     *
     * @param args The command-line arguments passed to the program.
     */
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