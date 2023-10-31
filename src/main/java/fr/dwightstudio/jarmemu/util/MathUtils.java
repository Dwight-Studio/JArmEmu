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

    public static int toInt(Byte byte0, Byte byte1, Byte byte2, Byte byte3) {
        short short0 = (short) ((byte0 << 8) + byte1);
        short short1 = (short) ((byte2 << 8) + byte3);
        return toInt(short0, short1);
    }

    public static int toInt(short short0, short short1) {
        return (short0 << 16) + short1;
    }

    public static String toBinString(byte b) {
        StringBuilder rtn = new StringBuilder();

        for (int i = 0 ; i < 8 ; i++) {
            rtn.append((b >> i) & 1);
        }

        return rtn.toString();
    }
}
