package fr.dwightstudio.jarmemu.asm.args;

import java.util.function.Supplier;

// Correspond à "imm"
public class ValueParser implements ArgumentParser<Integer> {
    @Override
    public Integer parse(String string) {
        return 0; // Nombre sur 8 bits
    }
}
