package com.github.donovan_dead.Utils.Bytes;

import java.io.IOException;

public class DynamicByteDecontainer {
    private final byte[] data;
    private int bitPosition; // The current position in bits from the start of the data array

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
