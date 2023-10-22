package fr.dwightstudio.jarmemu.asm.args;

import fr.dwightstudio.jarmemu.sim.StateContainer;
import org.jetbrains.annotations.NotNull;

// Correspond à "mem", à utiliser avec ValueOrRegisterParser et ShiftParser
public class AddressParser implements ArgumentParser<Integer> {
    @Override
    public Integer parse(@NotNull StateContainer stateContainer, @NotNull String string) {
        // TODO: Faire l'AddressParser
        return 0; // Nombre sur 8 bits
    }

    @Override
    public Integer none() {
        return null;
    }
}
