package org.geworkbench.util;

/**
 * <p>Title: Binary Encoder Decoder</p>
 * <p>Description: The class has several method for encoding decoding ints
 * into bytes</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class BinaryEncodeDecode {
    //number of bytes for a int
    public static final int SIZE_OF_INT = 4;

    //max size of a byte
    public static final int MASK = 0xff;

    /**
     * Encodes a int into a byte array starting at index.
     * Note: assume 4 bytes for a int, so the encoding will start at
     * index and will end at offset index+3.
     *
     * @param array byte[]
     * @param index the index to start encoding
     *              #param value the int to encode
     */
    static public void encodeUnsignedInt32(byte[] array, int index, int value) {
        int i = index * SIZE_OF_INT;
        array[i] = (byte) (value & MASK);
        array[i + 1] = (byte) ((value >> 8) & MASK);
        array[i + 2] = (byte) ((value >> 16) & MASK);
        array[i + 3] = (byte) ((value >> 24) & MASK);
    }

    /**
     * Encode an int array into a byte array
     *
     * @param value int[] the values to encode
     * @return byte[] the result of the encoding
     */
    static public byte[] encodeUnsignedInt32(int[] value) {
        byte[] encoded = new byte[value.length * SIZE_OF_INT];
        for (int i = 0; i < value.length; i++) {
            encodeUnsignedInt32(encoded, i, value[i]);
        }
        return encoded;
    }

    /**
     * Decode byte array into an int arrray
     *
     * @param array  byte[] array to decde
     * @param offset int begining of decoding
     * @param length int number of ints(!) to decode
     * @return int[]
     */
    static public int[] decodeUnsignedInt32(byte[] array, int offset, int length) {
        int[] intarray = new int[length];
        for (int i = 0; i < length; i++) {
            intarray[i] = decodeUnsignedInt32(array, offset + i);
        }
        return intarray;
    }

    static private int convertByte(byte b) {
        return (b >= 0) ? b : 256 + b;
    }

    static public int decodeUnsignedInt32(byte[] bytes, int offset) {
        int integer = -1;
        int dx = offset * SIZE_OF_INT;
        if (bytes != null) {
            integer = convertByte(bytes[dx + 3]);
            integer = integer * 256 + convertByte(bytes[dx + 2]);
            integer = integer * 256 + convertByte(bytes[dx + 1]);
            integer = integer * 256 + convertByte(bytes[dx]);
        }
        return integer;
    }

}
