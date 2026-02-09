package com.github.donovan_dead.Utils.Bytes;

import java.io.ByteArrayOutputStream;

public class DynamicByteContainer {
    private final ByteArrayOutputStream baos;
    private byte currentByte;
    private long bitCount;

    public DynamicByteContainer() {
        this.baos = new ByteArrayOutputStream();
        this.currentByte = 0;
        this.bitCount = 0;
    }

    /**
     * Writes a specific number of bits from a long value to the stream.
     * Bits are written from most significant to least significant.
     * @param value The long containing the bits to write.
     * @param numBits The number of bits to write from the value (up to 64).
     */
    public void write(long value, int numBits) {
        if (numBits > 64) {
            throw new IllegalArgumentException("Cannot write more than 64 bits at a time.");
        }

        for (int i = numBits - 1; i >= 0; i--) {
            long bit = (value >> i) & 1;
            writeBit((int)bit);
        }
    }

    private void writeBit(long bit) {
        // Shift the current byte to the left and add the new bit
        currentByte = (byte) ((currentByte << 1) | (bit & 1));
        bitCount++;

        if (bitCount == 8) {
            baos.write(currentByte);
            currentByte = 0;
            bitCount = 0;
        }
    }
    
    /**
     * Adds a DynamicByte object's data to the stream.
     * @param db The DynamicByte to add.
     */
    public void add(DynamicByte db) {
        write(db.getValue(), db.getNumBits());
    }

    /**
     * Appends another DynamicByteContainer to this one, bit by bit, ignoring padding.
     * @param other The container to append.
     */
    public void append(DynamicByteContainer other) {
        // Calculate the true number of bits in the other container before it gets padded.
        long otherTrueTotalBits = (other.baos.size() * 8) + other.bitCount;
        
        // Get the other container's data. Note: this pads the last byte if it's not full.
        byte[] otherData = other.getPackedData();

        // Iterate only through the true bits, ignoring any padding.
        for (int i = 0; i < otherTrueTotalBits; i++) {
            int byteIndex = i / 8;
            int bitIndex = 7 - (i % 8);
            int bit = (otherData[byteIndex] >> bitIndex) & 1;
            writeBit(bit);
        }
    }

    /**
     * Flushes any remaining bits and returns the complete byte array.
     * @return The packed byte array.
     */
    public byte[] getPackedData() {
        // If there are any remaining bits, pad with zeros and write the last byte
        if (bitCount > 0) {
            currentByte = (byte) (currentByte << (8 - bitCount));
            baos.write(currentByte);
        }
        return baos.toByteArray();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        byte[] packedData = getPackedData();
        for (byte b : packedData) {
            sb.append(String.format("%8s", Long.toBinaryString(b & 0xFF)).replace(' ', '0'));
            sb.append(" ");
        }
        return sb.toString();
    }
}