package fr.dwightstudio.jarmemu.util;

public class MathUtils {

    public static boolean hasCarry(int a, int b) {
        int r = a + b;
        // Overflow if both arguments have the opposite sign of the result and are positive
        return a > 0 && b > 0 && r < 0;
    }

    public static boolean hasCarryCHATGPT(int num1, int num2) {
        // Perform the addition
        int sum = num1 + num2;

        // Check for overflow (carry)
        return ((num1 & num2) | ((num1 | num2) & ~sum)) < 0;
    }

    public static boolean hasOverflow(int a, int b) {
        int r = a + b;
        // Overflow if both arguments have the opposite sign of the result
        return ((a ^ r) & (b ^ r)) < 0;
    }

}
