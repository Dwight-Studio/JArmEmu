package fr.dwightstudio.jarmemu.asm.args;

import java.util.function.Supplier;

public class NullParser implements ArgumentParser {
    @Override
    public int parse(String string) {
        throw new IllegalStateException("Parsing a Null Argument");
    }
}
