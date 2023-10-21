package fr.dwightstudio.jarmemu.asm.args;

import fr.dwightstudio.jarmemu.sim.StateContainer;
import org.jetbrains.annotations.NotNull;

// Correspond à "mem"
public class AddressParser implements ArgumentParser<Integer> {
    @Override
    public Integer parse(@NotNull StateContainer stateContainer, @NotNull String string) {
        return 0; // Nombre sur 8 bits
    }
}
