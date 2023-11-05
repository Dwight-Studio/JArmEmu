package fr.dwightstudio.jarmemu.util;

public enum RegisterUtils {

    R0(0),
    R1(1),
    R2(2),
    R3(3),
    R4(4),
    R5(5),
    R6(6),
    R7(7),
    R8(8),
    R9(9),
    R10(10),
    R11(11), FP(11),
    R12(12), IP(12),
    R13(13), SP(13),
    R14(14), LR(14),
    R15(15), PC(15),
    CPSR(16), SPSR(17);

    public static final int OFFSET_PC = 0;

    int n;

    RegisterUtils(int n) {
        this.n = n;
    }

    public int getN() {
        return n;
    }

    public boolean isSpecial() {
        return (getN() > 15);
    }

    public static int lineToPC(int line) {
        return OFFSET_PC + line * 4;
    }

    public static int PCToLine(int pc) {
        return (pc - OFFSET_PC)/4;
    }
}
