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

    public static boolean hasOverflowMul(int a, int b) {
        long r = (long)a * (long)b;
        return (int) r != r;
    }

}
