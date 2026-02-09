package com.github.donovan_dead.tools;

import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

public class Decompressor {

    public static void runDecompression(FileInputStream fileIn, Path pathOut) throws IOException {
        DataInputStream dis = new DataInputStream(fileIn);

        int width = dis.readInt();
        int height = dis.readInt();
        int colorCount = dis.readInt();

        HashMap<Integer, Integer> colorPalette = new HashMap<>();
        for (int i = 0; i < colorCount; i++) {
            colorPalette.put(i, dis.readInt());
        }

        List<Byte> compressedDataList = new ArrayList<>();
        while (dis.available() > 0) {
            compressedDataList.add(dis.readByte());
        }

        byte[] compressedData = new byte[compressedDataList.size()];
        for (int i = 0; i < compressedDataList.size(); i++) {
            compressedData[i] = compressedDataList.get(i);
        }

        BufferedImage decompressedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        int x = 0;
        int y = 0;
        int unaryCount = 0;

        for (byte b : compressedData) {
            for (int i = 7; i >= 0; i--) {
                if (y >= height) break;
                
                boolean isBitSet = ((b >> i) & 1) == 1;
                
                if (isBitSet) {
                    unaryCount++;
                } else {
                    int color = colorPalette.get(unaryCount);
                    decompressedImage.setRGB(x, y, color);
                    x++;
                    if (x >= width) {
                        x = 0;
                        y++;
                    }
                    unaryCount = 0;
                }
            }
             if (y >= height) break;
        }

        ImageIO.write(decompressedImage, "png", Paths.get(pathOut.toString(), "decompressed.png").toFile());
        System.out.println("Decompression complete. Image saved to " + pathOut.toString() + "/decompressed.png");
    }
}