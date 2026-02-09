package com.github.donovan_dead.Utils.Bytes;

/**
 * Represents a value that is intended to be written using a specific number of bits.
 * This class is used to store a value along with the number of bits required to represent it,
 * often used in bitwise serialization or compression schemes.
 */
public class DynamicByte {
    /**
     * The actual value stored.
     */
    private final long value;
    /**
     * The number of bits used to represent the `value`.
     */
    private final int numBits;

    /**
     * Constructor to hold a value and the number of bits it represents.
     *
     * @param value The long value to be stored.
     * @param numBits The number of bits this value should be written with. Must be between 1 and 64.
     */
    public DynamicByte(long value, int numBits) {
        if (numBits < 1 || numBits > 64) {
            throw new IllegalArgumentException("Number of bits must be between 1 and 64.");
        }
        this.value = value;
        this.numBits = numBits;
    }

    /**
     * Gets the stored value.
     * @return The long value.
     */
    public long getValue() {
        return value;
    }

    /**
     * Gets the number of bits used to represent the value.
     * @return The number of bits.
     */
    public int getNumBits() {
        return numBits;
    }
}