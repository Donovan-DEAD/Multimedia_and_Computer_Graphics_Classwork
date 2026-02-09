package com.github.donovan_dead.tools;

import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

import javax.imageio.ImageIO;

import com.github.donovan_dead.Utils.Bytes.DynamicByteDecontainer; // Import the new class

public class Decompressor {

    public static void runDecompression(FileInputStream fileIn, Path pathOut) throws IOException {
        DataInputStream dis = new DataInputStream(
            new GZIPInputStream(fileIn)
        );

        // Read image metadata
        int width = dis.readInt();
        int height = dis.readInt();
        int colorCount = dis.readInt();

        // Dynamically calculate the number of bits used for the color index
        final int bitsForIndex = (colorCount > 1) ? (int) Math.ceil(Math.log(colorCount) / Math.log(2)) : 1;

        // Read color palette
        HashMap<Integer, Integer> colorPalette = new HashMap<>();
        for (int i = 0; i < colorCount; i++) {
            colorPalette.put(i, dis.readInt());
        }

        byte[] compressedData = dis.readAllBytes();
        dis.close();

        DynamicByteDecontainer decontainer = new DynamicByteDecontainer(compressedData);

        BufferedImage decompressedImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        int x = 0;
        int y = 0;

        while (y < height && decontainer.hasMoreData()) {
            // Read the variable-length color index
            long colorIndex = decontainer.read(bitsForIndex);
            
            // The repeat flag might not exist if we're at the exact end of the stream
            if (!decontainer.hasMoreData()) break;
            long repeatFlag = decontainer.read(1);

            int actualRunLength;
            if (repeatFlag == 1) {
                if (!decontainer.hasMoreData()) break; // Check before reading repeat count
                long repeatCount = decontainer.read(8);
                actualRunLength = (int)repeatCount + 1;
            } else {
                actualRunLength = 1;
            }

            int rgbColor = colorPalette.get((int)colorIndex);

            for (int k = 0; k < actualRunLength; k++) {
                if (y >= height) break;

                decompressedImage.setRGB(x, y, rgbColor);
                x++;
                if (x >= width) {
                    x = 0;
                    y++;
                }
            }
        }

        // Save the decompressed image
        Path outputPath = Paths.get(pathOut.toString(), "decompressed.jpg");
        System.out.println("Attempting to save decompressed image to: " + outputPath.toAbsolutePath());
        ImageIO.write(decompressedImage, "jpg", outputPath.toFile());
        
        if (outputPath.toFile().exists()) {
            System.out.println("Decompression complete. Image saved successfully to " + outputPath.toAbsolutePath());
        } else {
            System.err.println("Error: Decompressed image was not found at " + outputPath.toAbsolutePath() + " after write attempt.");
        }
    }
}