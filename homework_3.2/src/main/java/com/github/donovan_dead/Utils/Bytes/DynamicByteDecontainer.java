package com.github.donovan_dead.Utils.Bytes;

import java.io.IOException;

/**
 * A utility class for reading a sequence of bits from a byte array.
 * It allows for reading variable-length integers from a stream of bits,
 * handling bit manipulation and tracking the current position.
 */
public class DynamicByteDecontainer {
    /**
     * The byte array containing the data to be de-containerized.
     */
    private final byte[] data;
    /**
     * The current position in bits from the start of the data array.
     * This indicates how many bits have already been read.
     */
    private int bitPosition; // The current position in bits from the start of the data array

    /**
     * Constructs a new DynamicByteDecontainer with the given byte array.
     * Initializes the bit position to 0.
     * @param data The byte array containing the bitstream data.
     */
    public DynamicByteDecontainer(byte[] data) {
        this.data = data;
        this.bitPosition = 0;
    }

    /**
     * Reads a specific number of bits from the stream.
     * @param numBits The number of bits to read (0-64).
     * @return The long value of the read bits.
     * @throws IOException If trying to read beyond the end of the stream.
     */

    /**
     * Reads a specific number of bits from the stream.
     * @param numBits The number of bits to read (0-64).
     * @return The long value of the read bits.
     * @throws IOException If trying to read beyond the end of the stream or if numBits is invalid.
     */
    public long read(int numBits) throws IOException {
        if (numBits < 0 || numBits > 64) {
            throw new IllegalArgumentException("Number of bits to read must be between 0 and 64.");
        }
        if (bitPosition + numBits > data.length * 8) {
            // Allow reading partial data at the very end if the stream is not byte-aligned
            numBits = data.length * 8 - bitPosition;
            if (numBits <= 0) {
                 throw new IOException("End of stream reached. No more bits to read.");
            }
        }

        long value = 0;
        for (int i = 0; i < numBits; i++) {
            int currentBitPosition = bitPosition + i;
            int byteIndex = currentBitPosition / 8;
            int bitIndexInByte = 7 - (currentBitPosition % 8);
            
            // Shift the value to the left to make room for the next bit
            value <<= 1;
            
            // Get the bit and set it in the value
            int bit = (data[byteIndex] >> bitIndexInByte) & 1;
            value |= bit;
        }

        bitPosition += numBits;
        return value;
    }

    /**
     * Checks if there are more bits to read in the stream.
     * @return True if the current bit position is not at the end of the data.
     */
    public boolean hasMoreData() {
        return bitPosition < data.length * 8;
    }
}
