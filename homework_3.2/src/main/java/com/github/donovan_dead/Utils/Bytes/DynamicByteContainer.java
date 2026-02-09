package com.github.donovan_dead.Utils.Bytes;

import java.util.ArrayList;
import java.util.List;

public class DynamicByteContainer {
    private final List<DynamicByte> chunks = new ArrayList<>();
    private byte[] packedData;
    private int totalBits = 0;

    public void add(DynamicByte db) {
    chunks.add(db);
    totalBits += db.getSize() + 1;
    packedData = null; // invalidar cache
}

public void append(DynamicByteContainer other) {
    for (DynamicByte db : other.chunks) {
        this.add(db);
    }
    packedData = null; // invalidar cache
}

    public void pack() {
        int totalBytes = (int) Math.ceil(totalBits / 8.0);
        packedData = new byte[totalBytes];

        int bitPos = 0;
        for (DynamicByte db : chunks) {
            byte[] data = db.getData();
            int bits = db.getSize() + 1;

            for (int i = 0; i < bits; i++) {
                int byteIndex = i / 8;
                int bitIndex = 7 - (i % 8);
                boolean bitValue = (data[byteIndex] & (1 << bitIndex)) != 0;

                if (bitValue) {
                    int outByteIndex = bitPos / 8;
                    int outBitIndex = 7 - (bitPos % 8);
                    packedData[outByteIndex] |= (1 << outBitIndex);
                }
                bitPos++;
            }
        }
    }

    public byte[] getPackedData() {
        if (packedData == null) {
            pack();
        }
        return packedData;
    }

    public int getTotalBits() {
        return totalBits;
    }

    @Override
    public String toString() {
        if (packedData == null) pack();
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < packedData.length; i++) {
            sb.append(String.format("%8s", Integer.toBinaryString(packedData[i] & 0xFF)).replace(' ', '0'));
            if (i < packedData.length - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
}