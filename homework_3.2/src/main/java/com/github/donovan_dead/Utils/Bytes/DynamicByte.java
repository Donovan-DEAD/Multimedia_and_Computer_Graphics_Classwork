package com.github.donovan_dead.Utils.Bytes;

public class DynamicByte {
    private final byte[] data;
    private final int size;
    /**
     * Constructor que recibe el número de unos a generar.
     * Se crean 'n' bits en 1 y se añade un bit 0 al final.
     *
     * @param onesCount número de bits en 1
     */
    public DynamicByte(int onesCount) {
        if (onesCount < 0) {
            throw new IllegalArgumentException("El número de unos no puede ser negativo.");
        }

        this.size = onesCount;
        
        int totalBits = onesCount + 1;
        int totalBytes = (int) Math.ceil(totalBits / 8.0);

        data = new byte[totalBytes];

        for (int i = 0; i < onesCount; i++) {
            int byteIndex = i / 8;
            int bitIndex = 7 - (i % 8);
            data[byteIndex] |= (1 << bitIndex);
        }

    }

    public byte[] getData() {
        return data;
    }

    public int getSize() {
        return size;
    }

}