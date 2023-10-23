package fr.dwightstudio.jarmemu.util;

public class MathUtils {

    public static boolean isOverflow(int a, int b) {
        int r = a + b;
        // Overflow if both arguments have the opposite sign of the result
        return ((a ^ r) & (b ^ r)) < 0;
    }

}
