package fr.dwightstudio.jarmemu.asm.args;

import fr.dwightstudio.jarmemu.sim.Register;
import fr.dwightstudio.jarmemu.sim.StateContainer;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

// Correspond à un argument supplémentaire à "arg"
public class ShiftParser implements ArgumentParser<Function<byte[],byte[]>> {
    @Override
    public Function<byte[],byte[]> parse(@NotNull StateContainer stateContainer, @NotNull String string) {
        return null; // Retourne une fonction qui effectue le shift ou une fonction qui ne fait rien s'il n'y a rien
    }
}
