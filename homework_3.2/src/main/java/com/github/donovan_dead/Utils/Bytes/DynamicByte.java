package com.github.donovan_dead.Utils.Bytes;

public class DynamicByte {
    private final long value;
    private final int numBits;

    /**
     * Constructor to hold a value and the number of bits it represents.
     *
     * @param value The long value to be stored.
     * @param numBits The number of bits this value should be written with.
     */
    public DynamicByte(long value, int numBits) {
        this.value = value;
        this.numBits = numBits;
    }

    public long getValue() {
        return value;
    }

    public int getNumBits() {
        return numBits;
    }
}