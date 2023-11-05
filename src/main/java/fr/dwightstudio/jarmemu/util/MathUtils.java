package fr.dwightstudio.jarmemu.util;

import java.nio.ByteBuffer;

public class MathUtils {

    public static boolean hasCarry(int num1, int num2) {
        // Perform the addition
        int sum = num1 + num2;

        // Check for carry
        return ((num1 & num2) | ((num1 | num2) & ~sum)) < 0;
    }

    public static boolean hasOverflow(int a, int b) {
        int r = a + b;
        // Overflow if both arguments have the opposite sign of the result
        return ((a ^ r) & (b ^ r)) < 0;
    }

    public static int toWord(Byte byte3, Byte byte2, Byte byte1, Byte byte0) {
        return ByteBuffer.wrap(new byte[]{byte3, byte2, byte1, byte0}).getInt();
    }

    public static String toBinString(byte b) {
        StringBuilder rtn = new StringBuilder();

        for (int i = 0 ; i < 8 ; i++) {
            rtn.append((b >> (7 - i)) & 1);
        }

        return rtn.toString();
    }
}
