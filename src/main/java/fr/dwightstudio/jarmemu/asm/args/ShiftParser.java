package fr.dwightstudio.jarmemu.asm.args;

import fr.dwightstudio.jarmemu.sim.Register;
import fr.dwightstudio.jarmemu.sim.StateContainer;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

// Correspond à un argument supplémentaire à "arg"
public class ShiftParser implements ArgumentParser<Function<byte[],byte[]>> {
    @Override
    public Function<byte[],byte[]> parse(@NotNull StateContainer stateContainer, @NotNull String string) {
        // TODO: Faire le ShiftParser
        return null; // Retourne une fonction qui effectue le shift
    }
}
