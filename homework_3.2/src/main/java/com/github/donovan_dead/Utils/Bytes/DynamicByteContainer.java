package com.github.donovan_dead.Utils.Bytes;

import java.io.ByteArrayOutputStream;

/**
 * A mutable container for dynamically building a sequence of bits.
 * It allows writing bits from values and appending other containers.
 * It handles packing bits into bytes and managing the current byte being built.
 */
public class DynamicByteContainer {
    /**
     * Internal buffer to store the packed bytes.
     */
    private final ByteArrayOutputStream baos;
    /**
     * The current byte being built. Bits are added to this byte until it's full.
     */
    private byte currentByte;
    /**
     * The number of bits currently stored in `currentByte`.
     */
    private long bitCount;

    /**
     * Constructs a new, empty DynamicByteContainer.
     */
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

    

        /**

         * Writes a single bit to the container. If the current byte is full, it's flushed.

         * @param bit The bit to write (0 or 1).

         */

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

         * This is a convenience method that calls {@code write(db.getValue(), db.getNumBits())}.

         * @param db The DynamicByte to add.

         */

        public void add(DynamicByte db) {

            write(db.getValue(), db.getNumBits());

        }

    

        /**

         * Appends the contents of another DynamicByteContainer to this one.

         * It ensures that all bits from the other container are written, handling potential padding.

         * @param other The DynamicByteContainer to append.

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

         * Flushes any remaining bits in the current byte and returns the complete byte array

         * representing the packed data. Any partial byte is padded with zeros to the right.

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

        

        /**

         * Returns a string representation of the container's packed data in binary format.

         * Each byte is represented as an 8-bit binary string, padded with leading zeros.

         * @return A string of binary representations of the packed bytes.

         */

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

    