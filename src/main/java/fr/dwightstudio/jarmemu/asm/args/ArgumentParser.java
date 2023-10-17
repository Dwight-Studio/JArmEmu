package fr.dwightstudio.jarmemu.asm.args;

import java.io.File;
import java.util.function.Supplier;

public interface ArgumentParser {

    public abstract int parse(String string);

}
