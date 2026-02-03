package com.github.donovan_dead.FileController;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class WritterForImage {
    private File output;
    private BufferedImage img;

    public WritterForImage(String fileName, BufferedImage img){
        this.output = new File(fileName);
        this.img = img;
    }

    public void saveImage(){
        try{
            ImageIO.write(this.img, "jpg", this.output);
        }catch(IOException e){
            System.err.println(e.getMessage());
        }
    }
}
