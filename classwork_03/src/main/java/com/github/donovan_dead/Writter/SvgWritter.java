package com.github.donovan_dead.Writter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.github.donovan_dead.Writter.Items.SvgComponent;

public class SvgWritter {
    private FileOutputStream file;
    private ArrayList<SvgComponent> contents;

    private int width;
    private int height;
    private String standar = "http://www.w3.org/2000/svg";
    private float version = 1.1f;

    public SvgWritter(int width, int height, String fileName){
        if(!fileName.endsWith(".svg")) throw new RuntimeException("No valid name for file");

        this.width = width;
        this.height = height;
        this.contents = new ArrayList<SvgComponent>();
        
        try {
            this.file = new FileOutputStream(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void AppendComponent(SvgComponent component){
        this.contents.add(component);
    }

    public void SaveOnFile() throws IOException{
        if(file == null) return;

        if(contents.size() != 0){
            file.write( ("<svg xmlns=\"" + this.standar +  "\" width=\"" + this.width + "\" height=\""+this.height + "\" version=\""+this.version  + "\">").getBytes() );

            for(SvgComponent svg : contents ){
                file.write(svg.Data());
            }

            file.write( "</svg>".getBytes() );
        } else System.err.println("No elements to write on file: " + file.getFD().toString());

        file.close();
    }

    @Override
    protected void finalize() throws IOException{
        if(file == null) return;

        if(contents.size() != 0){
            file.write( ("<svg xmlns=\"" + this.standar +  "\" width=\"" + this.width + "\" height=\""+this.height + "\" version=\""+this.version  + "\">").getBytes() );

            for(SvgComponent svg : contents ){
                file.write(svg.Data());
            }

            file.write( "</svg>".getBytes() );
        } else System.err.println("No elements to write on file: " + file.getFD().toString());

        file.close();
    }
}
