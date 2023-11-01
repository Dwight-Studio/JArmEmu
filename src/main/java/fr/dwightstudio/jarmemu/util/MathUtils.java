package fr.dwightstudio.jarmemu.util;

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

    public static int toInt(Byte byte3, Byte byte2, Byte byte1, Byte byte0) {
        short short0 = (short) (((0xFF & byte3) << 8) | (0xFF & byte2));
        short short1 = (short) (((0xFF & byte1) << 8) | (0xFF & byte0));
        return toInt(short0, short1);
    }

    public static int toInt(short short1, short short0) {
        return ((0xFFFF & short1) << 16) | (0xFFFF & short0);
    }

    public static String toBinString(byte b) {
        StringBuilder rtn = new StringBuilder();

        for (int i = 0 ; i < 8 ; i++) {
            rtn.append((b >> (7 - i)) & 1);
        }

        return rtn.toString();
    }
}
